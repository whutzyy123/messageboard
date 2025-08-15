# 项目结构说明

## 目录结构
```
msgbd/
├── backend/                    # Spring Boot后端项目
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── com/msgbd/
│   │   │   │       ├── controller/    # 控制器层
│   │   │   │       ├── service/       # 业务逻辑层
│   │   │   │       ├── repository/    # 数据访问层
│   │   │   │       ├── model/         # 实体模型
│   │   │   │       ├── dto/           # 数据传输对象
│   │   │   │       ├── config/        # 配置类
│   │   │   │       └── util/          # 工具类
│   │   │   └── resources/
│   │   │       ├── application.yml    # 应用配置
│   │   │       └── sql/               # SQL脚本
│   │   └── test/                      # 测试代码
│   ├── pom.xml                        # Maven依赖配置
│   └── Dockerfile                     # 后端Docker镜像配置
│
├── frontend/                   # Vue.js前端项目
│   ├── src/
│   │   ├── components/         # Vue组件
│   │   ├── views/              # 页面视图
│   │   ├── router/             # 路由配置
│   │   ├── store/              # 状态管理
│   │   ├── api/                # API接口
│   │   └── assets/             # 静态资源
│   ├── package.json            # Node.js依赖配置
│   └── Dockerfile              # 前端Docker镜像配置
│
├── docker/                      # Docker配置文件
│   ├── docker-compose.yml      # 服务编排配置
│   ├── mysql/                  # MySQL配置
│   ├── redis/                  # Redis配置
│   └── kafka/                  # Kafka配置
│
├── docs/                        # 项目文档
│   ├── database-design.md      # 数据库设计
│   ├── git-setup.md            # Git配置指南
│   ├── project-structure.md    # 项目结构说明
│   └── api-documentation.md    # API接口文档
│
├── scripts/                     # 脚本文件
│   ├── init-db.sql             # 数据库初始化脚本
│   └── deploy.sh               # 部署脚本
│
└── README.md                    # 项目说明文档
```

## 技术架构说明

### 后端架构 (Spring Boot)
- **Controller层**: 处理HTTP请求，参数验证
- **Service层**: 业务逻辑处理，事务管理
- **Repository层**: 数据访问，与数据库交互
- **Model层**: 实体类，对应数据库表
- **DTO层**: 数据传输对象，用于API接口
- **Config层**: 配置类，如数据库、Redis、Kafka配置
- **Util层**: 工具类，如加密、日期处理等

### 前端架构 (Vue.js)
- **Components**: 可复用的Vue组件
- **Views**: 页面级组件
- **Router**: 前端路由配置
- **Store**: 状态管理（Vuex）
- **API**: 与后端API的交互
- **Assets**: 静态资源文件

### 中间件配置
- **MySQL**: 主数据库，存储用户、留言、日志数据
- **Redis**: 缓存用户会话、热点数据
- **Kafka**: 异步消息处理，如日志记录、通知发送

## 开发规范

### 命名规范
- 类名: PascalCase (如: UserController)
- 方法名: camelCase (如: getUserById)
- 变量名: camelCase (如: userName)
- 常量名: UPPER_SNAKE_CASE (如: MAX_RETRY_COUNT)
- 包名: 全小写 (如: com.msgbd.controller)

### 代码组织
- 按功能模块组织代码
- 保持单一职责原则
- 使用适当的注释说明复杂逻辑
- 遵循RESTful API设计规范

### 版本控制
- 使用语义化版本号
- 每次提交都要有清晰的提交信息
- 定期合并和同步分支
- 重要功能合并前进行代码审查
