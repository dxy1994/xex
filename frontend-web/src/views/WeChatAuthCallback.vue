<template>
  <div class="wechat-callback">
    <div class="callback-card">
      <el-icon :size="48" class="loading-icon" :class="{ spin: loading }">
        <Loading v-if="loading" />
        <CircleCheck v-else-if="success" />
        <CircleClose v-else />
      </el-icon>
      <h2>{{ loading ? '微信登录中...' : success ? '登录成功' : '登录失败' }}</h2>
      <p class="msg">{{ message }}</p>
      <el-button v-if="!loading && !success" type="primary" @click="$router.push('/')">返回首页</el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { wechatLogin } from '../api'
import { useUserStore } from '../stores/user'
import { Loading, CircleCheck, CircleClose } from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const loading = ref(true)
const success = ref(false)
const message = ref('正在通过微信授权登录，请稍候...')

onMounted(async () => {
  const code = route.query.code
  if (!code) {
    loading.value = false
    success.value = false
    message.value = '缺少微信授权码，请重新尝试'
    return
  }

  try {
    const res = await wechatLogin({ code, state: route.query.state })
    userStore.setUserInfo(res.data)
    loading.value = false
    success.value = true
    message.value = `欢迎回来，${res.data.nickname || '同学'}！`
    setTimeout(() => router.push('/dashboard'), 1500)
  } catch (e) {
    loading.value = false
    success.value = false
    message.value = '微信登录失败，请重试或使用账号密码登录'
  }
})
</script>

<style scoped>
.wechat-callback {
  display: flex; justify-content: center; align-items: center; min-height: 100vh;
  background: linear-gradient(135deg, #f0f2f5 0%, #e8ecf1 100%);
}
.callback-card {
  text-align: center; padding: 48px 40px; background: #fff; border-radius: 16px;
  box-shadow: 0 4px 24px rgba(0,0,0,0.08); max-width: 400px; width: 90%;
}
.loading-icon { color: #3b82f6; margin-bottom: 16px; }
.loading-icon.success { color: #22c55e; }
.loading-icon.error { color: #ef4444; }
.spin { animation: rotate 1s linear infinite; }
@keyframes rotate { from { transform: rotate(0deg); } to { transform: rotate(360deg); } }
.callback-card h2 { font-size: 20px; color: #303133; margin: 0 0 8px; }
.msg { font-size: 14px; color: #909399; margin: 0 0 24px; }
</style>
