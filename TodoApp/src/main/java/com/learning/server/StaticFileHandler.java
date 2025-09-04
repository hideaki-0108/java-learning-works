package com.learning.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 静的ファイル（HTML、CSS、JS）を提供するハンドラー
 */
public class StaticFileHandler implements HttpHandler {
    private final String staticDirectory;

    public StaticFileHandler(String staticDirectory) {
        this.staticDirectory = staticDirectory;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        
        // ルートパス（/）の場合はindex.htmlを返す
        if (path.equals("/")) {
            path = "/index.html";
        }

        // ファイルパスを構築
        Path filePath = Paths.get(staticDirectory + path);
        
        try {
            if (Files.exists(filePath) && Files.isRegularFile(filePath)) {
                // ファイルが存在する場合
                byte[] fileContent = Files.readAllBytes(filePath);
                String contentType = getContentType(path);
                
                exchange.getResponseHeaders().set("Content-Type", contentType);
                exchange.sendResponseHeaders(200, fileContent.length);
                
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(fileContent);
                }
            } else {
                // ファイルが存在しない場合は404
                String notFoundResponse = "<html><body><h1>404 Not Found</h1></body></html>";
                byte[] responseBytes = notFoundResponse.getBytes();
                
                exchange.getResponseHeaders().set("Content-Type", "text/html");
                exchange.sendResponseHeaders(404, responseBytes.length);
                
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(responseBytes);
                }
            }
        } catch (Exception e) {
            System.err.println("静的ファイル提供エラー: " + e.getMessage());
            
            String errorResponse = "<html><body><h1>500 Internal Server Error</h1></body></html>";
            byte[] responseBytes = errorResponse.getBytes();
            
            exchange.getResponseHeaders().set("Content-Type", "text/html");
            exchange.sendResponseHeaders(500, responseBytes.length);
            
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(responseBytes);
            }
        } finally {
            exchange.close();
        }
    }

    /**
     * ファイル拡張子に基づいてContent-Typeを決定する
     */
    private String getContentType(String path) {
        if (path.endsWith(".html")) {
            return "text/html; charset=UTF-8";
        } else if (path.endsWith(".css")) {
            return "text/css; charset=UTF-8";
        } else if (path.endsWith(".js")) {
            return "application/javascript; charset=UTF-8";
        } else if (path.endsWith(".json")) {
            return "application/json; charset=UTF-8";
        } else if (path.endsWith(".png")) {
            return "image/png";
        } else if (path.endsWith(".jpg") || path.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (path.endsWith(".gif")) {
            return "image/gif";
        } else {
            return "text/plain; charset=UTF-8";
        }
    }
}
