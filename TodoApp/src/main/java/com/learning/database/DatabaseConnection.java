package com.learning.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * データベース接続を管理するクラス
 */
public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3307/todoapp?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Tokyo";
    private static final String USERNAME = "todouser";
    private static final String PASSWORD = "todopassword";
    
    private static DatabaseConnection instance;
    private Connection connection;

    // プライベートコンストラクタ（Singletonパターン）
    private DatabaseConnection() {
        try {
            // MySQL JDBCドライバの読み込み
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            System.out.println("データベースに正常に接続されました。");
        } catch (ClassNotFoundException e) {
            System.err.println("JDBCドライバが見つかりません: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("データベース接続エラー: " + e.getMessage());
        }
    }

    /**
     * DatabaseConnectionのインスタンスを取得する（Singletonパターン）
     * @return DatabaseConnectionのインスタンス
     */
    public static DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    /**
     * データベース接続を取得する
     * @return データベース接続
     */
    public Connection getConnection() {
        try {
            // 接続が閉じられている場合は再接続
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            }
        } catch (SQLException e) {
            System.err.println("データベース再接続エラー: " + e.getMessage());
        }
        return connection;
    }

    /**
     * データベース接続を閉じる
     */
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("データベース接続が閉じられました。");
            }
        } catch (SQLException e) {
            System.err.println("データベース接続クローズエラー: " + e.getMessage());
        }
    }
}
