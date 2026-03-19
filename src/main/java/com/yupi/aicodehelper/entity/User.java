package com.yupi.aicodehelper.entity;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 用户实体类
 */
@Data
public class User {
    private Integer id;
    private String username;
    private String password;
    private String nickname;
    private String email;
    private String phone;
    private Integer status;
    private Date createTime;
    private Date updateTime;
    
    // 关联的角色列表
    private List<Role> roles;
    // 关联的权限列表
    private List<Permission> permissions;
}