package com.learning.model;

import java.time.LocalDateTime;

// JavaScript: const user = { id: 1, name: "John" };
// Java: クラスとして定義
public class User {
    // プロパティ（JavaScriptのオブジェクトプロパティに相当）
    private Long id;           // private = 外部から直接アクセス不可
    private String username;
    private String email;
    private String passwordHash;
    private String firstName;
    private String lastName;
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastLoginAt;
    
    // コンストラクタ（オブジェクト作成時に呼ばれる）
    public User() {
        // 空のコンストラクタ
        this.isActive = true;  // デフォルトでアクティブ
    }
    
    public User(String username, String email, String firstName, String lastName) {
        this.username = username;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.isActive = true;
    }
    
    // ゲッター（プロパティを取得するメソッド）
    public Long getId() {
        return id;
    }
    
    public String getUsername() {
        return username;
    }
    
    public String getEmail() {
        return email;
    }
    
    public String getPasswordHash() {
        return passwordHash;
    }
    
    public String getFirstName() {
        return firstName;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public LocalDateTime getLastLoginAt() {
        return lastLoginAt;
    }
    
    // セッター（プロパティを設定するメソッド）
    public void setId(Long id) {
        this.id = id;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public void setActive(boolean active) {
        isActive = active;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public void setLastLoginAt(LocalDateTime lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
    }
    
    // toString メソッド（console.log で表示される内容）
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", isActive=" + isActive +
                ", createdAt=" + createdAt +
                ", lastLoginAt=" + lastLoginAt +
                '}';
    }
    
    // フルネームを取得するメソッド（便利メソッド）
    public String getFullName() {
        return lastName + " " + firstName;
    }
}
