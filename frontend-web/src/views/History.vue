<template>
  <div class="history-page">
    <div class="page-header">
      <div class="header-left"><el-icon :size="22" color="#6366f1"><Clock /></el-icon><h2>考试记录</h2></div>
      <el-tag size="small" effect="plain" round>共 {{ history.length }} 条记录</el-tag>
    </div>

    <div v-if="history.length > 0" class="history-grid">
      <el-card v-for="(item, index) in history" :key="index" class="history-card" shadow="hover" @click="$router.push(`/dashboard/exam/result/${item.userExam.id}`)">
        <div class="history-item">
          <div class="history-left">
            <div class="score-badge" :class="{ pass: getRate(item) >= 60, fail: getRate(item) < 60 }">{{ item.userExam.score }}</div>
            <span class="score-label">/ {{ item.exam.totalScore }}</span>
          </div>
          <div class="history-center">
            <h4 class="history-title">{{ item.exam.title }}</h4>
            <div class="history-meta">
              <el-tag :type="item.userExam.status === 1 ? 'success' : 'warning'" size="small" effect="plain" round>{{ item.userExam.status === 1 ? '已完成' : '进行中' }}</el-tag>
              <span class="meta-time">{{ item.userExam.startTime }}</span>
            </div>
          </div>
          <div class="history-right">
            <div class="rate-text" :class="{ pass: getRate(item) >= 60 }">{{ getRate(item) }}%</div>
            <el-button type="primary" round size="small" @click.stop="$router.push(`/dashboard/exam/result/${item.userExam.id}`)">查看详情<el-icon><ArrowRight /></el-icon></el-button>
          </div>
        </div>
      </el-card>
    </div>

    <el-empty v-else description="暂无考试记录" :image-size="120">
      <el-button type="primary" @click="$router.push('/dashboard')">去考试</el-button>
    </el-empty>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getExamHistory } from '../api'

const history = ref([])

onMounted(async () => {
  const res = await getExamHistory()
  history.value = res.data
})

const getRate = (item) => {
  const total = item.exam.totalScore || 100
  return Math.round((item.userExam.score / total) * 100)
}
</script>

<style scoped>
.history-page { }
.page-header { display: flex; align-items: center; justify-content: space-between; margin-bottom: 24px; }
.header-left { display: flex; align-items: center; gap: 10px; }
.header-left h2 { font-size: 20px; font-weight: 600; margin: 0; }
.history-grid { display: grid; grid-template-columns: repeat(2, 1fr); gap: 16px; }
.history-card { border-radius: 14px !important; cursor: pointer; transition: all 0.3s; }
.history-card:hover { transform: translateY(-4px); border-color: #6366f1 !important; box-shadow: 0 8px 24px rgba(0,0,0,0.08); }
.history-item { display: flex; align-items: center; gap: 24px; }
.history-left { display: flex; flex-direction: column; align-items: center; min-width: 70px; }
.score-badge { width: 52px; height: 52px; border-radius: 14px; display: flex; align-items: center; justify-content: center; font-size: 22px; font-weight: 800; color: #fff; }
.score-badge.pass { background: linear-gradient(135deg, #10b981, #059669); }
.score-badge.fail { background: linear-gradient(135deg, #ef4444, #dc2626); }
.score-label { font-size: 12px; color: #909399; margin-top: 4px; }
.history-center { flex: 1; min-width: 0; }
.history-title { font-size: 16px; font-weight: 600; color: #303133; margin: 0 0 8px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.history-meta { display: flex; align-items: center; gap: 12px; }
.meta-time { font-size: 13px; color: #909399; }
.history-right { display: flex; flex-direction: column; align-items: flex-end; gap: 10px; }
.rate-text { font-size: 18px; font-weight: 700; }
.rate-text.pass { color: #10b981; }
.rate-text.fail { color: #ef4444; }

@media (min-width: 1400px) {
  .history-grid { grid-template-columns: repeat(3, 1fr); }
}
@media (max-width: 900px) {
  .history-grid { grid-template-columns: 1fr; }
}
</style>
