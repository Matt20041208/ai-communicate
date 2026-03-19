package com.yupi.aicodehelper.controller;

import com.yupi.aicodehelper.entity.User;
import com.yupi.aicodehelper.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 认证控制器
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthService authService;
    
    /**
     * 登录接口
     * @param loginRequest 登录请求
     * @return 登录结果
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest loginRequest) {
        logger.info("收到登录请求，用户名: {}", loginRequest.getUsername());
        try {
            String token = authService.login(loginRequest.getUsername(), loginRequest.getPassword());
            Map<String, Object> result = new HashMap<>();
            result.put("token", token);
            result.put("message", "登录成功");
            logger.info("用户 {} 登录成功", loginRequest.getUsername());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("用户 {} 登录失败: {}", loginRequest.getUsername(), e.getMessage(), e);
            Map<String, Object> result = new HashMap<>();
            result.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }
    
    /**
     * 注册接口
     * @param user 用户信息
     * @return 注册结果
     */
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody User user) {
        logger.info("收到注册请求，用户名: {}", user.getUsername());
        try {
            boolean success = authService.register(user);
            Map<String, Object> result = new HashMap<>();
            result.put("success", success);
            result.put("message", "注册成功");
            logger.info("用户 {} 注册成功", user.getUsername());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("用户 {} 注册失败: {}", user.getUsername(), e.getMessage(), e);
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }
    
    /**
     * 登录请求参数
     */
    private static class LoginRequest {
        private String username;
        private String password;
        
        public String getUsername() {
            return username;
        }
        
        public void setUsername(String username) {
            this.username = username;
        }
        
        public String getPassword() {
            return password;
        }
        
        public void setPassword(String password) {
            this.password = password;
        }
    }
}