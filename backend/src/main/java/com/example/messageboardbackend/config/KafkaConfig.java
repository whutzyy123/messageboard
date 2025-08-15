package com.example.messageboardbackend.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

/**
 * Kafka配置类
 * 
 * @author Message Board Team
 * @version 1.0.0
 */
@Configuration
public class KafkaConfig {

    /**
     * 创建系统日志Topic
     */
    @Bean
    public NewTopic systemLogsTopic() {
        return TopicBuilder.name("system-logs")
                .partitions(3)
                .replicas(1)
                .build();
    }
}
