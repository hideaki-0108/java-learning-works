package com.learning.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.learning.database.UserDatabase;
import com.learning.model.User;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApiHandler {
    private final UserDatabase userDatabase;
    private final ObjectMapper objectMapper;
    
    public ApiHandler() {
        this.userDatabase = new UserDatabase();
        this.objectMapper = new ObjectMapper();
        // Java 8 日時サポートを有効化
        this.objectMapper.findAndRegisterModules();
    }
    
    // ユーザー一覧取得 API
    public void handleUsers(HttpExchange exchange) throws IOException {
        SimpleHttpServer.addCorsHeaders(exchange);
        
        if ("OPTIONS".equals(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(200, -1);
            return;
        }
        
        try {
            if ("GET".equals(exchange.getRequestMethod())) {
                // 全ユーザー取得
                List<User> users = userDatabase.getAllUsers();
                
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("users", users);
                response.put("count", users.size());
                response.put("message", "ユーザー一覧を取得しました");
                
                String jsonResponse = objectMapper.writeValueAsString(response);
                SimpleHttpServer.sendJsonResponse(exchange, 200, jsonResponse);
                
                System.out.println("✅ ユーザー一覧API: " + users.size() + "件のユーザーを返却");
                
            } else {
                SimpleHttpServer.sendErrorResponse(exchange, 405, "Method not allowed");
            }
        } catch (Exception e) {
            System.err.println("❌ ユーザー一覧API エラー: " + e.getMessage());
            SimpleHttpServer.sendErrorResponse(exchange, 500, "Internal server error");
        }
    }
    
    // ユーザー登録 API
    public void handleRegister(HttpExchange exchange) throws IOException {
        SimpleHttpServer.addCorsHeaders(exchange);
        
        if ("OPTIONS".equals(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(200, -1);
            return;
        }
        
        if (!"POST".equals(exchange.getRequestMethod())) {
            SimpleHttpServer.sendErrorResponse(exchange, 405, "Method not allowed");
            return;
        }
        
        try {
            // リクエストボディを読み取り
            String requestBody = readRequestBody(exchange);
            System.out.println("📥 登録リクエスト受信: " + requestBody);
            
            @SuppressWarnings("unchecked")
            Map<String, Object> requestData = objectMapper.readValue(requestBody, Map.class);
            
            // パラメータ取得
            String username = (String) requestData.get("username");
            String email = (String) requestData.get("email");
            String password = (String) requestData.get("password");
            String firstName = (String) requestData.get("firstName");
            String lastName = (String) requestData.get("lastName");
            
            // バリデーション
            if (username == null || username.trim().isEmpty() ||
                email == null || email.trim().isEmpty() ||
                password == null || password.trim().isEmpty() ||
                firstName == null || firstName.trim().isEmpty() ||
                lastName == null || lastName.trim().isEmpty()) {
                
                SimpleHttpServer.sendErrorResponse(exchange, 400, "すべての項目を入力してください");
                return;
            }
            
            // 重複チェック（簡易版）
            User existingUser = userDatabase.findUserByUsername(username);
            if (existingUser != null) {
                SimpleHttpServer.sendErrorResponse(exchange, 409, "このユーザー名は既に使用されています");
                return;
            }
            
            // ユーザー作成（実際の実装ではパスワードをハッシュ化する）
            User newUser = new User(username, email, firstName, lastName);
            newUser.setPasswordHash("hashed_" + password); // 簡易ハッシュ（実際はBCryptを使用）
            
            User createdUser = userDatabase.createUser(newUser);
            
            if (createdUser != null) {
                // レスポンス作成（パスワードハッシュは除外）
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "ユーザー登録が完了しました");
                response.put("user", createUserResponse(createdUser));
                
                String jsonResponse = objectMapper.writeValueAsString(response);
                SimpleHttpServer.sendJsonResponse(exchange, 201, jsonResponse);
                
                System.out.println("✅ ユーザー登録成功: " + username);
                
            } else {
                SimpleHttpServer.sendErrorResponse(exchange, 500, "ユーザー登録に失敗しました");
            }
            
        } catch (Exception e) {
            System.err.println("❌ ユーザー登録API エラー: " + e.getMessage());
            SimpleHttpServer.sendErrorResponse(exchange, 500, "Internal server error");
        }
    }
    
    // ログイン API
    public void handleLogin(HttpExchange exchange) throws IOException {
        SimpleHttpServer.addCorsHeaders(exchange);
        
        if ("OPTIONS".equals(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(200, -1);
            return;
        }
        
        if (!"POST".equals(exchange.getRequestMethod())) {
            SimpleHttpServer.sendErrorResponse(exchange, 405, "Method not allowed");
            return;
        }
        
        try {
            // リクエストボディを読み取り
            String requestBody = readRequestBody(exchange);
            System.out.println("📥 ログインリクエスト受信");
            
            @SuppressWarnings("unchecked")
            Map<String, Object> requestData = objectMapper.readValue(requestBody, Map.class);
            
            String username = (String) requestData.get("username");
            String password = (String) requestData.get("password");
            
            // バリデーション
            if (username == null || username.trim().isEmpty() ||
                password == null || password.trim().isEmpty()) {
                
                SimpleHttpServer.sendErrorResponse(exchange, 400, "ユーザー名とパスワードを入力してください");
                return;
            }
            
            // ユーザー検索
            User user = userDatabase.findUserByUsername(username);
            
            if (user != null && user.isActive()) {
                // 簡易パスワード検証（実際はBCryptで検証）
                if (("hashed_" + password).equals(user.getPasswordHash())) {
                    // 最終ログイン日時を更新
                    userDatabase.updateLastLogin(user.getId());
                    
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", true);
                    response.put("message", "ログインしました");
                    response.put("user", createUserResponse(user));
                    
                    String jsonResponse = objectMapper.writeValueAsString(response);
                    SimpleHttpServer.sendJsonResponse(exchange, 200, jsonResponse);
                    
                    System.out.println("✅ ログイン成功: " + username);
                    
                } else {
                    SimpleHttpServer.sendErrorResponse(exchange, 401, "ユーザー名またはパスワードが間違っています");
                    System.out.println("⚠️ ログイン失敗（パスワード不一致）: " + username);
                }
            } else {
                SimpleHttpServer.sendErrorResponse(exchange, 401, "ユーザー名またはパスワードが間違っています");
                System.out.println("⚠️ ログイン失敗（ユーザーが存在しない）: " + username);
            }
            
        } catch (Exception e) {
            System.err.println("❌ ログインAPI エラー: " + e.getMessage());
            SimpleHttpServer.sendErrorResponse(exchange, 500, "Internal server error");
        }
    }
    
    // ログアウト API
    public void handleLogout(HttpExchange exchange) throws IOException {
        SimpleHttpServer.addCorsHeaders(exchange);
        
        if ("OPTIONS".equals(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(200, -1);
            return;
        }
        
        if (!"POST".equals(exchange.getRequestMethod())) {
            SimpleHttpServer.sendErrorResponse(exchange, 405, "Method not allowed");
            return;
        }
        
        try {
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "ログアウトしました");
            
            String jsonResponse = objectMapper.writeValueAsString(response);
            SimpleHttpServer.sendJsonResponse(exchange, 200, jsonResponse);
            
            System.out.println("✅ ログアウト処理完了");
            
        } catch (Exception e) {
            System.err.println("❌ ログアウトAPI エラー: " + e.getMessage());
            SimpleHttpServer.sendErrorResponse(exchange, 500, "Internal server error");
        }
    }
    
    // ユーザーレスポンス作成（パスワードハッシュを除外）
    private Map<String, Object> createUserResponse(User user) {
        Map<String, Object> userResponse = new HashMap<>();
        userResponse.put("id", user.getId());
        userResponse.put("username", user.getUsername());
        userResponse.put("email", user.getEmail());
        userResponse.put("firstName", user.getFirstName());
        userResponse.put("lastName", user.getLastName());
        userResponse.put("fullName", user.getFullName());
        userResponse.put("isActive", user.isActive());
        userResponse.put("createdAt", user.getCreatedAt());
        userResponse.put("lastLoginAt", user.getLastLoginAt());
        return userResponse;
    }
    
    // リクエストボディを読み取るヘルパーメソッド
    private String readRequestBody(HttpExchange exchange) throws IOException {
        try (InputStream inputStream = exchange.getRequestBody()) {
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
