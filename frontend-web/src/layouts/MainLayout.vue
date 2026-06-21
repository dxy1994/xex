<template>
  <div class="layout-wrapper">
    <header class="layout-header">
      <div class="header-inner">
        <div class="header-left">
          <div class="logo" @click="$router.push('/dashboard')">
            <el-icon :size="28"><School /></el-icon>
            <span class="logo-text">学而习</span>
          </div>
          <!-- 学制切换 -->
          <div class="edu-switcher">
            <el-icon :size="14" class="edu-switcher-icon"><Switch /></el-icon>
            <div class="edu-switcher-toggle">
              <button
                :class="{ active: userStore.educationSystem === '63' }"
                @click="handleEduChange('63')"
              >六三制</button>
              <button
                :class="{ active: userStore.educationSystem === '54' }"
                @click="handleEduChange('54')"
              >五四制</button>
            </div>
          </div>
          <nav class="header-nav">
            <el-menu mode="horizontal" :ellipsis="false" :default-active="currentRoute" background-color="transparent" text-color="rgba(255,255,255,0.85)" active-text-color="#fff" class="header-menu" @select="handleMenuSelect">
              <el-menu-item index="/dashboard"><el-icon><HomeFilled /></el-icon><span>首页</span></el-menu-item>
              <el-menu-item index="/dashboard/history" :disabled="!userStore.token"><el-icon><Clock /></el-icon><span>考试记录</span></el-menu-item>
            </el-menu>
          </nav>
        </div>
        <div class="header-right">
          <template v-if="userStore.token">
            <el-dropdown trigger="click">
              <div class="user-info">
                <el-avatar :size="32" icon="UserFilled" />
                <span class="user-name">{{ userStore.nickname || userStore.username }}</span>
                <el-icon class="user-arrow"><ArrowDown /></el-icon>
              </div>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item disabled><el-icon><User /></el-icon>{{ { ADMIN: '管理员', TEACHER: '老师', STUDENT: '学生' }[userStore.userType] || '普通用户' }}</el-dropdown-item>
                  <el-dropdown-item divided @click="handleLogout"><el-icon><SwitchButton /></el-icon>退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
          <template v-else>
            <el-button class="btn-header-login" @click="showLogin = true">登录</el-button>
            <el-button type="primary" class="btn-header-register" @click="showRegister = true">注册</el-button>
          </template>
        </div>
      </div>
    </header>
    <main class="layout-main">
      <router-view :key="refreshKey" />
    </main>
    <footer class="layout-footer"><p>(c) 2026 学而习 · 学而时习之，不亦说乎</p></footer>

    <LoginModal v-model="showLogin" @switch-to-register="showRegister = true" @success="onLoginSuccess" />
    <RegisterModal v-model="showRegister" @switch-to-login="showLogin = true" @success="onRegisterSuccess" />
  </div>
</template>

<script setup>
import { computed, ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '../stores/user'
import { updateEducationSystem } from '../api'
import { ElMessage } from 'element-plus'
import LoginModal from '../components/LoginModal.vue'
import RegisterModal from '../components/RegisterModal.vue'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const showLogin = ref(false)
const showRegister = ref(false)
const refreshKey = ref(0)

const currentRoute = computed(() => {
  if (route.path.startsWith('/dashboard/history')) return '/dashboard/history'
  return '/dashboard'
})

const handleEduChange = async (val) => {
  if (userStore.educationSystem === val) return
  // 持久化到 localStorage（含未登录用户）
  userStore.setEducationSystem(val)
  // 已登录用户同步到数据库
  if (userStore.token) {
    try {
      await updateEducationSystem(val)
    } catch {
      ElMessage.warning('偏好保存失败')
    }
  }
  // 仅在仪表盘页面强制重渲染，重新拉取分类数据
  if (route.path === '/dashboard' || route.path === '/dashboard/') {
    refreshKey.value++
  }
}

const handleMenuSelect = (index) => {
  if (index === '/dashboard/history' && !userStore.token) {
    showLogin.value = true
    return
  }
  router.push(index)
}

const handleLogout = () => {
  userStore.logout()
  router.push('/')
}

const onLoginSuccess = () => {
  showLogin.value = false
}

const onRegisterSuccess = () => {
  showRegister.value = false
  showLogin.value = true
}
</script>

<style scoped>
.layout-wrapper { display: flex; flex-direction: column; min-height: 100vh; background: #f0f2f5; }
.layout-header { background: linear-gradient(135deg, #3b82f6 0%, #6366f1 50%, #8b5cf6 100%); box-shadow: 0 2px 12px rgba(59,130,246,0.3); position: sticky; top: 0; z-index: 1000; }
.header-inner { display: flex; align-items: center; justify-content: space-between; margin: 0 auto; padding: 0 32px; height: 60px; }
@media (min-width: 1600px) { .header-inner { padding: 0 48px; } }
@media (min-width: 1920px) { .header-inner { padding: 0 64px; } }
.header-left { display: flex; align-items: center; gap: 24px; }
.logo { display: flex; align-items: center; gap: 10px; cursor: pointer; color: #fff; flex-shrink: 0; }
.logo-text { font-size: 18px; font-weight: 700; letter-spacing: 1px; white-space: nowrap; }
.edu-switcher {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-left: 8px;
}
.edu-switcher-icon {
  color: rgba(255,255,255,0.55);
  flex-shrink: 0;
}
.edu-switcher-toggle {
  display: flex;
  background: rgba(255,255,255,0.12);
  border-radius: 8px;
  padding: 3px;
  gap: 2px;
}
.edu-switcher-toggle button {
  padding: 4px 14px;
  border: none;
  background: transparent;
  color: rgba(255,255,255,0.7);
  font-size: 13px;
  font-weight: 500;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.25s ease;
  white-space: nowrap;
  font-family: inherit;
  line-height: 1.5;
}
.edu-switcher-toggle button:hover {
  color: #fff;
  background: rgba(255,255,255,0.1);
}
.edu-switcher-toggle button.active {
  background: rgba(255,255,255,0.95);
  color: #3b82f6;
  font-weight: 600;
  box-shadow: 0 1px 4px rgba(0,0,0,0.08);
}
.header-nav { display: flex; align-items: center; }
.header-menu { border-bottom: none !important; background: transparent !important; }
.header-menu .el-menu-item { border-bottom: 2px solid transparent !important; transition: all 0.3s; margin: 0 2px; border-radius: 6px 6px 0 0; padding: 0 20px; font-size: 14px; }
.header-menu .el-menu-item:hover { background: rgba(255,255,255,0.12) !important; border-bottom-color: rgba(255,255,255,0.4) !important; }
.header-menu .el-menu-item.is-active { background: rgba(255,255,255,0.18) !important; border-bottom-color: #fff !important; font-weight: 600; }
.header-menu .el-menu-item.is-disabled { opacity: 0.5; cursor: pointer; }
.header-right { display: flex; align-items: center; gap: 10px; }
.btn-header-login {
  color: #fff; border: 1px solid rgba(255,255,255,0.4); background: rgba(255,255,255,0.1);
  border-radius: 8px; font-weight: 500; backdrop-filter: blur(10px);
}
.btn-header-login:hover { background: rgba(255,255,255,0.2); border-color: rgba(255,255,255,0.6); color: #fff; }
.btn-header-register {
  border-radius: 8px; font-weight: 500;
  background: rgba(255,255,255,0.95); color: #3b82f6; border: none;
}
.btn-header-register:hover { background: #fff; box-shadow: 0 2px 8px rgba(0,0,0,0.1); }
.user-info { display: flex; align-items: center; gap: 10px; cursor: pointer; padding: 6px 12px; border-radius: 8px; transition: background 0.3s; }
.user-info:hover { background: rgba(255,255,255,0.15); }
.user-name { color: #fff; font-size: 14px; font-weight: 500; }
.user-arrow { color: rgba(255,255,255,0.7); font-size: 12px; }
.layout-main { flex: 1; display: flex; flex-direction: column; width: 100%; margin: 0 auto; padding: 24px 32px; }
@media (min-width: 1600px) { .layout-main { padding: 28px 48px; } }
@media (min-width: 1920px) { .layout-main { padding: 32px 64px; } }
.layout-footer { text-align: center; padding: 20px; background: #fff; border-top: 1px solid #ebeef5; }
.layout-footer p { color: #909399; font-size: 13px; margin: 0; }
@media (max-width: 768px) {
  .header-inner { padding: 0 12px; } .header-left { gap: 12px; }
  .logo-text { font-size: 15px; } .header-menu .el-menu-item { padding: 0 12px; font-size: 13px; }
  .user-name { display: none; } .layout-main { padding: 12px 16px; }
}
</style>
