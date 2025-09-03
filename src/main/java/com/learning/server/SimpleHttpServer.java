package com.learning.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;

public class SimpleHttpServer {
    private static final int PORT = 8080;
    
    private HttpServer server;
    private final StaticFileHandler staticFileHandler;
    private final ApiHandler apiHandler;
    
    public SimpleHttpServer() {
        this.staticFileHandler = new StaticFileHandler();
        this.apiHandler = new ApiHandler();
    }
    
    public void start() throws IOException {
        server = HttpServer.create(new InetSocketAddress(PORT), 0);
        
        // 静的ファイル配信（HTML, CSS, JS）
        server.createContext("/", staticFileHandler);
        
        // API エンドポイント
        server.createContext("/api/test", new TestApiHandler());
        server.createContext("/api/users", apiHandler::handleUsers);
        server.createContext("/api/auth/register", apiHandler::handleRegister);
        server.createContext("/api/auth/login", apiHandler::handleLogin);
        server.createContext("/api/auth/logout", apiHandler::handleLogout);
        
        // スレッドプール設定
        server.setExecutor(Executors.newFixedThreadPool(10));
        
        server.start();
        System.out.println("🚀 HTTP Server started on http://localhost:" + PORT);
        System.out.println("🌐 サーバーが起動しました: http://localhost:" + PORT);
        System.out.println("📂 静的ファイル: http://localhost:" + PORT + "/html/");
        System.out.println("🔧 API テスト: http://localhost:" + PORT + "/api/test");
        System.out.println("⏹️  停止するには Ctrl+C を押してください");
        
        // シャットダウンフック
        Runtime.getRuntime().addShutdownHook(new Thread(this::stop));
    }
    
    public void stop() {
        if (server != null) {
            server.stop(0);
            System.out.println("🛑 サーバーが停止しました");
        }
    }
    
    // テスト用のシンプルなAPIハンドラー
    static class TestApiHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // CORS ヘッダーを追加
            addCorsHeaders(exchange);
            
            if ("OPTIONS".equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(200, -1);
                return;
            }
            
            // JSON レスポンス（fetch() で受け取れる形式）
            String jsonResponse = """
                {
                    "message": "🎉 API テスト成功！",
                    "timestamp": "%s",
                    "status": "ok",
                    "server": "Java Plain HTTP Server",
                    "version": "1.0.0"
                }
                """.formatted(java.time.LocalDateTime.now());
            
            sendJsonResponse(exchange, 200, jsonResponse);
        }
    }
    
    // CORS ヘッダーを追加するユーティリティメソッド
    public static void addCorsHeaders(HttpExchange exchange) {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type, Authorization");
        exchange.getResponseHeaders().add("Access-Control-Max-Age", "3600");
    }
    
    // JSON レスポンス送信のユーティリティメソッド
    public static void sendJsonResponse(HttpExchange exchange, int statusCode, String jsonContent) throws IOException {
        byte[] responseBytes = jsonContent.getBytes(StandardCharsets.UTF_8);
        
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(statusCode, responseBytes.length);
        
        try (OutputStream outputStream = exchange.getResponseBody()) {
            outputStream.write(responseBytes);
        }
    }
    
    // HTML レスポンス送信のユーティリティメソッド
    public static void sendHtmlResponse(HttpExchange exchange, int statusCode, String htmlContent) throws IOException {
        byte[] responseBytes = htmlContent.getBytes(StandardCharsets.UTF_8);
        
        exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
        exchange.sendResponseHeaders(statusCode, responseBytes.length);
        
        try (OutputStream outputStream = exchange.getResponseBody()) {
            outputStream.write(responseBytes);
        }
    }
    
    // エラーレスポンス送信のユーティリティメソッド
    public static void sendErrorResponse(HttpExchange exchange, int statusCode, String message) throws IOException {
        String errorJson = """
            {
                "success": false,
                "error": "%s",
                "statusCode": %d,
                "timestamp": "%s"
            }
            """.formatted(message, statusCode, java.time.LocalDateTime.now());
        
        sendJsonResponse(exchange, statusCode, errorJson);
    }
}
