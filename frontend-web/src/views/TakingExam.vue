<template>
  <div class="exam-taking-page">
    <el-card v-if="!started" class="exam-intro-card">
      <template #header>
        <div class="intro-header">
          <div class="intro-title"><el-icon :size="22" color="#6366f1"><Tickets /></el-icon><h3>{{ exam.title }}</h3></div>
          <el-tag :type="exam.type === 'RANDOM' ? 'info' : 'primary'" effect="light" round>{{ exam.type === 'RANDOM' ? '随机试卷' : '固定试卷' }}</el-tag>
        </div>
      </template>
      <div class="intro-stats">
        <div class="stat-item"><el-icon :size="32" color="#6366f1"><Document /></el-icon><div class="stat-num">{{ questions.length }}</div><div class="stat-label">总题数</div></div>
        <div class="stat-divider"></div>
        <div class="stat-item"><el-icon :size="32" color="#10b981"><Trophy /></el-icon><div class="stat-num">{{ exam.totalScore }}</div><div class="stat-label">总分</div></div>
        <div class="stat-divider"></div>
        <div class="stat-item"><el-icon :size="32" color="#f59e0b"><Clock /></el-icon><div class="stat-num">{{ exam.duration }}</div><div class="stat-label">分钟</div></div>
      </div>
      <div class="intro-start">
        <el-button type="primary" size="large" round class="start-btn" @click="handleStart"><el-icon><VideoPlay /></el-icon>开始答题</el-button>
      </div>

      <!-- 种子号展示 -->
      <div v-if="exam.seed" class="seed-section">
        <el-divider />
        <div class="seed-row">
          <span class="seed-label">种子号：</span>
          <el-tag type="success" size="large" effect="dark">{{ exam.seed }}</el-tag>
          <el-button type="primary" size="small" :icon="CopyDocument" @click="copySeed">复制</el-button>
          <el-button type="success" size="small" :icon="Share" @click="shareSeed">分享</el-button>
        </div>
      </div>
    </el-card>

    <div v-else class="exam-body">
      <div class="exam-body-inner">
        <!-- 左侧：题目列表 -->
        <div class="exam-questions">
          <div class="exam-topbar">
            <h3 class="exam-title">答题中</h3>
            <div class="topbar-right">
              <span v-if="exam.seed" class="seed-badge" @click="copySeed" title="点击复制种子号">
                <el-icon><Share /></el-icon>{{ exam.seed }}
              </span>
              <div class="timer-box" :class="{ 'timer-warning': remainingTime < 300 }"><el-icon><Clock /></el-icon><span>{{ formatTime(remainingTime) }}</span></div>
            </div>
          </div>

          <el-card v-for="(q, index) in questions" :key="q.id" :ref="el => questionRefs[index] = el" class="question-card" shadow="hover">
            <div class="question-header">
              <div class="question-meta">
                <el-tag :type="typeTagMap[q.type]" size="small" effect="dark" round>{{ typeNameMap[q.type] }}</el-tag>
                <span class="question-num">第 {{ index + 1 }} 题</span>
              </div>
              <span class="question-score">{{ q.score }} 分</span>
            </div>
            <div class="question-content">{{ q.content }}</div>

            <div v-if="q.type === 'CHOICE'" class="question-options">
              <div v-for="(opt, i) in parseOptions(q.options)" :key="i" class="option-item" :class="{ selected: answers[q.id] === opt.charAt(0) }" @click="answers[q.id] = opt.charAt(0)">
                <span class="option-letter">{{ String.fromCharCode(65 + i) }}</span><span class="option-text">{{ opt }}</span>
              </div>
            </div>

            <div v-else-if="q.type === 'JUDGE'" class="judge-options">
              <div class="option-item" :class="{ selected: answers[q.id] === 'true' }" @click="answers[q.id] = 'true'"><el-icon><CircleCheck /></el-icon><span>正确</span></div>
              <div class="option-item" :class="{ selected: answers[q.id] === 'false' }" @click="answers[q.id] = 'false'"><el-icon><CircleClose /></el-icon><span>错误</span></div>
            </div>

            <el-input v-else-if="q.type === 'FILL'" v-model="answers[q.id]" placeholder="请在此输入答案" size="large" class="answer-input" />
            <el-input v-else v-model="answers[q.id]" type="textarea" :rows="4" placeholder="请在此输入答案，可详细描述解题过程" class="answer-textarea" />
          </el-card>

          <div class="submit-area">
            <el-button type="primary" size="large" round class="submit-btn" :loading="submitting" @click="handleSubmit"><el-icon><Finished /></el-icon>提交答卷</el-button>
          </div>
        </div>

        <!-- 右侧：答题卡导航 -->
        <div class="exam-sheet">
          <div class="sheet-card">
            <div class="sheet-header">
              <el-icon :size="18" color="#6366f1"><List /></el-icon>
              <span>答题卡</span>
            </div>
            <div class="sheet-stats">
              <div class="sheet-stat">
                <span class="sheet-stat-num">{{ questions.length }}</span>
                <span class="sheet-stat-label">总题数</span>
              </div>
              <div class="sheet-stat answered">
                <span class="sheet-stat-num">{{ answeredCount }}</span>
                <span class="sheet-stat-label">已答</span>
              </div>
              <div class="sheet-stat unanswered">
                <span class="sheet-stat-num">{{ questions.length - answeredCount }}</span>
                <span class="sheet-stat-label">未答</span>
              </div>
            </div>
            <el-divider style="margin:12px 0" />
            <div class="sheet-grid">
              <div
                v-for="(q, index) in questions"
                :key="q.id"
                class="sheet-num"
                :class="{ answered: isAnswered(q.id), current: currentHighlight === index }"
                @click="scrollToQuestion(index)"
              >
                {{ index + 1 }}
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, onUnmounted, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getExamDetail, startExam, submitExam } from '../api'
import {
  Tickets, Document, Trophy, Clock, VideoPlay,
  CircleCheck, CircleClose, Finished, List, CopyDocument, Share
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const examId = route.params.id
const exam = ref({})
const questions = ref([])
const answers = reactive({})
const started = ref(false)
const userExamId = ref(null)
const remainingTime = ref(0)
const submitting = ref(false)
const questionRefs = ref([])
const currentHighlight = ref(-1)
let timer = null

const typeTagMap = { CHOICE: 'primary', FILL: 'success', JUDGE: 'warning', APPLICATION: 'danger' }
const typeNameMap = { CHOICE: '选择题', FILL: '填空题', JUDGE: '判断题', APPLICATION: '应用题' }

const answeredCount = computed(() => {
  return questions.value.filter(q => answers[q.id] && answers[q.id].trim() !== '').length
})

const isAnswered = (qId) => {
  return answers[qId] && answers[qId].trim() !== ''
}

const parseOptions = (optionsStr) => {
  if (!optionsStr) return []
  try { return JSON.parse(optionsStr) } catch { return [] }
}

const formatTime = (seconds) => {
  const m = Math.floor(seconds / 60)
  const s = seconds % 60
  return `${m.toString().padStart(2, '0')}:${s.toString().padStart(2, '0')}`
}

const scrollToQuestion = (index) => {
  currentHighlight.value = index
  nextTick(() => {
    const el = questionRefs.value[index]
    if (el) {
      const cardEl = el.$el || el
      cardEl.scrollIntoView({ behavior: 'smooth', block: 'center' })
    }
  })
  setTimeout(() => { currentHighlight.value = -1 }, 2000)
}

onMounted(async () => {
  const res = await getExamDetail(examId)
  exam.value = res.data.exam
  questions.value = res.data.questions
  remainingTime.value = exam.value.duration * 60
})

onUnmounted(() => { if (timer) clearInterval(timer) })

const handleStart = async () => {
  try {
    const res = await startExam(examId)
    userExamId.value = res.data.id
    started.value = true
    timer = setInterval(() => {
      remainingTime.value--
      if (remainingTime.value <= 0) { clearInterval(timer); ElMessage.warning('考试时间到，自动提交'); handleSubmit() }
    }, 1000)
  } catch (e) { /* handled by interceptor */ }
}

const handleSubmit = async () => {
  try { await ElMessageBox.confirm('确定提交答卷吗？提交后不能修改。', '确认提交', { type: 'warning', confirmButtonText: '确认提交', cancelButtonText: '继续检查' }) }
  catch { return }
  submitting.value = true
  try {
    const answerList = questions.value.map(q => ({ questionId: q.id, answer: answers[q.id] || '' }))
    const res = await submitExam({ examId: Number(examId), userExamId: userExamId.value, answers: answerList })
    if (timer) clearInterval(timer)
    ElMessage.success(`提交成功！得分：${res.data.score}`)
    router.push(`/dashboard/exam/result/${userExamId.value}`)
  } catch (e) { /* handled by interceptor */ }
  finally { submitting.value = false }
}

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
.exam-taking-page { }
.exam-intro-card { border-radius: 16px !important; max-width: 700px; margin: 0 auto; }
.intro-header { display: flex; align-items: center; justify-content: space-between; }
.intro-title { display: flex; align-items: center; gap: 10px; }
.intro-title h3 { font-size: 18px; font-weight: 600; margin: 0; }
.intro-stats { display: flex; align-items: center; justify-content: center; gap: 32px; padding: 32px 0; }
.stat-item { text-align: center; min-width: 100px; }
.stat-num { font-size: 32px; font-weight: 700; color: #303133; margin: 8px 0 4px; }
.stat-label { font-size: 13px; color: #909399; }
.stat-divider { width: 1px; height: 60px; background: #ebeef5; }
.intro-start { text-align: center; padding-bottom: 8px; }
.start-btn { padding: 14px 48px; font-size: 16px; letter-spacing: 2px; }

/* 种子号展示 */
.seed-section { text-align: center; padding: 0 0 8px; }
.seed-row { display: flex; align-items: center; justify-content: center; gap: 12px; flex-wrap: wrap; }
.seed-label { font-size: 14px; color: #606266; font-weight: 500; }

/* 双栏答题布局 */
.exam-body { }
.exam-body-inner { display: flex; gap: 24px; align-items: flex-start; }
.exam-questions { flex: 1; min-width: 0; }
.exam-sheet { width: 280px; flex-shrink: 0; position: sticky; top: 84px; }

.sheet-card {
  background: #fff; border-radius: 14px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.06);
  padding: 20px;
}
.sheet-header { display: flex; align-items: center; gap: 8px; font-size: 16px; font-weight: 600; color: #303133; margin-bottom: 16px; }
.sheet-stats { display: flex; gap: 0; }
.sheet-stat { flex: 1; text-align: center; padding: 8px 4px; border-radius: 8px; background: #f8f9fb; margin: 0 3px; }
.sheet-stat.answered { background: #f0fdf4; }
.sheet-stat.unanswered { background: #fef3c7; }
.sheet-stat-num { display: block; font-size: 20px; font-weight: 700; color: #303133; }
.sheet-stat.answered .sheet-stat-num { color: #10b981; }
.sheet-stat.unanswered .sheet-stat-num { color: #f59e0b; }
.sheet-stat-label { font-size: 11px; color: #909399; margin-top: 2px; display: block; }
.sheet-grid { display: grid; grid-template-columns: repeat(5, 1fr); gap: 8px; }
.sheet-num {
  display: flex; align-items: center; justify-content: center;
  width: 100%; aspect-ratio: 1;
  border-radius: 8px; background: #f0f2f5;
  font-size: 14px; font-weight: 600; color: #909399;
  cursor: pointer; transition: all 0.2s;
}
.sheet-num:hover { background: #e0e7ff; color: #6366f1; }
.sheet-num.answered { background: #d1fae5; color: #059669; }
.sheet-num.current { box-shadow: 0 0 0 2px #6366f1; background: #ede9fe; color: #6366f1; }

.exam-topbar { display: flex; align-items: center; justify-content: space-between; background: #fff; padding: 16px 24px; border-radius: 14px; box-shadow: 0 2px 8px rgba(0,0,0,0.06); margin-bottom: 20px; }
.exam-title { font-size: 17px; font-weight: 600; margin: 0; }
.topbar-right { display: flex; align-items: center; gap: 12px; }
.seed-badge { display: inline-flex; align-items: center; gap: 4px; font-size: 13px; font-weight: 600; color: #10b981; background: #ecfdf5; padding: 6px 14px; border-radius: 20px; cursor: pointer; transition: all 0.2s; }
.seed-badge:hover { background: #d1fae5; }
.timer-box { display: flex; align-items: center; gap: 8px; background: #ecf5ff; color: #409eff; padding: 8px 18px; border-radius: 20px; font-size: 18px; font-weight: 700; font-variant-numeric: tabular-nums; transition: all 0.3s; }
.timer-box.timer-warning { background: #fef0f0; color: #f56c6c; animation: pulse 1s infinite; }
@keyframes pulse { 0%,100% { transform: scale(1); } 50% { transform: scale(1.05); } }
.question-card { border-radius: 14px !important; margin-bottom: 16px; border-left: 4px solid #6366f1 !important; }
.question-header { display: flex; align-items: center; justify-content: space-between; margin-bottom: 14px; }
.question-meta { display: flex; align-items: center; gap: 12px; }
.question-num { font-weight: 600; font-size: 15px; color: #303133; }
.question-score { font-size: 13px; color: #909399; background: #f5f7fa; padding: 4px 12px; border-radius: 10px; }
.question-content { font-size: 16px; line-height: 1.7; color: #303133; margin-bottom: 18px; padding: 12px 16px; background: #fafbfc; border-radius: 10px; }
.question-options, .judge-options { display: flex; flex-direction: column; gap: 10px; }
.option-item { display: flex; align-items: center; gap: 12px; padding: 12px 16px; border: 2px solid #ebeef5; border-radius: 10px; cursor: pointer; transition: all 0.25s; font-size: 15px; }
.option-item:hover { border-color: #6366f1; background: #f5f3ff; }
.option-item.selected { border-color: #6366f1; background: #ede9fe; color: #6366f1; font-weight: 500; }
.option-letter { display: inline-flex; align-items: center; justify-content: center; width: 30px; height: 30px; border-radius: 50%; background: #f0f2f5; font-weight: 700; font-size: 14px; flex-shrink: 0; }
.option-item.selected .option-letter { background: #6366f1; color: #fff; }
.option-text { flex: 1; }
.answer-input, .answer-textarea { margin-top: 8px; }
.submit-area { text-align: center; margin: 32px 0; }
.submit-btn { padding: 14px 48px; font-size: 16px; letter-spacing: 2px; }

@media (max-width: 1024px) {
  .exam-body-inner { flex-direction: column; }
  .exam-sheet { width: 100%; position: static; }
  .sheet-grid { grid-template-columns: repeat(8, 1fr); }
}
</style>
