package com.yupi.aicodehelper.entity;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 角色实体类
 */
@Data
public class Role {
    private Integer id;
    private String roleName;
    private String roleDesc;
    private Date createTime;
    
    // 关联的权限列表
    private List<Permission> permissions;
}