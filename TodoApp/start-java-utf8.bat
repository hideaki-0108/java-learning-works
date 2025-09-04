@echo off
chcp 65001 > nul
echo === Todo Application Starting (UTF-8) ===
echo.
echo 1. Setting UTF-8 encoding...
set JAVA_OPTS=-Dfile.encoding=UTF-8 -Dsun.jnu.encoding=UTF-8 -Dconsole.encoding=UTF-8

echo 2. Compiling Java sources...
call mvn clean compile

if %ERRORLEVEL% NEQ 0 (
    echo Maven compilation failed!
    pause
    exit /b 1
)

echo.
echo 3. Starting Java application with UTF-8 encoding...
echo Server will start on http://localhost:8080
echo API endpoints available at http://localhost:8080/api/todos
echo.
echo Press Ctrl+C to stop the server
echo.

java %JAVA_OPTS% -cp "target/classes;target/dependency/*" com.learning.Main

pause
