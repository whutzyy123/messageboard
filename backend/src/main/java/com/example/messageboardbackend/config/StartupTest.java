package com.example.messageboardbackend.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * 启动测试类，验证Redis和Kafka连接
 * 
 * @author Message Board Team
 * @version 1.0.0
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class StartupTest implements CommandLineRunner {

    private final RedisTemplate<String, Object> redisTemplate;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Override
    public void run(String... args) throws Exception {
        testRedisConnection();
        testKafkaConnection();
    }

    /**
     * 测试Redis连接
     */
    private void testRedisConnection() {
        try {
            redisTemplate.opsForValue().set("startup-test", "Redis连接测试成功");
            Object result = redisTemplate.opsForValue().get("startup-test");
            log.info("✅ Redis连接测试成功: {}", result);
            
            // 清理测试数据
            redisTemplate.delete("startup-test");
        } catch (Exception e) {
            log.error("❌ Redis连接测试失败: {}", e.getMessage());
        }
    }

    /**
     * 测试Kafka连接
     */
    private void testKafkaConnection() {
        try {
            String testMessage = "Kafka连接测试消息";
            kafkaTemplate.send("startup-test", testMessage);
            log.info("✅ Kafka连接测试成功，测试消息已发送");
        } catch (Exception e) {
            log.error("❌ Kafka连接测试失败: {}", e.getMessage());
        }
    }
}
