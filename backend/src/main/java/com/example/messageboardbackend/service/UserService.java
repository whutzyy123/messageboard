package com.example.messageboardbackend.service;

import com.example.messageboardbackend.dto.LoginRequest;
import com.example.messageboardbackend.dto.LoginResponse;
import com.example.messageboardbackend.dto.UserDto;
import com.example.messageboardbackend.model.User;

import java.util.List;

/**
 * 用户服务接口
 * 
 * @author Message Board Team
 * @version 1.0.0
 */
public interface UserService {
    
    /**
     * 用户注册
     * 
     * @param userDto 用户信息
     * @return 注册后的用户
     */
    User register(UserDto userDto);
    
    /**
     * 用户登录
     * 
     * @param loginRequest 登录请求
     * @return 登录响应
     */
    LoginResponse login(LoginRequest loginRequest);
    
    /**
     * 根据ID获取用户
     * 
     * @param id 用户ID
     * @return 用户信息
     */
    User getUserById(Long id);
    
    /**
     * 根据用户名获取用户
     * 
     * @param username 用户名
     * @return 用户信息
     */
    User getUserByUsername(String username);
    
    /**
     * 获取所有用户
     * 
     * @return 用户列表
     */
    List<User> getAllUsers();
    
    /**
     * 更新用户信息
     * 
     * @param userDto 用户信息
     * @return 更新后的用户
     */
    User updateUser(UserDto userDto);
    
    /**
     * 删除用户
     * 
     * @param id 用户ID
     */
    void deleteUser(Long id);
    
    /**
     * 检查用户名是否存在
     * 
     * @param username 用户名
     * @return 是否存在
     */
    boolean existsByUsername(String username);
}
