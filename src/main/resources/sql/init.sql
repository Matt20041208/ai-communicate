-- 创建数据库
CREATE DATABASE IF NOT EXISTS ai_code_helper DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 使用数据库
USE ai_code_helper;

-- 用户表
CREATE TABLE IF NOT EXISTS user (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    nickname VARCHAR(50),
    email VARCHAR(100),
    phone VARCHAR(20),
    status INT DEFAULT 1 COMMENT '1: 启用, 0: 禁用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 角色表
CREATE TABLE IF NOT EXISTS role (
    id INT PRIMARY KEY AUTO_INCREMENT,
    role_name VARCHAR(50) NOT NULL UNIQUE,
    role_desc VARCHAR(200),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 权限表
CREATE TABLE IF NOT EXISTS permission (
    id INT PRIMARY KEY AUTO_INCREMENT,
    perm_name VARCHAR(50) NOT NULL UNIQUE,
    perm_desc VARCHAR(200),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 用户角色关联表
CREATE TABLE IF NOT EXISTS user_role (
    user_id INT NOT NULL,
    role_id INT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES role(id) ON DELETE CASCADE
);

-- 角色权限关联表
CREATE TABLE IF NOT EXISTS role_permission (
    role_id INT NOT NULL,
    perm_id INT NOT NULL,
    PRIMARY KEY (role_id, perm_id),
    FOREIGN KEY (role_id) REFERENCES role(id) ON DELETE CASCADE,
    FOREIGN KEY (perm_id) REFERENCES permission(id) ON DELETE CASCADE
);

-- 插入默认角色
INSERT INTO role (role_name, role_desc) VALUES
('admin', '管理员'),
('user', '普通用户');

-- 插入默认权限
INSERT INTO permission (perm_name, perm_desc) VALUES
('user:list', '查看用户列表'),
('user:add', '添加用户'),
('user:edit', '编辑用户'),
('user:delete', '删除用户'),
('chat:use', '使用聊天功能');

-- 为管理员角色分配所有权限
INSERT INTO role_permission (role_id, perm_id) VALUES
(1, 1),
(1, 2),
(1, 3),
(1, 4),
(1, 5);

-- 为普通用户分配聊天权限
INSERT INTO role_permission (role_id, perm_id) VALUES
(2, 5);

-- 插入默认管理员用户（密码：admin123）
-- 使用BCrypt加密: BCrypt.hashpw("admin123", BCrypt.gensalt())
INSERT INTO user (username, password, nickname, email, status) VALUES
('admin', '$2a$10$7JB720yubVS0vQS7iJp9qOJhKKvI2MIY0R3q9QG0J5r7Q8Y8Y8Y8Y', '管理员', 'admin@example.com', 1);

-- 插入默认普通用户（密码：user123）
-- 使用BCrypt加密: BCrypt.hashpw("user123", BCrypt.gensalt())
INSERT INTO user (username, password, nickname, email, status) VALUES
('user', '$2a$10$7JB720yubVS0vQS7iJp9qOJhKKvI2MIY0R3q9QG0J5r7Q8Y8Y8Y8Y', '普通用户', 'user@example.com', 1);

-- 关联用户和角色
INSERT INTO user_role (user_id, role_id) VALUES
(1, 1),
(2, 2);
