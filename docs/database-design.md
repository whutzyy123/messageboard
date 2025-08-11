# 数据库设计文档

## 数据库E-R图

```
┌─────────────┐         ┌─────────────┐         ┌─────────────┐
│    用户表   │         │   留言表    │         │   日志表    │
│   users    │         │  messages   │         │   logs      │
├─────────────┤         ├─────────────┤         ├─────────────┤
│ id (PK)    │         │ id (PK)     │         │ id (PK)     │
│ username   │◄────────┤ user_id     │         │ user_id     │
│ email      │         │ content     │         │ action      │
│ password   │         │ created_at  │         │ details     │
│ created_at │         │ updated_at  │         │ created_at  │
│ updated_at │         │ likes_count │         │ ip_address  │
│ status     │         │ is_hot      │         └─────────────┘
└─────────────┘         └─────────────┘
```

## 表结构设计

### 1. 用户表 (users)

| 字段名      | 类型         | 长度 | 是否为空 | 默认值 | 说明           |
|-------------|--------------|------|----------|--------|----------------|
| id          | BIGINT       | -    | NOT NULL | AUTO   | 主键，自增     |
| username    | VARCHAR      | 50   | NOT NULL | -      | 用户名，唯一   |
| email       | VARCHAR      | 100  | NOT NULL | -      | 邮箱，唯一     |
| password    | VARCHAR      | 255  | NOT NULL | -      | 加密密码       |
| status      | TINYINT      | -    | NOT NULL | 1      | 状态：1启用，0禁用 |
| created_at  | TIMESTAMP    | -    | NOT NULL | NOW()  | 创建时间       |
| updated_at  | TIMESTAMP    | -    | NOT NULL | NOW()  | 更新时间       |

**索引设计：**
- PRIMARY KEY (id)
- UNIQUE KEY uk_username (username)
- UNIQUE KEY uk_email (email)
- KEY idx_created_at (created_at)

### 2. 留言表 (messages)

| 字段名      | 类型         | 长度 | 是否为空 | 默认值 | 说明           |
|-------------|--------------|------|----------|--------|----------------|
| id          | BIGINT       | -    | NOT NULL | AUTO   | 主键，自增     |
| user_id     | BIGINT       | -    | NOT NULL | -      | 用户ID，外键   |
| content     | TEXT         | -    | NOT NULL | -      | 留言内容       |
| likes_count | INT          | -    | NOT NULL | 0      | 点赞数         |
| is_hot      | TINYINT      | -    | NOT NULL | 0      | 是否热门：1是，0否 |
| created_at  | TIMESTAMP    | -    | NOT NULL | NOW()  | 创建时间       |
| updated_at  | TIMESTAMP    | -    | NOT NULL | NOW()  | 更新时间       |

**索引设计：**
- PRIMARY KEY (id)
- KEY idx_user_id (user_id)
- KEY idx_created_at (created_at)
- KEY idx_is_hot (is_hot)
- KEY idx_likes_count (likes_count)

### 3. 日志表 (logs)

| 字段名      | 类型         | 长度 | 是否为空 | 默认值 | 说明           |
|-------------|--------------|------|----------|--------|----------------|
| id          | BIGINT       | -    | NOT NULL | AUTO   | 主键，自增     |
| user_id     | BIGINT       | -    | NULL     | -      | 用户ID         |
| action      | VARCHAR      | 100  | NOT NULL | -      | 操作类型       |
| details     | JSON         | -    | NULL     | -      | 操作详情       |
| ip_address  | VARCHAR      | 45   | NULL     | -      | IP地址         |
| created_at  | TIMESTAMP    | -    | NOT NULL | NOW()  | 创建时间       |

**索引设计：**
- PRIMARY KEY (id)
- KEY idx_user_id (user_id)
- KEY idx_action (action)
- KEY idx_created_at (created_at)

## 外键约束

```sql
ALTER TABLE messages 
ADD CONSTRAINT fk_messages_user 
FOREIGN KEY (user_id) REFERENCES users(id) 
ON DELETE CASCADE;

ALTER TABLE logs 
ADD CONSTRAINT fk_logs_user 
FOREIGN KEY (user_id) REFERENCES users(id) 
ON DELETE SET NULL;
```

## 数据库初始化脚本

```sql
-- 创建数据库
CREATE DATABASE IF NOT EXISTS messageboard 
CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE messageboard;

-- 创建用户表
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    status TINYINT NOT NULL DEFAULT 1,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 创建留言表
CREATE TABLE messages (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    likes_count INT NOT NULL DEFAULT 0,
    is_hot TINYINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 创建日志表
CREATE TABLE logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NULL,
    action VARCHAR(100) NOT NULL,
    details JSON NULL,
    ip_address VARCHAR(45) NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
);

-- 创建索引
CREATE INDEX idx_users_created_at ON users(created_at);
CREATE INDEX idx_messages_user_id ON messages(user_id);
CREATE INDEX idx_messages_created_at ON messages(created_at);
CREATE INDEX idx_messages_is_hot ON messages(is_hot);
CREATE INDEX idx_messages_likes_count ON messages(likes_count);
CREATE INDEX idx_logs_user_id ON logs(user_id);
CREATE INDEX idx_logs_action ON logs(action);
CREATE INDEX idx_logs_created_at ON logs(created_at);
```

## 缓存策略

### Redis缓存设计

1. **用户会话缓存**
   - Key: `user:session:{token}`
   - TTL: 24小时
   - Value: 用户信息JSON

2. **热门留言缓存**
   - Key: `hot:messages`
   - TTL: 1小时
   - Value: 热门留言列表

3. **用户信息缓存**
   - Key: `user:info:{userId}`
   - TTL: 30分钟
   - Value: 用户基本信息

4. **留言列表缓存**
   - Key: `messages:page:{pageNum}:{pageSize}`
   - TTL: 10分钟
   - Value: 分页留言数据
