<template>
  <div class="landing-page">
    <!-- ===== Header ===== -->
    <header class="landing-header" :class="{ scrolled: scrolled }">
      <div class="header-inner">
        <div class="header-logo" @click="scrollToTop">
          <div class="logo-icon">
            <el-icon :size="24"><School /></el-icon>
          </div>
          <span class="logo-text">学而习</span>
        </div>
        <nav class="header-nav">
          <a href="#features" @click.prevent="scrollTo('features')">功能特色</a>
          <a href="#how" @click.prevent="scrollTo('how')">使用流程</a>
          <a href="#contact" @click.prevent="scrollTo('contact')">关于我们</a>
        </nav>
        <div class="header-actions">
          <el-button class="btn-login" @click="showLogin = true">登录</el-button>
          <el-button class="btn-register" type="primary" @click="showRegister = true">免费注册</el-button>
        </div>
      </div>
    </header>

    <!-- ===== Hero Section ===== -->
    <section class="hero-section">
      <div class="hero-bg-shapes">
        <div class="shape shape-1"></div>
        <div class="shape shape-2"></div>
        <div class="shape shape-3"></div>
        <div class="shape shape-4"></div>
        <div class="shape shape-5"></div>
      </div>
      <div class="hero-content">
        <div class="hero-badge">
          <span class="badge-dot"></span>新一代智能考试平台
        </div>
        <h1 class="hero-title">
          让每一次考试<br />
          <span class="gradient-text">都变得简单高效</span>
        </h1>
        <p class="hero-desc">
          智能组卷、在线答题、自动阅卷、数据分析——一站式考试解决方案，
          帮助教育者和学习者轻松实现知识评测
        </p>
        <div class="hero-actions">
          <el-button type="primary" size="large" class="hero-btn-primary" @click="goDashboard">
            <el-icon><VideoPlay /></el-icon>无需登录，直接体验
          </el-button>
          <el-button size="large" class="hero-btn-secondary" @click="showRegister = true">
            注册账号 <el-icon><ArrowRight /></el-icon>
          </el-button>
        </div>
        <div class="hero-stats">
          <div class="stat-item">
            <span class="stat-value">10,000+</span>
            <span class="stat-label">题库数量</span>
          </div>
          <div class="stat-item">
            <span class="stat-value">50+</span>
            <span class="stat-label">考试分类</span>
          </div>
          <div class="stat-item">
            <span class="stat-value">99.9%</span>
            <span class="stat-label">系统稳定性</span>
          </div>
          <div class="stat-item">
            <span class="stat-value">100K+</span>
            <span class="stat-label">服务用户</span>
          </div>
        </div>
      </div>
      <div class="hero-visual">
        <div class="hero-card">
          <div class="hero-card-icon">
            <el-icon :size="40"><Document /></el-icon>
          </div>
          <div class="hero-card-lines">
            <span class="line line-1"></span>
            <span class="line line-2"></span>
            <span class="line line-3"></span>
            <span class="line line-4"></span>
          </div>
        </div>
        <div class="hero-float-card float-1">
          <el-icon :size="20" color="#10b981"><CircleCheckFilled /></el-icon>
          <span>智能自动阅卷</span>
        </div>
        <div class="hero-float-card float-2">
          <el-icon :size="20" color="#6366f1"><DataAnalysis /></el-icon>
          <span>成绩数据分析</span>
        </div>
        <div class="hero-float-card float-3">
          <el-icon :size="20" color="#f59e0b"><StarFilled /></el-icon>
          <span>多题型支持</span>
        </div>
      </div>
    </section>

    <!-- ===== Features Section ===== -->
    <section id="features" class="features-section">
      <div class="section-container">
        <div class="section-header">
          <span class="section-tag">核心功能</span>
          <h2>为考试全流程提供强大支持</h2>
          <p>从题库管理到智能组卷，从在线考试到成绩分析，我们覆盖每一个环节</p>
        </div>
        <div class="features-grid">
          <div class="feature-card" v-for="(feat, i) in features" :key="i" :style="{ animationDelay: i * 0.1 + 's' }">
            <div class="feature-icon" :style="{ background: feat.gradient }">
              <el-icon :size="28"><component :is="feat.icon" /></el-icon>
            </div>
            <h3>{{ feat.title }}</h3>
            <p>{{ feat.desc }}</p>
          </div>
        </div>
      </div>
    </section>

    <!-- ===== How It Works ===== -->
    <section id="how" class="how-section">
      <div class="section-container">
        <div class="section-header">
          <span class="section-tag">使用流程</span>
          <h2>三步轻松开启考试之旅</h2>
          <p>简单直观的操作流程，让考试管理从未如此轻松</p>
        </div>
        <div class="steps-wrapper">
          <div class="step-card" v-for="(step, i) in steps" :key="i">
            <div class="step-number">{{ i + 1 }}</div>
            <div class="step-icon-wrap">
              <el-icon :size="36"><component :is="step.icon" /></el-icon>
            </div>
            <h3>{{ step.title }}</h3>
            <p>{{ step.desc }}</p>
          </div>
          <div class="step-connector">
            <svg width="100%" height="4" viewBox="0 0 100 4" preserveAspectRatio="none">
              <line x1="0" y1="2" x2="100" y2="2" stroke="url(#dashGrad)" stroke-width="3" stroke-dasharray="8,6" />
              <defs>
                <linearGradient id="dashGrad" x1="0%" y1="0%" x2="100%" y2="0%">
                  <stop offset="0%" stop-color="#6366f1" />
                  <stop offset="100%" stop-color="#3b82f6" />
                </linearGradient>
              </defs>
            </svg>
          </div>
        </div>
      </div>
    </section>

    <!-- ===== CTA Section ===== -->
    <section id="contact" class="cta-section">
      <div class="section-container">
        <div class="cta-card">
          <div class="cta-bg-dots"></div>
          <h2>准备好开始了吗？</h2>
          <p>免费注册账户，即刻体验智能考试系统带来的便捷与高效</p>
          <div class="cta-actions">
            <el-button type="primary" size="large" round class="cta-btn" @click="goDashboard">
              <el-icon><VideoPlay /></el-icon>无需登录，马上开始
            </el-button>
            <el-button size="large" round class="cta-btn-secondary" @click="showRegister = true">
              注册账号解锁更多功能
            </el-button>
          </div>
        </div>
      </div>
    </section>

    <!-- ===== Footer ===== -->
    <footer class="landing-footer">
      <div class="footer-inner">
        <div class="footer-brand">
          <div class="footer-logo">
            <el-icon :size="22"><School /></el-icon>
            <span>学而习</span>
          </div>
          <p>智慧学习 · 高效测评 — 让每一次考试都变得简单高效</p>
        </div>
        <div class="footer-links">
          <div class="footer-col">
            <h4>产品</h4>
            <a href="#features" @click.prevent="scrollTo('features')">功能特色</a>
            <a href="#how" @click.prevent="scrollTo('how')">使用流程</a>
            <a href="#">更新日志</a>
          </div>
          <div class="footer-col">
            <h4>支持</h4>
            <a href="#">帮助中心</a>
            <a href="#">常见问题</a>
            <a href="#">联系我们</a>
          </div>
          <div class="footer-col">
            <h4>法律</h4>
            <a href="#">隐私政策</a>
            <a href="#">服务条款</a>
          </div>
        </div>
      </div>
      <div class="footer-bottom">
        <p>(c) 2026 学而习 版权所有</p>
      </div>
    </footer>

    <!-- ===== Login Modal ===== -->
    <LoginModal v-model="showLogin" @switch-to-register="onSwitchToRegister" @success="onLoginSuccess" />

    <!-- ===== Register Modal ===== -->
    <RegisterModal v-model="showRegister" @switch-to-login="onSwitchToLogin" @success="onRegisterSuccess" />
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import {
  School, VideoPlay, ArrowDown, ArrowRight, Document, CircleCheckFilled,
  DataAnalysis, StarFilled, Collection, Edit, TrendCharts,
  Files, Monitor, UserFilled
} from '@element-plus/icons-vue'
import LoginModal from '../components/LoginModal.vue'
import RegisterModal from '../components/RegisterModal.vue'

const router = useRouter()
const userStore = useUserStore()

const scrolled = ref(false)
const showLogin = ref(false)
const showRegister = ref(false)

const features = [
  {
    icon: 'Collection', gradient: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
    title: '海量题库管理', desc: '支持单选、多选、判断、填空等多种题型，轻松管理海量试题'
  },
  {
    icon: 'Edit', gradient: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)',
    title: '智能随机组卷', desc: '按题型比例自动抽取试题，支持固定组卷与随机组卷两种模式'
  },
  {
    icon: 'Monitor', gradient: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)',
    title: '在线限时答题', desc: '实时计时、自动保存、随时提交，提供流畅的在线考试体验'
  },
  {
    icon: 'TrendCharts', gradient: 'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)',
    title: '成绩智能分析', desc: '自动阅卷、即时出分、答题详情查看，全方位分析学习效果'
  },
  {
    icon: 'Files', gradient: 'linear-gradient(135deg, #fa709a 0%, #fee140 100%)',
    title: '考试记录归档', desc: '完整记录每次考试详情，支持历史查询和成绩对比'
  },
  {
    icon: 'UserFilled', gradient: 'linear-gradient(135deg, #a18cd1 0%, #fbc2eb 100%)',
    title: '角色权限管理', desc: '区分管理员与普通用户，精细化权限控制保障数据安全'
  }
]

const steps = [
  { icon: 'Edit', title: '选择分类 · 生成试卷', desc: '从题库中选择考试分类，系统智能组卷，一键生成专属试卷' },
  { icon: 'Monitor', title: '在线答题 · 实时计时', desc: '在规定时间内完成答题，系统实时保存进度，交卷即可查看结果' },
  { icon: 'TrendCharts', title: '查看成绩 · 分析报告', desc: '自动阅卷出分，查看每道题的对错详情，获取学习诊断报告' }
]

const scrollTo = (id) => {
  document.getElementById(id)?.scrollIntoView({ behavior: 'smooth' })
}
const goDashboard = () => { router.push('/dashboard') }
const scrollToTop = () => window.scrollTo({ top: 0, behavior: 'smooth' })

const onSwitchToRegister = () => { showRegister.value = true }
const onSwitchToLogin = () => { showLogin.value = true }
const onLoginSuccess = () => { router.push('/dashboard') }
const onRegisterSuccess = () => { showLogin.value = true }

const handleScroll = () => {
  scrolled.value = window.scrollY > 60
}

onMounted(() => window.addEventListener('scroll', handleScroll))
onUnmounted(() => window.removeEventListener('scroll', handleScroll))
</script>

<style scoped>
/* ===== 全局 ===== */
.landing-page {
  min-height: 100vh;
  background: #fff;
  overflow-x: hidden;
}

/* ===== Header ===== */
.landing-header {
  position: fixed; top: 0; left: 0; right: 0; z-index: 1000;
  padding: 0 24px; height: 64px;
  background: rgba(255,255,255,0.85); backdrop-filter: blur(20px);
  border-bottom: 1px solid transparent;
  transition: all 0.3s ease;
}
.landing-header.scrolled {
  border-bottom-color: #f0f0f0;
  box-shadow: 0 1px 6px rgba(0,0,0,0.06);
}
.header-inner {
  max-width: 1400px; margin: 0 auto; height: 100%;
  display: flex; align-items: center; justify-content: space-between;
}
.header-logo {
  display: flex; align-items: center; gap: 10px; cursor: pointer;
}
.logo-icon {
  width: 38px; height: 38px; display: flex; align-items: center; justify-content: center;
  background: linear-gradient(135deg, #3b82f6, #6366f1);
  border-radius: 10px; color: #fff;
}
.logo-text { font-size: 18px; font-weight: 700; color: #1e293b; letter-spacing: 1px; }
.header-nav { display: flex; gap: 32px; }
.header-nav a {
  font-size: 14px; color: #64748b; font-weight: 500; text-decoration: none;
  transition: color 0.2s; position: relative;
}
.header-nav a:hover { color: #3b82f6; }
.header-nav a::after {
  content: ''; position: absolute; bottom: -4px; left: 0; right: 0;
  height: 2px; background: #3b82f6; border-radius: 1px;
  transform: scaleX(0); transition: transform 0.2s;
}
.header-nav a:hover::after { transform: scaleX(1); }
.header-actions { display: flex; gap: 10px; }
.btn-login { border-radius: 8px; font-weight: 500; }
.btn-register { border-radius: 8px; font-weight: 500; background: linear-gradient(135deg, #3b82f6, #6366f1); border: none; }
.btn-register:hover { opacity: 0.9; box-shadow: 0 4px 12px rgba(59,130,246,0.3); }

/* ===== Hero ===== */
.hero-section {
  padding: 120px 24px 80px;
  background: linear-gradient(180deg, #f8fafc 0%, #fff 100%);
  position: relative; overflow: hidden;
  display: flex; align-items: center; justify-content: center;
  gap: 60px; min-height: 92vh;
}
.hero-bg-shapes { position: absolute; inset: 0; pointer-events: none; }
.shape { position: absolute; border-radius: 50%; }
.shape-1 {
  width: 600px; height: 600px; background: radial-gradient(circle, rgba(99,102,241,0.06) 0%, transparent 70%);
  top: -200px; right: -150px;
}
.shape-2 {
  width: 400px; height: 400px; background: radial-gradient(circle, rgba(59,130,246,0.05) 0%, transparent 70%);
  bottom: -100px; left: -100px;
}
.shape-3 {
  width: 300px; height: 300px; background: radial-gradient(circle, rgba(139,92,246,0.04) 0%, transparent 70%);
  top: 40%; left: 10%;
}
.shape-4 {
  width: 200px; height: 200px; background: radial-gradient(circle, rgba(16,185,129,0.05) 0%, transparent 70%);
  top: 20%; right: 30%;
}
.shape-5 {
  width: 250px; height: 250px; background: radial-gradient(circle, rgba(245,158,11,0.04) 0%, transparent 70%);
  bottom: 20%; right: 10%;
}
.hero-content { max-width: 560px; position: relative; z-index: 1; }
.hero-badge {
  display: inline-flex; align-items: center; gap: 8px;
  background: rgba(99,102,241,0.08); color: #6366f1;
  padding: 6px 16px; border-radius: 20px; font-size: 13px; font-weight: 600;
  margin-bottom: 24px;
}
.badge-dot {
  width: 8px; height: 8px; border-radius: 50%; background: #6366f1;
  animation: pulse-dot 2s infinite;
}
@keyframes pulse-dot { 0%,100% { opacity: 1; } 50% { opacity: 0.4; } }
.hero-title {
  font-size: 48px; font-weight: 800; color: #0f172a; line-height: 1.25;
  margin: 0 0 20px; letter-spacing: -1px;
}
.gradient-text { background: linear-gradient(135deg, #3b82f6 0%, #8b5cf6 100%); -webkit-background-clip: text; -webkit-text-fill-color: transparent; background-clip: text; }
.hero-desc { font-size: 17px; color: #64748b; line-height: 1.7; margin: 0 0 32px; }
.hero-actions { display: flex; gap: 16px; margin-bottom: 48px; }
.hero-btn-primary {
  height: 48px; padding: 0 28px; font-size: 16px; font-weight: 600;
  border-radius: 12px; border: none;
  background: linear-gradient(135deg, #3b82f6, #6366f1);
  box-shadow: 0 4px 20px rgba(59,130,246,0.3);
  display: flex; align-items: center; gap: 8px;
}
.hero-btn-primary:hover { transform: translateY(-2px); box-shadow: 0 8px 30px rgba(59,130,246,0.4); }
.hero-btn-secondary {
  height: 48px; padding: 0 28px; font-size: 16px; border-radius: 12px;
  border: 2px solid #e2e8f0; color: #475569; background: #fff;
  display: flex; align-items: center; gap: 8px;
}
.hero-btn-secondary:hover { border-color: #3b82f6; color: #3b82f6; }
.hero-stats { display: flex; gap: 40px; padding-top: 32px; border-top: 1px solid #f1f5f9; }
.stat-item { text-align: center; }
.stat-value { display: block; font-size: 28px; font-weight: 700; color: #0f172a; }
.stat-label { display: block; font-size: 13px; color: #94a3b8; margin-top: 4px; }

/* ===== Hero Visual ===== */
.hero-visual {
  position: relative; z-index: 1; flex-shrink: 0;
  display: flex; align-items: center; justify-content: center;
}
.hero-card {
  width: 260px; height: 340px; background: #fff;
  border-radius: 20px; box-shadow: 0 25px 60px rgba(0,0,0,0.08), 0 0 0 1px rgba(0,0,0,0.04);
  display: flex; flex-direction: column; align-items: center; justify-content: center;
  gap: 24px; position: relative; z-index: 2;
}
.hero-card-icon {
  width: 80px; height: 80px; border-radius: 20px;
  background: linear-gradient(135deg, #eef2ff, #e0e7ff);
  display: flex; align-items: center; justify-content: center; color: #6366f1;
}
.hero-card-lines { display: flex; flex-direction: column; gap: 10px; width: 140px; }
.line { height: 6px; border-radius: 3px; background: #e2e8f0; }
.line-1 { width: 100%; background: #6366f1; }
.line-2 { width: 75%; }
.line-3 { width: 90%; }
.line-4 { width: 55%; }
.hero-float-card {
  position: absolute; background: #fff; border-radius: 12px;
  padding: 10px 18px; box-shadow: 0 8px 24px rgba(0,0,0,0.08);
  display: flex; align-items: center; gap: 10px; font-size: 13px; font-weight: 600;
  color: #334155; white-space: nowrap; z-index: 3;
  animation: float-card 6s ease-in-out infinite;
}
.float-1 { top: -10px; right: -40px; animation-delay: 0s; }
.float-2 { bottom: 30px; left: -50px; animation-delay: -2s; }
.float-3 { bottom: 100px; right: -60px; animation-delay: -4s; }
@keyframes float-card {
  0%,100% { transform: translateY(0); }
  50% { transform: translateY(-12px); }
}

/* ===== Features ===== */
.features-section { padding: 80px 24px; }
.section-container { max-width: 1400px; margin: 0 auto; }
.section-header { text-align: center; margin-bottom: 56px; }
.section-tag {
  display: inline-block; padding: 4px 16px; background: rgba(99,102,241,0.08);
  color: #6366f1; font-size: 13px; font-weight: 600; border-radius: 20px;
  margin-bottom: 16px;
}
.section-header h2 { font-size: 32px; font-weight: 700; color: #0f172a; margin: 0 0 12px; }
.section-header p { font-size: 16px; color: #64748b; margin: 0; }
.features-grid {
  display: grid; grid-template-columns: repeat(3, 1fr); gap: 24px;
}
.feature-card {
  background: #fff; border-radius: 16px; padding: 32px 28px;
  border: 1px solid #f1f5f9; transition: all 0.3s ease;
  animation: fadeInUp 0.5s ease-out both;
  cursor: default;
}
.feature-card:hover {
  transform: translateY(-6px);
  box-shadow: 0 16px 40px rgba(0,0,0,0.08);
  border-color: #e0e7ff;
}
@keyframes fadeInUp {
  from { opacity: 0; transform: translateY(24px); }
  to { opacity: 1; transform: translateY(0); }
}
.feature-icon {
  width: 56px; height: 56px; border-radius: 14px;
  display: flex; align-items: center; justify-content: center;
  color: #fff; margin-bottom: 20px;
}
.feature-card h3 { font-size: 18px; font-weight: 600; color: #1e293b; margin: 0 0 8px; }
.feature-card p { font-size: 14px; color: #94a3b8; line-height: 1.6; margin: 0; }

/* ===== How It Works ===== */
.how-section {
  padding: 80px 24px;
  background: linear-gradient(180deg, #f8fafc 0%, #fff 100%);
}
.steps-wrapper { position: relative; display: flex; gap: 40px; justify-content: center; margin-top: 20px; }
.step-card {
  flex: 1; max-width: 320px; text-align: center;
  background: #fff; border-radius: 16px; padding: 40px 24px;
  border: 1px solid #f1f5f9; position: relative; z-index: 2;
  transition: all 0.3s ease;
}
.step-card:hover { transform: translateY(-4px); box-shadow: 0 12px 32px rgba(0,0,0,0.06); }
.step-number {
  position: absolute; top: -16px; left: 24px;
  width: 32px; height: 32px; border-radius: 10px;
  background: linear-gradient(135deg, #3b82f6, #6366f1);
  color: #fff; font-size: 16px; font-weight: 700;
  display: flex; align-items: center; justify-content: center;
}
.step-icon-wrap {
  width: 72px; height: 72px; border-radius: 20px;
  background: linear-gradient(135deg, #eef2ff, #e0e7ff);
  display: flex; align-items: center; justify-content: center;
  color: #6366f1; margin: 0 auto 20px;
}
.step-card h3 { font-size: 17px; font-weight: 600; color: #1e293b; margin: 0 0 8px; }
.step-card p { font-size: 14px; color: #94a3b8; line-height: 1.6; margin: 0; }
.step-connector {
  position: absolute; top: 56px; left: calc(20% + 40px); right: calc(20% + 40px);
  z-index: 1; opacity: 0.4;
}

/* ===== CTA ===== */
.cta-section { padding: 80px 24px; }
.cta-card {
  background: linear-gradient(135deg, #1e293b 0%, #0f172a 100%);
  border-radius: 24px; padding: 64px 40px; text-align: center;
  position: relative; overflow: hidden;
}
.cta-bg-dots {
  position: absolute; inset: 0;
  background-image: radial-gradient(rgba(255,255,255,0.06) 1px, transparent 1px);
  background-size: 24px 24px;
}
.cta-card h2 { font-size: 32px; font-weight: 700; color: #fff; margin: 0 0 12px; position: relative; }
.cta-card p { font-size: 16px; color: rgba(255,255,255,0.7); margin: 0 0 32px; position: relative; }
.cta-actions { display: flex; gap: 16px; justify-content: center; position: relative; }
.cta-btn {
  height: 48px; padding: 0 32px; font-size: 16px; font-weight: 600;
  background: linear-gradient(135deg, #3b82f6, #6366f1); border: none;
}
.cta-btn:hover { transform: translateY(-2px); box-shadow: 0 8px 24px rgba(59,130,246,0.4); }
.cta-btn-secondary {
  height: 48px; padding: 0 32px; font-size: 16px;
  border: 1px solid rgba(255,255,255,0.25); color: #fff; background: transparent;
}
.cta-btn-secondary:hover { border-color: #fff; background: rgba(255,255,255,0.1); }

/* ===== Footer ===== */
.landing-footer {
  background: #f8fafc; border-top: 1px solid #f1f5f9; padding: 48px 24px 0;
}
.footer-inner {
  max-width: 1400px; margin: 0 auto;
  display: flex; justify-content: space-between; gap: 48px;
  padding-bottom: 40px;
}
.footer-brand { max-width: 320px; }
.footer-logo { display: flex; align-items: center; gap: 8px; font-size: 17px; font-weight: 700; color: #1e293b; margin-bottom: 12px; }
.footer-brand p { font-size: 14px; color: #94a3b8; line-height: 1.6; margin: 0; }
.footer-links { display: flex; gap: 64px; }
.footer-col h4 { font-size: 13px; font-weight: 700; color: #94a3b8; text-transform: uppercase; letter-spacing: 1px; margin: 0 0 16px; }
.footer-col a { display: block; font-size: 14px; color: #64748b; text-decoration: none; margin-bottom: 10px; transition: color 0.2s; }
.footer-col a:hover { color: #3b82f6; }
.footer-bottom {
  border-top: 1px solid #e2e8f0; padding: 20px 0; text-align: center;
}
.footer-bottom p { font-size: 13px; color: #94a3b8; margin: 0; }

/* ===== Responsive ===== */
@media (max-width: 1024px) {
  .hero-section { flex-direction: column; text-align: center; padding: 100px 24px 60px; min-height: auto; }
  .hero-content { max-width: 100%; }
  .hero-actions { justify-content: center; }
  .hero-stats { justify-content: center; }
  .hero-visual { display: none; }
  .features-grid { grid-template-columns: repeat(2, 1fr); }
}
@media (max-width: 768px) {
  .hero-title { font-size: 32px; }
  .hero-stats { flex-wrap: wrap; gap: 24px; }
  .features-grid { grid-template-columns: 1fr; }
  .steps-wrapper { flex-direction: column; align-items: center; }
  .step-connector { display: none; }
  .header-nav { display: none; }
  .footer-inner { flex-direction: column; gap: 32px; }
  .footer-links { gap: 32px; }
  .cta-actions { flex-direction: column; align-items: center; }
}
</style>
