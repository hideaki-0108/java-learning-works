package com.learning.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.learning.database.TodoDAO;
import com.learning.model.Todo;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Todo API のHTTPリクエストを処理するハンドラー
 */
public class TodoApiHandler implements HttpHandler {
    private final TodoDAO todoDAO;
    private final ObjectMapper objectMapper;

    public TodoApiHandler() {
        this.todoDAO = new TodoDAO();
        this.objectMapper = new ObjectMapper();
        // Java 8 日時APIサポートを追加
        this.objectMapper.registerModule(new JavaTimeModule());
        // 日本語文字化け対策
        this.objectMapper.configure(com.fasterxml.jackson.core.JsonGenerator.Feature.ESCAPE_NON_ASCII, false);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // CORS設定
        setCorsHeaders(exchange);
        
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        System.out.println("API Request: " + method + " " + path);

        try {
            switch (method) {
                case "GET":
                    handleGet(exchange, path);
                    break;
                case "POST":
                    handlePost(exchange, path);
                    break;
                case "PUT":
                    handlePut(exchange, path);
                    break;
                case "DELETE":
                    handleDelete(exchange, path);
                    break;
                case "OPTIONS":
                    // CORS プリフライトリクエスト
                    exchange.sendResponseHeaders(200, 0);
                    break;
                default:
                    sendErrorResponse(exchange, 405, "Method Not Allowed");
            }
        } catch (Exception e) {
            System.err.println("API処理エラー: " + e.getMessage());
            e.printStackTrace();
            sendErrorResponse(exchange, 500, "Internal Server Error");
        } finally {
            exchange.close();
        }
    }

    /**
     * GETリクエストを処理
     */
    private void handleGet(HttpExchange exchange, String path) throws IOException {
        if (path.equals("/api/todos")) {
            // 全てのTodoを取得
            List<Todo> todos = todoDAO.getAllTodos();
            sendJsonResponse(exchange, 200, todos);
        } else if (path.startsWith("/api/todos/")) {
            // 特定のTodoを取得
            String idStr = path.substring("/api/todos/".length());
            try {
                int id = Integer.parseInt(idStr);
                Todo todo = todoDAO.getTodoById(id);
                if (todo != null) {
                    sendJsonResponse(exchange, 200, todo);
                } else {
                    sendErrorResponse(exchange, 404, "Todo not found");
                }
            } catch (NumberFormatException e) {
                sendErrorResponse(exchange, 400, "Invalid ID format");
            }
        } else {
            sendErrorResponse(exchange, 404, "Not Found");
        }
    }

    /**
     * POSTリクエストを処理（新規Todo作成）
     */
    private void handlePost(HttpExchange exchange, String path) throws IOException {
        if (path.equals("/api/todos")) {
            String requestBody = getRequestBody(exchange);
            try {
                Todo todo = objectMapper.readValue(requestBody, Todo.class);
                int newId = todoDAO.createTodo(todo);
                if (newId > 0) {
                    todo.setId(newId);
                    Todo createdTodo = todoDAO.getTodoById(newId);
                    sendJsonResponse(exchange, 201, createdTodo);
                } else {
                    sendErrorResponse(exchange, 500, "Failed to create todo");
                }
            } catch (Exception e) {
                System.err.println("JSON解析エラー: " + e.getMessage());
                sendErrorResponse(exchange, 400, "Invalid JSON format");
            }
        } else {
            sendErrorResponse(exchange, 404, "Not Found");
        }
    }

    /**
     * PUTリクエストを処理（Todo更新）
     */
    private void handlePut(HttpExchange exchange, String path) throws IOException {
        if (path.startsWith("/api/todos/")) {
            String idStr = path.substring("/api/todos/".length());
            try {
                int id = Integer.parseInt(idStr);
                String requestBody = getRequestBody(exchange);
                Todo todo = objectMapper.readValue(requestBody, Todo.class);
                todo.setId(id);
                
                boolean updated = todoDAO.updateTodo(todo);
                if (updated) {
                    Todo updatedTodo = todoDAO.getTodoById(id);
                    sendJsonResponse(exchange, 200, updatedTodo);
                } else {
                    sendErrorResponse(exchange, 404, "Todo not found");
                }
            } catch (NumberFormatException e) {
                sendErrorResponse(exchange, 400, "Invalid ID format");
            } catch (Exception e) {
                System.err.println("JSON解析エラー: " + e.getMessage());
                sendErrorResponse(exchange, 400, "Invalid JSON format");
            }
        } else {
            sendErrorResponse(exchange, 404, "Not Found");
        }
    }

    /**
     * DELETEリクエストを処理（Todo削除）
     */
    private void handleDelete(HttpExchange exchange, String path) throws IOException {
        if (path.startsWith("/api/todos/")) {
            String idStr = path.substring("/api/todos/".length());
            try {
                int id = Integer.parseInt(idStr);
                boolean deleted = todoDAO.deleteTodo(id);
                if (deleted) {
                    sendJsonResponse(exchange, 200, Map.of("message", "Todo deleted successfully"));
                } else {
                    sendErrorResponse(exchange, 404, "Todo not found");
                }
            } catch (NumberFormatException e) {
                sendErrorResponse(exchange, 400, "Invalid ID format");
            }
        } else {
            sendErrorResponse(exchange, 404, "Not Found");
        }
    }

    /**
     * リクエストボディを読み取る
     */
    private String getRequestBody(HttpExchange exchange) throws IOException {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            String result = sb.toString();
            System.out.println("Request body received: " + result); // デバッグ用
            return result;
        }
    }

    /**
     * JSON レスポンスを送信
     */
    private void sendJsonResponse(HttpExchange exchange, int statusCode, Object data) throws IOException {
        String jsonResponse = objectMapper.writeValueAsString(data);
        System.out.println("JSON response being sent: " + jsonResponse); // デバッグ用
        byte[] responseBytes = jsonResponse.getBytes(StandardCharsets.UTF_8);
        
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(statusCode, responseBytes.length);
        
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseBytes);
        }
    }

    /**
     * エラーレスポンスを送信
     */
    private void sendErrorResponse(HttpExchange exchange, int statusCode, String message) throws IOException {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", message);
        sendJsonResponse(exchange, statusCode, errorResponse);
    }

    /**
     * CORS ヘッダーを設定
     */
    private void setCorsHeaders(HttpExchange exchange) {
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type, Authorization");
    }
}
