@echo off
chcp 65001 >nul
echo ==========================================
echo   在线留言板系统部署状态检查脚本
echo ==========================================
echo VM IP: 192.168.159.128
echo.

REM 检查网络连通性
echo 🔍 检查网络连通性...
ping -n 1 192.168.159.128 >nul
if %errorlevel% equ 0 (
    echo ✅ VM 网络连通正常
) else (
    echo ❌ VM 网络不通，请检查网络配置
    pause
    exit /b 1
)

echo.
echo 🔍 检查中间件服务状态...

REM 检查 MySQL 服务
echo 检查 MySQL 服务 (端口 3306)...
powershell -Command "try { $tcp = New-Object System.Net.Sockets.TcpClient; $tcp.Connect('192.168.159.128', 3306); if($tcp.Connected) { Write-Host '✅ MySQL 服务正常' } else { Write-Host '❌ MySQL 服务异常' }; $tcp.Close() } catch { Write-Host '❌ MySQL 服务异常' }"

REM 检查 Redis 服务
echo 检查 Redis 服务 (端口 6379)...
powershell -Command "try { $tcp = New-Object System.Net.Sockets.TcpClient; $tcp.Connect('192.168.159.128', 6379); if($tcp.Connected) { Write-Host '✅ Redis 服务正常' } else { Write-Host '❌ Redis 服务异常' }; $tcp.Close() } catch { Write-Host '❌ Redis 服务异常' }"

REM 检查 Kafka 服务
echo 检查 Kafka 服务 (端口 9092)...
powershell -Command "try { $tcp = New-Object System.Net.Sockets.TcpClient; $tcp.Connect('192.168.159.128', 9092); if($tcp.Connected) { Write-Host '✅ Kafka 服务正常' } else { Write-Host '❌ Kafka 服务异常' }; $tcp.Close() } catch { Write-Host '❌ Kafka 服务异常' }"

REM 检查 Kafka UI
echo 检查 Kafka UI (端口 8080)...
powershell -Command "try { $tcp = New-Object System.Net.Sockets.TcpClient; $tcp.Connect('192.168.159.128', 8080); if($tcp.Connected) { Write-Host '✅ Kafka UI 服务正常' } else { Write-Host '❌ Kafka UI 服务异常' }; $tcp.Close() } catch { Write-Host '❌ Kafka UI 服务异常' }"

REM 检查 Redis Commander
echo 检查 Redis Commander (端口 8081)...
powershell -Command "try { $tcp = New-Object System.Net.Sockets.TcpClient; $tcp.Connect('192.168.159.128', 8081); if($tcp.Connected) { Write-Host '✅ Redis Commander 服务正常' } else { Write-Host '❌ Redis Commander 服务异常' }; $tcp.Close() } catch { Write-Host '❌ Redis Commander 服务异常' }"

echo.
echo ==========================================
echo   服务访问地址
echo ==========================================
echo MySQL:    192.168.159.128:3306
echo Redis:    192.168.159.128:6379
echo Kafka:    192.168.159.128:9092
echo Kafka UI: http://192.168.159.128:8080
echo Redis UI: http://192.168.159.128:8081
echo.
echo 数据库连接信息：
echo 用户名: messageboard
echo 密码: messageboard123
echo 数据库: messageboard
echo.
echo Redis 密码: redis123
echo.
echo 按任意键退出...
pause >nul
