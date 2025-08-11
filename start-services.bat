@echo off
chcp 65001 >nul
echo ==========================================
echo   在线留言板系统前后端启动脚本
echo ==========================================

REM 检查 Java 环境
echo 检查 Java 环境...
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Java 未安装或未配置环境变量
    echo 请安装 Java 17 或更高版本
    pause
    exit /b 1
)

REM 检查 Node.js 环境
echo 检查 Node.js 环境...
node --version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Node.js 未安装或未配置环境变量
    echo 请安装 Node.js 18 或更高版本
    pause
    exit /b 1
)

echo ✅ 环境检查通过

REM 提示用户配置 VM IP 地址
echo.
echo 请确保已更新配置文件中的 VM IP 地址：
echo 1. backend/src/main/resources/application.yml
echo 2. frontend/vite.config.js
echo.
set /p vm_ip="请输入 Ubuntu VM 的 IP 地址: "

if "%vm_ip%"=="" (
    echo ❌ 未输入 IP 地址，使用默认配置
    set vm_ip=localhost
)

echo 使用 VM IP: %vm_ip%

REM 启动后端服务
echo.
echo 🚀 启动后端服务...
cd backend
start "Spring Boot Backend" cmd /k "mvn spring-boot:run"
cd ..

REM 等待后端启动
echo ⏳ 等待后端服务启动...
timeout /t 10 /nobreak >nul

REM 启动前端服务
echo.
echo 🚀 启动前端服务...
cd frontend
start "Vue Frontend" cmd /k "npm run dev"
cd ..

echo.
echo ==========================================
echo   服务启动完成！
echo ==========================================
echo 后端服务: http://localhost:8080
echo 前端服务: http://localhost:3000
echo.
echo 中间件服务 (在 VM 上):
echo MySQL:     %vm_ip%:3306
echo Redis:     %vm_ip%:6379
echo Kafka:     %vm_ip%:9092
echo Kafka UI:  http://%vm_ip%:8080
echo Redis UI:  http://%vm_ip%:8081
echo.
echo 按任意键退出...
pause >nul
