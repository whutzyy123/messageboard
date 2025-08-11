# Git 安装与配置指南

## Windows 系统 Git 安装

### 方法一：官方安装包（推荐）

1. **下载 Git for Windows**
   - 访问官方网站：https://git-scm.com/download/win
   - 下载最新版本的 Git for Windows
   - 选择适合您系统的版本（32位或64位）

2. **安装步骤**
   - 双击下载的安装包
   - 按照安装向导的提示进行安装
   - 建议使用默认设置，特别是：
     - 选择默认编辑器（推荐 VS Code 或 Notepad++）
     - 选择默认分支名称（main）
     - 选择 PATH 环境（推荐 Git from the command line and also from 3rd-party software）

3. **验证安装**
   - 打开命令提示符或 PowerShell
   - 输入命令：`git --version`
   - 如果显示版本号，说明安装成功

### 方法二：包管理器安装

**使用 Chocolatey：**
```powershell
# 以管理员身份运行 PowerShell
choco install git
```

**使用 Scoop：**
```powershell
# 安装 Scoop（如果还没有）
Set-ExecutionPolicy RemoteSigned -Scope CurrentUser
irm get.scoop.sh | iex

# 安装 Git
scoop install git
```

**使用 Winget：**
```powershell
winget install --id Git.Git -e --source winget
```

## Git 基础配置

### 1. 设置用户信息

```bash
# 设置全局用户名和邮箱
git config --global user.name "您的姓名"
git config --global user.email "您的邮箱@example.com"

# 查看配置
git config --list
```

### 2. 配置默认编辑器

```bash
# 设置 VS Code 为默认编辑器
git config --global core.editor "code --wait"

# 设置 Notepad++ 为默认编辑器
git config --global core.editor "'C:/Program Files/Notepad++/notepad++.exe' -multiInst -notabbar -nosession -noPlugin"
```

### 3. 配置行尾符处理

```bash
# Windows 系统推荐设置
git config --global core.autocrlf true
git config --global core.eol crlf
```

## GitHub 配置

### 1. 生成 SSH 密钥

```bash
# 生成 SSH 密钥对
ssh-keygen -t ed25519 -C "您的邮箱@example.com"

# 启动 SSH 代理
eval "$(ssh-agent -s)"

# 添加私钥到 SSH 代理
ssh-add ~/.ssh/id_ed25519
```

### 2. 添加公钥到 GitHub

```bash
# 复制公钥内容
cat ~/.ssh/id_ed25519.pub
```

然后：
1. 登录 GitHub
2. 点击右上角头像 → Settings
3. 左侧菜单选择 "SSH and GPG keys"
4. 点击 "New SSH key"
5. 粘贴公钥内容，保存

### 3. 测试 SSH 连接

```bash
ssh -T git@github.com
```

如果看到 "Hi username! You've successfully authenticated..." 说明配置成功。

## 项目初始化

### 1. 克隆远程仓库

```bash
# 使用 SSH 克隆
git clone git@github.com:用户名/仓库名.git

# 使用 HTTPS 克隆
git clone https://github.com/用户名/仓库名.git
```

### 2. 创建新仓库

```bash
# 初始化本地仓库
git init

# 添加远程仓库
git remote add origin git@github.com:用户名/仓库名.git

# 或者使用 HTTPS
git remote add origin https://github.com/用户名/仓库名.git
```

### 3. 分支管理

```bash
# 查看所有分支
git branch -a

# 创建并切换到新分支
git checkout -b develop

# 创建个人开发分支
git checkout -b develop_您的姓名缩写

# 切换分支
git checkout 分支名

# 推送新分支到远程
git push -u origin 分支名
```

## 常用 Git 命令

### 基础操作

```bash
# 查看状态
git status

# 添加文件到暂存区
git add .                    # 添加所有文件
git add 文件名               # 添加指定文件

# 提交更改
git commit -m "提交信息"

# 查看提交历史
git log --oneline

# 推送更改
git push origin 分支名

# 拉取最新更改
git pull origin 分支名
```

### 分支操作

```bash
# 合并分支
git merge 分支名

# 删除分支
git branch -d 分支名        # 删除本地分支
git push origin --delete 分支名  # 删除远程分支

# 查看分支图
git log --graph --oneline --all
```

### 冲突解决

```bash
# 查看冲突文件
git status

# 解决冲突后
git add .
git commit -m "解决合并冲突"
```

## 提交规范

### 提交信息格式

```
<type>(<scope>): <subject>

<body>

<footer>
```

### 类型说明

- `feat`: 新功能
- `fix`: 修复bug
- `docs`: 文档更新
- `style`: 代码格式调整
- `refactor`: 代码重构
- `test`: 测试相关
- `chore`: 构建过程或辅助工具的变动

### 示例

```bash
git commit -m "feat: 添加用户登录功能"
git commit -m "fix: 修复留言列表分页bug"
git commit -m "docs: 更新API文档"
```

## 常见问题解决

### 1. 权限问题

```bash
# 如果遇到权限问题，检查 SSH 密钥配置
ssh -T git@github.com

# 或者使用个人访问令牌（PAT）
git remote set-url origin https://用户名:令牌@github.com/用户名/仓库名.git
```

### 2. 大文件问题

```bash
# 如果仓库中有大文件，使用 Git LFS
git lfs install
git lfs track "*.zip"
git add .gitattributes
```

### 3. 撤销操作

```bash
# 撤销最后一次提交
git reset --soft HEAD~1

# 撤销暂存区
git reset HEAD 文件名

# 撤销工作区更改
git checkout -- 文件名
```

## 推荐工具

### 图形化客户端

- **GitHub Desktop**: 官方客户端，简单易用
- **SourceTree**: 功能强大，支持多种版本控制系统
- **GitKraken**: 界面美观，功能丰富
- **VS Code**: 集成 Git 功能，开发体验好

### 命令行工具

- **Windows Terminal**: 现代化的终端模拟器
- **PowerShell**: Windows 原生命令行工具
- **Git Bash**: Git 自带的 Bash 环境

## 学习资源

- [Git 官方文档](https://git-scm.com/doc)
- [GitHub 帮助文档](https://help.github.com/)
- [Pro Git 中文版](https://git-scm.com/book/zh/v2)
- [Git 教程 - 廖雪峰](https://www.liaoxuefeng.com/wiki/896043488029600)
