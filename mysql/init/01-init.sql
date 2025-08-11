-- 创建数据库
CREATE DATABASE IF NOT EXISTS messageboard 
CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE messageboard;

-- 创建用户表
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    status TINYINT NOT NULL DEFAULT 1 COMMENT '1: 正常, 0: 禁用',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 创建留言表
CREATE TABLE IF NOT EXISTS messages (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    likes_count INT NOT NULL DEFAULT 0,
    is_hot TINYINT NOT NULL DEFAULT 0 COMMENT '1: 热门, 0: 普通',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_created_at (created_at),
    INDEX idx_likes_count (likes_count),
    INDEX idx_is_hot (is_hot)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 创建日志表
CREATE TABLE IF NOT EXISTS logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NULL,
    action VARCHAR(100) NOT NULL COMMENT '操作类型',
    details JSON NULL COMMENT '操作详情',
    ip_address VARCHAR(45) NULL COMMENT 'IP地址',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_user_id (user_id),
    INDEX idx_action (action),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 插入测试用户数据
INSERT INTO users (username, email, password) VALUES 
('admin', 'admin@messageboard.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi'), -- 密码: admin123
('testuser', 'test@messageboard.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi'); -- 密码: admin123

-- 插入测试留言数据
INSERT INTO messages (user_id, content, likes_count, is_hot) VALUES 
(1, '欢迎来到在线留言板系统！这是一个功能完整的留言板应用。', 5, 1),
(1, '系统支持用户注册、登录、发布留言、点赞等功能。', 3, 0),
(2, 'Redis 缓存热门留言，提高系统性能。', 7, 1),
(2, 'Kafka 异步处理系统日志，提升用户体验。', 4, 0);

-- 插入测试日志数据
INSERT INTO logs (user_id, action, details, ip_address) VALUES 
(1, 'USER_LOGIN', '{"browser": "Chrome", "os": "Windows"}', '192.168.1.100'),
(1, 'MESSAGE_POST', '{"message_id": 1, "content_length": 25}', '192.168.1.100'),
(2, 'USER_LOGIN', '{"browser": "Firefox", "os": "macOS"}', '192.168.1.101'),
(2, 'MESSAGE_POST', '{"message_id": 3, "content_length": 30}', '192.168.1.101');
