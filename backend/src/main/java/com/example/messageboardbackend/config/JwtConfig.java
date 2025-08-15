package com.example.messageboardbackend.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * JWT 配置类
 * 
 * @author Message Board Team
 * @version 1.0.0
 */
@Component
@ConfigurationProperties(prefix = "jwt")
@Data
public class JwtConfig {
    
    /**
     * JWT 密钥
     */
    private String secret;
    
    /**
     * JWT 过期时间（毫秒）
     */
    private Long expiration;
    
    /**
     * JWT 请求头名称
     */
    private String header;
    
    /**
     * JWT 前缀
     */
    private String prefix;
}
