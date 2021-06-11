package com.niuzhendong.service.service;

import com.niuzhendong.service.dto.Face;
import com.niuzhendong.service.utils.Pager;
import com.niuzhendong.service.vo.FaceVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FaceInfoViewService {

    String uploadFacePic(MultipartFile facePic);

    Pager<FaceVO> getFaceVOList(int page, int size);

    String editInfo(List<FaceVO> personInfos);

    String delFaceInfo(List<String> ids);



}
