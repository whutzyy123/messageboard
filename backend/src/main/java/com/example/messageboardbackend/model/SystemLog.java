package com.example.messageboardbackend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * 系统日志实体类
 * 
 * @author Message Board Team
 * @version 1.0.0
 */
@Entity
@Table(name = "system_log")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SystemLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "log_content", nullable = false, columnDefinition = "TEXT")
    private String logContent;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LogLevel level = LogLevel.INFO;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    /**
     * 日志级别枚举
     */
    public enum LogLevel {
        DEBUG, INFO, WARN, ERROR
    }
}
