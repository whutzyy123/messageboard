# 项目结构说明

## 目录结构

```
messageboard/
├── frontend/                    # 前端Vue项目
│   ├── public/                 # 静态资源
│   ├── src/                    # 源代码
│   │   ├── components/         # Vue组件
│   │   ├── views/              # 页面视图
│   │   ├── router/             # 路由配置
│   │   ├── store/              # 状态管理
│   │   ├── api/                # API接口
│   │   ├── utils/              # 工具函数
│   │   ├── assets/             # 资源文件
│   │   ├── App.vue             # 根组件
│   │   └── main.js             # 入口文件
│   ├── package.json            # 依赖配置
│   ├── vite.config.js          # Vite配置
│   └── index.html              # HTML模板
│
├── backend/                     # 后端Spring Boot项目
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── com/messageboard/
│   │   │   │       ├── MessageBoardApplication.java
│   │   │   │       ├── config/           # 配置类
│   │   │   │       ├── controller/       # 控制器
│   │   │   │       ├── service/          # 服务层
│   │   │   │       ├── repository/       # 数据访问层
│   │   │   │       ├── entity/           # 实体类
│   │   │   │       ├── dto/              # 数据传输对象
│   │   │   │       ├── security/         # 安全配置
│   │   │   │       └── util/             # 工具类
│   │   │   └── resources/
│   │   │       ├── application.yml       # 配置文件
│   │   │       ├── application-dev.yml   # 开发环境配置
│   │   │       └── application-prod.yml  # 生产环境配置
│   │   └── test/                         # 测试代码
│   ├── pom.xml                          # Maven配置
│   └── Dockerfile                        # Docker镜像构建
│
├── docker/                      # Docker配置文件
│   ├── docker-compose.yml      # 开发环境Docker Compose
│   ├── docker-compose.prod.yml # 生产环境Docker Compose
│   ├── mysql/                  # MySQL配置
│   ├── redis/                  # Redis配置
│   └── kafka/                  # Kafka配置
│
├── docs/                        # 项目文档
│   ├── architecture.md          # 系统架构设计
│   ├── database-design.md       # 数据库设计
│   ├── api-docs.md              # API接口文档
│   ├── deployment.md            # 部署说明
│   └── development.md           # 开发指南
│
├── scripts/                     # 部署和构建脚本
│   ├── build.sh                # 构建脚本
│   ├── deploy.sh               # 部署脚本
│   └── setup.sh                # 环境设置脚本
│
├── .gitignore                   # Git忽略文件
├── README.md                    # 项目说明
└── LICENSE                      # 许可证文件
```

## 模块说明

### 前端模块 (frontend/)

**核心功能模块：**
- **认证模块**: 用户登录、注册、JWT Token管理
- **留言模块**: 留言发布、查看、点赞、分页
- **用户模块**: 用户信息管理、个人中心
- **管理模块**: 留言管理、用户管理（管理员）

**技术特点：**
- 使用Vue 3 Composition API，代码更清晰
- Pinia状态管理，支持TypeScript
- Element Plus UI组件库，美观易用
- Vite构建工具，开发体验优秀
- 响应式设计，支持多设备

### 后端模块 (backend/)

**核心功能模块：**
- **用户服务**: 用户注册、登录、信息管理
- **留言服务**: 留言CRUD、点赞、热门排序
- **认证服务**: JWT Token生成、验证、刷新
- **缓存服务**: Redis缓存管理、热点数据缓存
- **日志服务**: Kafka消息队列、异步日志处理
- **文件服务**: 图片上传、文件管理

**技术特点：**
- Spring Boot 3.x，支持Java 17+
- Spring Security + JWT，安全可靠
- Spring Data JPA，数据访问简化
- Spring Data Redis，缓存集成
- Spring Kafka，消息队列集成
- Swagger API文档，接口清晰

### 中间件服务

**MySQL数据库：**
- 用户数据持久化
- 留言数据存储
- 系统日志记录
- 性能优化索引

**Redis缓存：**
- 用户会话缓存
- 热门留言缓存
- 接口响应缓存
- 分布式锁支持

**Kafka消息队列：**
- 异步日志处理
- 系统事件通知
- 数据同步消息
- 性能监控数据

### 部署配置

**Docker Compose：**
- 多服务容器编排
- 环境变量配置
- 网络配置管理
- 数据卷挂载

**环境配置：**
- 开发环境配置
- 生产环境配置
- 测试环境配置
- 环境变量管理

## 开发规范

### 代码规范
- 遵循各语言官方代码规范
- 使用ESLint + Prettier（前端）
- 使用Checkstyle（后端Java）
- 统一的命名规范和注释规范

### 提交规范
- 使用Conventional Commits规范
- 每次提交只做一件事
- 提交信息清晰明确
- 关联Issue和PR

### 分支管理
- main分支：生产环境，只接受PR合并
- develop分支：开发主干，功能集成测试
- feature分支：功能开发，从develop分支创建
- hotfix分支：紧急修复，从main分支创建

## 测试策略

### 单元测试
- 前端：Jest + Vue Test Utils
- 后端：JUnit 5 + Mockito
- 测试覆盖率要求：>80%

### 集成测试
- API接口测试
- 数据库集成测试
- 缓存集成测试
- 消息队列测试

### 端到端测试
- 用户操作流程测试
- 跨浏览器兼容性测试
- 性能压力测试
- 安全漏洞测试
