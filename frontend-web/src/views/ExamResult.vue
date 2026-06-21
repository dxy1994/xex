<template>
  <div class="result-page">
    <el-card class="score-card">
      <div class="score-hero">
        <div class="score-ring" :style="{ '--percent': percentage }">
          <svg viewBox="0 0 200 200">
            <circle cx="100" cy="100" r="90" class="ring-bg" />
            <circle cx="100" cy="100" r="90" class="ring-fill" :class="scoreColor" />
          </svg>
          <div class="score-value"><span class="score-num">{{ score }}</span><span class="score-total">/ {{ totalScore }}</span></div>
        </div>
        <div class="score-info">
          <h2 class="result-title">考试完成!</h2>
          <p class="result-exam">{{ exam.title }}</p>
          <div class="result-stats">
            <div class="rstat"><span class="rstat-val" :style="{ color: scoreColor === 'pass' ? '#10b981' : '#ef4444' }">{{ percentage }}%</span><span class="rstat-label">正确率</span></div>
            <div class="rstat-divider"></div>
            <div class="rstat"><span class="rstat-val">{{ correctCount }}/{{ details.length }}</span><span class="rstat-label">正确题数</span></div>
          </div>
        </div>
      </div>
    </el-card>

    <el-card class="detail-card">
      <template #header><div class="detail-header"><el-icon :size="18"><List /></el-icon><span>答题详情</span></div></template>
      <div v-for="(item, index) in details" :key="index" class="detail-item" :class="{ correct: item.isCorrect, wrong: !item.isCorrect }">
        <div class="detail-top">
          <div class="detail-meta">
            <el-tag :type="typeTagMap[item.type]" size="small" effect="dark" round>{{ typeNameMap[item.type] }}</el-tag>
            <span class="detail-num">第 {{ index + 1 }} 题</span>
          </div>
          <el-tag :type="item.isCorrect ? 'success' : 'danger'" size="small" effect="light">{{ item.isCorrect ? '✓ 正确' : '✗ 错误' }}</el-tag>
        </div>
        <p class="detail-content">{{ item.content }}</p>
        <div class="detail-answers">
          <div class="answer-row user-answer"><span class="answer-label">你的答案</span><span class="answer-text">{{ item.userAnswer || '未作答' }}</span></div>
          <div class="answer-row correct-answer"><span class="answer-label">正确答案</span><span class="answer-text">{{ item.correctAnswer }}</span></div>
        </div>
      </div>
    </el-card>

    <div class="result-actions">
      <el-button type="primary" size="large" round @click="$router.push('/dashboard')"><el-icon><HomeFilled /></el-icon>返回首页</el-button>
      <el-button size="large" round @click="$router.push('/dashboard/history')"><el-icon><List /></el-icon>查看历史</el-button>
    </div>

    <!-- 种子号展示与分享 -->
    <div v-if="exam.seed" class="seed-share-card">
      <el-card class="seed-card">
        <div class="seed-card-content">
          <span class="seed-card-label">试卷种子号：</span>
          <el-tag type="success" size="large" effect="dark" class="seed-card-tag">{{ exam.seed }}</el-tag>
          <el-button type="primary" :icon="CopyDocument" @click="copySeed">复制种子号</el-button>
          <el-button type="success" :icon="Share" @click="shareSeed">分享链接</el-button>
        </div>
        <p class="seed-card-tip">分享种子号给朋友，对方即可做同一套试卷</p>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { CopyDocument, Share } from '@element-plus/icons-vue'
import { getExamRecord } from '../api'

const route = useRoute()
const exam = ref({})
const details = ref([])
const score = ref(0)
const totalScore = ref(100)

const typeTagMap = { CHOICE: 'primary', FILL: 'success', JUDGE: 'warning', APPLICATION: 'danger' }
const typeNameMap = { CHOICE: '选择题', FILL: '填空题', JUDGE: '判断题', APPLICATION: '应用题' }

const percentage = computed(() => totalScore.value > 0 ? Math.round((score.value / totalScore.value) * 100) : 0)
const correctCount = computed(() => details.value.filter(d => d.isCorrect).length)
const scoreColor = computed(() => percentage.value >= 60 ? 'pass' : 'fail')

onMounted(async () => {
  const res = await getExamRecord(route.params.userExamId)
  const userExam = res.data.userExam
  exam.value = res.data.exam || {}
  details.value = res.data.details || []
  score.value = userExam.score || 0
  totalScore.value = exam.value.totalScore || 100
})

const copySeed = () => {
  if (!exam.value.seed) return
  navigator.clipboard.writeText(exam.value.seed).then(() => {
    ElMessage.success('种子号已复制到剪贴板')
  }).catch(() => {
    ElMessage.warning('复制失败，请手动复制')
  })
}

const shareSeed = () => {
  if (!exam.value.seed) return
  const url = `${window.location.origin}/dashboard/exam/config?categoryId=${exam.value.categoryId}&categoryName=${encodeURIComponent(exam.value.title)}&seed=${exam.value.seed}`
  navigator.clipboard.writeText(url).then(() => {
    ElMessage.success('分享链接已复制到剪贴板')
  }).catch(() => {
    ElMessage.warning('复制失败，请手动复制链接')
  })
}
</script>

<style scoped>
.result-page { max-width: 1000px; margin: 0 auto; }
.score-card { border-radius: 20px !important; margin-bottom: 24px; overflow: hidden; }
.score-hero { display: flex; align-items: center; gap: 48px; padding: 12px 0; }
.score-ring { position: relative; width: 200px; height: 200px; flex-shrink: 0; }
.score-ring svg { transform: rotate(-90deg); }
.ring-bg { fill: none; stroke: #f0f2f5; stroke-width: 12; }
.ring-fill { fill: none; stroke-width: 12; stroke-linecap: round; stroke-dasharray: 565.48; stroke-dashoffset: calc(565.48 * (1 - var(--percent) / 100)); transition: stroke-dashoffset 1.2s ease; }
.ring-fill.pass { stroke: #10b981; }
.ring-fill.fail { stroke: #ef4444; }
.score-value { position: absolute; inset: 0; display: flex; flex-direction: column; align-items: center; justify-content: center; }
.score-num { font-size: 42px; font-weight: 800; color: #303133; line-height: 1; }
.score-total { font-size: 14px; color: #909399; margin-top: 4px; }
.score-info { flex: 1; }
.result-title { font-size: 26px; font-weight: 700; color: #303133; margin: 0 0 4px; }
.result-exam { font-size: 14px; color: #909399; margin: 0 0 20px; }
.result-stats { display: flex; align-items: center; gap: 24px; }
.rstat { text-align: center; }
.rstat-val { display: block; font-size: 24px; font-weight: 700; }
.rstat-label { font-size: 12px; color: #909399; }
.rstat-divider { width: 1px; height: 36px; background: #ebeef5; }
.detail-card { border-radius: 16px !important; margin-bottom: 24px; }
.detail-header { display: flex; align-items: center; gap: 8px; font-size: 17px; font-weight: 600; }
.detail-item { padding: 18px; margin-bottom: 16px; border-radius: 12px; border: 2px solid #ebeef5; }
.detail-item:last-child { margin-bottom: 0; }
.detail-item.correct { border-color: #d1fae5; background: #f0fdf4; }
.detail-item.wrong { border-color: #fee2e2; background: #fef2f2; }
.detail-top { display: flex; align-items: center; justify-content: space-between; margin-bottom: 12px; }
.detail-meta { display: flex; align-items: center; gap: 10px; }
.detail-num { font-weight: 600; font-size: 14px; color: #303133; }
.detail-content { font-size: 15px; color: #303133; line-height: 1.6; margin: 0 0 14px; padding: 10px 14px; background: rgba(0,0,0,0.02); border-radius: 8px; }
.detail-answers { display: flex; flex-direction: column; gap: 8px; }
.answer-row { display: flex; gap: 12px; font-size: 14px; }
.answer-label { color: #909399; flex-shrink: 0; }
.user-answer .answer-text { color: #606266; }
.correct-answer .answer-text { color: #10b981; font-weight: 500; }
.result-actions { display: flex; justify-content: center; gap: 16px; margin-bottom: 20px; }

/* 种子号分享卡片 */
.seed-share-card { max-width: 600px; margin: 0 auto 20px; }
.seed-card { border-radius: 14px !important; background: #f0fdf4 !important; border: 1px solid #d1fae5 !important; }
.seed-card-content { display: flex; align-items: center; justify-content: center; gap: 12px; flex-wrap: wrap; }
.seed-card-label { font-size: 14px; color: #606266; font-weight: 500; }
.seed-card-tag { font-size: 18px !important; font-weight: 700 !important; letter-spacing: 2px; }
.seed-card-tip { margin: 12px 0 0; font-size: 13px; color: #909399; text-align: center; }

@media (max-width: 768px) { .score-hero { flex-direction: column; gap: 24px; text-align: center; } .result-stats { justify-content: center; } }
</style>
