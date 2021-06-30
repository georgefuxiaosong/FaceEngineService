package com.niuzhendong.service.controller;

import com.niuzhendong.service.dto.Face;
import com.niuzhendong.service.service.FaceInfoViewService;
import com.niuzhendong.service.utils.IDCardUtil;
import com.niuzhendong.service.utils.Pager;
import com.niuzhendong.service.utils.Result;
import com.niuzhendong.service.vo.FaceVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

        return faceInfoViewService.uploadFacePic(facePic);
    }

    /**
     * 查询所有的人脸信息并进行展示
     */
    @RequestMapping(value = "/api/getFaceVOList", method = RequestMethod.GET)
    public Pager<FaceVO> getFaceVOList(@RequestParam("page") int page, @RequestParam("size") int size){

        return faceInfoViewService.getFaceVOList(page, size);
    }

    /**
     * 编辑人脸信息
     */
    @RequestMapping(value = "/api/editInfo",method = RequestMethod.POST)
    public Result<String> editInfo(@RequestBody List<FaceVO> personInfos){
        if (personInfos == null || personInfos.size() == 0) {
            return new Result<String>().error(0,"请求参数为空");
        }

        return faceInfoViewService.editInfo(personInfos);

    }

    /**
     * 删除
     */
    @RequestMapping(value = "/api/delFaceInfo",method = RequestMethod.POST)
    public Result<String> delFaceInfo(@RequestBody List<String> ids){
        if (ids == null || ids.size() == 0) {
            return new Result<String>().error(0,"请求参数为空");
        }

        return new Result<String>().ok(faceInfoViewService.delFaceInfo(ids));

    }

    /**
     * 导出csv文件
     */
    @RequestMapping(value = "/api/exportCSV",method = RequestMethod.POST, produces = "application/json; charset=utf-8" )
    public void exportCSV(@RequestBody List<LinkedHashMap<String, Object>> dataList, HttpServletRequest request, HttpServletResponse response){

        faceInfoViewService.exportCSV(response,request,dataList);
//        return new Result().ok();

    }

}
