# Git 仓库初始化指南

## 初始化本地仓库

### 1. 初始化Git仓库
```bash
# 在项目根目录下执行
git init
```

### 2. 配置用户信息
```bash
# 设置全局用户信息（如果未设置）
git config --global user.name "你的姓名"
git config --global user.email "你的邮箱@example.com"

# 或者仅为当前项目设置
git config user.name "你的姓名"
git config user.email "你的邮箱@example.com"
```

### 3. 添加远程仓库（可选）
```bash
# 如果已有远程仓库，添加远程源
git remote add origin <远程仓库URL>
```

### 4. 创建并切换到main分支
```bash
# 创建并切换到main分支
git checkout -b main

# 或者如果默认分支是master，先重命名
git branch -M main
```

### 5. 创建develop分支
```bash
# 创建develop分支
git branch develop

# 切换到develop分支
git checkout develop
```

### 6. 创建个人开发分支
```bash
# 创建个人开发分支（请将yourname替换为你的姓名拼音）
git checkout -b dev_yourname
```

### 7. 添加初始文件并提交
```bash
# 添加所有文件到暂存区
git add .

# 创建初始提交
git commit -m "Initial commit: 项目初始化"

# 推送到远程仓库（如果有）
git push -u origin main
git push -u origin develop
git push -u origin dev_yourname
```

## 分支管理策略

### 分支说明
- **main**: 主分支，用于生产环境
- **develop**: 开发分支，用于集成测试
- **dev_yourname**: 个人开发分支，用于功能开发

### 工作流程
1. 在 `dev_yourname` 分支上进行功能开发
2. 完成功能后，将 `dev_yourname` 合并到 `develop` 分支
3. 测试完成后，将 `develop` 合并到 `main` 分支

### 常用Git命令
```bash
# 查看分支状态
git status

# 查看所有分支
git branch -a

# 切换分支
git checkout <分支名>

# 查看提交历史
git log --oneline

# 拉取最新代码
git pull origin <分支名>

# 推送代码
git push origin <分支名>
```

## 注意事项
- 每次提交前先拉取最新代码
- 提交信息要清晰明了
- 定期将个人分支同步到develop分支
- 重要功能合并前进行代码审查
