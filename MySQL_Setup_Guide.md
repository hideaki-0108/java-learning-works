# MySQL セットアップガイド

## 🎯 目標

Java Learning App 用の MySQL データベース環境を構築する

## 📋 必要な作業

1. MySQL Community Server のインストール
2. データベース `java_learning_db` の作成
3. ユーザー `java_app` の作成と権限設定
4. テーブルの作成
5. 接続テスト

---

## 1️⃣ MySQL Community Server のダウンロードとインストール

### ダウンロード

1. 公式サイトにアクセス: https://dev.mysql.com/downloads/mysql/
2. **Windows (x86, 64-bit), MSI Installer** を選択
3. **「No thanks, just start my download.」** をクリック

### インストール手順

1. **ダウンロードした MSI ファイルを実行**（管理者として実行推奨）

2. **Setup Type**: **「Developer Default」** を選択

   - MySQL Server 8.x
   - MySQL Workbench
   - MySQL Shell
   - Connector/J (Java 用)
   - サンプルデータベース

3. **Check Requirements**: 不足している依存関係は自動インストール

4. **Installation**: 「Execute」をクリック

5. **Product Configuration**:

   - **Config Type**: Development Computer
   - **Port**: 3306 (デフォルト)
   - **X Protocol Port**: 33060 (デフォルト)
   - **Authentication Method**: Use Strong Password Encryption

6. **Accounts and Roles**:

   - **Root Password**: `SecureRootPassword123!`
   - ⚠️ **重要**: パスワードを忘れずにメモしてください！

7. **Windows Service**:

   - Service Name: MySQL84
   - ✅ Start the MySQL Server at System Startup
   - ✅ Run Windows Service as Standard System Account

8. **Apply Configuration**: 「Execute」をクリック

---

## 2️⃣ データベースとユーザーの作成

### MySQL Workbench を使用（推奨）

1. **MySQL Workbench を起動**
2. **ローカル接続を作成**:

   - Connection Name: `Local instance MySQL84`
   - Hostname: `127.0.0.1`
   - Port: `3306`
   - Username: `root`
   - Password: [インストール時に設定したパスワード]

3. **以下の SQL を順番に実行**:

```sql
-- 1. データベース作成
CREATE DATABASE java_learning_db
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

-- 2. 専用ユーザー作成
CREATE USER 'java_app'@'localhost'
IDENTIFIED BY 'SecurePassword123!';

-- 3. 権限付与
GRANT ALL PRIVILEGES ON java_learning_db.*
TO 'java_app'@'localhost';
FLUSH PRIVILEGES;

-- 4. データベース選択
USE java_learning_db;

-- 5. ユーザーテーブル作成
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    last_login_at TIMESTAMP NULL,
    INDEX idx_username (username),
    INDEX idx_email (email)
);

-- 6. ユーザーセッションテーブル作成
CREATE TABLE user_sessions (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    session_token VARCHAR(255) NOT NULL UNIQUE,
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_session_token (session_token),
    INDEX idx_user_id (user_id),
    INDEX idx_expires_at (expires_at)
);

-- 7. テストデータ挿入
INSERT INTO users (username, email, password_hash, first_name, last_name)
VALUES ('testuser', 'test@example.com', 'dummy_hash', 'テスト', 'ユーザー');

-- 8. 確認
SELECT * FROM users;
SELECT * FROM user_sessions;
```

---

## 3️⃣ 接続テスト

### Java アプリケーションでのテスト

```bash
cd C:\Users\友利秀旭\Documents\git\java-learning-works
mvn exec:java
```

### 成功時の表示例

```
=== データベース接続テスト ===
データベース接続テスト開始...
データベース接続成功！
✅ 接続テスト成功！データベースに正常に接続できました
接続URL: jdbc:mysql://localhost:3306/java_learning_db
データベース名: java_learning_db
```

---

## 🔧 トラブルシューティング

### よくある問題と解決方法

#### 1. 「Communications link failure」エラー

**原因**: MySQL サーバーが起動していない
**解決方法**:

- Windows + R → `services.msc` → MySQL84 サービスを開始
- または、コマンドプロンプト（管理者）で: `net start MySQL84`

#### 2. 「Access denied for user」エラー

**原因**: ユーザーまたはパスワードが間違っている
**解決方法**:

- MySQL Workbench で root ユーザーでログイン
- ユーザー作成 SQL を再実行

#### 3. 「Unknown database 'java_learning_db'」エラー

**原因**: データベースが作成されていない
**解決方法**:

- データベース作成 SQL を実行

#### 4. ポート 3306 が使用中

**原因**: 他のアプリケーションがポートを使用
**解決方法**:

- `netstat -an | findstr 3306` でポート使用状況を確認
- 必要に応じて他のアプリケーションを停止

---

## ✅ チェックリスト

インストールと設定が完了したら、以下を確認してください：

- [ ] MySQL Community Server がインストールされている
- [ ] MySQL Workbench が起動できる
- [ ] root ユーザーで MySQL に接続できる
- [ ] データベース `java_learning_db` が存在する
- [ ] ユーザー `java_app` が存在し、適切な権限を持っている
- [ ] `users` テーブルが作成されている
- [ ] `user_sessions` テーブルが作成されている
- [ ] テストデータが挿入されている
- [ ] Java アプリケーションから接続できる

---

## 📞 サポート

設定でわからないことがあれば、以下を確認してください：

1. エラーメッセージの内容
2. MySQL Workbench での接続状況
3. Windows サービスでの MySQL84 の状態

すべて完了したら、次のステップ（CRUD 操作の実装）に進むことができます！
