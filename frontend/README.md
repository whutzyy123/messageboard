# 在线留言板系统 - 前端

这是在线留言板系统的前端项目，使用 Vue 3 + Vite 构建，提供现代化的用户界面和交互体验。

## 技术栈

- **框架**: Vue 3 (Composition API)
- **构建工具**: Vite 5.x
- **路由**: Vue Router 4.x
- **状态管理**: Pinia 2.x
- **UI 组件库**: Element Plus 2.x
- **HTTP 客户端**: Axios
- **样式**: CSS3 + Element Plus 主题

## 功能特性

### 用户认证
- 用户注册（用户名、邮箱、密码）
- 用户登录（JWT 认证）
- 路由守卫保护
- 自动登录状态管理

### 留言管理
- 发布新留言
- 浏览留言列表
- 留言搜索和排序
- 留言点赞功能
- 分页显示

### 用户体验
- 响应式设计
- 加载状态提示
- 表单验证
- 错误处理
- 美观的 UI 界面

## 项目结构

```
frontend/
├── public/                 # 静态资源
├── src/
│   ├── api/               # API 接口
│   │   ├── auth.js        # 认证相关 API
│   │   └── messages.js    # 留言相关 API
│   ├── components/        # 公共组件
│   ├── router/            # 路由配置
│   │   └── index.js
│   ├── stores/            # 状态管理
│   │   └── auth.js        # 认证状态
│   ├── views/             # 页面组件
│   │   ├── Home.vue       # 首页
│   │   ├── Login.vue      # 登录页
│   │   ├── Register.vue   # 注册页
│   │   ├── Messages.vue   # 留言列表页
│   │   └── PostMessage.vue # 发布留言页
│   ├── App.vue            # 根组件
│   └── main.js            # 应用入口
├── index.html             # HTML 模板
├── package.json           # 项目配置
├── vite.config.js         # Vite 配置
└── README.md              # 项目说明
```

## 环境要求

- Node.js 16.0+
- npm 8.0+ 或 yarn 1.22+

## 安装和运行

### 1. 安装依赖

```bash
cd frontend
npm install
```

### 2. 开发环境运行

```bash
npm run dev
```

开发服务器将在 `http://localhost:3000` 启动

### 3. 生产环境构建

```bash
npm run build
```

构建产物将输出到 `dist/` 目录

### 4. 预览构建结果

```bash
npm run preview
```

## 开发说明

### 开发模式
- 热重载支持
- API 代理配置（`/api` -> `http://localhost:8080`）
- ESLint 代码检查

### 代码规范
- 使用 Vue 3 Composition API
- 遵循 Vue 官方风格指南
- 使用 Element Plus 组件库
- 统一的错误处理和用户提示

### 状态管理
- 使用 Pinia 管理全局状态
- 认证状态持久化到 localStorage
- 自动的 token 管理

### API 集成
- 统一的 API 客户端配置
- 自动的请求拦截器（添加 token）
- 统一的错误处理

## 部署说明

### 静态部署
构建后的 `dist/` 目录可以部署到任何静态文件服务器：

- Nginx
- Apache
- CDN 服务
- 云存储服务

### Docker 部署
项目包含 Docker 配置，可以容器化部署：

```bash
# 构建镜像
docker build -t messageboard-frontend .

# 运行容器
docker run -p 3000:80 messageboard-frontend
```

## 浏览器支持

- Chrome 90+
- Firefox 88+
- Safari 14+
- Edge 90+

## 开发工具

- **VS Code**: 推荐使用 Vue 官方扩展
- **Vue DevTools**: 浏览器开发工具
- **Element Plus**: 组件库文档和示例

## 常见问题

### 1. 端口冲突
如果 3000 端口被占用，可以在 `vite.config.js` 中修改端口：

```javascript
server: {
  port: 3001
}
```

### 2. API 连接失败
确保后端服务在 `http://localhost:8080` 运行，或修改 `vite.config.js` 中的代理配置。

### 3. 依赖安装失败
尝试清除缓存并重新安装：

```bash
npm cache clean --force
rm -rf node_modules package-lock.json
npm install
```

## 贡献指南

1. Fork 项目
2. 创建功能分支
3. 提交代码
4. 创建 Pull Request

## 许可证

MIT License
