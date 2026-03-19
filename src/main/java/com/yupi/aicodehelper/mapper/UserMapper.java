package com.yupi.aicodehelper.mapper;

import com.yupi.aicodehelper.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户Mapper接口
 */
@Mapper
public interface UserMapper {
    /**
     * 根据用户名查询用户信息
     * @param username 用户名
     * @return 用户信息
     */
    User findByUsername(@Param("username") String username);
    
    /**
     * 查询用户的角色列表
     * @param userId 用户ID
     * @return 角色列表
     */
    List<String> findRolesByUserId(@Param("userId") Integer userId);
    
    /**
     * 查询用户的权限列表
     * @param userId 用户ID
     * @return 权限列表
     */
    List<String> findPermissionsByUserId(@Param("userId") Integer userId);
    
    /**
     * 插入新用户
     * @param user 用户信息
     * @return 影响行数
     */
    int insert(User user);
    
    /**
     * 更新用户信息
     * @param user 用户信息
     * @return 影响行数
     */
    int update(User user);
}