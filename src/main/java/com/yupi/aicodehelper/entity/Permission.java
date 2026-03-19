package com.yupi.aicodehelper.entity;

import lombok.Data;

import java.util.Date;

/**
 * 权限实体类
 */
@Data
public class Permission {
    private Integer id;
    private String permName;
    private String permDesc;
    private Date createTime;
}