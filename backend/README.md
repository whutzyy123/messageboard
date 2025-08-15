# Message Board Backend

在线留言板系统后端服务，基于Spring Boot 3.x + Java 17开发。

## 技术栈

- **Java**: 17
- **Spring Boot**: 3.2.0
- **Spring Security**: 6.x
- **Spring Data JPA**: 3.x
- **MySQL**: 8.x
- **Redis**: 6.x (缓存)
- **Kafka**: 3.x (消息队列)
- **JWT**: 0.12.3
- **Lombok**: 最新版本
- **Maven**: 3.x

## 功能特性

- ✅ 用户注册/登录
- ✅ JWT Token认证
- ✅ 留言CRUD操作
- ✅ 分页查询
- ✅ 参数验证
- ✅ 全局异常处理
- ✅ 日志记录
- ✅ CORS支持

## 环境要求

- JDK 17+
- Maven 3.6+
- MySQL 8.0+
- Redis 6.0+ (用于缓存)
- Kafka 3.0+ (用于消息队列)

## 快速开始

### 1. 克隆项目

```bash
git clone <repository-url>
cd message-board-backend
```

### 2. 配置数据库

#### 2.1 创建数据库

```sql
CREATE DATABASE message_board 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;
```

#### 2.2 修改配置文件

编辑 `src/main/resources/application.properties` 文件：

```properties
# 数据库连接信息
spring.datasource.url=jdbc:mysql://192.168.159.128:3306/message_board?useSSL=false&useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=your_actual_password

# Redis连接信息
spring.data.redis.host=192.168.159.128
spring.data.redis.port=6379

# Kafka连接信息
spring.kafka.bootstrap-servers=192.168.159.128:9092
```

**注意**: 
- 将 `your_actual_password` 替换为您的实际MySQL密码
- 确保MySQL服务运行在 `192.168.159.128:3306`
- 确保Redis服务运行在 `192.168.159.128:6379`
- 确保Kafka服务运行在 `192.168.159.128:9092`
- 如果使用本地服务，请修改IP地址为 `localhost` 或 `127.0.0.1`

### 3. 启动应用

#### 3.1 使用Maven启动

```bash
# 清理并编译
mvn clean compile

# 启动应用
mvn spring-boot:run
```

#### 3.2 打包后启动

```bash
# 打包
mvn clean package -DskipTests

# 运行JAR文件
java -jar target/message-board-backend-0.0.1-SNAPSHOT.jar
```

### 4. 验证启动

应用启动成功后，访问以下接口验证：

```bash
# 健康检查
curl http://localhost:8080/api/auth/health

# 预期响应
{
  "success": true,
  "message": "服务运行正常",
  "data": null,
  "timestamp": "2024-01-01T12:00:00"
}
```

## API接口

### 认证接口

#### 用户注册
```http
POST /api/auth/register
Content-Type: application/json

{
  "username": "testuser",
  "password": "123456"
}
```

#### 用户登录
```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "testuser",
  "password": "123456"
}
```

### 留言接口

#### 获取留言列表（支持Redis缓存）
```http
GET /api/messages?page=0&size=10
Authorization: Bearer <your-jwt-token>
```

#### 创建留言（自动发送Kafka日志）
```http
POST /api/messages
Authorization: Bearer <your-jwt-token>
Content-Type: application/json

{
  "userId": 1,
  "content": "这是一条测试留言"
}
```

#### 更新留言
```http
PUT /api/messages/{id}
Authorization: Bearer <your-jwt-token>
Content-Type: application/json

{
  "content": "更新后的留言内容"
}
```

#### 删除留言
```http
DELETE /api/messages/{id}
Authorization: Bearer <your-jwt-token>
```

#### 搜索留言
```http
GET /api/messages/search?content=关键词&page=0&size=10
Authorization: Bearer <your-jwt-token>
```

## 配置说明

### 数据库配置

- `spring.jpa.hibernate.ddl-auto=create-drop`: 每次启动时重新创建表结构（开发环境）
- `spring.jpa.show-sql=true`: 显示SQL语句（开发环境）
- `spring.jpa.properties.hibernate.format_sql=true`: 格式化SQL输出

### JWT配置

- `jwt.secret`: JWT签名密钥
- `jwt.expiration`: Token过期时间（毫秒）
- `jwt.header`: HTTP请求头名称
- `jwt.prefix`: Token前缀

### 缓存配置

- **Redis缓存**: 留言列表缓存5分钟，提高查询性能
- **缓存策略**: 写操作后自动清除缓存，保证数据一致性

### 消息队列配置

- **Kafka Topic**: `system-logs` 用于系统日志
- **异步日志**: 用户操作自动发送到Kafka，消费者保存到数据库
- **消息持久化**: 支持消息重试和错误处理

### 安全配置

- `/api/auth/**`: 允许匿名访问
- 其他所有接口需要JWT认证
- CORS已配置，支持前端跨域请求

## 开发指南

### 项目结构

```
src/main/java/com/example/messageboardbackend/
├── config/          # 配置类
├── controller/      # 控制器
├── dto/            # 数据传输对象
├── exception/      # 异常处理
├── filter/         # 过滤器
├── model/          # 实体类
├── repository/     # 数据访问层
├── service/        # 业务逻辑层
└── util/           # 工具类
```

### 添加新功能

1. 创建实体类 (Model)
2. 创建Repository接口
3. 创建Service接口和实现
4. 创建Controller
5. 添加必要的DTO类
6. 配置安全权限

### 测试

```bash
# 运行单元测试
mvn test

# 运行集成测试
mvn verify
```

## 部署

### Docker部署

```bash
# 构建镜像
docker build -t message-board-backend .

# 运行容器
docker run -d -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://host.docker.internal:3306/message_board \
  -e SPRING_DATASOURCE_USERNAME=root \
  -e SPRING_DATASOURCE_PASSWORD=your_password \
  message-board-backend
```

### 生产环境配置

1. 修改 `spring.jpa.hibernate.ddl-auto=validate`
2. 关闭SQL日志输出
3. 配置生产环境数据库连接
4. 设置适当的JWT密钥
5. 配置日志级别

## 常见问题

### 1. 数据库连接失败

- 检查MySQL服务是否运行
- 验证数据库连接信息
- 确认防火墙设置
- 检查MySQL用户权限

### 2. JWT认证失败

- 检查Token格式是否正确
- 验证Token是否过期
- 确认JWT密钥配置
- 检查请求头设置

### 3. CORS错误

- 确认前端域名配置
- 检查CORS配置
- 验证请求方法是否允许

## 贡献指南

1. Fork项目
2. 创建功能分支
3. 提交更改
4. 创建Pull Request

## 许可证

本项目采用MIT许可证。

## 联系方式

如有问题或建议，请通过以下方式联系：

- 提交Issue
- 发送邮件
- 项目讨论区

---

**注意**: 首次启动时，由于配置了 `create-drop` 模式，数据库表会被重新创建。生产环境请修改为 `validate` 或 `update` 模式。
