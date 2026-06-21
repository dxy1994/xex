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

## 许可

MIT License
