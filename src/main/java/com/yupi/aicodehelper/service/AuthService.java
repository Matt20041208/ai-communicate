package com.yupi.aicodehelper.service;

import com.yupi.aicodehelper.entity.User;
import com.yupi.aicodehelper.mapper.UserMapper;
import com.yupi.aicodehelper.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 认证服务
 */
@Service
public class AuthService {

    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    /**
     * 用户登录
     * @param username 用户名
     * @param password 密码
     * @return JWT令牌
     */
    public String login(String username, String password) {
        // 查询用户信息
        User user = userMapper.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("用户名不存在");
        }
        
        // 验证密码 - 暂时使用明文密码验证
        // TODO: 生产环境需要使用BCrypt加密验证
        // if (!passwordEncoder.matches(password, user.getPassword())) {
        //     throw new RuntimeException("密码错误");
        // }
        if (!password.equals(user.getPassword()) && !passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("密码错误");
        }
        
        // 检查用户状态
        if (user.getStatus() != 1) {
            throw new RuntimeException("用户已被禁用");
        }
        
        // 模拟用户角色和权限
        List<String> roles = List.of("admin");
        List<String> permissions = List.of("user:list", "user:add", "user:edit", "user:delete", "chat:use");
        
        // 生成JWT令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", username);
        claims.put("roles", roles);
        claims.put("permissions", permissions);
        
        return jwtUtil.generateToken(claims);
    }
    
    /**
     * 用户注册
     * @param user 用户信息
     * @return 注册结果
     */
    public boolean register(User user) {
        // 检查用户名是否已存在
        User existingUser = userMapper.findByUsername(user.getUsername());
        if (existingUser != null) {
            throw new RuntimeException("用户名已存在");
        }
        
        // 加密密码
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setStatus(1); // 默认启用
        
        // 插入用户
        return userMapper.insert(user) > 0;
    }
}