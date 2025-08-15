# 数据库设计文档

## 数据库概述
在线留言板系统使用MySQL作为主数据库，Redis作为缓存，Kafka作为消息队列。

## E-R 图设计

### 实体关系说明
- **用户 (user)** 与 **留言 (message)** 之间存在 **一对多** 关系
  - 一个用户可以发布多条留言
  - 每条留言必须属于一个用户
- **系统日志 (system_log)** 是独立实体，记录系统操作日志

### 表结构设计

#### 1. 用户表 (user)
| 字段名 | 数据类型 | 约束 | 说明 |
|--------|----------|------|------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | 用户唯一标识 |
| username | VARCHAR(50) | UNIQUE, NOT NULL | 用户名，唯一 |
| password | VARCHAR(255) | NOT NULL | 密码（加密存储） |
| created_at | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | 创建时间 |

#### 2. 留言表 (message)
| 字段名 | 数据类型 | 约束 | 说明 |
|--------|----------|------|------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | 留言唯一标识 |
| user_id | BIGINT | FOREIGN KEY, NOT NULL | 关联用户表的外键 |
| content | TEXT | NOT NULL | 留言内容 |
| created_at | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | 创建时间 |

#### 3. 系统日志表 (system_log)
| 字段名 | 数据类型 | 约束 | 说明 |
|--------|----------|------|------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | 日志唯一标识 |
| log_content | TEXT | NOT NULL | 日志内容 |
| created_at | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | 创建时间 |

## 外键关系
```sql
-- message表的外键约束
ALTER TABLE message 
ADD CONSTRAINT fk_message_user 
FOREIGN KEY (user_id) REFERENCES user(id) 
ON DELETE CASCADE;
```

## 索引设计
```sql
-- 用户表索引
CREATE INDEX idx_user_username ON user(username);
CREATE INDEX idx_user_created_at ON user(created_at);

-- 留言表索引
CREATE INDEX idx_message_user_id ON message(user_id);
CREATE INDEX idx_message_created_at ON message(created_at);

-- 日志表索引
CREATE INDEX idx_system_log_created_at ON system_log(created_at);
```

## 数据字典

### 用户状态
- 1: 正常
- 0: 禁用

### 留言状态
- 1: 正常显示
- 0: 已删除

### 日志级别
- INFO: 信息
- WARN: 警告
- ERROR: 错误
- DEBUG: 调试
