<template>
  <div class="exams-layout">
    <div class="exams-left">
      <div class="admin-toolbar"><h3 class="admin-title">试卷管理</h3></div>
      <el-card class="table-card">
        <el-table :data="list" border stripe v-loading="loading" empty-text="暂无试卷数据" highlight-current-row @row-click="handleRowClick" :row-class-name="rowClassName">
          <el-table-column prop="id" label="ID" width="70" align="center" />
          <el-table-column prop="title" label="试卷名称" min-width="200" show-overflow-tooltip />
          <el-table-column label="类型" width="100" align="center"><template #default="{row}"><el-tag :type="row.type==='RANDOM'?'info':'primary'" size="small" effect="dark" round>{{ row.type==='RANDOM'?'随机':'固定' }}</el-tag></template></el-table-column>
          <el-table-column prop="totalScore" label="总分" width="90" align="center" />
          <el-table-column prop="duration" label="时长(分)" width="90" align="center" />
          <el-table-column prop="createTime" label="创建时间" width="180" />
          <el-table-column label="操作" width="100" fixed="right"><template #default="{row}"><el-button type="danger" link :icon="Delete" @click.stop="handleDelete(row.id)">删除</el-button></template></el-table-column>
        </el-table>
        <el-pagination v-if="total>pageSize" class="table-pagination" :current-page="pageNum" :page-size="pageSize" :total="total" layout="total, prev, pager, next" @current-change="loadData" />
      </el-card>
    </div>

    <!-- 右侧：详情面板 -->
    <transition name="panel-slide">
      <div v-if="selectedExam" class="exams-right">
        <div class="panel-header">
          <div class="panel-title">
            <el-icon :size="18" color="#6366f1"><Tickets /></el-icon>
            <span>试卷详情</span>
          </div>
          <el-button :icon="Close" circle size="small" @click="selectedExam=null" />
        </div>
        <div class="panel-body" v-loading="detailLoading">
          <div class="detail-row"><span class="detail-label">试卷名称</span><span class="detail-value">{{ selectedExam.title }}</span></div>
          <div class="detail-row"><span class="detail-label">试卷类型</span><el-tag :type="selectedExam.type==='RANDOM'?'info':'primary'" size="small" effect="dark" round>{{ selectedExam.type==='RANDOM'?'随机':'固定' }}</el-tag></div>
          <div class="detail-row"><span class="detail-label">总分</span><span class="detail-value" style="font-weight:700;color:#6366f1">{{ selectedExam.totalScore }}</span></div>
          <div class="detail-row"><span class="detail-label">考试时长</span><span class="detail-value">{{ selectedExam.duration }} 分钟</span></div>
          <div class="detail-row"><span class="detail-label">创建时间</span><span class="detail-value">{{ selectedExam.createTime }}</span></div>
          <el-divider />
          <div class="detail-section-title"><el-icon><Document /></el-icon> 题目列表 ({{ examQuestions.length }} 题)</div>
          <div v-if="examQuestions.length > 0" class="question-list">
            <div v-for="(q, i) in examQuestions" :key="q.id" class="question-item">
              <span class="q-index">{{ i + 1 }}</span>
              <span class="q-content">{{ q.content }}</span>
              <el-tag :type="typeTagMap[q.type]" size="small" effect="plain" round>{{ typeNameMap[q.type] }}</el-tag>
            </div>
          </div>
          <el-empty v-else description="暂无题目数据" :image-size="60" />
        </div>
      </div>
    </transition>

    <div v-if="!selectedExam" class="exams-right exams-right--empty">
      <el-empty description="点击表格行查看试卷详情" :image-size="120" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Delete, Tickets, Document, Close } from '@element-plus/icons-vue'
import { adminGetExams, adminGetExamDetail, adminDeleteExam } from '../api'

const list = ref([])
const loading = ref(false)
const selectedExam = ref(null)
const examQuestions = ref([])
const detailLoading = ref(false)
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

const typeTagMap = { CHOICE: 'primary', FILL: 'success', JUDGE: 'warning', APPLICATION: 'danger' }
const typeNameMap = { CHOICE: '选择题', FILL: '填空题', JUDGE: '判断题', APPLICATION: '应用题' }

const loadData = async (p = 1) => {
  pageNum.value = p
  loading.value = true
  const res = await adminGetExams()
  list.value = res.data || []
  total.value = list.value.length
  loading.value = false
}

const handleRowClick = async (row) => {
  selectedExam.value = row
  detailLoading.value = true
  try {
    const res = await adminGetExamDetail(row.id)
    examQuestions.value = res.data?.questions || []
  } catch { examQuestions.value = [] }
  finally { detailLoading.value = false }
}

const rowClassName = ({ row }) => {
  return selectedExam.value?.id === row.id ? 'row-selected' : ''
}

const handleDelete = async (id) => {
  await ElMessageBox.confirm('确定删除该试卷吗？', '确认删除', { type: 'warning' })
  await adminDeleteExam(id)
  ElMessage.success('删除成功')
  if (selectedExam.value?.id === id) selectedExam.value = null
  loadData()
}

onMounted(loadData)
</script>

<style scoped>
.exams-layout { display: flex; gap: 24px; align-items: flex-start; }
.exams-left { flex: 1; min-width: 0; }
.exams-right {
  width: 420px; flex-shrink: 0;
  background: #fff; border-radius: 14px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.06);
  display: flex; flex-direction: column;
  max-height: calc(100vh - 120px);
  position: sticky; top: 80px;
}
.exams-right--empty {
  display: flex; align-items: center; justify-content: center;
  min-height: 400px; box-shadow: 0 2px 12px rgba(0,0,0,0.06);
}
.panel-header {
  display: flex; align-items: center; justify-content: space-between;
  padding: 16px 20px; border-bottom: 1px solid #ebeef5;
}
.panel-title { display: flex; align-items: center; gap: 8px; font-size: 16px; font-weight: 600; color: #303133; }
.panel-body { flex: 1; overflow-y: auto; padding: 20px; }
.admin-toolbar { display: flex; align-items: center; justify-content: space-between; margin-bottom: 16px; }
.admin-title { font-size: 18px; font-weight: 600; margin: 0; }
.table-card { border-radius: 14px !important; }
.table-pagination { margin-top: 16px; }

.detail-row { display: flex; align-items: center; justify-content: space-between; padding: 10px 0; border-bottom: 1px solid #f5f7fa; }
.detail-label { font-size: 14px; color: #909399; }
.detail-value { font-size: 14px; color: #303133; font-weight: 500; }
.detail-section-title { font-size: 15px; font-weight: 600; color: #303133; display: flex; align-items: center; gap: 6px; margin-bottom: 12px; }
.question-list { display: flex; flex-direction: column; gap: 8px; }
.question-item { display: flex; align-items: center; gap: 10px; padding: 8px 10px; background: #f8f9fb; border-radius: 8px; }
.q-index { display: inline-flex; align-items: center; justify-content: center; width: 24px; height: 24px; border-radius: 6px; background: #e0e7ff; color: #6366f1; font-size: 12px; font-weight: 700; flex-shrink: 0; }
.q-content { flex: 1; font-size: 13px; color: #303133; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }

:deep(.row-selected) { background-color: #ede9fe !important; }
:deep(.row-selected:hover > td) { background-color: #ddd6fe !important; }

.panel-slide-enter-active { transition: all 0.3s ease-out; }
.panel-slide-leave-active { transition: all 0.2s ease-in; }
.panel-slide-enter-from { opacity: 0; transform: translateX(20px); }
.panel-slide-leave-to { opacity: 0; transform: translateX(20px); }

@media (max-width: 1100px) {
  .exams-layout { flex-direction: column; }
  .exams-right, .exams-right--empty { width: 100%; max-height: none; position: static; }
}
</style>
