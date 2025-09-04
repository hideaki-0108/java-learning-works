package com.learning.database;

import com.learning.model.Todo;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Todo データアクセスオブジェクト（DAO）
 * データベースでのTodoのCRUD操作を担当
 */
public class TodoDAO {
    private final DatabaseConnection dbConnection;

    public TodoDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }

    /**
     * 全てのTodoを取得する
     * @return Todoリスト
     */
    public List<Todo> getAllTodos() {
        List<Todo> todos = new ArrayList<>();
        String sql = "SELECT * FROM todos ORDER BY created_at DESC";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Todo todo = createTodoFromResultSet(rs);
                todos.add(todo);
            }

        } catch (SQLException e) {
            System.err.println("Todo取得エラー: " + e.getMessage());
        }

        return todos;
    }

    /**
     * IDでTodoを取得する
     * @param id TodoのID
     * @return Todo、見つからない場合はnull
     */
    public Todo getTodoById(int id) {
        String sql = "SELECT * FROM todos WHERE id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return createTodoFromResultSet(rs);
            }

        } catch (SQLException e) {
            System.err.println("Todo取得エラー: " + e.getMessage());
        }

        return null;
    }

    /**
     * 新しいTodoを作成する
     * @param todo 作成するTodo
     * @return 作成されたTodoのID、失敗時は-1
     */
    public int createTodo(Todo todo) {
        String sql = "INSERT INTO todos (title, description, completed) VALUES (?, ?, ?)";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, todo.getTitle());
            stmt.setString(2, todo.getDescription());
            stmt.setBoolean(3, todo.isCompleted());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }

        } catch (SQLException e) {
            System.err.println("Todo作成エラー: " + e.getMessage());
        }

        return -1;
    }

    /**
     * Todoを更新する
     * @param todo 更新するTodo
     * @return 更新成功時はtrue、失敗時はfalse
     */
    public boolean updateTodo(Todo todo) {
        String sql = "UPDATE todos SET title = ?, description = ?, completed = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, todo.getTitle());
            stmt.setString(2, todo.getDescription());
            stmt.setBoolean(3, todo.isCompleted());
            stmt.setInt(4, todo.getId());

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Todo更新エラー: " + e.getMessage());
        }

        return false;
    }

    /**
     * Todoを削除する
     * @param id 削除するTodoのID
     * @return 削除成功時はtrue、失敗時はfalse
     */
    public boolean deleteTodo(int id) {
        String sql = "DELETE FROM todos WHERE id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Todo削除エラー: " + e.getMessage());
        }

        return false;
    }

    /**
     * ResultSetからTodoオブジェクトを作成する
     * @param rs ResultSet
     * @return Todo
     * @throws SQLException SQL例外
     */
    private Todo createTodoFromResultSet(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String title = rs.getString("title");
        String description = rs.getString("description");
        boolean completed = rs.getBoolean("completed");
        
        // TimestampをLocalDateTimeに変換
        Timestamp createdAtTimestamp = rs.getTimestamp("created_at");
        Timestamp updatedAtTimestamp = rs.getTimestamp("updated_at");
        
        LocalDateTime createdAt = createdAtTimestamp != null ? createdAtTimestamp.toLocalDateTime() : null;
        LocalDateTime updatedAt = updatedAtTimestamp != null ? updatedAtTimestamp.toLocalDateTime() : null;

        return new Todo(id, title, description, completed, createdAt, updatedAt);
    }
}
