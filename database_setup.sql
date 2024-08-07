drop database mysocialnetwork;
-- Tạo cơ sở dữ liệu với bộ ký tự UTF-8 để hỗ trợ tiếng Việt
CREATE DATABASE MySocialNetwork CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE MySocialNetwork;

-- Tạo bảng Users
CREATE TABLE Users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) DEFAULT 'user',
    public_id VARCHAR(255) UNIQUE NOT NULL,
    avatar VARCHAR(255),
    full_name VARCHAR(255) NOT NULL,
    birth_date DATE NOT NULL,
    occupation VARCHAR(255),
    location VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Tạo bảng Posts
CREATE TABLE Posts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    content TEXT,
    image_url VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted BOOLEAN DEFAULT FALSE,
    public_id VARCHAR(255) UNIQUE NOT NULL,
    FOREIGN KEY (user_id) REFERENCES Users(id)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Tạo bảng Comments
CREATE TABLE Comments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    post_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted BOOLEAN DEFAULT FALSE,
    public_id VARCHAR(255) UNIQUE NOT NULL,
    FOREIGN KEY (post_id) REFERENCES Posts(id),
    FOREIGN KEY (user_id) REFERENCES Users(id)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Tạo bảng Friends
CREATE TABLE Friends (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    friend_id BIGINT NOT NULL,
    status ENUM('PENDING', 'ACCEPTED', 'REJECTED') DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    public_id VARCHAR(255) UNIQUE NOT NULL,
    FOREIGN KEY (user_id) REFERENCES Users(id),
    FOREIGN KEY (friend_id) REFERENCES Users(id)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Tạo bảng PasswordResets
CREATE TABLE PasswordResets (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    token VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    public_id VARCHAR(255) UNIQUE NOT NULL,
    FOREIGN KEY (user_id) REFERENCES Users(id)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Tạo bảng Reports
CREATE TABLE Reports (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    report_date DATE NOT NULL,
    num_posts INT NOT NULL,
    num_comments INT NOT NULL,
    num_friends INT NOT NULL,
    num_likes INT NOT NULL,
    num_loves INT NOT NULL,
    num_angries INT NOT NULL,
    num_sads INT NOT NULL,
    num_hahas INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    public_id VARCHAR(255) UNIQUE NOT NULL,
    FOREIGN KEY (user_id) REFERENCES Users(id)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Tạo bảng Reactions (nếu bạn có bảng này)
CREATE TABLE Reactions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    post_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    reaction_type ENUM('LIKE', 'LOVE', 'ANGRY', 'SAD', 'HAHA') NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    public_id VARCHAR(255) UNIQUE NOT NULL,
    FOREIGN KEY (post_id) REFERENCES Posts(id),
    FOREIGN KEY (user_id) REFERENCES Users(id)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
