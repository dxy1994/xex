<template>
  <div class="login-page">
    <div class="bg-decoration">
      <div class="circle circle-1"></div>
      <div class="circle circle-2"></div>
      <div class="circle circle-3"></div>
    </div>
    <div class="login-card-wrapper">
      <div class="login-brand">
        <div class="brand-icon"><el-icon :size="36"><Setting /></el-icon></div>
        <h1 class="brand-title">管理后台</h1>
        <p class="brand-subtitle">学而习 · 管理员登录</p>
      </div>
      <el-card class="login-card" shadow="always">
        <h2 class="card-title">管理员登录</h2>
        <el-form :model="form" :rules="rules" ref="formRef" @submit.prevent="handleLogin">
          <el-form-item prop="username">
            <el-input v-model="form.username" placeholder="请输入管理员账号" size="large" :prefix-icon="User" clearable />
          </el-form-item>
          <el-form-item prop="password">
            <el-input v-model="form.password" placeholder="请输入密码" type="password" size="large" :prefix-icon="Lock" show-password @keyup.enter="handleLogin" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" size="large" class="login-btn" :loading="loading" @click="handleLogin">登 录</el-button>
          </el-form-item>
        </el-form>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock, Setting } from '@element-plus/icons-vue'
import { login } from '../api'
import { useUserStore } from '../stores/user'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref()
const loading = ref(false)

const form = reactive({ username: '', password: '' })

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const handleLogin = async () => {
  await formRef.value.validate().catch(() => {})
  loading.value = true
  try {
    const res = await login(form)
    if (res.data.userSource !== 'ADMIN') {
      ElMessage.error('该账号无管理员权限')
      return
    }
    userStore.setUserInfo(res.data)
    ElMessage.success('登录成功，欢迎回来！')
    router.push('/')
  } catch (e) { /* handled by interceptor */ }
  finally { loading.value = false }
}
</script>

<style scoped>
.login-page {
  display: flex; justify-content: center; align-items: center; min-height: 100vh;
  background: linear-gradient(135deg, #1e293b 0%, #334155 100%);
  position: relative; overflow: hidden;
}
.bg-decoration { position: absolute; inset: 0; pointer-events: none; }
.circle { position: absolute; border-radius: 50%; background: rgba(255,255,255,0.04); animation: float 20s infinite ease-in-out; }
.circle-1 { width: 400px; height: 400px; top: -100px; right: -100px; animation-delay: 0s; }
.circle-2 { width: 300px; height: 300px; bottom: -80px; left: -60px; animation-delay: -7s; }
.circle-3 { width: 200px; height: 200px; top: 50%; left: 50%; transform: translate(-50%,-50%); animation-delay: -14s; }
@keyframes float { 0%,100% { transform: translate(0,0) scale(1); } 33% { transform: translate(30px,-30px) scale(1.05); } 66% { transform: translate(-20px,20px) scale(0.95); } }
.login-card-wrapper { position: relative; z-index: 1; width: 420px; animation: fadeInUp 0.6s ease-out; }
@keyframes fadeInUp { from { opacity: 0; transform: translateY(30px); } to { opacity: 1; transform: translateY(0); } }
.login-brand { text-align: center; margin-bottom: 24px; }
.brand-icon { display: inline-flex; align-items: center; justify-content: center; width: 64px; height: 64px; background: rgba(255,255,255,0.1); border-radius: 16px; color: #fff; backdrop-filter: blur(10px); margin-bottom: 16px; }
.brand-title { font-size: 28px; font-weight: 700; color: #fff; margin: 0 0 8px; letter-spacing: 2px; }
.brand-subtitle { font-size: 14px; color: rgba(255,255,255,0.6); margin: 0; letter-spacing: 4px; }
.login-card { border-radius: 16px !important; padding: 8px; backdrop-filter: blur(10px); background: rgba(255,255,255,0.95) !important; }
.login-card :deep(.el-card__body) { padding: 32px 28px; }
.card-title { font-size: 20px; font-weight: 600; color: #303133; text-align: center; margin: 0 0 28px; }
.login-btn { width: 100%; height: 44px; font-size: 16px; letter-spacing: 4px; border-radius: 8px; background: linear-gradient(135deg, #334155, #1e293b); border: none; }
.login-btn:hover { opacity: 0.9; transform: translateY(-1px); box-shadow: 0 4px 15px rgba(30,41,59,0.4); }
</style>
