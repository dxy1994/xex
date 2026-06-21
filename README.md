# 学而习

> AI 驱动的智能在线试题学习平台 —— 支持试题生成、答案检查、试题讲解

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Vue](https://img.shields.io/badge/Vue-3.5-42b883.svg)](https://vuejs.org/)
[![JDK](https://img.shields.io/badge/JDK-17-orange.svg)](https://openjdk.org/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-blue.svg)](https://www.mysql.com/)
[![Redis](https://img.shields.io/badge/Redis-7-red.svg)](https://redis.io/)

---

## 项目简介

**学而习** 是一个基于 DeepSeek 大模型的智能试题平台，涵盖试题生成、智能判题、逐题讲解三大 AI 能力，支持多学科（语文、数学、英语、物理、化学）题型体系。用户可在线答题，系统自动评分并展示详细解析。

### 核心功能

- **🧠 AI 试题生成** — 根据知识点、难度自动出题，支持选择题/填空题/判断题/应用题等多种题型
- **✅ 智能答案检查** — AI 阅卷，支持客观题精确匹配与主观题语义评判，流式输出判分结果
- **📖 试题逐题讲解** — 流式输出考点分析、解题思路、详细步骤、知识点总结
- **📊 题型比例配置** — 灵活配置各类题型占比，支持随机组卷与固定组卷
- **🌲 树形分类导航** — 学制 → 年级 → 课程 → 分册四级分类体系
- **👥 双端应用** — 学生端在线答题 + 管理端题库维护
- **🔐 多方式登录** — 支持账号密码登录、微信扫码登录
- **🌱 种子号分享** — 同一试卷可通过种子号分享给他人作答

### 项目名称由来

取自《论语·学而》开篇 —— "**学而时习之，不亦说乎**"，寓意学习与练习相辅相成。

---

## 技术栈

### 后端

| 技术 | 说明 |
|------|------|
| Spring Boot 3.2.5 | 核心框架 |
| MyBatis-Plus 3.5.5 | ORM 框架 |
| MySQL 8.0 | 关系数据库 |
| Redis 7 | 缓存 / Session |
| Spring Security + JWT | 认证授权 |
| DeepSeek API | AI 大模型 |
| Maven | 项目构建 |

### 前端

| 技术 | 说明 |
|------|------|
| Vue 3.5 | 前端框架 |
| Vite 8 | 构建工具 |
| Element Plus 2.14 | UI 组件库 |
| Vue Router 4 | 路由管理 |
| Pinia 3 | 状态管理 |
| Axios | HTTP 客户端 |

### 开发环境

| 工具 | 说明 |
|------|------|
| Docker Compose | 基础设施容器化 |
| JDK 17 | Java 运行环境 |
| PowerShell / Bash | 脚本执行 |

---

## 项目结构

```
exam-questions/
├── backend/                        # 后端 Maven 多模块项目
│   ├── exam-common/                # 公共模块（实体、DTO、Mapper、Service、安全）
│   ├── exam-web/                   # 学生端 API 服务（端口 8080）
│   ├── exam-admin/                 # 管理端 API 服务（端口 8081）
│   ├── ai/                         # AI 模块（试题生成、答案检查、试题讲解）
│   └── pom.xml                     # 父 POM
├── frontend-web/                   # 学生端前端（Vue 3 + Element Plus）
│   └── src/
│       ├── api/                    # API 请求封装
│       ├── views/                  # 页面组件
│       ├── components/             # 公共组件
│       ├── stores/                 # Pinia 状态管理
│       └── router/                 # 路由配置
├── frontend-admin/                 # 管理端前端（Vue 3 + Element Plus）
│   └── src/views/                  # 管理页面
├── other/                          # 数据库初始化脚本
│   ├── schema.sql                  # 表结构
│   └── data.sql                    # 初始数据
├── docker-compose.yml              # Docker Compose 编排
├── start-infra.bat                 # 基础设施启动脚本（Windows）
└── README.md
```

---

## 快速开始

### 前置要求

- **JDK 17+**
- **Docker Desktop**（用于启动 MySQL 和 Redis）
- **Maven 3.8+**（或使用 IDE 内置 Maven）
- **Node.js 18+** / **pnpm**

### 1. 启动基础设施

```bash
# Windows
.\start-infra.bat

# 或直接使用 Docker Compose
docker-compose up -d
```

脚本会启动 MySQL 8.0 和 Redis 7 容器，并自动初始化数据库表结构和初始数据。等待输出 `[OK] 所有服务已就绪！` 后即可启动后端。

> **连接信息：**
> - MySQL: `localhost:3306` 数据库 `exam_questions` 用户 `root` / `root123456`
> - Redis: `localhost:6379` 密码 `redis123456`

### 2. 配置 DeepSeek API Key

```bash
# 方式一：设置环境变量（推荐）
set DEEPSEEK_API_KEY=你的API密钥

# 方式二：直接修改 application.yml
# backend/exam-web/src/main/resources/application.yml
# backend/exam-admin/src/main/resources/application.yml
# 找到 spring.ai.deepseek.api-key 字段
```

> 申请地址：[https://platform.deepseek.com/api_keys](https://platform.deepseek.com/api_keys)

### 3. 启动后端服务

```bash
cd backend

# 启动学生端（端口 8080）
mvn -pl exam-web spring-boot:run

# 启动管理端（端口 8081）
mvn -pl exam-admin spring-boot:run
```

### 4. 启动前端

```bash
# 学生端（端口 5173）
cd frontend-web
pnpm install
pnpm dev

# 管理端（端口 5174）
cd frontend-admin
pnpm install
pnpm dev
```

### 5. 访问应用

| 应用 | 地址 |
|------|------|
| 学生端 | [http://localhost:5173](http://localhost:5173) |
| 管理端 | [http://localhost:5174](http://localhost:5174) |
| 学生端 API | [http://localhost:8080](http://localhost:8080) |
| 管理端 API | [http://localhost:8081](http://localhost:8081) |

---

## AI 模块

### 模型架构

项目采用 **多模型路由 + 科目 Agent** 的可插拔架构：

```
请求 → SubjectAgentRegistry（科目路由）
         ├── MathAgent（数学）
         ├── ChineseAgent（语文）
         ├── EnglishAgent（英语）
         ├── PhysicsAgent（物理）
         ├── ChemistryAgent（化学）
         └── 默认 Agent（泛用）
              ↓
         ModelRouter（模型路由）
              ├── deepseek-chat（快速模型：答案检查）
              └── deepseek-reasoner（推理模型：试题生成、讲解）
```

### API 端点

| 端点 | 方法 | 说明 |
|------|------|------|
| `/api/ai/generate-questions` | POST | 按学科/知识点生成试题 |
| `/api/ai/check-answer` | POST | 检查答案（同步） |
| `/api/ai/check-answer/stream` | POST | 检查答案（流式 SSE） |
| `/api/ai/explain-question` | POST | 试题讲解（同步） |
| `/api/ai/explain-question/stream` | POST | 试题讲解（流式 SSE） |
| `/api/ai/subjects` | GET | 获取已注册科目及题型 |
| `/api/ai/health` | GET | 健康检查 |

### 流式输出

答案检查和试题讲解支持 **SSE（Server-Sent Events）** 流式输出，AI 生成的内容可逐字实时展示，提升用户体验。

```javascript
import { checkAnswerStream, explainQuestionStream } from '@/api'

// 流式检查答案
const controller = checkAnswerStream(
  { questionId, questionType, questionContent, correctAnswer, userAnswer, subjectName },
  (chunk) => console.log('收到文本块:', chunk),   // 逐块回调
  () => console.log('流结束'),                    // 完成回调
  (err) => console.error('出错:', err)            // 错误回调
)

// 中途取消
controller.abort()
```

---

## 题型体系

系统支持以下题型，不同学科有各自适配的题型组合：

| 题型代码 | 标签 | 说明 |
|----------|------|------|
| `SINGLE_CHOICE` | 单选题 | 唯一正确答案 |
| `MULTI_CHOICE` | 多选题 | 多个正确答案 |
| `JUDGE` | 判断题 | 正确/错误 |
| `FILL_BLANK` | 填空题 | 关键词/数值填空 |
| `CALCULATION` | 计算题 | 数学/物理计算 |
| `PROOF` | 证明题 | 几何/代数证明 |
| `DERIVATION` | 推导题 | 公式定理推导 |
| `APPLICATION` | 应用题 | 综合应用分析 |
| `EXPERIMENT` | 实验题 | 理化实验设计 |
| `ESSAY` | 论述题 | 语文作文/论述 |
| `TRANSLATION` | 翻译题 | 英语翻译 |

---

## 配置说明

### 模型路由规则

在 `ModelRouter.java` 中可自定义任务到模型的映射：

```java
case CHECK_ANSWER -> "chat";                          // 快速、低成本
case GENERATE_QUESTION, EXPLAIN_QUESTION -> "reasoner"; // 深度推理
```

### 模型参数

在 `application.yml` 中配置各模型的参数：

```yaml
spring:
  ai:
    deepseek:
      api-key: ${DEEPSEEK_API_KEY:your-api-key}
      models:
        chat:
          model: deepseek-chat
          temperature: 0.3
          max-tokens: 4096
        reasoner:
          model: deepseek-reasoner
          temperature: 0.7
          max-tokens: 8192
```

---

## 微信登录配置

```yaml
wechat:
  appid: your_wechat_appid
  secret: your_wechat_secret
  redirect-uri: http://localhost:5173/auth/wechat/callback
```

需在 [微信开放平台](https://open.weixin.qq.com/) 申请网站应用接入。

---

## 常见问题

**Q: 启动报错 "DEEPSEEK_API_KEY 未设置"**
A: 设置环境变量 `DEEPSEEK_API_KEY` 或在 `application.yml` 中修改 `api-key` 字段。

**Q: 前端页面空白或 API 请求 404**
A: 检查 Vite 代理配置（`vite.config.js`）是否正确指向后端地址。

**Q: Docker 容器启动失败**
A: 确保 Docker Desktop 正在运行，且 3306/6379 端口未被占用。

**Q: Maven 依赖下载失败**
A: 检查 Maven 镜像配置，DeepSeek 客户端仅为 HTTP 调用，无需额外 SDK。

---

## 开发计划

> 按实现难度从低到高排列，每个阶段可在上一阶段完成后独立推进。

### 难度等级

| 标记 | 含义 | 说明 |
|------|------|------|
| ⭐ | 低难度 | 数据梳理与配置为主，少量编码 |
| ⭐⭐ | 中难度 | 中等开发量，涉及算法或外部系统对接 |
| ⭐⭐⭐ | 高难度 | 大量开发工作，需突破技术限制或复杂架构设计 |

---

### 第一阶段：基础数据完善 ⭐

> **目标**：构建完整的 K12 学科章节-知识点数据骨架，覆盖小学、初中、高中全学段。

#### 1.1 完善科目章节列表

当前仅三年级语文/数学、七年级数学有完整章节（分册→大章节→小章节），其余课程仅有课程名。

**待完成：**

- 为小学 1~6 年级所有课程（语/数/英）补充分册→单元→课文/知识点层级
- 为初中 7~9 年级所有课程（语/数/英/物/化）补充分册→章→节层级
- 新增初中科目：生物、政治（道德与法治）、历史、地理
- 统一章节命名规范，与主流教材版本对齐（人教版为主，预留多版本扩展）

**涉及文件：** `other/data.sql`（分类数据 INSERT）

#### 1.2 高中课程扩展

当前分类体系仅覆盖小学+初中，需扩展高中阶段。

**待完成：**

- 新增 `33学制`（高中3年）作为顶级学制分类
- 添加高一~高三年级（十年级~十二年级）
- 添加高中课程：语文、数学、英语、物理、化学、生物、政治、历史、地理
- 按高中新课标补充分册（必修/选择性必修/选修）→章→节结构
- 高中专属题型适配（如：读后续写、实验探究、材料分析等）

**涉及文件：** `other/data.sql`、`other/schema.sql`（如需新增字段）、`backend/ai/`（新增高中 Agent）

#### 1.3 完善章节知识点标签体系

当前 `knowledge_tags` 字段仅在示例题目中散落使用，缺少系统化的知识点标签库。

**待完成：**

- 为每个小章节（叶子节点）定义 3~8 个核心知识点标签
- 建立知识点间的依赖关系（前置知识→后续知识），支撑学习路径规划
- 设计知识点标签层级（一级：学科大类，二级：知识模块，三级：具体知识点）
- 标签示例：`数学 > 代数 > 一元二次方程 > 求根公式`
- 新增 `knowledge_point` 表持久化知识点及其层级关系

**涉及文件：** `other/schema.sql`（新表）、`other/data.sql`（知识点数据）、`backend/exam-common/`（实体/Mapper）

---

### 第二阶段：试题库建设 ⭐⭐

> **目标**：通过爬虫+AI构建规模化、结构化的试题库，实现试题自动分类与知识点标注。

#### 2.1 爬虫试题采集系统

**待完成：**

- 设计通用爬虫框架（支持多数据源扩展）
- 对接公开试题资源网站，抓取试题内容、选项、答案、解析
- 数据清洗与格式化：去重、HTML标签清理、公式标准化（LaTeX）
- 增量抓取策略：定时任务 + 变更检测，避免重复入库
- 反爬应对：请求频率控制、User-Agent轮换、验证码识别预留
- 新增 `question_source` 字段记录试题来源（抓取/AI生成/人工录入）

**涉及文件：** 新建 `backend/exam-crawler/` 模块、`other/schema.sql`（字段扩展）

#### 2.2 AI辅助试题自动分类

**待完成：**

- 基于试题内容+知识点标签库，用 AI 自动匹配所属章节（category_id）
- 自动判定难度等级（1-简单 / 2-中等 / 3-困难），参考标准：
  - 简单：单一知识点、直接应用
  - 中等：2~3个知识点综合、需要分析推理
  - 困难：多知识点融合、复杂推理或创新应用
- 自动提取/补全知识点标签（knowledge_tags）
- 分类结果人工审核流程：低置信度试题标记为"待审核"
- 批量处理流水线：爬虫入库 → AI分类 → 自动入库 / 人工审核

**涉及文件：** `backend/ai/`（新增 `QuestionClassifier` 服务）、`backend/exam-admin/`（审核接口）

#### 2.3 知识点考察维度分析

**待完成：**

- 对每道试题进行考察维度拆解（布鲁姆认知层次）：
  - **识记**：对知识点的记忆与再现
  - **理解**：对概念/原理的理解与解释
  - **应用**：用知识解决具体问题
  - **分析**：分解复杂问题、识别关系
  - **综合**：整合多知识点创造性解决
  - **评价**：基于标准做出判断
- AI 自动标注考察维度（单选/多选），支持人工调整
- 组卷时展示知识点覆盖热力图，避免偏题
- 考试报告中按维度分析学生薄弱环节

**涉及文件：** `backend/ai/`（维度分析 Prompt）、`frontend-admin/`（可视化图表）

---

### 第三阶段：学习资源生态 ⭐⭐⭐

> **目标**：打通外部学习平台，构建"练-学-复"闭环，探索多模态试题支持。

#### 3.1 关联国家中小学智慧教育平台

**待完成：**

- 研究平台页面结构/API，获取课程视频、课件等资源链接
- 建立知识点 ↔ 平台资源的映射关系（按章节/知识点匹配）
- 在试题解析/讲解页嵌入平台资源卡片（视频讲解、微课等）
- 支持一键跳转至平台对应课程页面
- 注意：遵守平台使用条款，仅提供导航链接不缓存内容

**涉及文件：** `frontend-web/`（资源卡片组件）、`backend/exam-web/`（资源匹配接口）

#### 3.2 B站教育视频资源整合

**待完成：**

- 对接B站开放 API，按知识点关键词检索教育类视频
- 建立"知识点 → B站视频"推荐索引（按播放量/点赞数排序）
- 在复习/预习场景中展示相关视频（如：一元二次方程 → 精选讲解视频）
- 支持视频预览播放（B站嵌入式播放器）
- 用户可收藏视频，形成个人学习播放列表
- 缓存视频元数据（标题、封面、UP主、时长），定期更新

**涉及文件：** `backend/exam-web/`（B站API对接）、`frontend-web/`（视频卡片/播放器）、`other/schema.sql`（视频索引表）

#### 3.3 图片试题支持探索

> ⚠️ **当前限制**：DeepSeek 等基础模型为纯文本模型，无法直接解析或生成包含图片的试题（如几何图形、化学结构式、地理图表、物理电路图等）。

**近期方案（绕过限制）：**

- 图片试题以"文本描述+图片URL"形式存储，AI只处理文本部分
- 几何题：用 LaTeX TikZ / ASCII Art 替代简单图形
- 图表题：将图表数据转为 Markdown 表格供 AI 分析

**中长期方案（需技术升级）：**

- 接入多模态模型（如 GPT-4V / Qwen-VL）实现图片→文本理解
- 引入 OCR 模块识别试题图片中的文字和公式
- 使用 AI 绘图生成试题配图（如函数图像、几何图形）
- 新增 `question_images` 表存储试题关联图片

**涉及文件：** `other/schema.sql`（新表）、`backend/ai/`（多模态模型适配）

---

### 实施路线图

```
第一阶段（1-2周）          第二阶段（2-4周）             第三阶段（4-8周）
├─ 章节列表完善            ├─ 爬虫框架开发              ├─ 智慧教育平台对接
├─ 高中课程扩展            ├─ AI自动分类                ├─ B站视频整合
└─ 知识点标签体系          ├─ 考察维度分析              └─ 图片试题方案
                           └─ 试题库规模化入库
```

| 阶段 | 预估工期 | 核心产出 | 依赖 |
|------|----------|----------|------|
| 第一阶段 | 1-2 周 | 完整学科章节-知识点数据 | 无 |
| 第二阶段 | 2-4 周 | 爬虫模块 + 自动分类流水线 | 第一阶段（知识点标签） |
| 第三阶段 | 4-8 周 | 外部平台集成 + 图片试题方案 | 第二阶段（试题库基础） |

---

## 许可

本项目基于 MIT License 开源，并附加以下**非商用声明**：

- ✅ 允许：个人学习、研究、教育机构内部教学使用、二次开发（需保留原作者署名）
- ❌ 禁止：未经授权的商业用途，包括但不限于——
  - 将本项目代码或衍生品作为商品出售
  - 基于本项目提供付费 SaaS 服务
  - 将本项目整合至商业产品中进行盈利
  - 任何以收取服务费为目的的使用行为

> 本项目为公益性、非营利性质的教育工具，旨在利国利民、助力教育公平。若仅收取少量必要的运营成本（如服务器、带宽、API 调用等费用）以维持服务正常运作，且无盈利目的，不视为商业用途。如需将本项目用于商业盈利，请联系作者另行协商授权。

MIT License © exam-questions contributors
