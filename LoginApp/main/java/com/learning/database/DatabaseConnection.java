package com.learning.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    // データベース接続情報（設定ファイルから読み込むのが理想）
    private static final String URL = "jdbc:mysql://localhost:3306/java_learning_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true&useUnicode=true&characterEncoding=UTF-8";
    private static final String USERNAME = "java_app";
    private static final String PASSWORD = "SecurePassword123!";
    
    // データベースに接続するメソッド
    public static Connection getConnection() throws SQLException {
        
        try {
            Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            System.out.println("データベース接続成功！");
            return connection;
        } catch (SQLException e) {
            System.err.println("データベース接続エラー: " + e.getMessage());
            System.err.println("MySQLサーバーが起動しているか確認してください");
            System.err.println("データベース 'java_learning_db' とユーザー 'java_app' が存在するか確認してください");
            throw e;
        }
    }
    
    // 接続テスト用のメソッド
    public static void testConnection() {
        System.out.println("データベース接続テスト開始...");
        try (Connection connection = getConnection()) {
            if (connection != null && !connection.isClosed()) {
                System.out.println("✅ 接続テスト成功！データベースに正常に接続できました");
                System.out.println("接続URL: " + connection.getMetaData().getURL());
                System.out.println("データベース名: " + connection.getCatalog());
            }
        } catch (SQLException e) {
            System.err.println("❌ 接続テスト失敗: " + e.getMessage());
            System.err.println("\n=== トラブルシューティング ===");
            System.err.println("1. MySQLサーバーが起動しているか確認");
            System.err.println("2. 以下のSQLを実行してデータベースとユーザーを作成:");
            System.err.println("   CREATE DATABASE java_learning_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;");
            System.err.println("   CREATE USER 'java_app'@'localhost' IDENTIFIED BY 'SecurePassword123!';");
            System.err.println("   GRANT ALL PRIVILEGES ON java_learning_db.* TO 'java_app'@'localhost';");
            System.err.println("   FLUSH PRIVILEGES;");
        }
    }
    
    // データベース設定情報を表示するメソッド
    public static void showConnectionInfo() {
        System.out.println("=== データベース設定情報 ===");
        System.out.println("URL: " + URL);
        System.out.println("ユーザー名: " + USERNAME);
        System.out.println("パスワード: [セキュリティのため非表示]");
    }
}
