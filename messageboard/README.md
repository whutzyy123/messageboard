<<<<<<< HEAD
# 在线留言板系统 (Message Board)

## 项目简介

这是一个基于前后端分离架构的在线留言板系统，采用现代化的技术栈构建，具备完整的用户认证、留言管理、缓存优化和异步日志处理功能。

## 技术架构

```
┌─────────────────┐    HTTP/HTTPS    ┌─────────────────┐
│   前端 (Vue)    │ ◄──────────────► │  后端 (Spring   │
│   (Node.js)     │                  │     Boot)       │
└─────────────────┘                  └─────────────────┘
                                              │
                                              │
                    ┌─────────────────────────────────────┐
                    │                                     │
                    ▼                                     ▼
            ┌─────────────┐                      ┌─────────────┐
            │   MySQL    │                      │    Redis    │
            │  (主数据库) │                      │   (缓存)    │
            └─────────────┘                      └─────────────┘
                    │                                     │
                    │                                     │
                    ▼                                     ▼
            ┌─────────────┐                      ┌─────────────┐
            │   Kafka    │                      │   Docker    │
            │ (消息队列)  │                      │ (容器化部署) │
            └─────────────┘                      └─────────────┘
```

## 功能特性

- 🔐 **用户认证**: 支持用户注册、登录，JWT Token认证
- 💬 **留言管理**: 发布、查看、点赞留言
- 🚀 **性能优化**: Redis缓存热门留言，提升访问速度
- 📊 **异步处理**: Kafka消息队列处理系统日志
- 🐳 **容器化部署**: Docker Compose一键部署
- 📱 **响应式设计**: 支持多设备访问

## 技术栈

### 前端
- **框架**: Vue.js 3 + Composition API
- **构建工具**: Vite
- **HTTP客户端**: Axios
- **状态管理**: Pinia
- **UI组件库**: Element Plus
- **路由**: Vue Router

### 后端
- **框架**: Spring Boot 3.x
- **安全**: Spring Security + JWT
- **数据访问**: Spring Data JPA
- **缓存**: Spring Data Redis
- **消息队列**: Spring Kafka
- **数据库**: MySQL 8.0

### 中间件
- **数据库**: MySQL 8.0
- **缓存**: Redis 7.x
- **消息队列**: Apache Kafka 3.x

### 部署与运维
- **容器化**: Docker + Docker Compose
- **版本控制**: GitHub

## 项目结构

```
messageboard/
├── frontend/          # 前端Vue项目
├── backend/           # 后端Spring Boot项目
├── docker/            # Docker配置文件
├── docs/              # 项目文档
│   ├── architecture.md    # 系统架构设计
│   └── database-design.md # 数据库设计
├── scripts/           # 部署脚本
└── README.md          # 项目说明
```

## 快速开始

### 环境要求

- Java 17+
- Node.js 18+
- Docker & Docker Compose
- MySQL 8.0
- Redis 7.x
- Kafka 3.x

### 安装步骤

1. **克隆项目**
   ```bash
   git clone <your-github-repo-url>
   cd messageboard
   ```

2. **启动中间件服务**
   ```bash
   docker-compose up -d mysql redis kafka
   ```

3. **配置后端**
   ```bash
   cd backend
   # 修改application.yml中的数据库配置
   mvn spring-boot:run
   ```

4. **启动前端**
   ```bash
   cd frontend
   npm install
   npm run dev
   ```

5. **访问应用**
   - 前端: http://localhost:3000
   - 后端API: http://localhost:8080

## 开发指南

### 分支管理

- `main`: 生产环境分支
- `develop`: 开发主干分支
- `develop_姓名缩写`: 个人开发分支

### 提交规范

- `feat`: 新功能
- `fix`: 修复bug
- `docs`: 文档更新
- `refactor`: 代码重构
- `test`: 测试相关
- `chore`: 构建过程或辅助工具的变动

### API文档

后端API文档地址: http://localhost:8080/swagger-ui.html

## 部署说明

### Docker部署

```bash
# 一键部署所有服务
docker-compose up -d

# 查看服务状态
docker-compose ps

# 查看日志
docker-compose logs -f
```

### 生产环境配置

1. 修改`docker-compose.prod.yml`中的环境变量
2. 配置生产环境的数据库连接
3. 设置Redis和Kafka的生产配置
4. 配置Nginx反向代理

## 贡献指南

1. Fork本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 创建Pull Request

## 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情

## 联系方式

- 项目维护者: [您的姓名]
- 邮箱: [您的邮箱]
- 项目地址: [GitHub仓库地址]

## 更新日志

### v1.0.0 (计划中)
- 基础用户认证功能
- 留言发布和查看
- Redis缓存集成
- Kafka日志处理
- Docker容器化部署
=======
# messageboard
test
>>>>>>> b430343be3840b643f4dcc0ea9f9bc7d708447c8
