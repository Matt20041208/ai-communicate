package com.yupi.aicodehelper.service;

import com.yupi.aicodehelper.entity.User;
import com.yupi.aicodehelper.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用户服务
 */
@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;
    
    /**
     * 根据用户名查询用户信息
     * @param username 用户名
     * @return 用户信息
     */
    public User findByUsername(String username) {
        return userMapper.findByUsername(username);
    }
    
    /**
     * 查询用户的角色列表
     * @param userId 用户ID
     * @return 角色列表
     */
    public List<String> findRolesByUserId(Integer userId) {
        return userMapper.findRolesByUserId(userId);
    }
    
    /**
     * 查询用户的权限列表
     * @param userId 用户ID
     * @return 权限列表
     */
    public List<String> findPermissionsByUserId(Integer userId) {
        return userMapper.findPermissionsByUserId(userId);
    }
    
    /**
     * 更新用户信息
     * @param user 用户信息
     * @return 更新结果
     */
    public boolean update(User user) {
        return userMapper.update(user) > 0;
    }
}