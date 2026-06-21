SET NAMES utf8mb4;

-- 后台管理员表
CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    nickname VARCHAR(50),
    email VARCHAR(100),
    status TINYINT DEFAULT 1,
    deleted TINYINT DEFAULT 0,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 前台用户表
CREATE TABLE IF NOT EXISTS web_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    nickname VARCHAR(50),
    email VARCHAR(100),
    user_type VARCHAR(20) DEFAULT 'STUDENT' COMMENT '用户分类：TEACHER-老师，STUDENT-学生',
    education_system VARCHAR(10) DEFAULT '63' COMMENT '学制偏好：63-六三制，54-五四制',
    status TINYINT DEFAULT 1,
    vip_level INT DEFAULT 0 COMMENT 'VIP等级：0-普通用户，预留',
    vip_expire_time TIMESTAMP NULL COMMENT 'VIP过期时间，预留',
    wechat_openid VARCHAR(100) UNIQUE COMMENT '微信开放平台openid（网站扫码登录）',
    wechat_unionid VARCHAR(100) COMMENT '微信unionid（跨平台统一标识）',
    wechat_mp_openid VARCHAR(100) COMMENT '微信小程序openid',
    avatar_url VARCHAR(500) COMMENT '头像URL',
    phone VARCHAR(20) COMMENT '手机号',
    deleted TINYINT DEFAULT 0,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 分类表（支持父子层级：学制 -> 年级 -> 课程，可向下扩展知识点子分类）
CREATE TABLE IF NOT EXISTS category (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    parent_id BIGINT DEFAULT NULL COMMENT '父分类ID，NULL表示顶级',
    name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    image_url VARCHAR(500) DEFAULT NULL COMMENT '封面图片URL',
    sort_order INT DEFAULT 0,
    deleted TINYINT DEFAULT 0,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 题目表
CREATE TABLE IF NOT EXISTS question (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    category_id BIGINT NOT NULL,
    type VARCHAR(30) NOT NULL COMMENT '题型代码：SINGLE_CHOICE/MULTI_CHOICE/JUDGE/FILL_BLANK/CALCULATION等',
    difficulty TINYINT DEFAULT 1 COMMENT '难度：1-简单 2-中等 3-困难',
    content TEXT NOT NULL,
    options TEXT COMMENT '选项JSON数组',
    answer TEXT NOT NULL,
    analysis TEXT COMMENT '题目解析',
    knowledge_tags VARCHAR(500) DEFAULT NULL COMMENT '知识点标签JSON数组，如["拼音","汉字"]',
    score DECIMAL(5,1) DEFAULT 5.0,
    deleted TINYINT DEFAULT 0,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 分类题型比例配置表（动态题型 + 难度分配）
-- type_ratios: 题型比例JSON，如 {"SINGLE_CHOICE":40,"MULTI_CHOICE":20,"FILL_BLANK":20,"CALCULATION":20}
-- difficulty_ratios: 难度比例JSON，如 {"1":40,"2":40,"3":20}（简单/中等/困难占比）
CREATE TABLE IF NOT EXISTS category_type_ratio (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    category_id BIGINT NOT NULL UNIQUE,
    type_ratios JSON COMMENT '题型比例JSON，key为题型代码，value为百分比(0-100)',
    difficulty_ratios JSON COMMENT '难度比例JSON，{"1":简单%, "2":中等%, "3":困难%}',
    deleted TINYINT DEFAULT 0,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 试卷表
CREATE TABLE IF NOT EXISTS exam (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    category_id BIGINT,
    total_score DECIMAL(6,1) DEFAULT 100.0,
    duration INT DEFAULT 60,
    type VARCHAR(20) DEFAULT 'RANDOM',
    seed VARCHAR(32) DEFAULT NULL COMMENT '种子号，用于重现相同试卷',
    status TINYINT DEFAULT 1,
    deleted TINYINT DEFAULT 0,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 试卷题目关联表
CREATE TABLE IF NOT EXISTS exam_question (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    exam_id BIGINT NOT NULL,
    question_id BIGINT NOT NULL,
    sort_order INT DEFAULT 0
);

-- 用户考试记录表
CREATE TABLE IF NOT EXISTS user_exam (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    exam_id BIGINT NOT NULL,
    answers TEXT,
    score DECIMAL(6,1),
    status TINYINT DEFAULT 0,
    start_time TIMESTAMP,
    end_time TIMESTAMP,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
