<template>
  <div class="records-layout">
    <div class="records-left">
      <div class="admin-toolbar"><h3 class="admin-title">用户考试记录</h3></div>
      <el-card class="table-card">
        <el-table :data="list" border stripe v-loading="loading" empty-text="暂无考试记录" highlight-current-row @row-click="handleRowClick" :row-class-name="rowClassName">
          <el-table-column prop="id" label="ID" width="70" align="center" />
          <el-table-column prop="userId" label="用户ID" width="90" align="center" />
          <el-table-column prop="examId" label="试卷ID" width="90" align="center" />
          <el-table-column prop="score" label="得分" width="80" align="center"><template #default="{row}"><span :style="{ color: row.score>=60?'#10b981':'#ef4444', fontWeight: 700 }">{{ row.score }}</span></template></el-table-column>
          <el-table-column label="状态" width="100" align="center"><template #default="{row}"><el-tag :type="row.status===1?'success':'warning'" size="small" effect="dark" round>{{ row.status===1?'已完成':'进行中' }}</el-tag></template></el-table-column>
          <el-table-column prop="startTime" label="开始时间" width="170" />
          <el-table-column prop="endTime" label="结束时间" width="170" />
        </el-table>
        <el-pagination class="table-pagination" :current-page="pageNum" :page-size="pageSize" :total="total" layout="total, prev, pager, next" @current-change="loadData" />
      </el-card>
    </div>

    <!-- 右侧：详情面板 -->
    <transition name="panel-slide">
      <div v-if="selectedRecord" class="records-right">
        <div class="panel-header">
          <div class="panel-title">
            <el-icon :size="18" color="#6366f1"><Notebook /></el-icon>
            <span>记录详情 #{{ selectedRecord.id }}</span>
          </div>
          <el-button :icon="Close" circle size="small" @click="selectedRecord=null" />
        </div>
        <div class="panel-body">
          <div class="detail-row"><span class="detail-label">记录 ID</span><span class="detail-value">{{ selectedRecord.id }}</span></div>
          <div class="detail-row"><span class="detail-label">用户 ID</span><span class="detail-value">{{ selectedRecord.userId }}</span></div>
          <div class="detail-row"><span class="detail-label">试卷 ID</span><span class="detail-value">{{ selectedRecord.examId }}</span></div>
          <div class="detail-row">
            <span class="detail-label">得分</span>
            <span class="detail-value" :style="{ color: selectedRecord.score>=60?'#10b981':'#ef4444', fontWeight: 700, fontSize: '20px' }">{{ selectedRecord.score }}</span>
          </div>
          <div class="detail-row">
            <span class="detail-label">状态</span>
            <el-tag :type="selectedRecord.status===1?'success':'warning'" size="small" effect="dark" round>{{ selectedRecord.status===1?'已完成':'进行中' }}</el-tag>
          </div>
          <div class="detail-row"><span class="detail-label">开始时间</span><span class="detail-value">{{ selectedRecord.startTime }}</span></div>
          <div class="detail-row"><span class="detail-label">结束时间</span><span class="detail-value">{{ selectedRecord.endTime || '-' }}</span></div>
          <el-divider />
          <div class="detail-section-title"><el-icon><Clock /></el-icon> 用时统计</div>
          <div class="detail-row">
            <span class="detail-label">考试用时</span>
            <span class="detail-value">{{ calcDuration }}</span>
          </div>
        </div>
      </div>
    </transition>

    <div v-if="!selectedRecord" class="records-right records-right--empty">
      <el-empty description="点击表格行查看记录详情" :image-size="120" />
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { Close, Notebook, Clock } from '@element-plus/icons-vue'
import { adminGetRecords } from '../api'

const list = ref([])
const loading = ref(false)
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const selectedRecord = ref(null)

const calcDuration = computed(() => {
  if (!selectedRecord.value) return '-'
  const r = selectedRecord.value
  if (r.startTime && r.endTime) {
    try {
      const start = new Date(r.startTime)
      const end = new Date(r.endTime)
      const diff = Math.round((end - start) / 60000)
      return diff > 0 ? `${diff} 分钟` : '< 1 分钟'
    } catch { return '-' }
  }
  return '进行中'
})

const loadData = async (p = 1) => {
  pageNum.value = p
  loading.value = true
  const res = await adminGetRecords({ pageNum: p, pageSize: pageSize.value })
  list.value = res.data.records
  total.value = Number(res.data.total)
  loading.value = false
}

const handleRowClick = (row) => {
  selectedRecord.value = row
}

const rowClassName = ({ row }) => {
  return selectedRecord.value?.id === row.id ? 'row-selected' : ''
}

onMounted(() => loadData())
</script>

<style scoped>
.records-layout { display: flex; gap: 24px; align-items: flex-start; }
.records-left { flex: 1; min-width: 0; }
.records-right {
  width: 380px; flex-shrink: 0;
  background: #fff; border-radius: 14px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.06);
  display: flex; flex-direction: column;
  max-height: calc(100vh - 120px);
  position: sticky; top: 80px;
}
.records-right--empty {
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

:deep(.row-selected) { background-color: #ede9fe !important; }
:deep(.row-selected:hover > td) { background-color: #ddd6fe !important; }

.panel-slide-enter-active { transition: all 0.3s ease-out; }
.panel-slide-leave-active { transition: all 0.2s ease-in; }
.panel-slide-enter-from { opacity: 0; transform: translateX(20px); }
.panel-slide-leave-to { opacity: 0; transform: translateX(20px); }

@media (max-width: 1100px) {
  .records-layout { flex-direction: column; }
  .records-right, .records-right--empty { width: 100%; max-height: none; position: static; }
}
</style>
