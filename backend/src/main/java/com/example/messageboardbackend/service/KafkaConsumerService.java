package com.example.messageboardbackend.service;

import com.example.messageboardbackend.model.SystemLog;
import com.example.messageboardbackend.repository.SystemLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Kafka消费者服务
 * 
 * @author Message Board Team
 * @version 1.0.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumerService {

    private final SystemLogRepository systemLogRepository;

    /**
     * 监听系统日志Topic
     * 
     * @param message 日志消息
     */
    @KafkaListener(topics = "system-logs", groupId = "message-board-group")
    public void consumeSystemLog(String message) {
        try {
            log.info("收到系统日志消息: {}", message);
            
            // 创建系统日志记录
            SystemLog systemLog = new SystemLog();
            systemLog.setLogContent(message);
            systemLog.setCreatedAt(LocalDateTime.now());
            
            // 保存到数据库
            systemLogRepository.save(systemLog);
            
            log.info("系统日志已保存到数据库: id={}, content={}", systemLog.getId(), message);
        } catch (Exception e) {
            log.error("处理系统日志消息时发生异常: message={}, error={}", message, e.getMessage(), e);
        }
    }
}
