<template>
  <el-dialog v-model="visible" title="" width="450px" :close-on-click-modal="false" destroy-on-close class="register-dialog" @close="handleClose">
    <div class="modal-brand">
      <div class="modal-brand-icon">
        <el-icon :size="28"><School /></el-icon>
      </div>
      <h2>创建账户</h2>
      <p>加入学而习，开启学习之旅</p>
    </div>
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
        <el-button type="primary" size="large" class="modal-submit-btn" :loading="loading" @click="handleRegister">注 册</el-button>
      </el-form-item>
    </el-form>
    <div class="modal-footer">
      <span>已有账号？</span>
      <el-link type="primary" underline="never" @click="switchToLogin">去登录 →</el-link>
    </div>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { User, Lock, UserFilled, Message, School } from '@element-plus/icons-vue'
import { register } from '../api'

const props = defineProps({ modelValue: Boolean })
const emit = defineEmits(['update:modelValue', 'switch-to-login', 'success'])

const formRef = ref()
const loading = ref(false)

const visible = ref(false)
watch(() => props.modelValue, (val) => { visible.value = val })
watch(visible, (val) => { emit('update:modelValue', val) })

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
    visible.value = false
    emit('success')
  } catch (e) { /* handled by interceptor */ }
  finally { loading.value = false }
}

const switchToLogin = () => {
  visible.value = false
  emit('switch-to-login')
}

const handleClose = () => {
  Object.assign(form, { username: '', password: '', nickname: '', email: '' })
  formRef.value?.resetFields()
}
</script>

<style scoped>
.register-dialog :deep(.el-dialog__header) { display: none; }
.register-dialog :deep(.el-dialog__body) { padding: 28px 36px 20px; }
.modal-brand { text-align: center; margin-bottom: 24px; }
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
</style>
