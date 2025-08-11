#!/bin/bash

# 等待 Kafka 服务启动
echo "等待 Kafka 服务启动..."
sleep 30

# 创建系统日志主题
echo "创建系统日志主题..."
kafka-topics --bootstrap-server kafka:29092 --create --topic system-logs --partitions 3 --replication-factor 1 --if-not-exists

# 创建用户操作日志主题
echo "创建用户操作日志主题..."
kafka-topics --bootstrap-server kafka:29092 --create --topic user-actions --partitions 3 --replication-factor 1 --if-not-exists

# 创建消息事件主题
echo "创建消息事件主题..."
kafka-topics --bootstrap-server kafka:29092 --create --topic message-events --partitions 3 --replication-factor 1 --if-not-exists

# 列出所有主题
echo "列出所有主题:"
kafka-topics --bootstrap-server kafka:29092 --list

echo "Kafka 主题初始化完成！"
