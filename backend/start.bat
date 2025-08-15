@echo off
chcp 65001 >nul

echo ==========================================
echo   在线留言板系统后端服务启动脚本
echo ==========================================

REM 检查Java版本
echo 检查Java版本...
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo 错误: 未找到Java，请先安装JDK 17+
    pause
    exit /b 1
)

REM 检查Maven
echo 检查Maven...
mvn -version >nul 2>&1
if %errorlevel% neq 0 (
    echo 错误: 未找到Maven，请先安装Maven
    pause
    exit /b 1
)

REM 检查配置文件
echo 检查配置文件...
if not exist "src\main\resources\application.properties" (
    echo 错误: 未找到application.properties配置文件
    pause
    exit /b 1
)

REM 检查数据库配置
echo 检查数据库配置...
findstr "your_password" src\main\resources\application.properties >nul
if %errorlevel% equ 0 (
    echo 警告: 请先修改数据库密码配置
    echo 编辑 src\main\resources\application.properties 文件
    echo 将 your_password 替换为实际密码
    set /p continue="是否继续启动? (y/N): "
    if /i not "%continue%"=="y" (
        echo 启动已取消
        pause
        exit /b 0
    )
)

REM 清理并编译
echo 清理并编译项目...
call mvn clean compile
if %errorlevel% neq 0 (
    echo 错误: 项目编译失败
    pause
    exit /b 1
)

echo 编译成功！

REM 启动应用
echo 启动Spring Boot应用...
echo 应用将在 http://localhost:8080 启动
echo API接口前缀: /api
echo.
echo 按 Ctrl+C 停止应用
echo ==========================================

call mvn spring-boot:run

pause
