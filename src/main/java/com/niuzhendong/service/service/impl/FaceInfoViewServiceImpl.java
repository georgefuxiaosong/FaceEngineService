package com.niuzhendong.service.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.niuzhendong.service.dao.FaceDao;
import com.niuzhendong.service.dao.FaceInfoViewDao;
import com.niuzhendong.service.dto.Face;
import com.niuzhendong.service.service.FaceInfoViewService;
import com.niuzhendong.service.service.MinioService;
import com.niuzhendong.service.utils.*;
import com.niuzhendong.service.vo.FaceVO;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;
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
    public Result<String> uploadFacePic(MultipartFile facePic) {
        if (facePic == null || facePic.isEmpty()) {
            return new Result<String>().error(500,"文件为空");
        }
        long id = System.currentTimeMillis() * 100000L + System.nanoTime() % 1000000L;
        //获取文件名,判断是否为合法的身份证号
        String OriginalFilename = facePic.getOriginalFilename();
        String idcard = FilenameUtils.getBaseName(OriginalFilename);
        String idcardVerify = IDCardUtil.IDCardValidate(idcard);
        if(!"yes".equals(idcardVerify)){
            return new Result<String>().error(500,idcard +":" + idcardVerify);
        }
        String faceFileNme="";
        try {
            faceFileNme = OriginalFilename.replace(idcard,String.valueOf(id));//用编号来为图片命名
            String url= minioService.uploadFile(facePic.getInputStream(),"face",faceFileNme);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Face faceInfo = new Face();

        faceInfo.setId(id);//时间戳作为id
        faceInfo.setBucketName("face");
        faceInfo.setFileName(faceFileNme);
        faceInfo.setPeoId(idcard);
        faceInfo.setDelFlag(0);
        Integer affectRows = faceInfoViewDao.saveFaceInfo(faceInfo);
        String insertRes=null;
        if (affectRows == 1) {
            insertRes="新增人脸成功";
        }else{
            insertRes="重新新增人脸成功";
        }

        return new Result<String>().ok(insertRes);
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
    public Result<String> editInfo(List<FaceVO> personInfos){
        List<Map<String,String>> personLabelsList= faceInfoViewDao.allPersonLabel();//查询所有的人员类型标签
        Map personLabelsMap = personLabelsList.stream().collect(Collectors.toMap(p->p.get("label"),p->p.get("code")));
        for(FaceVO face : personInfos){
            //判断身份证号是否符合要求
            String idcardVerify = IDCardUtil.IDCardValidate(face.getPeoId());
            if(!"yes".equals(idcardVerify)){
                return new Result<String>().error(500,face.getPeoId() +":" + idcardVerify);
            }
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

        return new Result<String>().ok("数据更新成功");
    }

    @Override
    public String delFaceInfo(List<String> ids){

        faceInfoViewDao.delFaceInfo(ids);

        return "数据删除成功";
    }

    @Override
    public void exportCSV(HttpServletResponse response, HttpServletRequest request, List<LinkedHashMap<String, Object>> dataList) {

//        String titles = "编号,姓名,图片,身份证号码,人员类型,是否布控,状态"; // 设置表头
//        // 设置每列字段
//        String keys = "id,peoName,facePic,peoId,peoType,type,status";
//        // 设置导出文件前缀
//        String excelName = "face_info";
//        LinkedHashMap<String, String> fieldMap = new LinkedHashMap<>();
//        fieldMap.put("id", "编号");
//        fieldMap.put("peoName", "姓名");
//        fieldMap.put("facePic", "图片");
//        fieldMap.put("peoId", "身份证号码");
//        fieldMap.put("peoType", "人员类型");
//        fieldMap.put("type", "是否布控");
//        fieldMap.put("status", "状态");
//
//        //导出用户相关信息
//        new ExportExcelUtils();
//        ExportExcelUtils.export(excelName,dataList,fieldMap,response);

        String fileName = null;
        try {
            fileName = this.getFileName(request, "face_info.csv");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM.toString());
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName);

        LinkedHashMap<String, Object> header = new LinkedHashMap<>();
        LinkedHashMap<String, Object> body = new LinkedHashMap<>();
        List<LinkedHashMap<String, Object>> data = new ArrayList<>();
        header.put("id", "编号");
        header.put("peoId", "身份证号码");
        header.put("peoName", "姓名");
        header.put("status", "状态");
        header.put("peoType", "人员类型");
        header.put("type", "是否布控");
        header.put("facePic", "图片");

        data.add(header);
        data.addAll(dataList);

        try {
            FileCopyUtils.copy(ExportUtil.exportCSV(data), response.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getFileName(HttpServletRequest request, String name) throws UnsupportedEncodingException {
        String userAgent = request.getHeader("USER-AGENT");
        return userAgent.contains("Mozilla") ? new String(name.getBytes(), "ISO8859-1") : name;
    }





}
