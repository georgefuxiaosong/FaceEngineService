package com.niuzhendong.service.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.niuzhendong.service.dao.FaceDao;
import com.niuzhendong.service.dao.FaceInfoViewDao;
import com.niuzhendong.service.dto.Face;
import com.niuzhendong.service.service.FaceInfoViewService;
import com.niuzhendong.service.service.MinioService;
import com.niuzhendong.service.utils.ConvertUtils;
import com.niuzhendong.service.utils.IDCardUtil;
import com.niuzhendong.service.utils.Pager;
import com.niuzhendong.service.vo.FaceVO;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FaceInfoViewServiceImpl implements FaceInfoViewService {

    @Autowired
    private MinioService minioService;

    @Autowired
    private FaceInfoViewDao faceInfoViewDao;

    @Autowired
    private FaceDao faceDao;

    @Override
    public String uploadFacePic(MultipartFile facePic) {
        if (facePic == null || facePic.isEmpty()) {
            return "文件为空";
        }
        //获取文件名,判断是否为合法的身份证号
        String OriginalFilename = facePic.getOriginalFilename();
        String idcard = FilenameUtils.getBaseName(OriginalFilename);
        String idcardVerify = IDCardUtil.IDCardValidate(idcard);
        if(!"yes".equals(idcardVerify)){
            return idcard +":" + idcardVerify;
        }
        try {
            String url= minioService.uploadFile(facePic.getInputStream(),"face",OriginalFilename);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Face faceInfo = new Face();
        faceInfo.setId(System.currentTimeMillis() * 100000L + System.nanoTime() % 1000000L);//时间戳作为id
        faceInfo.setBucketName("face");
        faceInfo.setFileName(OriginalFilename);
        faceInfo.setPeoId(idcard);
        faceInfo.setDelFlag(0);
        Integer affectRows = faceInfoViewDao.saveFaceInfo(faceInfo);
        String insertRes=null;
        if (affectRows == 1) {
            insertRes="新增人脸成功";
        }else{
            insertRes="重新新增人脸成功";
        }

        return insertRes;
    }

    @Override
    public Pager<FaceVO> getFaceVOList(int page, int size) {

        PageHelper.startPage(page,size);
        List<Face> faceRes= faceDao.getFaceList();
        PageInfo<Face> res = new PageInfo<>(faceRes);
        List<FaceVO> faceVoList = new ArrayList<>();
        List<Map<String,String>> personLabelsList= faceInfoViewDao.allPersonLabel();//查询所有的人员类型标签
        Map personLabelsMap = personLabelsList.stream().collect(Collectors.toMap(p->p.get("code"),p->p.get("label")));
        for(Face face : faceRes){
            List<String> personType = Arrays.asList((face.getPeoType()!=null ?face.getPeoType() : "") .split(","));
            List<Object> peoTypeList = personType.stream().map(item -> {
                return personLabelsMap.get(item);
            }).collect(Collectors.toList());//人员类型列表
            String peoType = StringUtils.join(peoTypeList,",");//用逗号连接多个人员类型
            String facePicUrl = minioService.getUrlFromMinio(face.getBucketName(),face.getFileName());//人脸图像
            FaceVO faceVo = ConvertUtils.sourceToTarget(face, FaceVO.class);
            faceVo.setFacePic(facePicUrl);
            faceVo.setPeoType(peoType);
            faceVo.setId(String.valueOf(face.getId()));
            faceVoList.add(faceVo);
        }

        Pager<FaceVO> pager = new Pager<>();
        pager.setRows(faceVoList);
        pager.setTotal(res.getTotal());
        pager.setSize(size);
        pager.setPage(page);

        return pager;
    }

    @Override
    public String editInfo(List<FaceVO> personInfos){
        List<Map<String,String>> personLabelsList= faceInfoViewDao.allPersonLabel();//查询所有的人员类型标签
        Map personLabelsMap = personLabelsList.stream().collect(Collectors.toMap(p->p.get("label"),p->p.get("code")));
        for(FaceVO face : personInfos){
            List<String> personType = Arrays.asList((face.getPeoType()!=null ?face.getPeoType() : "") .split(","));
            List<Object> peoTypeList = personType.stream().map(item -> {
                return personLabelsMap.get(item);
            }).collect(Collectors.toList());//人员类型列表
            String peoType = StringUtils.join(peoTypeList,",");//用逗号连接多个人员类型

            Face faceDTO = ConvertUtils.sourceToTarget(face, Face.class);
            faceDTO.setPeoType(peoType);
            faceDTO.setId(Long.valueOf(face.getId()));// TODO 如何防止有人更改身份证号后导致图片名字改变
            faceInfoViewDao.editInfo(faceDTO);
        }

        return "数据更新成功";
    }

    @Override
    public String delFaceInfo(List<String> ids){

        faceInfoViewDao.delFaceInfo(ids);

        return "数据删除成功";
    }

}
