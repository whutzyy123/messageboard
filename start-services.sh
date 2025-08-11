#!/bin/bash

# 在线留言板系统中间件服务启动脚本
# 在 Ubuntu VM 上执行

echo "=========================================="
echo "  在线留言板系统中间件服务启动脚本"
echo "=========================================="

# 检查 Docker 是否运行
if ! docker info > /dev/null 2>&1; then
    echo "❌ Docker 未运行，请先启动 Docker 服务"
    echo "sudo systemctl start docker"
    exit 1
fi

# 检查 Docker Compose 是否可用
if ! docker-compose --version > /dev/null 2>&1; then
    echo "❌ Docker Compose 不可用，请先安装"
    exit 1
fi

echo "✅ Docker 环境检查通过"

# 停止现有服务（如果存在）
echo "🔄 停止现有服务..."
docker-compose down

# 清理旧的数据卷（可选，取消注释以清理数据）
# echo "🧹 清理旧的数据卷..."
# docker-compose down -v

# 启动所有服务
echo "🚀 启动中间件服务..."
docker-compose up -d

# 等待服务启动
echo "⏳ 等待服务启动..."
sleep 30

# 检查服务状态
echo "📊 检查服务状态..."
docker-compose ps

# 检查服务健康状态
echo "🏥 检查服务健康状态..."

# 检查 MySQL
if docker exec messageboard-mysql mysqladmin ping -u messageboard -pmessageboard123 --silent; then
    echo "✅ MySQL 服务正常"
else
    echo "❌ MySQL 服务异常"
fi

# 检查 Redis
if docker exec messageboard-redis redis-cli -a redis123 ping | grep -q "PONG"; then
    echo "✅ Redis 服务正常"
else
    echo "❌ Redis 服务异常"
fi

# 检查 Kafka
if docker exec messageboard-kafka kafka-topics --bootstrap-server localhost:9092 --list > /dev/null 2>&1; then
    echo "✅ Kafka 服务正常"
else
    echo "❌ Kafka 服务异常"
fi

# 显示服务访问信息
echo ""
echo "=========================================="
echo "  服务访问信息"
echo "=========================================="
echo "MySQL:     localhost:3306"
echo "Redis:     localhost:6379"
echo "Kafka:     localhost:9092"
echo "Zookeeper: localhost:2181"
echo "Kafka UI:  http://localhost:8080"
echo "Redis UI:  http://localhost:8081"
echo ""
echo "数据库连接信息："
echo "用户名: messageboard"
echo "密码: messageboard123"
echo "数据库: messageboard"
echo ""
echo "Redis 密码: redis123"
echo ""
echo "✅ 所有服务启动完成！"
echo "使用 'docker-compose logs -f' 查看服务日志"
echo "使用 'docker-compose down' 停止服务"
