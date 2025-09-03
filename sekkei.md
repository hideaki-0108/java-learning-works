# Java 研修

アカウント作成画面、ログイン画面、ホーム画面を Java を用いて作成
Java はプレーンとし、Java の基本的な処理を学習する
データベースは MySQL を利用する
画面は HTML,CSS,JS を利用し、バリデーションチェックの一部は JS を利用する

## 前提条件

- Java: 未経験
- MySQL: 未経験
- HTML/CSS/JavaScript: 4 年の経験あり

## テーブル設計

- ユーザーテーブル（users）
  | カラム名 | データ型 | 制約 | デフォルト値 | コメント |
  | --- | --- | --- | --- | --- |
  | id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | - | ユーザー ID |
  | username | VARCHAR(50) | NOT NULL, UNIQUE | - | ユーザー名（ログイン ID） |
  | email | VARCHAR(100) | NOT NULL, UNIQUE | - | メールアドレス |
  | password_hash | VARCHAR(255) | NOT NULL | - | パスワードハッシュ |
  | first_name | VARCHAR(50) | NOT NULL | - | 姓 |
  | last_name | VARCHAR(50) | NOT NULL | - | 名 |
  | is_active | BOOLEAN | - | TRUE | アカウント有効フラグ |
  | created_at | TIMESTAMP | - | CURRENT_TIMESTAMP | 作成日時 |
  | updated_at | TIMESTAMP | - | CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | 更新日時 |
  | last_login_at | TIMESTAMP | - | NULL | 最終ログイン日時 |

- ユーザーセッションテーブル（user_sessions）
  | カラム名 | データ型 | 制約 | デフォルト値 | コメント |
  | --- | --- | --- | --- | --- |
  | id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | - | セッション ID |
  | user_id | BIGINT | NOT NULL, FOREIGN KEY | - | ユーザー ID |
  | session_token | VARCHAR(255) | NOT NULL, UNIQUE | - | セッショントークン |
  | expires_at | TIMESTAMP | NOT NULL | - | セッション有効期限 |
  | created_at | TIMESTAMP | - | CURRENT_TIMESTAMP | 作成日時 |
  | updated_at | TIMESTAMP | - | CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | 更新日時 |

## 開発手順書（Java・MySQL 初心者向け）

### 0. 事前学習（1-2 日）

#### 0.1 Java 基礎概念の理解

**JavaScript との違いを理解しよう**

| 概念       | JavaScript                       | Java                                 |
| ---------- | -------------------------------- | ------------------------------------ |
| 型         | 動的型付け (`let name = "John"`) | 静的型付け (`String name = "John";`) |
| 変数宣言   | `let`, `const`, `var`            | `int`, `String`, `boolean` など      |
| クラス     | ES6 以降でサポート               | オブジェクト指向の基本               |
| エラー処理 | `try-catch`                      | `try-catch` + checked exceptions     |
| 実行環境   | ブラウザ・Node.js                | JVM（Java 仮想マシン）               |

**Java の基本文法（JavaScript 経験者向け）**

```java
// JavaScript: let message = "Hello World";
// Java:
String message = "Hello World";

// JavaScript: function greet(name) { return "Hello " + name; }
// Java:
public String greet(String name) {
    return "Hello " + name;
}

// JavaScript: const users = [];
// Java:
List<String> users = new ArrayList<>();
```

#### 0.2 MySQL 基礎概念の理解

**データベースとは**

- データを整理して保存する仕組み
- テーブル（表）形式でデータを管理
- SQL（Structured Query Language）でデータを操作

**基本的な SQL 操作**

```sql
-- データ作成（CREATE）
INSERT INTO users (username, email) VALUES ('john', 'john@example.com');

-- データ読み取り（READ）
SELECT * FROM users WHERE username = 'john';

-- データ更新（UPDATE）
UPDATE users SET email = 'newemail@example.com' WHERE id = 1;

-- データ削除（DELETE）
DELETE FROM users WHERE id = 1;
```

### 1. 開発環境のセットアップ（1 日）

#### 1.1 必要なソフトウェアのインストール

1. **Java Development Kit (JDK) 25.0.0**


    - 既に mise.toml で設定済み
    - `java -version` で確認

2. **Maven 3.9.11**


    - Java のパッケージ管理ツール（npm のようなもの）
    - 既に mise.toml で設定済み
    - `mvn -version` で確認

3. **MySQL 8.0**


    ```bash
    # Windows（PowerShell）
    winget install Oracle.MySQL

    # または公式サイトからダウンロード
    # https://dev.mysql.com/downloads/mysql/
    ```

4. **MySQL Workbench**（GUI 管理ツール）


    - データベースの可視化・管理用
    - 公式サイトからダウンロード

#### 1.2 MySQL の初期設定

```sql
-- 1. MySQLにrootでログイン
mysql -u root -p

-- 2. データベース作成
CREATE DATABASE java_learning_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 3. 専用ユーザー作成（セキュリティのため）
CREATE USER 'java_app'@'localhost' IDENTIFIED BY 'SecurePassword123!';
GRANT ALL PRIVILEGES ON java_learning_db.* TO 'java_app'@'localhost';
FLUSH PRIVILEGES;

-- 4. 作成したデータベースを選択
USE java_learning_db;
```

#### 1.3 プロジェクト構造の理解

```
java-learning-works/
├── src/
│   ├── main/
│   │   ├── java/           ← Javaソースコード
│   │   │   └── com/
│   │   │       └── learning/
│   │   │           ├── Main.java        ← アプリケーション開始点
│   │   │           ├── model/           ← データの形を定義
│   │   │           ├── database/        ← データベース接続
│   │   │           └── server/          ← Webサーバー機能
│   │   └── resources/      ← 設定ファイル・静的ファイル
│   │       └── static/
│   │           ├── html/   ← HTMLファイル（あなたの得意分野）
│   │           ├── css/    ← CSSファイル（あなたの得意分野）
│   │           └── js/     ← JavaScriptファイル（あなたの得意分野）
│   └── test/               ← テストコード
├── pom.xml                 ← 依存関係管理（package.jsonのようなもの）
└── sekkei.md
```

### 2. Java 基礎学習（2-3 日）

#### 2.1 Java プロジェクトの作成

**pom.xml の作成（package.json に相当）**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
        http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.learning</groupId>
    <artifactId>java-learning-app</artifactId>
    <version>1.0.0</version>
    <packaging>jar</packaging>

    <properties>
        <maven.compiler.source>25</maven.compiler.source>
        <maven.compiler.target>25</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>

    <dependencies>
        <!-- MySQL接続用ライブラリ -->
        <dependency>
            <groupId>com.mysql</groupId>
            <artifactId>mysql-connector-j</artifactId>
            <version>8.2.0</version>
        </dependency>

        <!-- パスワード暗号化用ライブラリ -->
        <dependency>
            <groupId>org.mindrot</groupId>
            <artifactId>jbcrypt</artifactId>
            <version>0.4</version>
        </dependency>

        <!-- JSON処理用ライブラリ -->
        <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-databind</artifactId>
            <version>2.16.1</version>
        </dependency>
    </dependencies>
</project>
```

#### 2.2 最初の Java クラス（Hello World）

```java
// src/main/java/com/learning/Main.java
package com.learning;  // フォルダ構造を表す

public class Main {    // クラス名はファイル名と同じにする
    public static void main(String[] args) {  // プログラムの開始点
        System.out.println("Hello, Java World!");  // console.log() に相当
    }
}
```

**実行方法**

```bash
# コンパイル（.javaファイルを.classファイルに変換）
mvn compile

# 実行
mvn exec:java -Dexec.mainClass="com.learning.Main"
```

#### 2.3 データクラスの作成（JavaScript のオブジェクトに相当）

```java
// src/main/java/com/learning/model/User.java
package com.learning.model;

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

    // コンストラクタ（オブジェクト作成時に呼ばれる）
    public User() {
        // 空のコンストラクタ
    }

    public User(String username, String email, String firstName, String lastName) {
        this.username = username;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
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

    // toString メソッド（console.log で表示される内容）
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
```

### 3. データベース操作の基礎（2-3 日）

#### 3.1 データベーステーブルの作成

```sql
-- java_learning_db データベースを選択
USE java_learning_db;

-- ユーザーテーブル作成
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,     -- 自動採番のID
    username VARCHAR(50) NOT NULL UNIQUE,     -- 重複不可のユーザー名
    email VARCHAR(100) NOT NULL UNIQUE,       -- 重複不可のメール
    password_hash VARCHAR(255) NOT NULL,      -- 暗号化されたパスワード
    first_name VARCHAR(50) NOT NULL,          -- 姓
    last_name VARCHAR(50) NOT NULL,           -- 名
    is_active BOOLEAN DEFAULT TRUE,           -- アクティブフラグ
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,  -- 作成日時
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP  -- 更新日時
);

-- テストデータの挿入
INSERT INTO users (username, email, password_hash, first_name, last_name)
VALUES ('testuser', 'test@example.com', 'dummy_hash', 'テスト', 'ユーザー');

-- データの確認
SELECT * FROM users;
```

#### 3.2 Java からデータベースに接続

```java
// src/main/java/com/learning/database/DatabaseConnection.java
package com.learning.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    // データベース接続情報（設定ファイルから読み込むのが理想）
    private static final String URL = "jdbc:mysql://localhost:3306/java_learning_db";
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
            throw e;
        }
    }

    // 接続テスト用のメソッド
    public static void testConnection() {
        try (Connection connection = getConnection()) {
            System.out.println("接続テスト成功！");
        } catch (SQLException e) {
            System.err.println("接続テスト失敗: " + e.getMessage());
        }
    }
}
```

#### 3.3 データベース操作の実装（CRUD 操作）

```java
// src/main/java/com/learning/database/UserDatabase.java
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
                        System.out.println("ユーザー作成成功: " + user);
                        return user;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("ユーザー作成エラー: " + e.getMessage());
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

                    System.out.println("ユーザー検索成功: " + user);
                    return user;
                }
            }
        } catch (SQLException e) {
            System.err.println("ユーザー検索エラー: " + e.getMessage());
        }

        System.out.println("ユーザーが見つかりませんでした: " + username);
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
                User user = new User();
                user.setId(resultSet.getLong("id"));
                user.setUsername(resultSet.getString("username"));
                user.setEmail(resultSet.getString("email"));
                user.setFirstName(resultSet.getString("first_name"));
                user.setLastName(resultSet.getString("last_name"));

                users.add(user);  // JavaScript の push() に相当
            }

            System.out.println("全ユーザー取得成功: " + users.size() + "件");

        } catch (SQLException e) {
            System.err.println("全ユーザー取得エラー: " + e.getMessage());
        }

        return users;
    }
}
```

#### 3.4 データベース操作のテスト

```java
// src/main/java/com/learning/Main.java を更新
package com.learning;

import com.learning.database.DatabaseConnection;
import com.learning.database.UserDatabase;
import com.learning.model.User;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("Java Learning App 開始");

        // 1. データベース接続テスト
        DatabaseConnection.testConnection();

        // 2. ユーザーデータベース操作テスト
        UserDatabase userDb = new UserDatabase();

        // 新しいユーザーを作成
        User newUser = new User("john_doe", "john@example.com", "田中", "太郎");
        newUser.setPasswordHash("temporary_hash");  // 後でパスワード暗号化を実装

        User createdUser = userDb.createUser(newUser);
        if (createdUser != null) {
            System.out.println("作成されたユーザー: " + createdUser);
        }

        // ユーザーを検索
        User foundUser = userDb.findUserByUsername("john_doe");
        if (foundUser != null) {
            System.out.println("検索されたユーザー: " + foundUser);
        }

        // 全ユーザーを取得
        List<User> allUsers = userDb.getAllUsers();
        System.out.println("全ユーザー数: " + allUsers.size());
        for (User user : allUsers) {  // JavaScript の for...of に相当
            System.out.println("- " + user);
        }
    }
}
```

### 4. Web サーバーの基礎（2-3 日）

#### 4.1 シンプルな HTTP サーバーの作成

```java
// src/main/java/com/learning/server/SimpleServer.java
package com.learning.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public class SimpleServer {
    private static final int PORT = 8080;

    public static void main(String[] args) throws IOException {
        // HTTPサーバーを作成（Express.js のようなもの）
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

        // ルート設定（Express の app.get() に相当）
        server.createContext("/", new HelloHandler());
        server.createContext("/api/test", new ApiTestHandler());

        // サーバー開始
        server.setExecutor(null);
        server.start();

        System.out.println("サーバーが起動しました: http://localhost:" + PORT);
        System.out.println("停止するには Ctrl+C を押してください");
    }

    // ルートページのハンドラー
    static class HelloHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response = """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <title>Java Learning App</title>
                </head>
                <body>
                    <h1>Java Learning App へようこそ！</h1>
                    <p>JavaでWebサーバーが動いています</p>
                    <a href="/api/test">APIテスト</a>
                </body>
                </html>
                """;

            // レスポンスヘッダー設定
            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
            exchange.sendResponseHeaders(200, response.getBytes(StandardCharsets.UTF_8).length);

            // レスポンス送信
            try (OutputStream outputStream = exchange.getResponseBody()) {
                outputStream.write(response.getBytes(StandardCharsets.UTF_8));
            }
        }
    }

    // API テストハンドラー
    static class ApiTestHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // JSON レスポンス（fetch() で受け取れる形式）
            String jsonResponse = """
                {
                    "message": "API テスト成功！",
                    "timestamp": "%s",
                    "status": "ok"
                }
                """.formatted(java.time.LocalDateTime.now());

            exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");  // CORS対応
            exchange.sendResponseHeaders(200, jsonResponse.getBytes(StandardCharsets.UTF_8).length);

            try (OutputStream outputStream = exchange.getResponseBody()) {
                outputStream.write(jsonResponse.getBytes(StandardCharsets.UTF_8));
            }
        }
    }
}
```

#### 4.2 静的ファイル配信の実装

```java
// src/main/java/com/learning/server/StaticFileServer.java
package com.learning.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class StaticFileServer implements HttpHandler {
    private static final String STATIC_DIR = "src/main/resources/static";

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String requestPath = exchange.getRequestURI().getPath();

        // デフォルトページの設定
        if ("/".equals(requestPath)) {
            requestPath = "/html/index.html";
        }

        // ファイルパスの構築
        Path filePath = Paths.get(STATIC_DIR + requestPath);

        if (Files.exists(filePath) && !Files.isDirectory(filePath)) {
            // ファイルが存在する場合
            String contentType = getContentType(requestPath);
            byte[] fileContent = Files.readAllBytes(filePath);

            exchange.getResponseHeaders().set("Content-Type", contentType);
            exchange.sendResponseHeaders(200, fileContent.length);

            try (OutputStream outputStream = exchange.getResponseBody()) {
                outputStream.write(fileContent);
            }
        } else {
            // ファイルが存在しない場合（404エラー）
            String notFoundResponse = "404 - File Not Found";
            exchange.sendResponseHeaders(404, notFoundResponse.length());

            try (OutputStream outputStream = exchange.getResponseBody()) {
                outputStream.write(notFoundResponse.getBytes());
            }
        }
    }

    private String getContentType(String path) {
        if (path.endsWith(".html")) return "text/html; charset=UTF-8";
        if (path.endsWith(".css")) return "text/css; charset=UTF-8";
        if (path.endsWith(".js")) return "application/javascript; charset=UTF-8";
        if (path.endsWith(".json")) return "application/json; charset=UTF-8";
        return "text/plain; charset=UTF-8";
    }
}
```

### 5. フロントエンド統合（1-2 日）

#### 5.1 基本的な HTML ページの作成

```html
<!-- src/main/resources/static/html/index.html -->
<!DOCTYPE html>
<html lang="ja">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Java Learning App</title>
    <link rel="stylesheet" href="/css/styles.css" />
  </head>
  <body>
    <div class="container">
      <h1>Java Learning App</h1>

      <div class="auth-section">
        <h2>ログイン</h2>
        <form id="loginForm">
          <input type="text" id="username" placeholder="ユーザー名" required />
          <input
            type="password"
            id="password"
            placeholder="パスワード"
            required
          />
          <button type="submit">ログイン</button>
        </form>
      </div>

      <div class="test-section">
        <h2>API テスト</h2>
        <button id="testApiBtn">API テスト実行</button>
        <div id="apiResult"></div>
      </div>

      <div class="users-section">
        <h2>ユーザー一覧</h2>
        <button id="loadUsersBtn">ユーザー読み込み</button>
        <div id="usersList"></div>
      </div>
    </div>

    <script src="/js/app.js"></script>
  </body>
</html>
```

#### 5.2 JavaScript で API を呼び出し

```javascript
// src/main/resources/static/js/app.js
// あなたの得意分野！JavaからAPIを呼び出すJavaScript

document.addEventListener("DOMContentLoaded", function () {
  console.log("Java Learning App フロントエンド開始");

  // API テストボタンのイベントリスナー
  document.getElementById("testApiBtn").addEventListener("click", testApi);

  // ユーザー読み込みボタンのイベントリスナー
  document.getElementById("loadUsersBtn").addEventListener("click", loadUsers);

  // ログインフォームのイベントリスナー
  document.getElementById("loginForm").addEventListener("submit", handleLogin);
});

// API テスト関数
async function testApi() {
  try {
    console.log("API テスト開始...");

    const response = await fetch("/api/test");
    const data = await response.json();

    document.getElementById("apiResult").innerHTML = `
            <h3>API レスポンス:</h3>
            <pre>${JSON.stringify(data, null, 2)}</pre>
        `;

    console.log("API テスト成功:", data);
  } catch (error) {
    console.error("API テストエラー:", error);
    document.getElementById("apiResult").innerHTML = `
            <p style="color: red;">エラー: ${error.message}</p>
        `;
  }
}

// ユーザー一覧読み込み関数
async function loadUsers() {
  try {
    console.log("ユーザー一覧読み込み開始...");

    const response = await fetch("/api/users");
    const users = await response.json();

    const usersList = document.getElementById("usersList");
    usersList.innerHTML = "<h3>ユーザー一覧:</h3>";

    if (users.length === 0) {
      usersList.innerHTML += "<p>ユーザーが見つかりませんでした。</p>";
    } else {
      const ul = document.createElement("ul");
      users.forEach((user) => {
        const li = document.createElement("li");
        li.textContent = `${user.username} (${user.email})`;
        ul.appendChild(li);
      });
      usersList.appendChild(ul);
    }

    console.log("ユーザー一覧読み込み成功:", users);
  } catch (error) {
    console.error("ユーザー一覧読み込みエラー:", error);
    document.getElementById("usersList").innerHTML = `
            <p style="color: red;">エラー: ${error.message}</p>
        `;
  }
}

// ログイン処理関数
async function handleLogin(event) {
  event.preventDefault();

  const username = document.getElementById("username").value;
  const password = document.getElementById("password").value;

  try {
    console.log("ログイン試行:", username);

    const response = await fetch("/api/auth/login", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        username: username,
        password: password,
      }),
    });

    const result = await response.json();

    if (response.ok) {
      alert("ログイン成功！");
      console.log("ログイン成功:", result);
    } else {
      alert("ログイン失敗: " + result.message);
      console.error("ログイン失敗:", result);
    }
  } catch (error) {
    console.error("ログインエラー:", error);
    alert("ログインエラー: " + error.message);
  }
}
```

### 6. 学習の進め方とチェックポイント

#### 6.1 段階的学習スケジュール（合計 10-14 日）

**Week 1: Java 基礎**

- Day 1-2: Java 基礎概念と MySQL 環境構築
- Day 3-4: Java クラス作成とデータベース接続
- Day 5-7: CRUD 操作の実装と理解

**Week 2: Web 開発**

- Day 8-10: HTTP サーバーの実装
- Day 11-12: フロントエンドとの連携
- Day 13-14: 認証機能の実装と総合テスト

#### 6.2 各段階でのチェックポイント

**Java 基礎チェック**

- [ ] Hello World が実行できる
- [ ] User クラスが作成できる
- [ ] データベースに接続できる
- [ ] 基本的な CRUD 操作ができる

**Web 開発チェック**

- [ ] HTTP サーバーが起動できる
- [ ] ブラウザでページが表示される
- [ ] JavaScript から API を呼び出せる
- [ ] JSON データの送受信ができる

**総合チェック**

- [ ] ユーザー登録機能が動作する
- [ ] ログイン機能が動作する
- [ ] セッション管理ができる
- [ ] エラーハンドリングが適切に動作する

### 7. よくある問題と解決方法

#### 7.1 Java 初心者がつまづきやすい点

**1. コンパイルエラー**

```java
// ❌ よくある間違い
public class user {  // クラス名は大文字で始める
    String name;
    public void getName() {
        return name;  // 戻り値の型が違う
    }
}

// ✅ 正しい書き方
public class User {
    private String name;
    public String getName() {
        return name;
    }
}
```

**2. NullPointerException**

```java
// ❌ よくある間違い
String name = null;
int length = name.length();  // エラー！

// ✅ 正しい書き方
String name = null;
if (name != null) {
    int length = name.length();
}
```

#### 7.2 MySQL 初心者がつまづきやすい点

**1. 文字化け**

```sql
-- データベース作成時に文字セットを指定
CREATE DATABASE java_learning_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

**2. 接続エラー**

```java
// 接続URLに適切なパラメータを追加
String url = "jdbc:mysql://localhost:3306/java_learning_db?useSSL=false&serverTimezone=UTC";
```

### 8. 次のステップ（発展学習）

この基礎研修を完了した後の学習パス：

1. **Spring Boot フレームワーク**


    - より実践的な Web アプリケーション開発
    - 依存性注入とアノテーション

2. **セキュリティ強化**


    - JWT 認証
    - HTTPS 対応
    - セキュリティヘッダー

3. **テスト駆動開発**


    - JUnit を使用した単体テスト
    - 統合テスト

4. **デプロイメント**


    - Docker コンテナ化
    - クラウドデプロイ

この手順書に従って学習を進めることで、Java・MySQL 未経験からでも実践的な Web アプリケーション開発ができるようになります。フロントエンドの知識を活かしながら、バックエンド開発のスキルを身につけていきましょう！
