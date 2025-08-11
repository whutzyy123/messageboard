# 在线留言板系统 - 后端服务

## 项目简介

这是在线留言板系统的后端服务，基于Spring Boot 3.x构建，提供RESTful API服务。

## 技术栈

- **框架**: Spring Boot 3.2.0
- **Java版本**: 17+
- **数据库**: MySQL 8.0
- **ORM**: Spring Data JPA + Hibernate
- **安全**: Spring Security + JWT
- **缓存**: Redis
- **消息队列**: Apache Kafka
- **构建工具**: Maven

## 环境要求

- JDK 17+
- Maven 3.6+
- MySQL 8.0+
- Redis 7.x+
- Apache Kafka 3.x+

## 快速开始

### 1. 克隆项目

```bash
git clone https://github.com/whutzyy123/messageboard.git
cd messageboard/backend
```

### 2. 配置数据库

在MySQL中创建数据库：

```sql
CREATE DATABASE messageboard CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 3. 修改配置

编辑 `src/main/resources/application.yml`，修改数据库连接信息：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/messageboard?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
    username: your_username
    password: your_password
```

### 4. 启动中间件服务

确保以下服务正在运行：
- MySQL (端口: 3306)
- Redis (端口: 6379)
- Kafka (端口: 9092)

### 5. 运行项目

```bash
# 编译项目
mvn clean compile

# 运行项目
mvn spring-boot:run
```

或者使用IDE直接运行 `MessageBoardApplication` 类。

## API文档

### 认证接口

#### 用户注册
```
POST /api/auth/register
Content-Type: application/json

{
    "username": "testuser",
    "email": "test@example.com",
    "password": "123456",
    "confirmPassword": "123456"
}
```

#### 用户登录
```
POST /api/auth/login
Content-Type: application/json

{
    "username": "testuser",
    "password": "123456"
}
```

#### 健康检查
```
GET /api/auth/health
```

## 项目结构

```
src/main/java/com/messageboard/
├── MessageBoardApplication.java    # 主应用类
├── config/                        # 配置类
│   └── SecurityConfig.java        # 安全配置
├── controller/                     # 控制器层
│   └── AuthController.java        # 认证控制器
├── dto/                          # 数据传输对象
│   ├── UserLoginDto.java         # 登录DTO
│   └── UserRegistrationDto.java  # 注册DTO
├── entity/                       # 实体类
│   ├── User.java                # 用户实体
│   └── Message.java             # 留言实体
├── repository/                   # 数据访问层
│   ├── UserRepository.java      # 用户Repository
│   └── MessageRepository.java   # 留言Repository
├── service/                     # 服务层
│   └── UserService.java         # 用户服务
└── util/                        # 工具类
    └── JwtUtil.java             # JWT工具类
```

## 开发说明

### 分支管理

- `main`: 生产环境分支
- `develop`: 开发主干分支
- `feature/*`: 功能开发分支

### 提交规范

- `feat`: 新功能
- `fix`: 修复bug
- `docs`: 文档更新
- `refactor`: 代码重构
- `test`: 测试相关
- `chore`: 构建过程或辅助工具的变动

## 部署

### Docker部署

```bash
# 构建镜像
docker build -t messageboard-backend .

# 运行容器
docker run -p 8080:8080 messageboard-backend
```

### 生产环境配置

修改 `application-prod.yml` 配置文件，设置生产环境参数。

## 测试

```bash
# 运行单元测试
mvn test

# 运行集成测试
mvn verify
```

## 常见问题

### 1. 数据库连接失败

检查MySQL服务是否启动，以及用户名密码是否正确。

### 2. Redis连接失败

确保Redis服务正在运行，检查端口和密码配置。

### 3. JWT令牌无效

检查JWT密钥配置是否正确，令牌是否过期。

## 贡献指南

1. Fork 项目
2. 创建功能分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 打开 Pull Request

## 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情。

## 联系方式

- 项目维护者: whutzyy123
- 项目地址: https://github.com/whutzyy123/messageboard
