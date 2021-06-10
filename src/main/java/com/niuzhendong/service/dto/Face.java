package com.niuzhendong.service.dto;

import lombok.Data;

@Data
public class Face {
    private Long id;
    private String peoId;
    private String peoName;

    private int status;
    private String peoType;
    private String peoDes;

    private String bucketName;
    private String fileName;

    private int type;
    private byte[] feature;

    private int delFlag;
}
