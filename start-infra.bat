@echo off
chcp 65001 >nul

echo [INFO] 正在启动基础服务...

docker-compose up -d

if %ERRORLEVEL% neq 0 (
    echo [ERROR] 启动失败，请检查 Docker 是否正在运行
    exit /b 1
)

echo [INFO] 等待服务就绪...

set mysqlStatus=
set redisStatus=
set retryCount=0
set maxRetries=60

:checkLoop
if %retryCount% geq %maxRetries% goto :timeout
timeout /t 1 /nobreak >nul
set /a retryCount+=1

for /f "delims=" %%i in ('docker inspect --format="{{.State.Health.Status}}" exam-mysql 2^>nul') do set mysqlStatus=%%i
for /f "delims=" %%i in ('docker inspect --format="{{.State.Health.Status}}" exam-redis 2^>nul') do set redisStatus=%%i

if "%mysqlStatus%"=="unhealthy" goto :error
if "%redisStatus%"=="unhealthy" goto :error
if "%mysqlStatus%"=="" goto :error
if "%redisStatus%"=="" goto :error
if "%mysqlStatus%"=="healthy" if "%redisStatus%"=="healthy" goto :done
goto :checkLoop

:timeout
echo.
echo [ERROR] 等待超时（%maxRetries% 秒），以下服务未就绪:
if not "%mysqlStatus%"=="healthy" echo   MySQL: %mysqlStatus%
if not "%redisStatus%"=="healthy" echo   Redis: %redisStatus%
echo   请使用 docker logs 查看容器日志排查原因。
goto :showInfo

:error
echo.
echo [ERROR] 服务异常:
if "%mysqlStatus%"=="unhealthy" echo   MySQL: unhealthy（健康检查失败）
if "%mysqlStatus%"=="" echo   MySQL: 容器不存在或未启动
if "%redisStatus%"=="unhealthy" echo   Redis: unhealthy（健康检查失败）
if "%redisStatus%"=="" echo   Redis: 容器不存在或未启动
echo   请使用 docker logs 查看容器日志排查原因。
goto :showInfo

:done
echo.
echo [OK] 所有服务已就绪！
echo.
echo 服务状态:

if "%mysqlStatus%"=="healthy" (
    echo   MySQL ^(3306^): 运行中
) else (
    echo   MySQL ^(3306^): %mysqlStatus%
)

if "%redisStatus%"=="healthy" (
    echo   Redis ^(6379^): 运行中
) else (
    echo   Redis ^(6379^): %redisStatus%
)

:showInfo
echo.
echo 连接信息:
echo   MySQL:
echo     Host:     localhost:3306
echo     Database: exam_questions
echo     User:     root / root123456
echo     User:     exam / exam123456
echo   Redis:
echo     Host:     localhost:6379
echo     Password: redis123456
echo.
echo 停止服务: docker-compose down
echo 停止并清除数据: docker-compose down -v
pause
