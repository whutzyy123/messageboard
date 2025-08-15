package com.example.messageboardbackend.controller;

import com.example.messageboardbackend.dto.ApiResponse;
import com.example.messageboardbackend.dto.LoginRequest;
import com.example.messageboardbackend.dto.LoginResponse;
import com.example.messageboardbackend.dto.UserDto;
import com.example.messageboardbackend.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 * 
 * @author Message Board Team
 * @version 1.0.0
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:8080"})
public class AuthController {
    
    private final UserService userService;
    
    /**
     * 用户注册
     * 
     * @param userDto 用户信息
     * @return 注册结果
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<String>> register(@Valid @RequestBody UserDto userDto) {
        try {
            userService.register(userDto);
            return ResponseEntity.ok(ApiResponse.success("用户注册成功"));
        } catch (Exception e) {
            log.error("用户注册失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error("用户注册失败: " + e.getMessage()));
        }
    }
    
    /**
     * 用户登录
     * 
     * @param loginRequest 登录请求
     * @return 登录结果
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            LoginResponse response = userService.login(loginRequest);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            log.error("用户登录失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error("用户登录失败: " + e.getMessage()));
        }
    }
    
    /**
     * 健康检查
     * 
     * @return 健康状态
     */
    @GetMapping("/health")
    public ResponseEntity<ApiResponse<String>> health() {
        return ResponseEntity.ok(ApiResponse.success("服务运行正常"));
    }
}
