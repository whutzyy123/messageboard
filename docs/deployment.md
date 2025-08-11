# 在线留言板系统部署指南

## 部署架构

```
┌─────────────────────────────────────────────────────────────┐
│                    VMware Ubuntu VM                        │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐        │
│  │   MySQL     │  │    Redis    │  │    Kafka    │        │
│  │   (3306)    │  │   (6379)    │  │   (9092)    │        │
│  └─────────────┘  └─────────────┘  └─────────────┘        │
└─────────────────────────────────────────────────────────────┘
                              │
                              │ 网络连接
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                    物理机 (Windows)                        │
│  ┌─────────────┐  ┌─────────────┐                        │
│  │  前端      │  │   后端      │                        │
│  │  (Vue)     │  │ (Spring)    │                        │
│  │  (3000)    │  │  (8080)     │                        │
│  └─────────────┘  └─────────────┘                        │
└─────────────────────────────────────────────────────────────┘
```

## 前置要求

### Ubuntu VM 要求
- Ubuntu 20.04 或更高版本
- Docker 20.10+ 和 Docker Compose 2.0+
- 至少 4GB RAM
- 至少 20GB 可用磁盘空间

### 物理机要求
- Java 17+ (后端开发)
- Node.js 18+ (前端开发)
- 网络访问 Ubuntu VM 的 IP 地址

## 部署步骤

### 1. 在 Ubuntu VM 上部署中间件服务

#### 1.1 克隆项目到 VM
```bash
# 在 Ubuntu VM 上执行
git clone https://github.com/whutzyy123/messageboard.git
cd messageboard
```

#### 1.2 启动中间件服务
```bash
# 启动所有服务
docker-compose up -d

# 查看服务状态
docker-compose ps

# 查看服务日志
docker-compose logs -f
```

#### 1.3 验证服务状态
```bash
# 检查 MySQL 连接
docker exec -it messageboard-mysql mysql -u messageboard -p
# 密码: messageboard123

# 检查 Redis 连接
docker exec -it messageboard-redis redis-cli -a redis123 ping

# 检查 Kafka 主题
docker exec -it messageboard-kafka kafka-topics --bootstrap-server localhost:9092 --list
```

### 2. 配置网络连接

#### 2.1 获取 Ubuntu VM IP 地址
```bash
# 在 Ubuntu VM 上执行
ip addr show
# 或
hostname -I
```

#### 2.2 配置防火墙（如果需要）
```bash
# 在 Ubuntu VM 上执行
sudo ufw allow 3306  # MySQL
sudo ufw allow 6379  # Redis
sudo ufw allow 9092  # Kafka
sudo ufw allow 2181  # Zookeeper
sudo ufw allow 8080  # Kafka UI
sudo ufw allow 8081  # Redis Commander
```

### 3. 在物理机上配置应用

#### 3.1 更新后端配置
修改 `backend/src/main/resources/application.yml` 中的数据库连接信息：

```yaml
spring:
  datasource:
    url: jdbc:mysql://[VM_IP]:3306/messageboard?useSSL=false&serverTimezone=UTC
    username: messageboard
    password: messageboard123
  
  redis:
    host: [VM_IP]
    port: 6379
    password: redis123
  
  kafka:
    bootstrap-servers: [VM_IP]:9092
```

#### 3.2 更新前端配置
修改 `frontend/vite.config.js` 中的 API 代理配置：

```javascript
server: {
  port: 3000,
  proxy: {
    '/api': {
      target: 'http://[VM_IP]:8080',  // 替换为 VM IP
      changeOrigin: true,
      rewrite: (path) => path.replace(/^\/api/, '')
    }
  }
}
```

## 服务访问地址

| 服务 | 端口 | 访问地址 | 说明 |
|------|------|----------|------|
| MySQL | 3306 | `[VM_IP]:3306` | 数据库服务 |
| Redis | 6379 | `[VM_IP]:6379` | 缓存服务 |
| Kafka | 9092 | `[VM_IP]:9092` | 消息队列 |
| Zookeeper | 2181 | `[VM_IP]:2181` | Kafka 协调服务 |
| Kafka UI | 8080 | `http://[VM_IP]:8080` | Kafka 管理界面 |
| Redis Commander | 8081 | `http://[VM_IP]:8081` | Redis 管理界面 |

## 常用管理命令

### Docker Compose 命令
```bash
# 启动服务
docker-compose up -d

# 停止服务
docker-compose down

# 重启服务
docker-compose restart

# 查看服务状态
docker-compose ps

# 查看服务日志
docker-compose logs -f [service_name]

# 进入容器
docker exec -it [container_name] bash
```

### 数据库管理
```bash
# 连接 MySQL
docker exec -it messageboard-mysql mysql -u messageboard -p

# 备份数据库
docker exec messageboard-mysql mysqldump -u messageboard -p messageboard > backup.sql

# 恢复数据库
docker exec -i messageboard-mysql mysql -u messageboard -p messageboard < backup.sql
```

### Redis 管理
```bash
# 连接 Redis
docker exec -it messageboard-redis redis-cli -a redis123

# 查看所有键
KEYS *

# 查看内存使用
INFO memory

# 清空缓存
FLUSHALL
```

### Kafka 管理
```bash
# 查看主题列表
docker exec -it messageboard-kafka kafka-topics --bootstrap-server localhost:9092 --list

# 查看主题详情
docker exec -it messageboard-kafka kafka-topics --bootstrap-server localhost:9092 --describe --topic system-logs

# 查看消费者组
docker exec -it messageboard-kafka kafka-consumer-groups --bootstrap-server localhost:9092 --list
```

## 故障排除

### 常见问题

#### 1. 服务无法启动
```bash
# 检查端口占用
sudo netstat -tlnp | grep :3306
sudo netstat -tlnp | grep :6379
sudo netstat -tlnp | grep :9092

# 检查磁盘空间
df -h

# 检查 Docker 状态
sudo systemctl status docker
```

#### 2. 连接超时
- 检查 VM 网络配置
- 确认防火墙设置
- 验证 IP 地址是否正确

#### 3. 数据丢失
- 检查 Docker 卷挂载
- 确认数据持久化配置
- 查看容器日志

### 日志查看
```bash
# 查看所有服务日志
docker-compose logs

# 查看特定服务日志
docker-compose logs mysql
docker-compose logs redis
docker-compose logs kafka

# 实时跟踪日志
docker-compose logs -f
```

## 性能优化

### MySQL 优化
- 调整 `innodb_buffer_pool_size`
- 配置合适的连接池大小
- 定期分析慢查询

### Redis 优化
- 设置合适的内存限制
- 配置键过期策略
- 启用持久化

### Kafka 优化
- 调整分区数量
- 配置合适的副本因子
- 优化消费者配置

## 监控和维护

### 健康检查
```bash
# 检查所有服务健康状态
docker-compose ps

# 手动健康检查
curl http://[VM_IP]:8080/actuator/health
```

### 备份策略
- 定期备份 MySQL 数据
- 备份 Redis 数据（如需要）
- 备份配置文件

### 更新策略
```bash
# 拉取最新代码
git pull origin main

# 重新构建并启动服务
docker-compose down
docker-compose up -d --build
```

## 安全注意事项

1. **修改默认密码**：部署后立即修改所有服务的默认密码
2. **网络隔离**：在生产环境中使用私有网络
3. **访问控制**：限制管理界面的访问权限
4. **定期更新**：保持 Docker 镜像和系统更新
5. **日志监控**：监控异常访问和操作日志
