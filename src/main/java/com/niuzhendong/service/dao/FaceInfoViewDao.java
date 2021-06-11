package com.niuzhendong.service.dao;

import com.niuzhendong.service.dto.Face;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface FaceInfoViewDao {

    Integer saveFaceInfo(Face faceInfo);

    List<Map<String,String>> allPersonLabel();

     void editInfo(Face personInfos);

     void delFaceInfo(List<String> ids);




}
