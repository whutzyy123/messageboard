package com.example.messageboardbackend.repository;

import com.example.messageboardbackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 用户数据访问接口
 * 
 * @author Message Board Team
 * @version 1.0.0
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * 根据用户名查找用户
     * 
     * @param username 用户名
     * @return 用户信息
     */
    Optional<User> findByUsername(String username);
    
    /**
     * 检查用户名是否存在
     * 
     * @param username 用户名
     * @return 是否存在
     */
    boolean existsByUsername(String username);
    
    /**
     * 根据用户名和启用状态查找用户
     * 
     * @param username 用户名
     * @param enabled 是否启用
     * @return 用户信息
     */
    Optional<User> findByUsernameAndEnabled(String username, Boolean enabled);
    
    /**
     * 查找所有启用的用户
     * 
     * @return 启用用户列表
     */
    List<User> findByEnabledTrue();
    
    /**
     * 根据用户名模糊查询
     * 
     * @param username 用户名（模糊匹配）
     * @return 用户列表
     */
    @Query("SELECT u FROM User u WHERE u.username LIKE %:username% AND u.enabled = true")
    List<User> findByUsernameContaining(@Param("username") String username);
}
