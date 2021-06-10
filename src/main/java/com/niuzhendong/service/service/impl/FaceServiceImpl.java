package com.niuzhendong.service.service.impl;

import com.arcsoft.face.FaceFeature;
import com.arcsoft.face.FaceInfo;
import com.arcsoft.face.FaceSimilar;
import com.arcsoft.face.ImageQuality;
import com.arcsoft.face.toolkit.ImageInfo;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.niuzhendong.service.dao.FaceDao;
import com.niuzhendong.service.dto.Face;
import com.niuzhendong.service.dto.FeatureItem;
import com.niuzhendong.service.service.FaceService;
import com.niuzhendong.service.service.MinioService;
import com.niuzhendong.service.utils.*;
import com.niuzhendong.service.vo.FaceVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
public class FaceServiceImpl implements FaceService {

    @Autowired
    private FaceUtils faceUtils;

    @Autowired
    private FaceDao faceDao;

    @Override
    public List<FaceInfo> faceExists(ImageInfo imageInfo) {
        return faceUtils.faceExists(imageInfo);
    }

    @Override
    public List<Face> getFaceInfo() {
        return faceDao.getFaceInfo();
    }

    @Override
    public Pager<Face> getFaceList(int page, int size) {
        PageHelper.startPage(page,size);
        List<Face> faceRes= faceDao.getFaceList();
        PageInfo<Face> res = new PageInfo<>(faceRes);
        Pager<Face> pager = new Pager<>();
        pager.setRows(res.getList());
        pager.setTotal(res.getTotal());
        pager.setSize(size);
        pager.setPage(page);

        return pager;
    }

    @Override
    public List<Face> findFaceInfo(List<Long> ids) {
        return faceDao.findFaceInfo(ids);
    }

    @Override
    public List<Face> findFaceInfoForList(String query) {
        return faceDao.findFaceInfoForList(query);
    }

    @Override
    public List<FeatureItem> imageQualityDetect(ImageInfo imageInfo, int mask, List<FaceInfo> faceInfos) {
        return faceUtils.imageQualityDetect(imageInfo,mask,faceInfos);
    }

    @Override
    public FeatureItem imageQualityDetectForSingle(ImageInfo imageInfo, int mask, FaceInfo faceInfo) {
        return faceUtils.imageQualityDetectForSingle(imageInfo,mask,faceInfo);
    }

    @Override
    public FaceFeature getImageFeature(ImageInfo imageInfo, FaceInfo faceInfo) {
        return faceUtils.getImageFeature(imageInfo,faceInfo);
    }

    @Override
    public FaceSimilar compareFaceFeature(FaceFeature targetFaceFeature, FaceFeature sourceFaceFeature) {
        return faceUtils.compareFaceFeature(targetFaceFeature,sourceFaceFeature);
    }

    @Override
    public void updateFace(Face face) {
        faceDao.updateFace(face);
    }

    @Override
    public void updateFaceStatus(List<Long> ids) {
        faceDao.updateFaceStatus(ids);
    }



}
