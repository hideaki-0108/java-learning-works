@echo off
echo === Java Backend Server Starting ===
echo.
echo 1. Compiling Java sources...
call mvn clean compile

if %ERRORLEVEL% NEQ 0 (
    echo Maven compilation failed!
    pause
    exit /b 1
)

echo.
echo 2. Starting Java application...
echo Server will start on http://localhost:8080
echo API endpoints available at http://localhost:8080/api/todos
echo.
echo Press Ctrl+C to stop the server
echo.

call mvn exec:java -Dfile.encoding=UTF-8 -Dexec.args="-Dfile.encoding=UTF-8"

pause
