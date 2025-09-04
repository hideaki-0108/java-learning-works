@echo off
REM Todo App ビルドスクリプト (Windows)
echo === Todo App ビルド開始 ===

REM フロントエンドのビルド
echo 1. フロントエンドをビルド中...
cd frontend
call npm install
call npm run build
cd ..

REM Javaプロジェクトのコンパイル
echo 2. Javaプロジェクトをコンパイル中...
call mvn clean compile

echo === ビルド完了 ===
echo.
echo アプリケーションを起動するには:
echo 1. Docker でMySQLを起動: docker-compose up -d
echo 2. Javaアプリを起動: mvn exec:java -Dexec.mainClass="com.learning.Main"
echo 3. ブラウザで http://localhost:8080 にアクセス
pause
