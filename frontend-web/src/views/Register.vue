<template>
  <div class="register-page">
    <div class="bg-decoration">
      <div class="circle circle-1"></div>
      <div class="circle circle-2"></div>
      <div class="circle circle-3"></div>
    </div>
    <div class="register-card-wrapper">
      <div class="register-brand">
        <div class="brand-icon"><el-icon :size="36"><School /></el-icon></div>
        <h1 class="brand-title">学而习</h1>
        <p class="brand-subtitle">创建您的学习账户</p>
      </div>
      <el-card class="register-card" shadow="always">
        <h2 class="card-title">注册账号</h2>
        <el-form :model="form" :rules="rules" ref="formRef">
          <el-form-item prop="username">
            <el-input v-model="form.username" placeholder="用户名（3-50字符）" size="large" :prefix-icon="User" clearable />
          </el-form-item>
          <el-form-item prop="password">
            <el-input v-model="form.password" placeholder="密码（至少6位）" type="password" size="large" :prefix-icon="Lock" show-password />
          </el-form-item>
          <el-form-item prop="nickname">
            <el-input v-model="form.nickname" placeholder="昵称（选填）" size="large" :prefix-icon="UserFilled" clearable />
          </el-form-item>
          <el-form-item prop="email">
            <el-input v-model="form.email" placeholder="邮箱（选填）" size="large" :prefix-icon="Message" clearable />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" size="large" class="register-btn" :loading="loading" @click="handleRegister">注 册</el-button>
          </el-form-item>
        </el-form>
        <div class="register-footer">
          <span>已有账号？</span>
          <el-link type="primary" underline="never" @click="$router.push('/login')">去登录 →</el-link>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock, UserFilled, Message } from '@element-plus/icons-vue'
import { register } from '../api'

const router = useRouter()
const formRef = ref()
const loading = ref(false)
const form = reactive({ username: '', password: '', nickname: '', email: '' })

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 50, message: '用户名长度在3-50个字符之间', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码至少6位', trigger: 'blur' }
  ]
}

const handleRegister = async () => {
  await formRef.value.validate().catch(() => {})
  loading.value = true
  try {
    await register(form)
    ElMessage.success('注册成功，请登录')
    router.push('/login')
  } catch (e) { /* handled by interceptor */ }
  finally { loading.value = false }
}
</script>

<style scoped>
.register-page {
  display: flex; justify-content: center; align-items: center; min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  position: relative; overflow: hidden;
}
.bg-decoration { position: absolute; inset: 0; pointer-events: none; }
.circle { position: absolute; border-radius: 50%; background: rgba(255,255,255,0.08); animation: float 20s infinite ease-in-out; }
.circle-1 { width: 400px; height: 400px; top: -100px; left: -100px; }
.circle-2 { width: 300px; height: 300px; bottom: -80px; right: -60px; animation-delay: -7s; }
.circle-3 { width: 200px; height: 200px; top: 40%; right: 10%; animation-delay: -14s; }
@keyframes float { 0%,100% { transform: translate(0,0) scale(1); } 33% { transform: translate(30px,-30px) scale(1.05); } 66% { transform: translate(-20px,20px) scale(0.95); } }
.register-card-wrapper { position: relative; z-index: 1; width: 450px; animation: fadeInUp 0.6s ease-out; }
@keyframes fadeInUp { from { opacity: 0; transform: translateY(30px); } to { opacity: 1; transform: translateY(0); } }
.register-brand { text-align: center; margin-bottom: 20px; }
.brand-icon { display: inline-flex; align-items: center; justify-content: center; width: 56px; height: 56px; background: rgba(255,255,255,0.2); border-radius: 14px; color: #fff; backdrop-filter: blur(10px); margin-bottom: 12px; }
.brand-title { font-size: 24px; font-weight: 700; color: #fff; margin: 0 0 6px; letter-spacing: 2px; }
.brand-subtitle { font-size: 13px; color: rgba(255,255,255,0.8); margin: 0; }
.register-card { border-radius: 16px !important; backdrop-filter: blur(10px); background: rgba(255,255,255,0.95) !important; }
.register-card :deep(.el-card__body) { padding: 28px 28px 20px; }
.card-title { font-size: 20px; font-weight: 600; color: #303133; text-align: center; margin: 0 0 24px; }
.register-btn { width: 100%; height: 44px; font-size: 16px; letter-spacing: 4px; border-radius: 8px; background: linear-gradient(135deg,#667eea,#764ba2); border: none; }
.register-btn:hover { opacity: 0.9; transform: translateY(-1px); box-shadow: 0 4px 15px rgba(102,126,234,0.4); }
.register-footer { text-align: center; font-size: 14px; color: #909399; }
.register-footer .el-link { font-weight: 600; margin-left: 4px; }
</style>
