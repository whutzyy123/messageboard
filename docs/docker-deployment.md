# Docker 部署指南

本文档介绍如何使用 Docker 部署在线留言板系统的前后端服务。

## 目录

- [环境要求](#环境要求)
- [快速开始](#快速开始)
- [手动部署](#手动部署)
- [网络配置说明](#网络配置说明)
- [服务管理](#服务管理)
- [故障排除](#故障排除)
- [生产环境部署](#生产环境部署)

## 环境要求

### 必需软件

- **Docker**: 20.10+
- **Docker Compose**: 2.0+
- **操作系统**: Linux, macOS, Windows 10/11

### 网络要求

- 确保能访问虚拟机中的中间件服务：
  - MySQL: `192.168.159.128:3306`
  - Redis: `192.168.159.128:6379`
  - Kafka: `192.168.159.128:9092`

## 快速开始

### 1. 使用部署脚本（推荐）

#### Linux/macOS
```bash
# 给脚本执行权限
chmod +x docker-deploy.sh

# 运行部署脚本
./docker-deploy.sh
```

#### Windows
```cmd
# 双击运行
docker-deploy.bat
```

### 2. 使用 Docker Compose 命令

```bash
# 构建并启动所有服务
docker-compose up -d --build

# 查看服务状态
docker-compose ps

# 查看服务日志
docker-compose logs -f
```

## 手动部署

### 1. 构建镜像

```bash
# 构建后端镜像
docker build -t message-board-backend ./backend

# 构建前端镜像
docker build -t message-board-frontend ./frontend
```

### 2. 启动服务

```bash
# 启动后端服务
docker run -d \
  --name message-board-backend \
  --network host \
  -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://192.168.159.128:3306/message_board \
  -e SPRING_DATASOURCE_USERNAME=root \
  -e SPRING_DATASOURCE_PASSWORD=your_password \
  message-board-backend

# 启动前端服务
docker run -d \
  --name message-board-frontend \
  -p 8000:80 \
  message-board-frontend
```

## 网络配置说明

### 虚拟机中间件连接问题

由于 Docker Compose 默认会创建自己的网络，需要特殊配置来访问虚拟机中的中间件服务。

#### 方案1：使用 Host 网络模式（推荐）

```yaml
services:
  backend-app:
    network_mode: host
    # 其他配置...
```

**优点**：
- 容器直接使用主机网络，可以访问所有主机网络接口
- 配置简单，性能好

**缺点**：
- 容器端口直接绑定到主机端口
- 安全性相对较低

#### 方案2：使用 Extra Hosts

```yaml
services:
  backend-app:
    extra_hosts:
      - "192.168.159.128:host-gateway"
    # 其他配置...
```

**优点**：
- 保持容器网络隔离
- 安全性更好

**缺点**：
- 在某些网络环境下可能不工作
- 需要确保网络配置正确

#### 方案3：使用自定义网络

```yaml
networks:
  custom-network:
    driver: bridge
    ipam:
      config:
        - subnet: 172.20.0.0/16

services:
  backend-app:
    networks:
      - custom-network
    # 其他配置...
```

### 推荐配置

对于开发环境，我们推荐使用 **Host 网络模式**，因为：

1. 配置简单，减少网络问题
2. 性能更好，延迟更低
3. 便于调试和开发

对于生产环境，建议使用 **自定义网络** 或 **Extra Hosts** 方案。

## 服务管理

### 常用命令

```bash
# 查看服务状态
docker-compose ps

# 启动服务
docker-compose up -d

# 停止服务
docker-compose down

# 重启服务
docker-compose restart

# 查看服务日志
docker-compose logs -f [service-name]

# 进入容器
docker-compose exec [service-name] /bin/bash

# 查看容器资源使用
docker stats
```

### 服务健康检查

系统内置了健康检查机制：

- **后端服务**: 检查 `/api/auth/health` 端点
- **前端服务**: 检查 `/health` 端点

```bash
# 手动检查健康状态
curl http://localhost:8080/api/auth/health
curl http://localhost:8000/health
```

## 故障排除

### 常见问题

#### 1. 后端无法连接中间件

**症状**: 后端启动失败，日志显示连接超时

**解决方案**:
```bash
# 检查网络连通性
ping 192.168.159.128

# 检查端口连通性
telnet 192.168.159.128 3306
telnet 192.168.159.128 6379
telnet 192.168.159.128 9092

# 检查防火墙设置
# Linux: iptables -L
# Windows: netsh advfirewall show allprofiles
```

#### 2. 前端无法访问后端API

**症状**: 前端页面加载正常，但API调用失败

**解决方案**:
```bash
# 检查后端服务状态
docker-compose ps backend-app

# 检查后端日志
docker-compose logs backend-app

# 检查网络配置
docker network ls
docker network inspect [network-name]
```

#### 3. 容器启动失败

**症状**: 容器状态为 `Exit` 或 `Error`

**解决方案**:
```bash
# 查看容器日志
docker-compose logs [service-name]

# 检查镜像是否存在
docker images | grep message-board

# 重新构建镜像
docker-compose build --no-cache [service-name]
```

### 日志分析

```bash
# 查看实时日志
docker-compose logs -f --tail=100

# 查看特定服务的日志
docker-compose logs -f backend-app

# 搜索错误日志
docker-compose logs | grep -i error

# 导出日志到文件
docker-compose logs > deployment.log
```

## 生产环境部署

### 安全配置

1. **修改默认密码**
   ```yaml
   environment:
     - SPRING_DATASOURCE_PASSWORD=strong_password_here
     - JWT_SECRET=very_long_random_secret_key
   ```

2. **限制容器权限**
   ```yaml
   security_opt:
     - no-new-privileges:true
   ```

3. **使用非root用户**
   - 后端和前端镜像已配置非root用户

### 性能优化

1. **资源限制**
   ```yaml
   deploy:
     resources:
       limits:
         cpus: '1.0'
         memory: 1G
       reservations:
         cpus: '0.5'
         memory: 512M
   ```

2. **健康检查优化**
   ```yaml
   healthcheck:
     test: ["CMD", "curl", "-f", "http://localhost:8080/api/auth/health"]
     interval: 30s
     timeout: 10s
     retries: 3
     start_period: 60s
   ```

### 监控和日志

1. **日志轮转**
   ```yaml
   logging:
     driver: "json-file"
     options:
       max-size: "10m"
       max-file: "3"
   ```

2. **集成监控系统**
   - Prometheus + Grafana
   - ELK Stack
   - 云服务商的监控服务

## 总结

通过 Docker 部署，您可以：

- ✅ 快速部署前后端服务
- ✅ 统一开发和生产环境
- ✅ 简化依赖管理
- ✅ 支持水平扩展
- ✅ 便于维护和更新

如果遇到问题，请参考故障排除部分或查看服务日志。对于生产环境，建议进行充分的测试和性能调优。
