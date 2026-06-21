<template>
  <div class="admin-layout">
    <aside class="admin-sidebar">
      <div class="sidebar-logo" @click="$router.push('/')">
        <el-icon :size="24"><Setting /></el-icon>
        <span>管理后台</span>
      </div>
      <el-menu :default-active="currentRoute" router class="sidebar-menu" background-color="#1e293b" text-color="rgba(255,255,255,0.65)" active-text-color="#fff">
        <el-menu-item index="/"><el-icon><HomeFilled /></el-icon><span>控制台</span></el-menu-item>
        <el-menu-item index="/categories"><el-icon><Collection /></el-icon><span>分类管理</span></el-menu-item>
        <el-menu-item index="/questions"><el-icon><Document /></el-icon><span>题库管理</span></el-menu-item>
        <el-menu-item index="/ratios"><el-icon><DataAnalysis /></el-icon><span>比例配置</span></el-menu-item>
        <el-menu-item index="/exams"><el-icon><Tickets /></el-icon><span>试卷管理</span></el-menu-item>
        <el-menu-item index="/records"><el-icon><Notebook /></el-icon><span>考试记录</span></el-menu-item>
      </el-menu>
    </aside>
    <div class="admin-main">
      <header class="admin-header">
        <div class="header-left">
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item>{{ currentPageName }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="header-right">
          <el-dropdown trigger="click">
            <div class="user-info">
              <el-avatar :size="32" icon="UserFilled" />
              <span class="user-name">{{ userStore.nickname || userStore.username }}</span>
              <el-icon class="user-arrow"><ArrowDown /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item disabled><el-icon><User /></el-icon>管理员</el-dropdown-item>
                <el-dropdown-item divided @click="handleLogout"><el-icon><SwitchButton /></el-icon>退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </header>
      <main class="admin-content">
        <router-view />
      </main>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const pageNameMap = {
  '/': '控制台',
  '/categories': '分类管理',
  '/questions': '题库管理',
  '/ratios': '比例配置',
  '/exams': '试卷管理',
  '/records': '考试记录'
}

const currentRoute = computed(() => route.path)
const currentPageName = computed(() => pageNameMap[route.path] || '管理')

const handleLogout = () => {
  userStore.logout()
  router.push('/login')
}
</script>

<style scoped>
.admin-layout { display: flex; min-height: 100vh; background: #f0f2f5; }
.admin-sidebar { width: 220px; background: #1e293b; display: flex; flex-direction: column; flex-shrink: 0; }
.sidebar-logo { display: flex; align-items: center; gap: 10px; padding: 20px; color: #fff; font-size: 17px; font-weight: 700; cursor: pointer; border-bottom: 1px solid rgba(255,255,255,0.08); }
.sidebar-menu { border-right: none; flex: 1; }
.sidebar-menu .el-menu-item { margin: 4px 8px; border-radius: 8px; }
.sidebar-menu .el-menu-item:hover { background: rgba(255,255,255,0.08) !important; }
.sidebar-menu .el-menu-item.is-active { background: linear-gradient(135deg, #3b82f6, #6366f1) !important; }
.admin-main { flex: 1; display: flex; flex-direction: column; overflow: hidden; }
.admin-header { display: flex; align-items: center; justify-content: space-between; padding: 0 24px; height: 56px; background: #fff; box-shadow: 0 1px 4px rgba(0,0,0,0.06); }
.header-right { display: flex; align-items: center; }
.user-info { display: flex; align-items: center; gap: 10px; cursor: pointer; padding: 6px 12px; border-radius: 8px; transition: background 0.3s; }
.user-info:hover { background: #f5f5f5; }
.user-name { font-size: 14px; font-weight: 500; color: #303133; }
.user-arrow { color: #909399; font-size: 12px; }
.admin-content { flex: 1; padding: 24px 32px; overflow-y: auto; }
@media (min-width: 1600px) { .admin-content { padding: 28px 40px; } }
@media (min-width: 1920px) { .admin-content { padding: 32px 48px; } }
</style>
