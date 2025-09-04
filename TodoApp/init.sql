-- Todoアプリ用のデータベースとテーブルを初期化

USE todoapp;

-- 文字エンコーディングを設定
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;

-- Todoテーブルの作成
CREATE TABLE IF NOT EXISTS todos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    completed BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- サンプルデータの挿入
INSERT INTO todos (title, description, completed) VALUES
('Java学習', 'プレーンJavaでTodoアプリを作成する', false),
('Docker設定', 'MySQL用のDocker環境を構築する', false),
('React開発', 'React19でフロントエンド画面を作成する', false);
