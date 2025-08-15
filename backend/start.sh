#!/bin/bash

# Message Board Backend 启动脚本
# 作者: Message Board Team
# 版本: 1.0.0

echo "=========================================="
echo "  在线留言板系统后端服务启动脚本"
echo "=========================================="

# 检查Java版本
echo "检查Java版本..."
java_version=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2)
echo "当前Java版本: $java_version"

# 检查Maven
echo "检查Maven..."
if command -v mvn &> /dev/null; then
    mvn_version=$(mvn -version | head -n 1)
    echo "Maven版本: $mvn_version"
else
    echo "错误: 未找到Maven，请先安装Maven"
    exit 1
fi

# 检查配置文件
echo "检查配置文件..."
if [ ! -f "src/main/resources/application.properties" ]; then
    echo "错误: 未找到application.properties配置文件"
    exit 1
fi

# 检查数据库配置
echo "检查数据库配置..."
db_password=$(grep "spring.datasource.password" src/main/resources/application.properties | cut -d'=' -f2)
if [ "$db_password" = "your_password" ]; then
    echo "警告: 请先修改数据库密码配置"
    echo "编辑 src/main/resources/application.properties 文件"
    echo "将 your_password 替换为实际密码"
    read -p "是否继续启动? (y/N): " -n 1 -r
    echo
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        echo "启动已取消"
        exit 0
    fi
fi

# 清理并编译
echo "清理并编译项目..."
mvn clean compile

if [ $? -ne 0 ]; then
    echo "错误: 项目编译失败"
    exit 1
fi

echo "编译成功！"

# 启动应用
echo "启动Spring Boot应用..."
echo "应用将在 http://localhost:8080 启动"
echo "API接口前缀: /api"
echo ""
echo "按 Ctrl+C 停止应用"
echo "=========================================="

mvn spring-boot:run
