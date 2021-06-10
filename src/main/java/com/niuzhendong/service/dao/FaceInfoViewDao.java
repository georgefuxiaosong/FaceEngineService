package com.niuzhendong.service.dao;

import com.niuzhendong.service.dto.Face;
import com.niuzhendong.service.vo.FaceVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface FaceInfoViewDao {

    Integer saveFaceInfo(Face faceInfo);

    List<Map<String,String>> allPersonLabel();

    String editInfo(List<FaceVO> personInfos);



}
