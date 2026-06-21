<template>
  <el-dialog v-model="visible" title="" width="420px" :close-on-click-modal="false" destroy-on-close class="login-dialog" @close="handleClose">
    <div class="modal-brand">
      <div class="modal-brand-icon">
        <el-icon :size="28"><School /></el-icon>
      </div>
      <h2>欢迎回来</h2>
      <p>登录您的学而习账户</p>
    </div>
    <el-form :model="form" :rules="rules" ref="formRef" @submit.prevent="handleLogin">
      <el-form-item prop="username">
        <el-input v-model="form.username" placeholder="请输入用户名" size="large" :prefix-icon="User" clearable />
      </el-form-item>
      <el-form-item prop="password">
        <el-input v-model="form.password" placeholder="请输入密码" type="password" size="large" :prefix-icon="Lock" show-password @keyup.enter="handleLogin" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" size="large" class="modal-submit-btn" :loading="loading" @click="handleLogin">登 录</el-button>
      </el-form-item>
      <el-form-item>
        <div class="wechat-divider"><span>或者</span></div>
        <el-button size="large" class="wechat-login-btn" :loading="wechatLoading" @click="handleWechatLogin">
          微信登录
        </el-button>
      </el-form-item>
    </el-form>
    <div class="modal-footer">
      <span>还没有账号？</span>
      <el-link type="primary" underline="never" @click="switchToRegister">立即注册 →</el-link>
    </div>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { User, Lock, School } from '@element-plus/icons-vue'
import { login, getWechatAuthUrl } from '../api'
import { useUserStore } from '../stores/user'

const props = defineProps({ modelValue: Boolean })
const emit = defineEmits(['update:modelValue', 'switch-to-register', 'success'])

const userStore = useUserStore()
const formRef = ref()
const loading = ref(false)
const wechatLoading = ref(false)

const visible = ref(false)
watch(() => props.modelValue, (val) => { visible.value = val })
watch(visible, (val) => { emit('update:modelValue', val) })

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
    userStore.setUserInfo(res.data)
    ElMessage.success('登录成功，欢迎回来！')
    emit('success')
    visible.value = false
  } catch (e) { /* handled by interceptor */ }
  finally { loading.value = false }
}

const switchToRegister = () => {
  visible.value = false
  emit('switch-to-register')
}

const handleClose = () => {
  form.username = ''
  form.password = ''
  formRef.value?.resetFields()
}

const handleWechatLogin = async () => {
  wechatLoading.value = true
  try {
    const res = await getWechatAuthUrl()
    // 跳转到微信 OAuth 授权页
    window.location.href = res.data.url
  } catch (e) {
    wechatLoading.value = false
  }
}
</script>

<style scoped>
.login-dialog :deep(.el-dialog__header) { display: none; }
.login-dialog :deep(.el-dialog__body) { padding: 32px 36px 24px; }
.modal-brand { text-align: center; margin-bottom: 28px; }
.modal-brand-icon {
  display: inline-flex; align-items: center; justify-content: center;
  width: 56px; height: 56px; background: linear-gradient(135deg, #3b82f6, #6366f1);
  border-radius: 14px; color: #fff; margin-bottom: 16px; box-shadow: 0 4px 12px rgba(59,130,246,0.3);
}
.modal-brand h2 { font-size: 22px; font-weight: 700; color: #303133; margin: 0 0 6px; }
.modal-brand p { font-size: 13px; color: #909399; margin: 0; }
.modal-submit-btn { width: 100%; height: 44px; font-size: 16px; letter-spacing: 4px; border-radius: 8px; background: linear-gradient(135deg, #3b82f6, #6366f1); border: none; }
.modal-submit-btn:hover { opacity: 0.9; transform: translateY(-1px); box-shadow: 0 4px 15px rgba(59,130,246,0.4); }
.modal-footer { text-align: center; font-size: 14px; color: #909399; }
.modal-footer .el-link { font-weight: 600; margin-left: 4px; }
.wechat-divider {
  display: flex; align-items: center; margin: 0 0 12px; color: #c0c4cc; font-size: 13px;
}
.wechat-divider::before, .wechat-divider::after {
  content: ''; flex: 1; height: 1px; background: #e4e7ed;
}
.wechat-divider span { padding: 0 12px; }
.wechat-login-btn {
  width: 100%; height: 44px; font-size: 15px; letter-spacing: 2px; border-radius: 8px;
  background: #07c160; border-color: #07c160; color: #fff;
}
.wechat-login-btn:hover { background: #06ad56; border-color: #06ad56; color: #fff; }
</style>
