package com.learning.database;

import com.learning.model.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDatabase {
    
    // ユーザーを作成（CREATE）
    public User createUser(User user) {
        String sql = "INSERT INTO users (username, email, password_hash, first_name, last_name) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            // ?の部分にデータを設定（SQLインジェクション対策）
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getEmail());
            statement.setString(3, user.getPasswordHash());
            statement.setString(4, user.getFirstName());
            statement.setString(5, user.getLastName());
            
            // SQL実行
            int affectedRows = statement.executeUpdate();
            
            if (affectedRows > 0) {
                // 自動生成されたIDを取得
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        user.setId(generatedKeys.getLong(1));
                        System.out.println("✅ ユーザー作成成功: " + user);
                        return user;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ ユーザー作成エラー: " + e.getMessage());
        }
        return null;
    }
    
    // ユーザーを検索（READ）
    public User findUserByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setString(1, username);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    // データベースの行をUserオブジェクトに変換
                    User user = new User();
                    user.setId(resultSet.getLong("id"));
                    user.setUsername(resultSet.getString("username"));
                    user.setEmail(resultSet.getString("email"));
                    user.setPasswordHash(resultSet.getString("password_hash"));
                    user.setFirstName(resultSet.getString("first_name"));
                    user.setLastName(resultSet.getString("last_name"));
                    user.setActive(resultSet.getBoolean("is_active"));
                    
                    // 日時データの処理
                    Timestamp createdAt = resultSet.getTimestamp("created_at");
                    if (createdAt != null) {
                        user.setCreatedAt(createdAt.toLocalDateTime());
                    }
                    
                    Timestamp updatedAt = resultSet.getTimestamp("updated_at");
                    if (updatedAt != null) {
                        user.setUpdatedAt(updatedAt.toLocalDateTime());
                    }
                    
                    Timestamp lastLoginAt = resultSet.getTimestamp("last_login_at");
                    if (lastLoginAt != null) {
                        user.setLastLoginAt(lastLoginAt.toLocalDateTime());
                    }
                    
                    System.out.println("✅ ユーザー検索成功: " + user);
                    return user;
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ ユーザー検索エラー: " + e.getMessage());
        }
        
        System.out.println("⚠️ ユーザーが見つかりませんでした: " + username);
        return null;
    }
    
    // IDでユーザーを検索
    public User findUserById(Long id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setLong(1, id);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    User user = mapResultSetToUser(resultSet);
                    System.out.println("✅ ユーザー検索成功 (ID: " + id + "): " + user);
                    return user;
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ ユーザー検索エラー (ID: " + id + "): " + e.getMessage());
        }
        
        System.out.println("⚠️ ユーザーが見つかりませんでした (ID: " + id + ")");
        return null;
    }
    
    // 全ユーザーを取得
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();  // JavaScript の [] に相当
        String sql = "SELECT * FROM users ORDER BY created_at DESC";
        
        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            
            while (resultSet.next()) {
                User user = mapResultSetToUser(resultSet);
                users.add(user);  // JavaScript の push() に相当
            }
            
            System.out.println("✅ 全ユーザー取得成功: " + users.size() + "件");
            
        } catch (SQLException e) {
            System.err.println("❌ 全ユーザー取得エラー: " + e.getMessage());
        }
        
        return users;
    }
    
    // ユーザー情報を更新（UPDATE）
    public boolean updateUser(User user) {
        String sql = "UPDATE users SET email = ?, first_name = ?, last_name = ?, is_active = ? WHERE id = ?";
        
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setString(1, user.getEmail());
            statement.setString(2, user.getFirstName());
            statement.setString(3, user.getLastName());
            statement.setBoolean(4, user.isActive());
            statement.setLong(5, user.getId());
            
            int affectedRows = statement.executeUpdate();
            
            if (affectedRows > 0) {
                System.out.println("✅ ユーザー更新成功: " + user);
                return true;
            } else {
                System.out.println("⚠️ 更新対象のユーザーが見つかりませんでした (ID: " + user.getId() + ")");
                return false;
            }
            
        } catch (SQLException e) {
            System.err.println("❌ ユーザー更新エラー: " + e.getMessage());
            return false;
        }
    }
    
    // 最終ログイン日時を更新
    public void updateLastLogin(Long userId) {
        String sql = "UPDATE users SET last_login_at = CURRENT_TIMESTAMP WHERE id = ?";
        
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setLong(1, userId);
            
            int affectedRows = statement.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("✅ 最終ログイン日時更新成功 (ID: " + userId + ")");
            } else {
                System.out.println("⚠️ 更新対象のユーザーが見つかりませんでした (ID: " + userId + ")");
            }
        } catch (SQLException e) {
            System.err.println("❌ 最終ログイン日時更新エラー: " + e.getMessage());
        }
    }
    
    // ユーザーを削除（DELETE）
    public boolean deleteUser(Long userId) {
        String sql = "DELETE FROM users WHERE id = ?";
        
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setLong(1, userId);
            
            int affectedRows = statement.executeUpdate();
            
            if (affectedRows > 0) {
                System.out.println("✅ ユーザー削除成功 (ID: " + userId + ")");
                return true;
            } else {
                System.out.println("⚠️ 削除対象のユーザーが見つかりませんでした (ID: " + userId + ")");
                return false;
            }
            
        } catch (SQLException e) {
            System.err.println("❌ ユーザー削除エラー: " + e.getMessage());
            return false;
        }
    }
    
    // ResultSetからUserオブジェクトへの変換（共通処理）
    private User mapResultSetToUser(ResultSet resultSet) throws SQLException {
        User user = new User();
        user.setId(resultSet.getLong("id"));
        user.setUsername(resultSet.getString("username"));
        user.setEmail(resultSet.getString("email"));
        user.setPasswordHash(resultSet.getString("password_hash"));
        user.setFirstName(resultSet.getString("first_name"));
        user.setLastName(resultSet.getString("last_name"));
        user.setActive(resultSet.getBoolean("is_active"));
        
        // 日時データの処理
        Timestamp createdAt = resultSet.getTimestamp("created_at");
        if (createdAt != null) {
            user.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        Timestamp updatedAt = resultSet.getTimestamp("updated_at");
        if (updatedAt != null) {
            user.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        
        Timestamp lastLoginAt = resultSet.getTimestamp("last_login_at");
        if (lastLoginAt != null) {
            user.setLastLoginAt(lastLoginAt.toLocalDateTime());
        }
        
        return user;
    }
}
