<template>
  <div class="exam-config-page">
    <div class="config-header">
      <el-button type="default" :icon="ArrowLeft" @click="$router.push('/dashboard')">返回</el-button>
      <h2>考试配置</h2>
    </div>
    <el-card class="config-card">
      <template #header>
        <div class="card-header"><el-icon :size="20" color="#6366f1"><Edit /></el-icon><span>{{ categoryName }}</span></div>
      </template>
      <div class="ratio-section">
        <div class="ratio-title">题型比例配置</div>
        <el-row :gutter="16">
          <el-col :xs="12" :sm="6" v-for="item in ratioItems" :key="item.key">
            <div class="ratio-item" :style="{ borderColor: item.color }">
              <div class="ratio-label" :style="{ color: item.color }"><el-icon><component :is="item.icon" /></el-icon>{{ item.label }}</div>
              <div class="ratio-value" :style="{ color: item.color }">{{ ratio[item.key] }}%</div>
            </div>
          </el-col>
        </el-row>
      </div>
      <el-divider />
      <el-form :model="form" label-width="120px" class="config-form">
        <el-form-item label="题目数量">
          <el-input-number v-model="form.totalQuestions" :min="1" :max="100" size="large" />
          <span class="form-tip">建议 10-50 题</span>
        </el-form-item>
        <el-form-item label="考试时长（分钟）">
          <el-input-number v-model="form.duration" :min="5" :max="180" size="large" />
          <span class="form-tip">建议 30-120 分钟</span>
        </el-form-item>

        <!-- 种子号输入区（可选） -->
        <el-form-item label="种子号">
          <el-input v-model="seedInput" placeholder="输入种子号可重现相同试卷（可选）" size="large" clearable class="seed-input" maxlength="10" @keyup.enter="handleGenerate" />
          <span class="form-tip">留空则自动生成</span>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" size="large" class="generate-btn" :loading="loading" @click="handleGenerate">
            <el-icon><MagicStick /></el-icon>生成试卷并开始考试
          </el-button>
        </el-form-item>
      </el-form>

      <!-- 生成成功后显示种子号 -->
      <div v-if="generatedSeed" class="seed-display">
        <el-divider />
        <div class="seed-info">
          <span class="seed-label">试卷种子号：</span>
          <el-tag type="success" size="large" effect="dark" class="seed-tag">{{ generatedSeed }}</el-tag>
          <el-button type="primary" size="small" :icon="CopyDocument" @click="copySeed">复制</el-button>
          <el-button type="success" size="small" :icon="Share" @click="shareSeed">分享链接</el-button>
        </div>
        <p class="seed-tip">将此种子号分享给他人，对方输入相同种子号即可做同一套试卷</p>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowLeft, Edit, MagicStick, Histogram, EditPen, CircleCheck, Document, CopyDocument, Share } from '@element-plus/icons-vue'
import { getCategoryRatio, generateExam } from '../api'

const route = useRoute()
const router = useRouter()
const categoryId = route.query.categoryId
const categoryName = route.query.categoryName
const loading = ref(false)
const seedInput = ref('')
const generatedSeed = ref('')
const ratio = reactive({ choiceRatio: 60, fillRatio: 20, judgeRatio: 10, applicationRatio: 10 })
const form = reactive({ totalQuestions: 20, duration: 60 })

const ratioItems = [
  { key: 'choiceRatio', label: '选择题', icon: 'Histogram', color: '#6366f1' },
  { key: 'fillRatio', label: '填空题', icon: 'EditPen', color: '#10b981' },
  { key: 'judgeRatio', label: '判断题', icon: 'CircleCheck', color: '#f59e0b' },
  { key: 'applicationRatio', label: '应用题', icon: 'Document', color: '#ef4444' },
]

onMounted(async () => {
  const res = await getCategoryRatio(categoryId)
  Object.assign(ratio, res.data)
  // 如果从分享链接进入（带 seed 参数），自动填充种子号
  if (route.query.seed) {
    seedInput.value = route.query.seed
  }
})

const handleGenerate = async () => {
  loading.value = true
  generatedSeed.value = ''
  try {
    const payload = {
      categoryId: Number(categoryId),
      totalQuestions: form.totalQuestions,
      duration: form.duration
    }
    // 如果用户输入了种子号，带上种子号
    if (seedInput.value.trim()) {
      payload.seed = seedInput.value.trim()
    }
    const res = await generateExam(payload)
    generatedSeed.value = res.data.seed || ''
    ElMessage.success('试卷生成成功，即将开始考试')
    router.push(`/dashboard/exam/${res.data.exam.id}`)
  } catch (e) { /* handled by interceptor */ }
  finally { loading.value = false }
}

const copySeed = () => {
  if (!generatedSeed.value) return
  navigator.clipboard.writeText(generatedSeed.value).then(() => {
    ElMessage.success('种子号已复制到剪贴板')
  }).catch(() => {
    ElMessage.warning('复制失败，请手动复制')
  })
}

const shareSeed = () => {
  if (!generatedSeed.value) return
  const url = `${window.location.origin}/dashboard/exam/config?categoryId=${categoryId}&categoryName=${encodeURIComponent(categoryName)}&seed=${generatedSeed.value}`
  navigator.clipboard.writeText(url).then(() => {
    ElMessage.success('分享链接已复制到剪贴板，发送给朋友即可')
  }).catch(() => {
    ElMessage.warning('复制失败，请手动复制链接')
  })
}
</script>

<style scoped>
.exam-config-page { max-width: 800px; margin: 0 auto; }
.config-header { display: flex; align-items: center; gap: 16px; margin-bottom: 24px; }
.config-header h2 { font-size: 20px; font-weight: 600; margin: 0; }
.config-card { border-radius: 16px !important; }
.card-header { display: flex; align-items: center; gap: 8px; font-size: 17px; font-weight: 600; }
.ratio-section { margin-bottom: 8px; }
.ratio-title { font-size: 15px; font-weight: 600; color: #303133; margin-bottom: 16px; }
.ratio-item { text-align: center; padding: 16px 12px; border-radius: 12px; border: 2px solid; background: #fafafa; margin-bottom: 12px; }
.ratio-label { display: flex; align-items: center; justify-content: center; gap: 6px; font-size: 13px; font-weight: 500; margin-bottom: 6px; }
.ratio-value { font-size: 28px; font-weight: 700; }
.config-form { margin-top: 8px; }
.form-tip { margin-left: 12px; font-size: 13px; color: #909399; }
.seed-input { max-width: 220px; }
.generate-btn { width: 100%; height: 48px; font-size: 16px; border-radius: 10px; letter-spacing: 2px; }

/* 种子号展示区 */
.seed-display { text-align: center; padding: 4px 0; }
.seed-info { display: flex; align-items: center; justify-content: center; gap: 12px; flex-wrap: wrap; }
.seed-label { font-size: 14px; color: #606266; font-weight: 500; }
.seed-tag { font-size: 20px !important; font-weight: 700 !important; letter-spacing: 3px; padding: 8px 20px !important; }
.seed-tip { margin-top: 12px; font-size: 13px; color: #909399; }
</style>
