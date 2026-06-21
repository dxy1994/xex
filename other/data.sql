SET NAMES utf8mb4;

-- 管理员用户由 AdminUserInitializer 在应用启动时自动创建，无需手动插入

-- ============================================================
-- 分类数据（二级结构：学制 -> 年级 -> 课程，可向下扩展知识点子分类）
-- ============================================================

-- ==================== 一级分类：学制 ====================
INSERT INTO category (id, parent_id, name, description, sort_order) VALUES (1, NULL, '63学制', '小学6年+初中3年', 1)
ON DUPLICATE KEY UPDATE name=VALUES(name), description=VALUES(description), sort_order=VALUES(sort_order);
INSERT INTO category (id, parent_id, name, description, sort_order) VALUES (2, NULL, '54学制', '小学5年+初中4年', 2)
ON DUPLICATE KEY UPDATE name=VALUES(name), description=VALUES(description), sort_order=VALUES(sort_order);

-- ==================== 二级分类：年级（直接挂在学制下，无需学段中间层） ====================
-- 63学制 - 小学（一年级~六年级）
INSERT INTO category (id, parent_id, name, description, sort_order) VALUES (110, 1, '一年级', '63小学一年级', 1)
ON DUPLICATE KEY UPDATE name=VALUES(name), description=VALUES(description), sort_order=VALUES(sort_order);
INSERT INTO category (id, parent_id, name, description, sort_order) VALUES (120, 1, '二年级', '63小学二年级', 2)
ON DUPLICATE KEY UPDATE name=VALUES(name), description=VALUES(description), sort_order=VALUES(sort_order);
INSERT INTO category (id, parent_id, name, description, sort_order) VALUES (130, 1, '三年级', '63小学三年级', 3)
ON DUPLICATE KEY UPDATE name=VALUES(name), description=VALUES(description), sort_order=VALUES(sort_order);
INSERT INTO category (id, parent_id, name, description, sort_order) VALUES (140, 1, '四年级', '63小学四年级', 4)
ON DUPLICATE KEY UPDATE name=VALUES(name), description=VALUES(description), sort_order=VALUES(sort_order);
INSERT INTO category (id, parent_id, name, description, sort_order) VALUES (150, 1, '五年级', '63小学五年级', 5)
ON DUPLICATE KEY UPDATE name=VALUES(name), description=VALUES(description), sort_order=VALUES(sort_order);
INSERT INTO category (id, parent_id, name, description, sort_order) VALUES (160, 1, '六年级', '63小学六年级', 6)
ON DUPLICATE KEY UPDATE name=VALUES(name), description=VALUES(description), sort_order=VALUES(sort_order);

-- 63学制 - 初中（七年级~九年级）
INSERT INTO category (id, parent_id, name, description, sort_order) VALUES (170, 1, '七年级', '63初中七年级', 7)
ON DUPLICATE KEY UPDATE name=VALUES(name), description=VALUES(description), sort_order=VALUES(sort_order);
INSERT INTO category (id, parent_id, name, description, sort_order) VALUES (180, 1, '八年级', '63初中八年级', 8)
ON DUPLICATE KEY UPDATE name=VALUES(name), description=VALUES(description), sort_order=VALUES(sort_order);
INSERT INTO category (id, parent_id, name, description, sort_order) VALUES (190, 1, '九年级', '63初中九年级', 9)
ON DUPLICATE KEY UPDATE name=VALUES(name), description=VALUES(description), sort_order=VALUES(sort_order);

-- 54学制 - 小学（一年级~五年级）
INSERT INTO category (id, parent_id, name, description, sort_order) VALUES (210, 2, '一年级', '54小学一年级', 1)
ON DUPLICATE KEY UPDATE name=VALUES(name), description=VALUES(description), sort_order=VALUES(sort_order);
INSERT INTO category (id, parent_id, name, description, sort_order) VALUES (220, 2, '二年级', '54小学二年级', 2)
ON DUPLICATE KEY UPDATE name=VALUES(name), description=VALUES(description), sort_order=VALUES(sort_order);
INSERT INTO category (id, parent_id, name, description, sort_order) VALUES (230, 2, '三年级', '54小学三年级', 3)
ON DUPLICATE KEY UPDATE name=VALUES(name), description=VALUES(description), sort_order=VALUES(sort_order);
INSERT INTO category (id, parent_id, name, description, sort_order) VALUES (240, 2, '四年级', '54小学四年级', 4)
ON DUPLICATE KEY UPDATE name=VALUES(name), description=VALUES(description), sort_order=VALUES(sort_order);
INSERT INTO category (id, parent_id, name, description, sort_order) VALUES (250, 2, '五年级', '54小学五年级', 5)
ON DUPLICATE KEY UPDATE name=VALUES(name), description=VALUES(description), sort_order=VALUES(sort_order);

-- 54学制 - 初中（六年级~九年级）
INSERT INTO category (id, parent_id, name, description, sort_order) VALUES (260, 2, '六年级', '54初中六年级', 6)
ON DUPLICATE KEY UPDATE name=VALUES(name), description=VALUES(description), sort_order=VALUES(sort_order);
INSERT INTO category (id, parent_id, name, description, sort_order) VALUES (270, 2, '七年级', '54初中七年级', 7)
ON DUPLICATE KEY UPDATE name=VALUES(name), description=VALUES(description), sort_order=VALUES(sort_order);
INSERT INTO category (id, parent_id, name, description, sort_order) VALUES (280, 2, '八年级', '54初中八年级', 8)
ON DUPLICATE KEY UPDATE name=VALUES(name), description=VALUES(description), sort_order=VALUES(sort_order);
INSERT INTO category (id, parent_id, name, description, sort_order) VALUES (290, 2, '九年级', '54初中九年级', 9)
ON DUPLICATE KEY UPDATE name=VALUES(name), description=VALUES(description), sort_order=VALUES(sort_order);

-- ==================== 三级分类：课程（名称不加年级前缀） ====================
-- 后续可继续在课程下扩展知识点子分类，如：数学 -> 小数/有理数/方程

-- ---------- 63学制 - 小学课程 ----------
-- 一年级 (110)
INSERT INTO category (id, parent_id, name, description, sort_order) VALUES (1101, 110, '语文', '63小学一年级语文', 1)
ON DUPLICATE KEY UPDATE name=VALUES(name), description=VALUES(description), sort_order=VALUES(sort_order);
INSERT INTO category (id, parent_id, name, description, sort_order) VALUES (1102, 110, '数学', '63小学一年级数学', 2)
ON DUPLICATE KEY UPDATE name=VALUES(name), description=VALUES(description), sort_order=VALUES(sort_order);
INSERT INTO category (id, parent_id, name, description, sort_order) VALUES (1103, 110, '英语', '63小学一年级英语', 3)
ON DUPLICATE KEY UPDATE name=VALUES(name), description=VALUES(description), sort_order=VALUES(sort_order);

-- 二年级 (120)
INSERT INTO category (id, parent_id, name, description, sort_order) VALUES (1201, 120, '语文', '63小学二年级语文', 1)
ON DUPLICATE KEY UPDATE name=VALUES(name), description=VALUES(description), sort_order=VALUES(sort_order);
INSERT INTO category (id, parent_id, name, description, sort_order) VALUES (1202, 120, '数学', '63小学二年级数学', 2)
ON DUPLICATE KEY UPDATE name=VALUES(name), description=VALUES(description), sort_order=VALUES(sort_order);
INSERT INTO category (id, parent_id, name, description, sort_order) VALUES (1203, 120, '英语', '63小学二年级英语', 3)
ON DUPLICATE KEY UPDATE name=VALUES(name), description=VALUES(description), sort_order=VALUES(sort_order);

-- 三年级 (130)
INSERT INTO category (id, parent_id, name, description, sort_order) VALUES (1301, 130, '语文', '63小学三年级语文', 1)
ON DUPLICATE KEY UPDATE name=VALUES(name), description=VALUES(description), sort_order=VALUES(sort_order);
INSERT INTO category (id, parent_id, name, description, sort_order) VALUES (1302, 130, '数学', '63小学三年级数学', 2)
ON DUPLICATE KEY UPDATE name=VALUES(name), description=VALUES(description), sort_order=VALUES(sort_order);
INSERT INTO category (id, parent_id, name, description, sort_order) VALUES (1303, 130, '英语', '63小学三年级英语', 3)
ON DUPLICATE KEY UPDATE name=VALUES(name), description=VALUES(description), sort_order=VALUES(sort_order);

-- 四年级 (140)
INSERT INTO category (id, parent_id, name, description, sort_order) VALUES (1401, 140, '语文', '63小学四年级语文', 1)
ON DUPLICATE KEY UPDATE name=VALUES(name), description=VALUES(description), sort_order=VALUES(sort_order);
INSERT INTO category (id, parent_id, name, description, sort_order) VALUES (1402, 140, '数学', '63小学四年级数学', 2)
ON DUPLICATE KEY UPDATE name=VALUES(name), description=VALUES(description), sort_order=VALUES(sort_order);
INSERT INTO category (id, parent_id, name, description, sort_order) VALUES (1403, 140, '英语', '63小学四年级英语', 3)
ON DUPLICATE KEY UPDATE name=VALUES(name), description=VALUES(description), sort_order=VALUES(sort_order);

-- 五年级 (150)
INSERT INTO category (id, parent_id, name, description, sort_order) VALUES (1501, 150, '语文', '63小学五年级语文', 1)
ON DUPLICATE KEY UPDATE name=VALUES(name), description=VALUES(description), sort_order=VALUES(sort_order);
INSERT INTO category (id, parent_id, name, description, sort_order) VALUES (1502, 150, '数学', '63小学五年级数学', 2)
ON DUPLICATE KEY UPDATE name=VALUES(name), description=VALUES(description), sort_order=VALUES(sort_order);
INSERT INTO category (id, parent_id, name, description, sort_order) VALUES (1503, 150, '英语', '63小学五年级英语', 3)
ON DUPLICATE KEY UPDATE name=VALUES(name), description=VALUES(description), sort_order=VALUES(sort_order);

-- 六年级 (160)
INSERT INTO category (id, parent_id, name, description, sort_order) VALUES (1601, 160, '语文', '63小学六年级语文', 1)
ON DUPLICATE KEY UPDATE name=VALUES(name), description=VALUES(description), sort_order=VALUES(sort_order);
INSERT INTO category (id, parent_id, name, description, sort_order) VALUES (1602, 160, '数学', '63小学六年级数学', 2)
ON DUPLICATE KEY UPDATE name=VALUES(name), description=VALUES(description), sort_order=VALUES(sort_order);
INSERT INTO category (id, parent_id, name, description, sort_order) VALUES (1603, 160, '英语', '63小学六年级英语', 3)
ON DUPLICATE KEY UPDATE name=VALUES(name), description=VALUES(description), sort_order=VALUES(sort_order);

-- ---------- 63学制 - 初中课程 ----------
-- 七年级 (170)
INSERT INTO category (id, parent_id, name, description, sort_order) VALUES (1701, 170, '语文', '63初中七年级语文', 1)
ON DUPLICATE KEY UPDATE name=VALUES(name), description=VALUES(description), sort_order=VALUES(sort_order);
INSERT INTO category (id, parent_id, name, description, sort_order) VALUES (1702, 170, '数学', '63初中七年级数学', 2)
ON DUPLICATE KEY UPDATE name=VALUES(name), description=VALUES(description), sort_order=VALUES(sort_order);
INSERT INTO category (id, parent_id, name, description, sort_order) VALUES (1703, 170, '英语', '63初中七年级英语', 3)
ON DUPLICATE KEY UPDATE name=VALUES(name), description=VALUES(description), sort_order=VALUES(sort_order);
INSERT INTO category (id, parent_id, name, description, sort_order) VALUES (1704, 170, '物理', '63初中七年级物理', 4)
ON DUPLICATE KEY UPDATE name=VALUES(name), description=VALUES(description), sort_order=VALUES(sort_order);
INSERT INTO category (id, parent_id, name, description, sort_order) VALUES (1705, 170, '化学', '63初中七年级化学', 5)
ON DUPLICATE KEY UPDATE name=VALUES(name), description=VALUES(description), sort_order=VALUES(sort_order);

-- 八年级 (180)
INSERT INTO category (id, parent_id, name, description, sort_order) VALUES (1801, 180, '语文', '63初中八年级语文', 1)
ON DUPLICATE KEY UPDATE name=VALUES(name), description=VALUES(description), sort_order=VALUES(sort_order);
INSERT INTO category (id, parent_id, name, description, sort_order) VALUES (1802, 180, '数学', '63初中八年级数学', 2)
ON DUPLICATE KEY UPDATE name=VALUES(name), description=VALUES(description), sort_order=VALUES(sort_order);
INSERT INTO category (id, parent_id, name, description, sort_order) VALUES (1803, 180, '英语', '63初中八年级英语', 3)
ON DUPLICATE KEY UPDATE name=VALUES(name), description=VALUES(description), sort_order=VALUES(sort_order);
INSERT INTO category (id, parent_id, name, description, sort_order) VALUES (1804, 180, '物理', '63初中八年级物理', 4)
ON DUPLICATE KEY UPDATE name=VALUES(name), description=VALUES(description), sort_order=VALUES(sort_order);
INSERT INTO category (id, parent_id, name, description, sort_order) VALUES (1805, 180, '化学', '63初中八年级化学', 5)
ON DUPLICATE KEY UPDATE name=VALUES(name), description=VALUES(description), sort_order=VALUES(sort_order);

-- 九年级 (190)
INSERT INTO category (id, parent_id, name, description, sort_order) VALUES (1901, 190, '语文', '63初中九年级语文', 1)
ON DUPLICATE KEY UPDATE name=VALUES(name), description=VALUES(description), sort_order=VALUES(sort_order);
INSERT INTO category (id, parent_id, name, description, sort_order) VALUES (1902, 190, '数学', '63初中九年级数学', 2)
ON DUPLICATE KEY UPDATE name=VALUES(name), description=VALUES(description), sort_order=VALUES(sort_order);
INSERT INTO category (id, parent_id, name, description, sort_order) VALUES (1903, 190, '英语', '63初中九年级英语', 3)
ON DUPLICATE KEY UPDATE name=VALUES(name), description=VALUES(description), sort_order=VALUES(sort_order);
INSERT INTO category (id, parent_id, name, description, sort_order) VALUES (1904, 190, '物理', '63初中九年级物理', 4)
ON DUPLICATE KEY UPDATE name=VALUES(name), description=VALUES(description), sort_order=VALUES(sort_order);
INSERT INTO category (id, parent_id, name, description, sort_order) VALUES (1905, 190, '化学', '63初中九年级化学', 5)
ON DUPLICATE KEY UPDATE name=VALUES(name), description=VALUES(description), sort_order=VALUES(sort_order);

-- ---------- 54学制 - 小学课程 ----------
-- 一年级 (210)
INSERT INTO category (id, parent_id, name, description, sort_order) VALUES (2101, 210, '语文', '54小学一年级语文', 1)
ON DUPLICATE KEY UPDATE name=VALUES(name), description=VALUES(description), sort_order=VALUES(sort_order);
INSERT INTO category (id, parent_id, name, description, sort_order) VALUES (2102, 210, '数学', '54小学一年级数学', 2)
ON DUPLICATE KEY UPDATE name=VALUES(name), description=VALUES(description), sort_order=VALUES(sort_order);
INSERT INTO category (id, parent_id, name, description, sort_order) VALUES (2103, 210, '英语', '54小学一年级英语', 3)
ON DUPLICATE KEY UPDATE name=VALUES(name), description=VALUES(description), sort_order=VALUES(sort_order);

-- 二年级 (220)
INSERT INTO category (id, parent_id, name, description, sort_order) VALUES (2201, 220, '语文', '54小学二年级语文', 1)
ON DUPLICATE KEY UPDATE name=VALUES(name), description=VALUES(description), sort_order=VALUES(sort_order);
INSERT INTO category (id, parent_id, name, description, sort_order) VALUES (2202, 220, '数学', '54小学二年级数学', 2)
ON DUPLICATE KEY UPDATE name=VALUES(name), description=VALUES(description), sort_order=VALUES(sort_order);
INSERT INTO category (id, parent_id, name, description, sort_order) VALUES (2203, 220, '英语', '54小学二年级英语', 3)
ON DUPLICATE KEY UPDATE name=VALUES(name), description=VALUES(description), sort_order=VALUES(sort_order);

-- 三年级 (230)
INSERT INTO category (id, parent_id, name, description, sort_order) VALUES (2301, 230, '语文', '54小学三年级语文', 1)
ON DUPLICATE KEY UPDATE name=VALUES(name), description=VALUES(description), sort_order=VALUES(sort_order);
INSERT INTO category (id, parent_id, name, description, sort_order) VALUES (2302, 230, '数学', '54小学三年级数学', 2)
ON DUPLICATE KEY UPDATE name=VALUES(name), description=VALUES(description), sort_order=VALUES(sort_order);
INSERT INTO category (id, parent_id, name, description, sort_order) VALUES (2303, 230, '英语', '54小学三年级英语', 3)
ON DUPLICATE KEY UPDATE name=VALUES(name), description=VALUES(description), sort_order=VALUES(sort_order);

-- 四年级 (240)
INSERT INTO category (id, parent_id, name, description, sort_order) VALUES (2401, 240, '语文', '54小学四年级语文', 1)
ON DUPLICATE KEY UPDATE name=VALUES(name), description=VALUES(description), sort_order=VALUES(sort_order);
INSERT INTO category (id, parent_id, name, description, sort_order) VALUES (2402, 240, '数学', '54小学四年级数学', 2)
ON DUPLICATE KEY UPDATE name=VALUES(name), description=VALUES(description), sort_order=VALUES(sort_order);
INSERT INTO category (id, parent_id, name, description, sort_order) VALUES (2403, 240, '英语', '54小学四年级英语', 3)
ON DUPLICATE KEY UPDATE name=VALUES(name), description=VALUES(description), sort_order=VALUES(sort_order);

-- 五年级 (250)
INSERT INTO category (id, parent_id, name, description, sort_order) VALUES (2501, 250, '语文', '54小学五年级语文', 1)
ON DUPLICATE KEY UPDATE name=VALUES(name), description=VALUES(description), sort_order=VALUES(sort_order);
INSERT INTO category (id, parent_id, name, description, sort_order) VALUES (2502, 250, '数学', '54小学五年级数学', 2)
ON DUPLICATE KEY UPDATE name=VALUES(name), description=VALUES(description), sort_order=VALUES(sort_order);
INSERT INTO category (id, parent_id, name, description, sort_order) VALUES (2503, 250, '英语', '54小学五年级英语', 3)
ON DUPLICATE KEY UPDATE name=VALUES(name), description=VALUES(description), sort_order=VALUES(sort_order);

-- ---------- 54学制 - 初中课程 ----------
-- 六年级 (260)
INSERT INTO category (id, parent_id, name, description, sort_order) VALUES (2601, 260, '语文', '54初中六年级语文', 1)
ON DUPLICATE KEY UPDATE name=VALUES(name), description=VALUES(description), sort_order=VALUES(sort_order);
INSERT INTO category (id, parent_id, name, description, sort_order) VALUES (2602, 260, '数学', '54初中六年级数学', 2)
ON DUPLICATE KEY UPDATE name=VALUES(name), description=VALUES(description), sort_order=VALUES(sort_order);
INSERT INTO category (id, parent_id, name, description, sort_order) VALUES (2603, 260, '英语', '54初中六年级英语', 3)
ON DUPLICATE KEY UPDATE name=VALUES(name), description=VALUES(description), sort_order=VALUES(sort_order);
INSERT INTO category (id, parent_id, name, description, sort_order) VALUES (2604, 260, '物理', '54初中六年级物理', 4)
ON DUPLICATE KEY UPDATE name=VALUES(name), description=VALUES(description), sort_order=VALUES(sort_order);
INSERT INTO category (id, parent_id, name, description, sort_order) VALUES (2605, 260, '化学', '54初中六年级化学', 5)
ON DUPLICATE KEY UPDATE name=VALUES(name), description=VALUES(description), sort_order=VALUES(sort_order);

-- 七年级 (270)
INSERT INTO category (id, parent_id, name, description, sort_order) VALUES (2701, 270, '语文', '54初中七年级语文', 1)
ON DUPLICATE KEY UPDATE name=VALUES(name), description=VALUES(description), sort_order=VALUES(sort_order);
INSERT INTO category (id, parent_id, name, description, sort_order) VALUES (2702, 270, '数学', '54初中七年级数学', 2)
ON DUPLICATE KEY UPDATE name=VALUES(name), description=VALUES(description), sort_order=VALUES(sort_order);
INSERT INTO category (id, parent_id, name, description, sort_order) VALUES (2703, 270, '英语', '54初中七年级英语', 3)
ON DUPLICATE KEY UPDATE name=VALUES(name), description=VALUES(description), sort_order=VALUES(sort_order);
INSERT INTO category (id, parent_id, name, description, sort_order) VALUES (2704, 270, '物理', '54初中七年级物理', 4)
ON DUPLICATE KEY UPDATE name=VALUES(name), description=VALUES(description), sort_order=VALUES(sort_order);
INSERT INTO category (id, parent_id, name, description, sort_order) VALUES (2705, 270, '化学', '54初中七年级化学', 5)
ON DUPLICATE KEY UPDATE name=VALUES(name), description=VALUES(description), sort_order=VALUES(sort_order);

-- 八年级 (280)
INSERT INTO category (id, parent_id, name, description, sort_order) VALUES (2801, 280, '语文', '54初中八年级语文', 1)
ON DUPLICATE KEY UPDATE name=VALUES(name), description=VALUES(description), sort_order=VALUES(sort_order);
INSERT INTO category (id, parent_id, name, description, sort_order) VALUES (2802, 280, '数学', '54初中八年级数学', 2)
ON DUPLICATE KEY UPDATE name=VALUES(name), description=VALUES(description), sort_order=VALUES(sort_order);
INSERT INTO category (id, parent_id, name, description, sort_order) VALUES (2803, 280, '英语', '54初中八年级英语', 3)
ON DUPLICATE KEY UPDATE name=VALUES(name), description=VALUES(description), sort_order=VALUES(sort_order);
INSERT INTO category (id, parent_id, name, description, sort_order) VALUES (2804, 280, '物理', '54初中八年级物理', 4)
ON DUPLICATE KEY UPDATE name=VALUES(name), description=VALUES(description), sort_order=VALUES(sort_order);
INSERT INTO category (id, parent_id, name, description, sort_order) VALUES (2805, 280, '化学', '54初中八年级化学', 5)
ON DUPLICATE KEY UPDATE name=VALUES(name), description=VALUES(description), sort_order=VALUES(sort_order);

-- 九年级 (290)
INSERT INTO category (id, parent_id, name, description, sort_order) VALUES (2901, 290, '语文', '54初中九年级语文', 1)
ON DUPLICATE KEY UPDATE name=VALUES(name), description=VALUES(description), sort_order=VALUES(sort_order);
INSERT INTO category (id, parent_id, name, description, sort_order) VALUES (2902, 290, '数学', '54初中九年级数学', 2)
ON DUPLICATE KEY UPDATE name=VALUES(name), description=VALUES(description), sort_order=VALUES(sort_order);
INSERT INTO category (id, parent_id, name, description, sort_order) VALUES (2903, 290, '英语', '54初中九年级英语', 3)
ON DUPLICATE KEY UPDATE name=VALUES(name), description=VALUES(description), sort_order=VALUES(sort_order);
INSERT INTO category (id, parent_id, name, description, sort_order) VALUES (2904, 290, '物理', '54初中九年级物理', 4)
ON DUPLICATE KEY UPDATE name=VALUES(name), description=VALUES(description), sort_order=VALUES(sort_order);
INSERT INTO category (id, parent_id, name, description, sort_order) VALUES (2905, 290, '化学', '54初中九年级化学', 5)
ON DUPLICATE KEY UPDATE name=VALUES(name), description=VALUES(description), sort_order=VALUES(sort_order);

-- ============================================================
-- 四~六级分类示例：课程 -> 上册/下册 -> 大章节 -> 小章节
-- ============================================================

-- ========== 63三年级语文（1301）-> 分册 -> 章节 -> 课文 ==========
-- 上册/下册
INSERT INTO category (id, parent_id, name, description, sort_order) VALUES (13011, 1301, '上册', '三年级语文上册', 1)
ON DUPLICATE KEY UPDATE name=VALUES(name), description=VALUES(description), sort_order=VALUES(sort_order);
INSERT INTO category (id, parent_id, name, description, sort_order) VALUES (13012, 1301, '下册', '三年级语文下册', 2)
ON DUPLICATE KEY UPDATE name=VALUES(name), description=VALUES(description), sort_order=VALUES(sort_order);

-- 上册 -> 大章节（单元）
INSERT INTO category (id, parent_id, name, description, sort_order) VALUES (130111, 13011, '第一单元', '学校生活', 1)
ON DUPLICATE KEY UPDATE name=VALUES(name), description=VALUES(description), sort_order=VALUES(sort_order);
INSERT INTO category (id, parent_id, name, description, sort_order) VALUES (130112, 13011, '第二单元', '金秋时节', 2)
ON DUPLICATE KEY UPDATE name=VALUES(name), description=VALUES(description), sort_order=VALUES(sort_order);

-- 下册 -> 大章节（单元）
INSERT INTO category (id, parent_id, name, description, sort_order) VALUES (130121, 13012, '第一单元', '可爱的生灵', 1)
ON DUPLICATE KEY UPDATE name=VALUES(name), description=VALUES(description), sort_order=VALUES(sort_order);
INSERT INTO category (id, parent_id, name, description, sort_order) VALUES (130122, 13012, '第二单元', '寓言故事', 2)
ON DUPLICATE KEY UPDATE name=VALUES(name), description=VALUES(description), sort_order=VALUES(sort_order);

-- 上册第一单元 -> 小章节（课文）
INSERT INTO category (id, parent_id, name, description, sort_order) VALUES (1301111, 130111, '大青树下的小学', '描写民族小学的课文', 1)
ON DUPLICATE KEY UPDATE name=VALUES(name), description=VALUES(description), sort_order=VALUES(sort_order);
INSERT INTO category (id, parent_id, name, description, sort_order) VALUES (1301112, 130111, '花的学校', '泰戈尔散文诗', 2)
ON DUPLICATE KEY UPDATE name=VALUES(name), description=VALUES(description), sort_order=VALUES(sort_order);

-- 上册第二单元 -> 课文
INSERT INTO category (id, parent_id, name, description, sort_order) VALUES (1301121, 130112, '古诗三首', '《山行》《赠刘景文》《夜书所见》', 1)
ON DUPLICATE KEY UPDATE name=VALUES(name), description=VALUES(description), sort_order=VALUES(sort_order);

-- 下册第一单元 -> 课文
INSERT INTO category (id, parent_id, name, description, sort_order) VALUES (1301211, 130121, '古诗三首', '《绝句》《惠崇春江晚景》《三衢道中》', 1)
ON DUPLICATE KEY UPDATE name=VALUES(name), description=VALUES(description), sort_order=VALUES(sort_order);
INSERT INTO category (id, parent_id, name, description, sort_order) VALUES (1301212, 130121, '燕子', '郑振铎散文', 2)
ON DUPLICATE KEY UPDATE name=VALUES(name), description=VALUES(description), sort_order=VALUES(sort_order);

-- 下册第二单元 -> 课文
INSERT INTO category (id, parent_id, name, description, sort_order) VALUES (1301221, 130122, '守株待兔', '文言文寓言', 1)
ON DUPLICATE KEY UPDATE name=VALUES(name), description=VALUES(description), sort_order=VALUES(sort_order);
INSERT INTO category (id, parent_id, name, description, sort_order) VALUES (1301222, 130122, '陶罐和铁罐', '寓言故事', 2)
ON DUPLICATE KEY UPDATE name=VALUES(name), description=VALUES(description), sort_order=VALUES(sort_order);

-- ========== 63三年级数学（1302）-> 分册 -> 章节 ==========
INSERT INTO category (id, parent_id, name, description, sort_order) VALUES (13021, 1302, '上册', '三年级数学上册', 1)
ON DUPLICATE KEY UPDATE name=VALUES(name), description=VALUES(description), sort_order=VALUES(sort_order);
INSERT INTO category (id, parent_id, name, description, sort_order) VALUES (13022, 1302, '下册', '三年级数学下册', 2)
ON DUPLICATE KEY UPDATE name=VALUES(name), description=VALUES(description), sort_order=VALUES(sort_order);

-- 上册 -> 大章节
INSERT INTO category (id, parent_id, name, description, sort_order) VALUES (130211, 13021, '第一单元 时、分、秒', '时间单位', 1)
ON DUPLICATE KEY UPDATE name=VALUES(name), description=VALUES(description), sort_order=VALUES(sort_order);
INSERT INTO category (id, parent_id, name, description, sort_order) VALUES (130212, 13021, '第二单元 万以内的加法和减法', '加减运算', 2)
ON DUPLICATE KEY UPDATE name=VALUES(name), description=VALUES(description), sort_order=VALUES(sort_order);

-- 下册 -> 大章节
INSERT INTO category (id, parent_id, name, description, sort_order) VALUES (130221, 13022, '第一单元 位置与方向', '空间方位', 1)
ON DUPLICATE KEY UPDATE name=VALUES(name), description=VALUES(description), sort_order=VALUES(sort_order);
INSERT INTO category (id, parent_id, name, description, sort_order) VALUES (130222, 13022, '第二单元 除数是一位数的除法', '除法运算', 2)
ON DUPLICATE KEY UPDATE name=VALUES(name), description=VALUES(description), sort_order=VALUES(sort_order);

-- 上册时、分、秒 -> 小章节
INSERT INTO category (id, parent_id, name, description, sort_order) VALUES (1302111, 130211, '秒的认识', '认识时间单位秒', 1)
ON DUPLICATE KEY UPDATE name=VALUES(name), description=VALUES(description), sort_order=VALUES(sort_order);
INSERT INTO category (id, parent_id, name, description, sort_order) VALUES (1302112, 130211, '时间的计算', '时、分、秒换算', 2)
ON DUPLICATE KEY UPDATE name=VALUES(name), description=VALUES(description), sort_order=VALUES(sort_order);

-- 上册万以内加减 -> 小章节
INSERT INTO category (id, parent_id, name, description, sort_order) VALUES (1302121, 130212, '两位数加减两位数', '口算与笔算', 1)
ON DUPLICATE KEY UPDATE name=VALUES(name), description=VALUES(description), sort_order=VALUES(sort_order);

-- 下册位置与方向 -> 小章节
INSERT INTO category (id, parent_id, name, description, sort_order) VALUES (1302211, 130221, '认识东、南、西、北', '基本方位', 1)
ON DUPLICATE KEY UPDATE name=VALUES(name), description=VALUES(description), sort_order=VALUES(sort_order);

-- ========== 63七年级数学（1702）-> 分册 -> 章节 ==========
INSERT INTO category (id, parent_id, name, description, sort_order) VALUES (17021, 1702, '上册', '七年级数学上册', 1)
ON DUPLICATE KEY UPDATE name=VALUES(name), description=VALUES(description), sort_order=VALUES(sort_order);
INSERT INTO category (id, parent_id, name, description, sort_order) VALUES (17022, 1702, '下册', '七年级数学下册', 2)
ON DUPLICATE KEY UPDATE name=VALUES(name), description=VALUES(description), sort_order=VALUES(sort_order);

-- 上册 -> 大章节
INSERT INTO category (id, parent_id, name, description, sort_order) VALUES (170211, 17021, '第一章 有理数', '正负数与运算', 1)
ON DUPLICATE KEY UPDATE name=VALUES(name), description=VALUES(description), sort_order=VALUES(sort_order);
INSERT INTO category (id, parent_id, name, description, sort_order) VALUES (170212, 17021, '第二章 整式的加减', '代数式基础', 2)
ON DUPLICATE KEY UPDATE name=VALUES(name), description=VALUES(description), sort_order=VALUES(sort_order);

-- 下册 -> 大章节
INSERT INTO category (id, parent_id, name, description, sort_order) VALUES (170221, 17022, '第五章 相交线与平行线', '几何基础', 1)
ON DUPLICATE KEY UPDATE name=VALUES(name), description=VALUES(description), sort_order=VALUES(sort_order);
INSERT INTO category (id, parent_id, name, description, sort_order) VALUES (170222, 17022, '第六章 实数', '平方根与立方根', 2)
ON DUPLICATE KEY UPDATE name=VALUES(name), description=VALUES(description), sort_order=VALUES(sort_order);

-- 有理数 -> 小章节
INSERT INTO category (id, parent_id, name, description, sort_order) VALUES (1702111, 170211, '正数和负数', '正负数的概念', 1)
ON DUPLICATE KEY UPDATE name=VALUES(name), description=VALUES(description), sort_order=VALUES(sort_order);
INSERT INTO category (id, parent_id, name, description, sort_order) VALUES (1702112, 170211, '有理数的加减法', '有理数运算', 2)
ON DUPLICATE KEY UPDATE name=VALUES(name), description=VALUES(description), sort_order=VALUES(sort_order);

-- 整式的加减 -> 小章节
INSERT INTO category (id, parent_id, name, description, sort_order) VALUES (1702121, 170212, '用字母表示数', '代数式概念', 1)
ON DUPLICATE KEY UPDATE name=VALUES(name), description=VALUES(description), sort_order=VALUES(sort_order);

-- 相交线与平行线 -> 小章节
INSERT INTO category (id, parent_id, name, description, sort_order) VALUES (1702211, 170221, '对顶角与邻补角', '角的分类', 1)
ON DUPLICATE KEY UPDATE name=VALUES(name), description=VALUES(description), sort_order=VALUES(sort_order);

-- ============================================================
-- 题型比例配置（JSON格式，按学科自动适配题型）
-- type_ratios: 题型→百分比  difficulty_ratios: 难度→百分比
-- ============================================================
INSERT INTO category_type_ratio (id, category_id, type_ratios, difficulty_ratios) VALUES (1, 1101,
  '{"SINGLE_CHOICE":40,"MULTI_CHOICE":10,"JUDGE":10,"FILL_BLANK":20,"DICTATION":10,"SHORT_ANSWER":10}',
  '{"1":50,"2":35,"3":15}')
ON DUPLICATE KEY UPDATE type_ratios=VALUES(type_ratios), difficulty_ratios=VALUES(difficulty_ratios);
INSERT INTO category_type_ratio (id, category_id, type_ratios, difficulty_ratios) VALUES (2, 1102,
  '{"SINGLE_CHOICE":40,"MULTI_CHOICE":10,"JUDGE":10,"FILL_BLANK":20,"CALCULATION":20}',
  '{"1":50,"2":35,"3":15}')
ON DUPLICATE KEY UPDATE type_ratios=VALUES(type_ratios), difficulty_ratios=VALUES(difficulty_ratios);
INSERT INTO category_type_ratio (id, category_id, type_ratios, difficulty_ratios) VALUES (3, 1701,
  '{"SINGLE_CHOICE":30,"MULTI_CHOICE":10,"JUDGE":10,"FILL_BLANK":10,"DICTATION":10,"SHORT_ANSWER":10,"ESSAY":10,"MATERIAL_ANALYSIS":10}',
  '{"1":40,"2":40,"3":20}')
ON DUPLICATE KEY UPDATE type_ratios=VALUES(type_ratios), difficulty_ratios=VALUES(difficulty_ratios);
INSERT INTO category_type_ratio (id, category_id, type_ratios, difficulty_ratios) VALUES (4, 1702,
  '{"SINGLE_CHOICE":30,"MULTI_CHOICE":10,"JUDGE":10,"FILL_BLANK":15,"CALCULATION":20,"PROOF":10,"DERIVATION":5}',
  '{"1":35,"2":40,"3":25}')
ON DUPLICATE KEY UPDATE type_ratios=VALUES(type_ratios), difficulty_ratios=VALUES(difficulty_ratios);
INSERT INTO category_type_ratio (id, category_id, type_ratios, difficulty_ratios) VALUES (5, 1301,
  '{"SINGLE_CHOICE":40,"MULTI_CHOICE":10,"JUDGE":10,"FILL_BLANK":20,"DICTATION":10,"SHORT_ANSWER":10}',
  '{"1":50,"2":35,"3":15}')
ON DUPLICATE KEY UPDATE type_ratios=VALUES(type_ratios), difficulty_ratios=VALUES(difficulty_ratios);
INSERT INTO category_type_ratio (id, category_id, type_ratios, difficulty_ratios) VALUES (6, 1302,
  '{"SINGLE_CHOICE":40,"MULTI_CHOICE":10,"JUDGE":10,"FILL_BLANK":20,"CALCULATION":20}',
  '{"1":50,"2":35,"3":15}')
ON DUPLICATE KEY UPDATE type_ratios=VALUES(type_ratios), difficulty_ratios=VALUES(difficulty_ratios);

-- ============================================================
-- 示例题目（挂在叶子分类上）
-- ============================================================

-- 63小学一年级语文（category_id=1101）
INSERT INTO question (id, category_id, type, difficulty, content, options, answer, knowledge_tags, score) VALUES (1, 1101, 'SINGLE_CHOICE', 1, '"天"字有几画？', '["A. 3画","B. 4画","C. 5画","D. 6画"]', 'B', '["汉字","笔画"]', 5.0)
ON DUPLICATE KEY UPDATE content=VALUES(content), options=VALUES(options), answer=VALUES(answer), knowledge_tags=VALUES(knowledge_tags), score=VALUES(score);
INSERT INTO question (id, category_id, type, difficulty, content, options, answer, knowledge_tags, score) VALUES (2, 1101, 'SINGLE_CHOICE', 1, '下面哪个是正确的拼音？', '["A. tian","B. tian1","C. tiān","D. t-i-an"]', 'C', '["拼音","声调"]', 5.0)
ON DUPLICATE KEY UPDATE content=VALUES(content), options=VALUES(options), answer=VALUES(answer), knowledge_tags=VALUES(knowledge_tags), score=VALUES(score);
INSERT INTO question (id, category_id, type, difficulty, content, options, answer, knowledge_tags, score) VALUES (3, 1101, 'JUDGE', 1, '"日"和"目"是同一个字', NULL, 'false', '["汉字","形近字"]', 5.0)
ON DUPLICATE KEY UPDATE content=VALUES(content), answer=VALUES(answer), knowledge_tags=VALUES(knowledge_tags), score=VALUES(score);
INSERT INTO question (id, category_id, type, difficulty, content, options, answer, knowledge_tags, score) VALUES (4, 1101, 'FILL_BLANK', 1, '春天来了，小草从地里____出来', NULL, '长', '["词语","季节"]', 5.0)
ON DUPLICATE KEY UPDATE content=VALUES(content), answer=VALUES(answer), knowledge_tags=VALUES(knowledge_tags), score=VALUES(score);

-- 63小学一年级数学（category_id=1102）
INSERT INTO question (id, category_id, type, difficulty, content, options, answer, knowledge_tags, score) VALUES (5, 1102, 'SINGLE_CHOICE', 1, '3 + 5 = ?', '["A. 7","B. 8","C. 9","D. 10"]', 'B', '["加法","计算"]', 5.0)
ON DUPLICATE KEY UPDATE content=VALUES(content), options=VALUES(options), answer=VALUES(answer), knowledge_tags=VALUES(knowledge_tags), score=VALUES(score);
INSERT INTO question (id, category_id, type, difficulty, content, options, answer, knowledge_tags, score) VALUES (6, 1102, 'SINGLE_CHOICE', 1, '下面哪个数最大？', '["A. 12","B. 8","C. 15","D. 9"]', 'C', '["比大小","数感"]', 5.0)
ON DUPLICATE KEY UPDATE content=VALUES(content), options=VALUES(options), answer=VALUES(answer), knowledge_tags=VALUES(knowledge_tags), score=VALUES(score);
INSERT INTO question (id, category_id, type, difficulty, content, options, answer, knowledge_tags, score) VALUES (7, 1102, 'JUDGE', 1, '10 - 3 = 8', NULL, 'false', '["减法","计算"]', 5.0)
ON DUPLICATE KEY UPDATE content=VALUES(content), answer=VALUES(answer), knowledge_tags=VALUES(knowledge_tags), score=VALUES(score);
INSERT INTO question (id, category_id, type, difficulty, content, options, answer, knowledge_tags, score) VALUES (8, 1102, 'FILL_BLANK', 1, '7 + ____ = 10', NULL, '3', '["加法","填空"]', 5.0)
ON DUPLICATE KEY UPDATE content=VALUES(content), answer=VALUES(answer), knowledge_tags=VALUES(knowledge_tags), score=VALUES(score);

-- 63初中七年级语文（category_id=1701）
INSERT INTO question (id, category_id, type, difficulty, content, options, answer, knowledge_tags, score) VALUES (9, 1701, 'SINGLE_CHOICE', 2, '《春》的作者是？', '["A. 老舍","B. 朱自清","C. 鲁迅","D. 巴金"]', 'B', '["文学常识","作家作品"]', 5.0)
ON DUPLICATE KEY UPDATE content=VALUES(content), options=VALUES(options), answer=VALUES(answer), knowledge_tags=VALUES(knowledge_tags), score=VALUES(score);
INSERT INTO question (id, category_id, type, difficulty, content, options, answer, knowledge_tags, score) VALUES (10, 1701, 'SINGLE_CHOICE', 2, '下面哪个成语表示"形容事物非常微小"？', '["A. 微不足道","B. 庞然大物","C. 一目了然","D. 翻天覆地"]', 'A', '["成语","词义"]', 5.0)
ON DUPLICATE KEY UPDATE content=VALUES(content), options=VALUES(options), answer=VALUES(answer), knowledge_tags=VALUES(knowledge_tags), score=VALUES(score);
INSERT INTO question (id, category_id, type, difficulty, content, options, answer, knowledge_tags, score) VALUES (11, 1701, 'FILL_BLANK', 2, '"学而不思则罔，思而不学则____"出自《论语》', NULL, '殆', '["古诗词","论语","默写"]', 5.0)
ON DUPLICATE KEY UPDATE content=VALUES(content), answer=VALUES(answer), knowledge_tags=VALUES(knowledge_tags), score=VALUES(score);
INSERT INTO question (id, category_id, type, difficulty, content, options, answer, analysis, knowledge_tags, score) VALUES (12, 1701, 'SHORT_ANSWER', 3, '请简述朱自清《春》中"春草图"的主要内容。', NULL, '作者描写了春草嫩绿、柔软、充满生机的特点，人们在草地上嬉戏玩耍的场景，表达了对春天的喜爱之情。', '春草图是《春》中五幅春景图之一', '["现代文阅读","概括"]', 10.0)
ON DUPLICATE KEY UPDATE content=VALUES(content), answer=VALUES(answer), analysis=VALUES(analysis), knowledge_tags=VALUES(knowledge_tags), score=VALUES(score);

-- 63初中七年级数学（category_id=1702）
INSERT INTO question (id, category_id, type, difficulty, content, options, answer, knowledge_tags, score) VALUES (13, 1702, 'SINGLE_CHOICE', 2, '(-3)² 的值是？', '["A. -9","B. 9","C. -6","D. 6"]', 'B', '["有理数","乘方"]', 5.0)
ON DUPLICATE KEY UPDATE content=VALUES(content), options=VALUES(options), answer=VALUES(answer), knowledge_tags=VALUES(knowledge_tags), score=VALUES(score);
INSERT INTO question (id, category_id, type, difficulty, content, options, answer, knowledge_tags, score) VALUES (14, 1702, 'SINGLE_CHOICE', 2, '下列哪个是有理数？', '["A. √2","B. π","C. -3/7","D. √3"]', 'C', '["有理数","实数"]', 5.0)
ON DUPLICATE KEY UPDATE content=VALUES(content), options=VALUES(options), answer=VALUES(answer), knowledge_tags=VALUES(knowledge_tags), score=VALUES(score);
INSERT INTO question (id, category_id, type, difficulty, content, options, answer, knowledge_tags, score) VALUES (15, 1702, 'JUDGE', 2, '所有的整数都是有理数', NULL, 'true', '["有理数","整数"]', 5.0)
ON DUPLICATE KEY UPDATE content=VALUES(content), answer=VALUES(answer), knowledge_tags=VALUES(knowledge_tags), score=VALUES(score);
INSERT INTO question (id, category_id, type, difficulty, content, options, answer, analysis, knowledge_tags, score) VALUES (16, 1702, 'CALCULATION', 3, '解方程：2x + 5 = 17', NULL, 'x = 6', '移项得 2x = 17 - 5 = 12，所以 x = 6', '["一元一次方程","计算"]', 10.0)
ON DUPLICATE KEY UPDATE content=VALUES(content), answer=VALUES(answer), analysis=VALUES(analysis), knowledge_tags=VALUES(knowledge_tags), score=VALUES(score);

-- 63初中七年级英语（category_id=1703）
INSERT INTO question (id, category_id, type, difficulty, content, options, answer, knowledge_tags, score) VALUES (17, 1703, 'SINGLE_CHOICE', 1, 'What is the plural form of "child"?', '["A. childs","B. children","C. childrens","D. childes"]', 'B', '["名词","复数"]', 5.0)
ON DUPLICATE KEY UPDATE content=VALUES(content), options=VALUES(options), answer=VALUES(answer), knowledge_tags=VALUES(knowledge_tags), score=VALUES(score);
INSERT INTO question (id, category_id, type, difficulty, content, options, answer, knowledge_tags, score) VALUES (18, 1703, 'SINGLE_CHOICE', 1, 'She ____ to school every day.', '["A. go","B. goes","C. going","D. went"]', 'B', '["一般现在时","三单"]', 5.0)
ON DUPLICATE KEY UPDATE content=VALUES(content), options=VALUES(options), answer=VALUES(answer), knowledge_tags=VALUES(knowledge_tags), score=VALUES(score);
INSERT INTO question (id, category_id, type, difficulty, content, options, answer, knowledge_tags, score) VALUES (19, 1703, 'FILL_BLANK', 1, 'The opposite of "hot" is ____', NULL, 'cold', '["反义词","词汇"]', 5.0)
ON DUPLICATE KEY UPDATE content=VALUES(content), answer=VALUES(answer), knowledge_tags=VALUES(knowledge_tags), score=VALUES(score);
INSERT INTO question (id, category_id, type, difficulty, content, options, answer, knowledge_tags, score) VALUES (20, 1703, 'JUDGE', 1, '"I am" 的缩写形式是 "I\'m"', NULL, 'true', '["缩写","语法"]', 5.0)
ON DUPLICATE KEY UPDATE content=VALUES(content), answer=VALUES(answer), knowledge_tags=VALUES(knowledge_tags), score=VALUES(score);

-- 63初中八年级物理（category_id=1804）
INSERT INTO question (id, category_id, type, difficulty, content, options, answer, knowledge_tags, score) VALUES (21, 1804, 'SINGLE_CHOICE', 2, '力的国际单位是？', '["A. 千克","B. 牛顿","C. 焦耳","D. 瓦特"]', 'B', '["力","单位"]', 5.0)
ON DUPLICATE KEY UPDATE content=VALUES(content), options=VALUES(options), answer=VALUES(answer), knowledge_tags=VALUES(knowledge_tags), score=VALUES(score);
INSERT INTO question (id, category_id, type, difficulty, content, options, answer, knowledge_tags, score) VALUES (22, 1804, 'SINGLE_CHOICE', 2, '下列哪个不是机械运动？', '["A. 汽车行驶","B. 小鸟飞行","C. 水结成冰","D. 地球自转"]', 'C', '["机械运动","物态变化"]', 5.0)
ON DUPLICATE KEY UPDATE content=VALUES(content), options=VALUES(options), answer=VALUES(answer), knowledge_tags=VALUES(knowledge_tags), score=VALUES(score);
INSERT INTO question (id, category_id, type, difficulty, content, options, answer, knowledge_tags, score) VALUES (23, 1804, 'JUDGE', 2, '物体的惯性与其速度有关', NULL, 'false', '["惯性","牛顿定律"]', 5.0)
ON DUPLICATE KEY UPDATE content=VALUES(content), answer=VALUES(answer), knowledge_tags=VALUES(knowledge_tags), score=VALUES(score);
INSERT INTO question (id, category_id, type, difficulty, content, options, answer, analysis, knowledge_tags, score) VALUES (24, 1804, 'CALCULATION', 3, '一个质量为2kg的物体受到10N的力作用，求其加速度。', NULL, 'a = F/m = 10/2 = 5 m/s²', '根据牛顿第二定律 F=ma，a=F/m', '["牛顿第二定律","计算"]', 10.0)
ON DUPLICATE KEY UPDATE content=VALUES(content), answer=VALUES(answer), analysis=VALUES(analysis), knowledge_tags=VALUES(knowledge_tags), score=VALUES(score);

-- 63初中九年级化学（category_id=1905）
INSERT INTO question (id, category_id, type, difficulty, content, options, answer, knowledge_tags, score) VALUES (25, 1905, 'SINGLE_CHOICE', 2, '水的化学式是？', '["A. CO2","B. H2O","C. O2","D. NaCl"]', 'B', '["化学式","水"]', 5.0)
ON DUPLICATE KEY UPDATE content=VALUES(content), options=VALUES(options), answer=VALUES(answer), knowledge_tags=VALUES(knowledge_tags), score=VALUES(score);
INSERT INTO question (id, category_id, type, difficulty, content, options, answer, knowledge_tags, score) VALUES (26, 1905, 'SINGLE_CHOICE', 2, '下列哪个是化学变化？', '["A. 冰融化","B. 水蒸发","C. 铁生锈","D. 玻璃破碎"]', 'C', '["物理变化","化学变化"]', 5.0)
ON DUPLICATE KEY UPDATE content=VALUES(content), options=VALUES(options), answer=VALUES(answer), knowledge_tags=VALUES(knowledge_tags), score=VALUES(score);
INSERT INTO question (id, category_id, type, difficulty, content, options, answer, knowledge_tags, score) VALUES (27, 1905, 'FILL_BLANK', 2, '元素周期表中，氧元素的原子序数是____', NULL, '8', '["元素周期表","原子序数"]', 5.0)
ON DUPLICATE KEY UPDATE content=VALUES(content), answer=VALUES(answer), knowledge_tags=VALUES(knowledge_tags), score=VALUES(score);
INSERT INTO question (id, category_id, type, difficulty, content, options, answer, knowledge_tags, score) VALUES (28, 1905, 'JUDGE', 2, '所有的燃烧都需要氧气', NULL, 'false', '["燃烧","氧化反应"]', 5.0)
ON DUPLICATE KEY UPDATE content=VALUES(content), answer=VALUES(answer), knowledge_tags=VALUES(knowledge_tags), score=VALUES(score);

-- ============================================================
-- 小章节级别示例题目（挂在子分类叶子节点上）
-- ============================================================

-- 三年级语文·上册·第一单元·大青树下的小学（1301111）
INSERT INTO question (id, category_id, type, difficulty, content, options, answer, knowledge_tags, score) VALUES (29, 1301111, 'SINGLE_CHOICE', 1, '"大青树下的小学"中，学生来自几个民族？', '["A. 3个","B. 4个","C. 5个","D. 6个"]', 'C', '["阅读理解","细节"]', 5.0)
ON DUPLICATE KEY UPDATE content=VALUES(content), options=VALUES(options), answer=VALUES(answer), knowledge_tags=VALUES(knowledge_tags), score=VALUES(score);
INSERT INTO question (id, category_id, type, difficulty, content, options, answer, knowledge_tags, score) VALUES (30, 1301111, 'FILL_BLANK', 1, '上课了，不同民族的小学生，在同一间教室里____', NULL, '学习', '["词语","填空"]', 5.0)
ON DUPLICATE KEY UPDATE content=VALUES(content), answer=VALUES(answer), knowledge_tags=VALUES(knowledge_tags), score=VALUES(score);
INSERT INTO question (id, category_id, type, difficulty, content, options, answer, knowledge_tags, score) VALUES (31, 1301111, 'JUDGE', 1, '大青树下的小学是一所边疆小学', NULL, 'true', '["阅读理解","判断"]', 5.0)
ON DUPLICATE KEY UPDATE content=VALUES(content), answer=VALUES(answer), knowledge_tags=VALUES(knowledge_tags), score=VALUES(score);

-- 三年级语文·上册·第一单元·花的学校（1301112）
INSERT INTO question (id, category_id, type, difficulty, content, options, answer, knowledge_tags, score) VALUES (32, 1301112, 'SINGLE_CHOICE', 1, '《花的学校》作者是谁？', '["A. 冰心","B. 泰戈尔","C. 朱自清","D. 老舍"]', 'B', '["文学常识","作家"]', 5.0)
ON DUPLICATE KEY UPDATE content=VALUES(content), options=VALUES(options), answer=VALUES(answer), knowledge_tags=VALUES(knowledge_tags), score=VALUES(score);

-- 三年级语文·上册·第二单元·古诗三首（1301121）
INSERT INTO question (id, category_id, type, difficulty, content, options, answer, knowledge_tags, score) VALUES (33, 1301121, 'SINGLE_CHOICE', 1, '"停车坐爱枫林晚"出自哪首诗？', '["A. 《赠刘景文》","B. 《山行》","C. 《夜书所见》","D. 《绝句》"]', 'B', '["古诗词","出处"]', 5.0)
ON DUPLICATE KEY UPDATE content=VALUES(content), options=VALUES(options), answer=VALUES(answer), knowledge_tags=VALUES(knowledge_tags), score=VALUES(score);
INSERT INTO question (id, category_id, type, difficulty, content, options, answer, knowledge_tags, score) VALUES (34, 1301121, 'FILL_BLANK', 1, '"霜叶红于二月____"', NULL, '花', '["古诗词","默写"]', 5.0)
ON DUPLICATE KEY UPDATE content=VALUES(content), answer=VALUES(answer), knowledge_tags=VALUES(knowledge_tags), score=VALUES(score);

-- 三年级数学·上册·第一单元·秒的认识（1302111）
INSERT INTO question (id, category_id, type, difficulty, content, options, answer, knowledge_tags, score) VALUES (35, 1302111, 'SINGLE_CHOICE', 1, '1分钟等于多少秒？', '["A. 10秒","B. 30秒","C. 60秒","D. 100秒"]', 'C', '["时间","单位换算"]', 5.0)
ON DUPLICATE KEY UPDATE content=VALUES(content), options=VALUES(options), answer=VALUES(answer), knowledge_tags=VALUES(knowledge_tags), score=VALUES(score);
INSERT INTO question (id, category_id, type, difficulty, content, options, answer, knowledge_tags, score) VALUES (36, 1302111, 'JUDGE', 1, '秒是比分更小的时间单位', NULL, 'true', '["时间","概念"]', 5.0)
ON DUPLICATE KEY UPDATE content=VALUES(content), answer=VALUES(answer), knowledge_tags=VALUES(knowledge_tags), score=VALUES(score);

-- 三年级数学·上册·第一单元·时间的计算（1302112）
INSERT INTO question (id, category_id, type, difficulty, content, options, answer, knowledge_tags, score) VALUES (37, 1302112, 'SINGLE_CHOICE', 1, '2分30秒等于多少秒？', '["A. 120秒","B. 130秒","C. 140秒","D. 150秒"]', 'D', '["时间","计算"]', 5.0)
ON DUPLICATE KEY UPDATE content=VALUES(content), options=VALUES(options), answer=VALUES(answer), knowledge_tags=VALUES(knowledge_tags), score=VALUES(score);

-- 七年级数学·上册·第一章·正数和负数（1702111）
INSERT INTO question (id, category_id, type, difficulty, content, options, answer, knowledge_tags, score) VALUES (38, 1702111, 'SINGLE_CHOICE', 2, '下列哪个是负数？', '["A. 0","B. 5","C. -3","D. 1.5"]', 'C', '["正负数","概念"]', 5.0)
ON DUPLICATE KEY UPDATE content=VALUES(content), options=VALUES(options), answer=VALUES(answer), knowledge_tags=VALUES(knowledge_tags), score=VALUES(score);
INSERT INTO question (id, category_id, type, difficulty, content, options, answer, knowledge_tags, score) VALUES (39, 1702111, 'FILL_BLANK', 2, '如果收入100元记作+100，那么支出50元记作____', NULL, '-50', '["正负数","应用"]', 5.0)
ON DUPLICATE KEY UPDATE content=VALUES(content), answer=VALUES(answer), knowledge_tags=VALUES(knowledge_tags), score=VALUES(score);

-- 七年级数学·上册·第一章·有理数的加减法（1702112）
INSERT INTO question (id, category_id, type, difficulty, content, options, answer, knowledge_tags, score) VALUES (40, 1702112, 'SINGLE_CHOICE', 2, '(-5) + 8 = ?', '["A. 13","B. 3","C. -3","D. -13"]', 'B', '["有理数","加法"]', 5.0)
ON DUPLICATE KEY UPDATE content=VALUES(content), options=VALUES(options), answer=VALUES(answer), knowledge_tags=VALUES(knowledge_tags), score=VALUES(score);
