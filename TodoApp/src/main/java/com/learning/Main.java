package com.learning;

import com.learning.database.DatabaseConnection;
import com.learning.server.StaticFileHandler;
import com.learning.server.TodoApiHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * Todoアプリのメインクラス
 * HTTPサーバーを起動し、APIエンドポイントと静的ファイルの提供を開始する
 */
public class Main {
    private static final int PORT = 8080;
    private static final String STATIC_FILES_DIR = "src/main/resources/static";

    public static void main(String[] args) {
        try {
            // データベース接続の初期化
            System.out.println("=== Todo Application Starting ===");
            DatabaseConnection.getInstance();

            // HTTPサーバーの作成
            HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

            // APIハンドラーの設定
            server.createContext("/api", new TodoApiHandler());

            // 静的ファイルハンドラーの設定
            server.createContext("/", new StaticFileHandler(STATIC_FILES_DIR));

            // サーバー開始
            server.setExecutor(null); // デフォルトのExecutorを使用
            server.start();

            System.out.println("サーバーが開始されました:");
            System.out.println("  - Webアプリケーション: http://localhost:" + PORT);
            System.out.println("  - API エンドポイント: http://localhost:" + PORT + "/api/todos");
            System.out.println("サーバーを停止するには Ctrl+C を押してください。");

            // シャットダウンフックの追加
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("\n=== サーバーを停止中 ===");
                server.stop(0);
                DatabaseConnection.getInstance().closeConnection();
                System.out.println("サーバーが停止されました。");
            }));

        } catch (IOException e) {
            System.err.println("サーバー起動エラー: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
