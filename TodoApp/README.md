# Todo App - Java 研修

Notion ライクなシンプルでモノトーンなデザインの Todo アプリを Java + Docker/MySQL + React19 + Geist UI を用いて作成。

## 技術スタック

- **バックエンド**: Java (プレーン) - 基本的な HTTP サーバーと CRUD API
- **データベース**: MySQL (Docker) - タスクデータの永続化
- **フロントエンド**: React 19 + Geist UI - Notion 風のモダンな UI
- **デザイン**: Vercel Geist UI - シンプルなモノトーン・タイポグラフィー

## 前提条件

- Java: 未経験
- Docker / MySQL: 未経験
- HTML/CSS/JavaScript: 4 年の経験あり
- React： 2 年の経験あり

## セットアップ手順

### 1. データベース起動

```bash
# 既存のMySQLコンテナを停止（必要に応じて）
docker-compose down

# MySQLコンテナを起動（ポート3307を使用）
docker-compose up -d
```

**注意**: MySQL はポート 3307 で起動します（標準の 3306 が使用中の場合）

### 2. フロントエンドビルド（開発用）

```bash
cd frontend
npm install
npm run dev  # 開発サーバー起動 (http://localhost:3000)
```

### 3. フロントエンドビルド（本番用）

```bash
cd frontend
npm install
npm run build  # 静的ファイルを src/main/resources/static に出力
```

### 4. Java アプリケーション起動

```bash
mvn clean compile
mvn exec:java -Dexec.mainClass="com.learning.Main"
```

### 5. アクセス

- アプリケーション: http://localhost:8080
- API エンドポイント: http://localhost:8080/api/todos

## API エンドポイント

- `GET /api/todos` - 全タスク取得
- `POST /api/todos` - 新規タスク作成
- `PUT /api/todos/{id}` - タスク更新
- `DELETE /api/todos/{id}` - タスク削除

## プロジェクト構成

```
TodoApp/
├── src/main/java/com/learning/     # Java ソースコード
│   ├── Main.java                   # メインクラス
│   ├── model/Todo.java             # Todo モデル
│   ├── database/                   # データベース関連
│   └── server/                     # HTTP サーバー
├── src/main/resources/static/      # 静的ファイル（ビルド後）
├── frontend/                       # React フロントエンド
│   ├── src/App.jsx                 # メインコンポーネント
│   └── package.json                # 依存関係
├── docker-compose.yml              # MySQL 設定
├── init.sql                        # データベース初期化
└── pom.xml                         # Maven 設定
```

## 学習ポイント

- **Java**: プレーン Java での HTTP サーバー実装と CRUD 操作
- **MySQL**: Docker を使用したデータベース環境構築
- **React**: React 19 の新機能と Geist UI ライブラリの使用
- **API 設計**: RESTful API の設計と実装
- **デザイン**: Notion ライクなモダンなユーザーインターフェース
