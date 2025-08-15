package com.example.messageboardbackend.service.impl;

import com.example.messageboardbackend.dto.LoginRequest;
import com.example.messageboardbackend.dto.LoginResponse;
import com.example.messageboardbackend.dto.UserDto;
import com.example.messageboardbackend.model.User;
import com.example.messageboardbackend.repository.UserRepository;
import com.example.messageboardbackend.service.UserService;
import com.example.messageboardbackend.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 用户服务实现类
 * 
 * @author Message Board Team
 * @version 1.0.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserServiceImpl implements UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    
    @Override
    public User register(UserDto userDto) {
        // 检查用户名是否已存在
        if (existsByUsername(userDto.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }
        
        // 创建新用户
        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setEnabled(true);
        
        User savedUser = userRepository.save(user);
        log.info("用户注册成功: {}", savedUser.getUsername());
        
        return savedUser;
    }
    
    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        // 查找用户
        User user = userRepository.findByUsernameAndEnabled(loginRequest.getUsername(), true)
                .orElseThrow(() -> new RuntimeException("用户名或密码错误"));
        
        // 验证密码
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }
        
        // 生成JWT Token
        String token = jwtUtil.generateToken(user.getUsername());
        
        log.info("用户登录成功: {}", user.getUsername());
        
        return new LoginResponse(token, user.getUsername(), "登录成功", true);
    }
    
    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
    }
    
    @Override
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
    }
    
    @Override
    public List<User> getAllUsers() {
        return userRepository.findByEnabledTrue();
    }
    
    @Override
    public User updateUser(UserDto userDto) {
        User user = getUserById(userDto.getId());
        
        // 更新用户信息
        if (userDto.getUsername() != null && !userDto.getUsername().equals(user.getUsername())) {
            if (existsByUsername(userDto.getUsername())) {
                throw new RuntimeException("用户名已存在");
            }
            user.setUsername(userDto.getUsername());
        }
        
        if (userDto.getPassword() != null && !userDto.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        }
        
        if (userDto.getEnabled() != null) {
            user.setEnabled(userDto.getEnabled());
        }
        
        User updatedUser = userRepository.save(user);
        log.info("用户信息更新成功: {}", updatedUser.getUsername());
        
        return updatedUser;
    }
    
    @Override
    public void deleteUser(Long id) {
        User user = getUserById(id);
        user.setEnabled(false);
        userRepository.save(user);
        log.info("用户已禁用: {}", user.getUsername());
    }
    
    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
}
