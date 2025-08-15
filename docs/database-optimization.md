# 数据库优化指南

本文档介绍在线留言板系统的数据库优化策略，包括索引设计、查询优化和性能调优。

## 目录

- [索引设计策略](#索引设计策略)
- [表结构优化](#表结构优化)
- [查询性能优化](#查询性能优化)
- [缓存策略](#缓存策略)
- [监控和调优](#监控和调优)

## 索引设计策略

### 1. Message表索引设计

```sql
-- 用户ID索引：优化按用户查询留言的性能
CREATE INDEX idx_message_user_id ON message(user_id);

-- 创建时间索引：优化按时间排序和范围查询的性能
CREATE INDEX idx_message_created_at ON message(created_at);

-- 删除状态索引：优化软删除查询的性能
CREATE INDEX idx_message_deleted ON message(deleted);

-- 复合索引：优化用户留言列表查询（用户ID + 删除状态 + 创建时间）
CREATE INDEX idx_message_user_deleted_created ON message(user_id, deleted, created_at);

-- 内容搜索索引：优化全文搜索性能
CREATE INDEX idx_message_content ON message(content(100));
```

### 2. User表索引设计

```sql
-- 用户名唯一索引：确保用户名唯一性，优化登录查询
CREATE UNIQUE INDEX idx_user_username ON user(username);

-- 创建时间索引：优化用户统计查询
CREATE INDEX idx_user_created_at ON user(created_at);
```

### 3. SystemLog表索引设计

```sql
-- 创建时间索引：优化日志查询和清理
CREATE INDEX idx_system_log_created_at ON system_log(created_at);

-- 日志内容索引：优化日志搜索
CREATE INDEX idx_system_log_content ON system_log(log_content(100));
```

## 表结构优化

### 1. 字段类型优化

```sql
-- 优化前
CREATE TABLE message (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    deleted TINYINT(1) DEFAULT 0,
    created_at DATETIME,
    updated_at DATETIME
);

-- 优化后
CREATE TABLE message (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    deleted BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- 添加索引
    INDEX idx_user_id (user_id),
    INDEX idx_created_at (created_at),
    INDEX idx_deleted (deleted),
    INDEX idx_user_deleted_created (user_id, deleted, created_at),
    INDEX idx_content (content(100)),
    
    -- 添加外键约束
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE
);
```

### 2. 分区策略（适用于大数据量）

```sql
-- 按时间分区（适用于日志表）
CREATE TABLE system_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    log_content TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) PARTITION BY RANGE (YEAR(created_at)) (
    PARTITION p2023 VALUES LESS THAN (2024),
    PARTITION p2024 VALUES LESS THAN (2025),
    PARTITION p2025 VALUES LESS THAN (2026),
    PARTITION p_future VALUES LESS THAN MAXVALUE
);
```

## 查询性能优化

### 1. 分页查询优化

```java
// 优化前：使用OFFSET进行分页
@Query("SELECT m FROM Message m WHERE m.deleted = false ORDER BY m.createdAt DESC")
Page<Message> findByDeletedFalse(Pageable pageable);

// 优化后：使用游标分页（适用于大数据量）
@Query("SELECT m FROM Message m WHERE m.deleted = false AND m.id < :lastId ORDER BY m.id DESC")
Page<Message> findByDeletedFalseAndIdLessThan(@Param("lastId") Long lastId, Pageable pageable);
```

### 2. 关联查询优化

```java
// 优化前：N+1查询问题
@Query("SELECT m FROM Message m WHERE m.deleted = false")
List<Message> findAllMessages();

// 优化后：使用JOIN FETCH避免N+1查询
@Query("SELECT m FROM Message m JOIN FETCH m.user WHERE m.deleted = false ORDER BY m.createdAt DESC")
List<Message> findAllMessagesWithUser();
```

### 3. 批量操作优化

```java
// 批量插入优化
@Modifying
@Query(value = "INSERT INTO message (user_id, content, created_at, updated_at) VALUES " +
       "(:#{#message.user.id}, :#{#message.content}, :#{#message.createdAt}, :#{#message.updatedAt})",
       nativeQuery = true)
void insertMessage(@Param("message") Message message);

// 批量更新优化
@Modifying
@Query("UPDATE Message m SET m.content = :content, m.updatedAt = :updatedAt WHERE m.id IN :ids")
int updateMessagesByIds(@Param("content") String content, 
                       @Param("updatedAt") LocalDateTime updatedAt, 
                       @Param("ids") List<Long> ids);
```

## 缓存策略

### 1. Redis缓存配置

```java
@Configuration
@EnableCaching
public class CacheConfig {
    
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory factory) {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofMinutes(5))  // 默认5分钟过期
            .serializeKeysWith(RedisSerializationContext.SerializationPair
                .fromSerializer(new StringRedisSerializer()))
            .serializeValuesWith(RedisSerializationContext.SerializationPair
                .fromSerializer(new GenericJackson2JsonRedisSerializer()));
        
        return RedisCacheManager.builder(factory)
            .cacheDefaults(config)
            .withCacheConfiguration("messageList", 
                config.entryTtl(Duration.ofMinutes(10)))  // 留言列表缓存10分钟
            .withCacheConfiguration("userProfile", 
                config.entryTtl(Duration.ofHours(1)))    // 用户信息缓存1小时
            .build();
    }
}
```

### 2. 缓存注解使用

```java
@Service
public class MessageService {
    
    // 缓存留言列表
    @Cacheable(value = "messageList", key = "#pageable.pageNumber + '_' + #pageable.pageSize")
    public Page<MessageDto> getMessages(Pageable pageable) {
        // 业务逻辑
    }
    
    // 更新后清除缓存
    @CacheEvict(value = "messageList", allEntries = true)
    public MessageDto createMessage(MessageDto messageDto) {
        // 业务逻辑
    }
    
    // 条件清除缓存
    @CacheEvict(value = "messageList", key = "#messageDto.userId + '_*'")
    public MessageDto updateMessage(MessageDto messageDto) {
        // 业务逻辑
    }
}
```

## 监控和调优

### 1. 慢查询监控

```sql
-- 启用慢查询日志
SET GLOBAL slow_query_log = 'ON';
SET GLOBAL long_query_time = 1;  -- 1秒以上的查询记录为慢查询
SET GLOBAL log_queries_not_using_indexes = 'ON';

-- 查看慢查询日志
SHOW VARIABLES LIKE 'slow_query_log_file';
```

### 2. 性能分析

```sql
-- 使用EXPLAIN分析查询计划
EXPLAIN SELECT m.*, u.username 
FROM message m 
JOIN user u ON m.user_id = u.id 
WHERE m.deleted = false 
ORDER BY m.created_at DESC 
LIMIT 0, 10;

-- 查看索引使用情况
SHOW INDEX FROM message;

-- 查看表状态
SHOW TABLE STATUS LIKE 'message';
```

### 3. 连接池优化

```properties
# application.properties
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.connection-timeout=30000
spring.datasource.hikari.idle-timeout=600000
spring.datasource.hikari.max-lifetime=1800000
```

## 最佳实践

### 1. 索引设计原则

- **选择性原则**：选择区分度高的字段建立索引
- **最左前缀原则**：复合索引的字段顺序很重要
- **覆盖索引**：尽量使用覆盖索引避免回表查询
- **避免过多索引**：索引会占用存储空间，影响写入性能

### 2. 查询优化原则

- **避免SELECT ***：只查询需要的字段
- **使用LIMIT**：分页查询必须使用LIMIT
- **避免隐式转换**：确保WHERE条件中的类型匹配
- **使用EXISTS代替IN**：大数据量时EXISTS性能更好

### 3. 缓存使用原则

- **热点数据缓存**：经常访问的数据优先缓存
- **缓存更新策略**：选择合适的缓存更新策略
- **缓存穿透防护**：使用布隆过滤器或空值缓存
- **缓存雪崩防护**：设置不同的过期时间

## 性能测试

### 1. 压力测试工具

```bash
# 使用Apache Bench进行压力测试
ab -n 1000 -c 100 http://localhost:8080/api/messages

# 使用JMeter进行性能测试
jmeter -n -t test-plan.jmx -l results.jtl
```

### 2. 性能指标

- **响应时间**：平均响应时间 < 200ms
- **吞吐量**：QPS > 1000
- **并发用户**：支持100+并发用户
- **资源使用**：CPU < 80%, 内存 < 80%

## 总结

通过合理的索引设计、查询优化和缓存策略，可以显著提升数据库性能。关键点包括：

1. **合理设计索引**：根据查询模式设计合适的索引
2. **优化查询语句**：避免N+1查询，使用JOIN FETCH
3. **实施缓存策略**：使用Redis缓存热点数据
4. **监控和调优**：持续监控性能指标，及时优化
5. **定期维护**：清理无用索引，优化表结构

记住：过早优化是万恶之源，应该在性能问题出现后再进行针对性优化。
