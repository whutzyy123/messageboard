package com.messageboard.service;

import com.messageboard.dto.UserLoginDto;
import com.messageboard.dto.UserRegistrationDto;
import com.messageboard.entity.User;
import com.messageboard.repository.UserRepository;
import com.messageboard.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户服务类
 */
@Service
@Transactional
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    /**
     * 用户注册
     */
    public Map<String, Object> register(UserRegistrationDto registrationDto) {
        Map<String, Object> result = new HashMap<>();
        
        // 验证确认密码
        if (!registrationDto.getPassword().equals(registrationDto.getConfirmPassword())) {
            result.put("success", false);
            result.put("message", "两次输入的密码不一致");
            return result;
        }
        
        // 检查用户名是否已存在
        if (userRepository.existsByUsername(registrationDto.getUsername())) {
            result.put("success", false);
            result.put("message", "用户名已存在");
            return result;
        }
        
        // 检查邮箱是否已存在
        if (userRepository.existsByEmail(registrationDto.getEmail())) {
            result.put("success", false);
            result.put("message", "邮箱已被注册");
            return result;
        }
        
        // 创建新用户
        User user = new User();
        user.setUsername(registrationDto.getUsername());
        user.setEmail(registrationDto.getEmail());
        user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
        user.setStatus(1);
        
        User savedUser = userRepository.save(user);
        
        result.put("success", true);
        result.put("message", "注册成功");
        result.put("user", savedUser);
        
        return result;
    }
    
    /**
     * 用户登录
     */
    public Map<String, Object> login(UserLoginDto loginDto) {
        Map<String, Object> result = new HashMap<>();
        
        // 查找用户
        User user = userRepository.findByUsernameAndStatus(loginDto.getUsername(), 1)
                .orElse(null);
        
        if (user == null) {
            result.put("success", false);
            result.put("message", "用户名或密码错误");
            return result;
        }
        
        // 验证密码
        if (!passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
            result.put("success", false);
            result.put("message", "用户名或密码错误");
            return result;
        }
        
        // 生成JWT令牌
        String token = jwtUtil.generateToken(user.getUsername());
        
        result.put("success", true);
        result.put("message", "登录成功");
        result.put("token", token);
        result.put("user", user);
        
        return result;
    }
    
    /**
     * 根据用户名查找用户
     */
    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }
    
    /**
     * 根据ID查找用户
     */
    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }
}
