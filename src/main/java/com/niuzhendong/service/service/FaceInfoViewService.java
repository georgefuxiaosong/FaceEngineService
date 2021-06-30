package com.niuzhendong.service.service;

import com.niuzhendong.service.dto.Face;
import com.niuzhendong.service.utils.Pager;
import com.niuzhendong.service.utils.Result;
import com.niuzhendong.service.vo.FaceVO;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public interface FaceInfoViewService {

    Result<String> uploadFacePic(MultipartFile facePic);

    Pager<FaceVO> getFaceVOList(int page, int size);

    Result<String> editInfo(List<FaceVO> personInfos);

    String delFaceInfo(List<String> ids);

    void exportCSV(HttpServletResponse response, HttpServletRequest request,List<LinkedHashMap<String, Object>> dataList);



}
