-- 在线留言板系统数据库初始化脚本
-- 数据库名: message_board
-- 字符集: utf8mb4
-- 排序规则: utf8mb4_unicode_ci

-- 创建数据库
CREATE DATABASE IF NOT EXISTS message_board 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

USE message_board;

-- 创建用户表
CREATE TABLE IF NOT EXISTS `user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户唯一标识',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `password` VARCHAR(255) NOT NULL COMMENT '密码（加密存储）',
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `enabled` BOOLEAN NOT NULL DEFAULT TRUE COMMENT '是否启用',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 创建留言表
CREATE TABLE IF NOT EXISTS `message` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '留言唯一标识',
    `user_id` BIGINT NOT NULL COMMENT '关联用户表的外键',
    `content` TEXT NOT NULL COMMENT '留言内容',
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` BOOLEAN NOT NULL DEFAULT FALSE COMMENT '是否删除',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_created_at` (`created_at`),
    KEY `idx_deleted` (`deleted`),
    CONSTRAINT `fk_message_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='留言表';

-- 创建系统日志表
CREATE TABLE IF NOT EXISTS `system_log` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '日志唯一标识',
    `log_content` TEXT NOT NULL COMMENT '日志内容',
    `level` ENUM('DEBUG', 'INFO', 'WARN', 'ERROR') NOT NULL DEFAULT 'INFO' COMMENT '日志级别',
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_level` (`level`),
    KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统日志表';

-- 插入测试数据

-- 插入测试用户
INSERT INTO `user` (`username`, `password`, `enabled`) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', TRUE), -- 密码: admin123
('testuser', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', TRUE), -- 密码: admin123
('demo', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', TRUE); -- 密码: admin123

-- 插入测试留言
INSERT INTO `message` (`user_id`, `content`) VALUES
(1, '欢迎来到在线留言板系统！'),
(2, '这是一个测试留言，用于演示系统功能。'),
(3, '系统运行正常，所有功能都可以正常使用。'),
(1, '感谢大家的使用和支持！'),
(2, '如果遇到问题，请及时反馈。');

-- 插入测试日志
INSERT INTO `system_log` (`log_content`, `level`) VALUES
('系统启动成功', 'INFO'),
('数据库连接正常', 'INFO'),
('用户注册功能测试通过', 'INFO'),
('留言发布功能测试通过', 'INFO'),
('系统初始化完成', 'INFO');

-- 创建索引
CREATE INDEX `idx_user_enabled` ON `user` (`enabled`);
CREATE INDEX `idx_message_user_deleted` ON `message` (`user_id`, `deleted`);
CREATE INDEX `idx_system_log_level_time` ON `system_log` (`level`, `created_at`);

-- 显示表结构
DESCRIBE `user`;
DESCRIBE `message`;
DESCRIBE `system_log`;

-- 显示测试数据
SELECT '用户表数据:' AS info;
SELECT id, username, enabled, created_at FROM `user`;

SELECT '留言表数据:' AS info;
SELECT m.id, m.content, u.username, m.created_at 
FROM `message` m 
JOIN `user` u ON m.user_id = u.id 
WHERE m.deleted = FALSE;

SELECT '系统日志表数据:' AS info;
SELECT id, log_content, level, created_at FROM `system_log`;
