package com.learning.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;

public class StaticFileHandler implements HttpHandler {
    private static final String STATIC_DIR = "src/main/resources/static";
    
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        SimpleHttpServer.addCorsHeaders(exchange);
        
        if ("OPTIONS".equals(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(200, -1);
            return;
        }
        
        String requestPath = exchange.getRequestURI().getPath();
        
        // デフォルトページの設定
        if ("/".equals(requestPath)) {
            requestPath = "/html/index.html";
        }
        
        // ファイルパスの構築
        Path filePath = Paths.get(STATIC_DIR + requestPath);
        
        if (Files.exists(filePath) && !Files.isDirectory(filePath)) {
            // ファイルが存在する場合
            try {
                String contentType = getContentType(requestPath);
                byte[] fileContent = Files.readAllBytes(filePath);
                
                exchange.getResponseHeaders().set("Content-Type", contentType);
                exchange.sendResponseHeaders(200, fileContent.length);
                
                try (OutputStream outputStream = exchange.getResponseBody()) {
                    outputStream.write(fileContent);
                }
                
                System.out.println("📄 静的ファイル配信: " + requestPath + " (" + fileContent.length + " bytes)");
                
            } catch (IOException e) {
                System.err.println("❌ ファイル読み込みエラー: " + requestPath + " - " + e.getMessage());
                send404Response(exchange);
            }
        } else {
            // ファイルが存在しない場合
            System.out.println("⚠️ ファイルが見つかりません: " + requestPath);
            send404Response(exchange);
        }
    }
    
    private String getContentType(String path) {
        if (path.endsWith(".html")) return "text/html; charset=UTF-8";
        if (path.endsWith(".css")) return "text/css; charset=UTF-8";
        if (path.endsWith(".js")) return "application/javascript; charset=UTF-8";
        if (path.endsWith(".json")) return "application/json; charset=UTF-8";
        if (path.endsWith(".png")) return "image/png";
        if (path.endsWith(".jpg") || path.endsWith(".jpeg")) return "image/jpeg";
        if (path.endsWith(".gif")) return "image/gif";
        if (path.endsWith(".svg")) return "image/svg+xml";
        if (path.endsWith(".ico")) return "image/x-icon";
        return "text/plain; charset=UTF-8";
    }
    
    private void send404Response(HttpExchange exchange) throws IOException {
        String notFoundHtml = """
            <!DOCTYPE html>
            <html lang="ja">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>404 - ページが見つかりません</title>
                <style>
                    body {
                        font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                        margin: 0;
                        padding: 40px;
                        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                        color: white;
                        text-align: center;
                    }
                    .container {
                        max-width: 600px;
                        margin: 0 auto;
                        background: rgba(255, 255, 255, 0.1);
                        padding: 40px;
                        border-radius: 10px;
                        backdrop-filter: blur(10px);
                    }
                    h1 { font-size: 4em; margin: 0; }
                    h2 { margin: 20px 0; }
                    p { font-size: 1.2em; margin: 20px 0; }
                    .links {
                        margin-top: 30px;
                    }
                    a {
                        color: #ffeb3b;
                        text-decoration: none;
                        margin: 0 15px;
                        font-weight: bold;
                    }
                    a:hover { text-decoration: underline; }
                </style>
            </head>
            <body>
                <div class="container">
                    <h1>404</h1>
                    <h2>ページが見つかりません</h2>
                    <p>お探しのページは存在しないか、移動した可能性があります。</p>
                    <div class="links">
                        <a href="/">ホーム</a>
                        <a href="/html/index.html">メインページ</a>
                        <a href="/api/test">API テスト</a>
                    </div>
                </div>
            </body>
            </html>
            """;
        
        SimpleHttpServer.sendHtmlResponse(exchange, 404, notFoundHtml);
    }
}
