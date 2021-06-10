package com.niuzhendong.service.controller;

import com.niuzhendong.service.dto.Face;
import com.niuzhendong.service.service.FaceInfoViewService;
import com.niuzhendong.service.utils.IDCardUtil;
import com.niuzhendong.service.utils.Pager;
import com.niuzhendong.service.utils.Result;
import com.niuzhendong.service.vo.FaceVO;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
public class FaceInfoViewController {

    @Autowired
    private FaceInfoViewService faceInfoViewService;

    /**
     * 新增人脸
     * @param facePic
     * @return
     */
    @RequestMapping(value = "/api/uploadFacePic", method = RequestMethod.POST)
    public Result<String> uploadFacePic(@RequestPart("facePic") MultipartFile facePic){

        return new Result<String>().ok(faceInfoViewService.uploadFacePic(facePic));
    }

    /**
     * 查询所有的人脸信息并进行展示
     */
    @RequestMapping(value = "/api/getFaceVOList", method = RequestMethod.GET)
    public Pager<FaceVO> getFaceVOList(@RequestParam("page") int page, @RequestParam("size") int size){

        return faceInfoViewService.getFaceVOList(page, size);
    }

    /**
     * 编辑/删除人脸信息
     */
    @RequestMapping(value = "/api/editInfo",method = RequestMethod.POST)
    public Result<String> editInfo(List<FaceVO> personInfos){
        if (personInfos == null || personInfos.size() == 0) {
            return new Result<String>().error(0,"请求参数为空");
        }

        return new Result<String>().ok(faceInfoViewService.editInfo(personInfos));

    }
}
