package com.learning;  // フォルダ構造を表す

import com.learning.model.User;
import com.learning.database.DatabaseConnection;
import com.learning.database.UserDatabase;
import com.learning.server.SimpleHttpServer;
import java.util.List;
import java.util.Scanner;

public class Main {    // クラス名はファイル名と同じにする
    public static void main(String[] args) {  // プログラムの開始点
        System.out.println("Hello, Java World!");  // console.log() に相当
        System.out.println("Java Learning App 開始");
        System.out.println("プロジェクトセットアップ完了！");
        
        // Userクラスのテスト
        System.out.println("\n=== Userクラスのテスト ===");
        
        // 新しいユーザーを作成
        User user1 = new User("john_doe", "john@example.com", "田中", "太郎");
        user1.setPasswordHash("dummy_hash_for_test");
        
        System.out.println("作成されたユーザー: " + user1);
        System.out.println("フルネーム: " + user1.getFullName());
        System.out.println("アクティブ状態: " + user1.isActive());
        
        // 空のコンストラクタでユーザー作成
        User user2 = new User();
        user2.setUsername("jane_smith");
        user2.setEmail("jane@example.com");
        user2.setFirstName("山田");
        user2.setLastName("花子");
        
        System.out.println("2番目のユーザー: " + user2);
        System.out.println("フルネーム: " + user2.getFullName());
        
        // データベース接続テスト
        System.out.println("\n=== データベース接続テスト ===");
        DatabaseConnection.showConnectionInfo();
        DatabaseConnection.testConnection();
        
        // CRUD操作のテスト
        System.out.println("\n=== CRUD操作のテスト ===");
        UserDatabase userDb = new UserDatabase();
        
        // 1. CREATE - 新しいユーザーを作成
        System.out.println("\n--- CREATE操作 ---");
        User newUser = new User("java_learner", "learner@example.com", "学習", "太郎");
        newUser.setPasswordHash("hashed_password_123");
        
        User createdUser = userDb.createUser(newUser);
        if (createdUser != null) {
            System.out.println("データベースに保存されたユーザー: " + createdUser);
        }
        
        // 2. READ - ユーザーを検索
        System.out.println("\n--- READ操作 ---");
        User foundUser = userDb.findUserByUsername("testuser");  // 事前に挿入したテストユーザー
        if (foundUser != null) {
            System.out.println("検索されたユーザー: " + foundUser);
        }
        
        // 新しく作成したユーザーも検索
        if (createdUser != null) {
            User foundNewUser = userDb.findUserByUsername("java_learner");
            if (foundNewUser != null) {
                System.out.println("新規作成ユーザーの検索結果: " + foundNewUser);
            }
        }
        
        // 3. 全ユーザーを取得
        System.out.println("\n--- 全ユーザー取得 ---");
        List<User> allUsers = userDb.getAllUsers();
        System.out.println("全ユーザー数: " + allUsers.size());
        for (User user : allUsers) {  // JavaScript の for...of に相当
            System.out.println("- " + user.getUsername() + " (" + user.getFullName() + ")");
        }
        
        // 4. UPDATE - ユーザー情報を更新
        System.out.println("\n--- UPDATE操作 ---");
        if (createdUser != null) {
            createdUser.setEmail("updated_learner@example.com");
            createdUser.setFirstName("更新");
            boolean updateResult = userDb.updateUser(createdUser);
            System.out.println("更新結果: " + (updateResult ? "成功" : "失敗"));
            
            // 更新後のデータを確認
            User updatedUser = userDb.findUserById(createdUser.getId());
            if (updatedUser != null) {
                System.out.println("更新後のユーザー: " + updatedUser);
            }
        }
        
        // 5. 最終ログイン日時の更新
        System.out.println("\n--- 最終ログイン日時更新 ---");
        if (createdUser != null) {
            userDb.updateLastLogin(createdUser.getId());
            
            // 更新後のデータを確認
            User loginUpdatedUser = userDb.findUserById(createdUser.getId());
            if (loginUpdatedUser != null) {
                System.out.println("最終ログイン更新後: " + loginUpdatedUser);
            }
        }
        
        System.out.println("\n🎉 CRUD操作のテストが完了しました！");
        
        // HTTPサーバーの起動
        System.out.println("\n=== HTTPサーバーの起動 ===");
        try {
            SimpleHttpServer server = new SimpleHttpServer();
            server.start();
            
            // サーバーが起動したらユーザーの入力を待つ
            System.out.println("\n📱 ブラウザで http://localhost:8080 にアクセスしてください");
            System.out.println("🛑 サーバーを停止するには 'quit' と入力してください");
            
            Scanner scanner = new Scanner(System.in);
            String input;
            do {
                System.out.print("> ");
                input = scanner.nextLine().trim().toLowerCase();
                
                if ("help".equals(input)) {
                    System.out.println("📋 利用可能なコマンド:");
                    System.out.println("  - help: このヘルプを表示");
                    System.out.println("  - status: サーバー状態を表示");
                    System.out.println("  - users: データベース内のユーザー数を表示");
                    System.out.println("  - quit: サーバーを停止して終了");
                } else if ("status".equals(input)) {
                    System.out.println("✅ サーバー稼働中 - http://localhost:8080");
                } else if ("users".equals(input)) {
                    List<User> users = userDb.getAllUsers();
                    System.out.println("👥 データベース内のユーザー数: " + users.size());
                }
                
            } while (!"quit".equals(input));
            
            server.stop();
            scanner.close();
            
        } catch (Exception e) {
            System.err.println("❌ サーバー起動エラー: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
