package com.niuzhendong.service.vo;

import lombok.Data;

@Data
public class FaceVO {
    //人员id
    private String id;

    //身份证号
    private String peoId;

    //姓名
    private String peoName;

    //人脸库状态, 1:已提取，0:未提取
    private int status;

    //人员类型
    private String peoType;

    //是否布控
    private int type;

    //人脸图片
    private String facePic;
}
