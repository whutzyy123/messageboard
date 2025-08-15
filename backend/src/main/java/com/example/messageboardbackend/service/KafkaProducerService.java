package com.example.messageboardbackend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

/**
 * Kafka生产者服务
 * 
 * @author Message Board Team
 * @version 1.0.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerService {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private static final String SYSTEM_LOGS_TOPIC = "system-logs";

    /**
     * 发送系统日志消息
     * 
     * @param message 日志消息
     */
    public void sendSystemLog(String message) {
        try {
            CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(SYSTEM_LOGS_TOPIC, message);
            
            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("系统日志消息发送成功: topic={}, partition={}, offset={}, message={}", 
                            result.getRecordMetadata().topic(),
                            result.getRecordMetadata().partition(),
                            result.getRecordMetadata().offset(),
                            message);
                } else {
                    log.error("系统日志消息发送失败: message={}, error={}", message, ex.getMessage());
                }
            });
        } catch (Exception e) {
            log.error("发送系统日志消息时发生异常: message={}, error={}", message, e.getMessage(), e);
        }
    }

    /**
     * 发送用户发布留言的日志
     * 
     * @param username 用户名
     */
    public void sendUserMessageLog(String username) {
        String logMessage = String.format("用户 [%s] 发布了一条新留言", username);
        sendSystemLog(logMessage);
    }
}
