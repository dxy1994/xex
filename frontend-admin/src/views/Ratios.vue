<template>
  <div class="ratios-layout">
    <div class="ratios-left">
      <div class="admin-toolbar"><h3 class="admin-title">题型比例配置</h3></div>
      <el-card class="tree-card">
        <div class="tree-header">
          <span><el-icon><Collection /></el-icon> 考试分类</span>
          <span class="tree-count">{{ categoryCount }} 个分类</span>
        </div>
        <el-tree
          :data="categories"
          :props="{ label: 'name', children: 'children' }"
          node-key="id"
          highlight-current
          default-expand-all
          @node-click="handleNodeClick"
        >
          <template #default="{ node, data }">
            <span class="tree-node" :class="{ active: selectedCategory === data.id }">
              <el-icon :size="16" color="#6366f1"><Folder /></el-icon>
              <span>{{ node.label }}</span>
            </span>
          </template>
        </el-tree>
      </el-card>
    </div>

    <div class="ratios-right">
      <el-card v-if="selectedCategory" class="ratio-card" v-loading="ratioLoading">
        <template #header>
          <div class="ratio-header">
            <el-icon :size="18" color="#6366f1"><DataAnalysis /></el-icon>
            <span>比例设置 — {{ selectedCategoryName }}</span>
            <el-tag size="small" type="success" v-if="subjectName">{{ subjectName }}学科</el-tag>
          </div>
        </template>

        <!-- 题型比例 -->
        <h4 class="section-title">
          <el-icon><Histogram /></el-icon> 题型占比（总计必须 = 100%）
        </h4>
        <el-form label-width="120px">
          <div v-for="item in typeRatioItems" :key="item.code" class="ratio-row">
            <div class="ratio-label-row">
              <span class="ratio-icon" :style="{ background: item.color }">
                <el-icon :size="16"><component :is="item.icon" /></el-icon>
              </span>
              <span class="ratio-name">{{ item.label }}</span>
              <code class="ratio-code">{{ item.code }}</code>
            </div>
            <el-slider v-model="typeRatios[item.code]" :min="0" :max="100" show-input style="flex:1;" />
          </div>
          <el-divider />
          <div class="ratio-total">
            <el-alert :title="'题型占比合计: '+typeRatioSum+'% '+(typeRatioSum===100?'✓':'⚠ 需要等于 100%')"
              :type="typeRatioSum===100?'success':'error'" :closable="false" show-icon />
          </div>
        </el-form>

        <!-- 难度比例 -->
        <h4 class="section-title" style="margin-top:28px;">
          <el-icon><TrendCharts /></el-icon> 难度分布（总计必须 = 100%）
        </h4>
        <el-form label-width="120px">
          <div v-for="item in difficultyItems" :key="item.code" class="ratio-row">
            <div class="ratio-label-row">
              <span class="ratio-icon" :style="{ background: item.color }">
                <el-icon :size="16"><component :is="item.icon" /></el-icon>
              </span>
              <span class="ratio-name">{{ item.label }}</span>
            </div>
            <el-slider v-model="difficultyRatios[item.code]" :min="0" :max="100" show-input style="flex:1;" />
          </div>
          <el-divider />
          <div class="ratio-total">
            <el-alert :title="'难度占比合计: '+difficultyRatioSum+'% '+(difficultyRatioSum===100?'✓':'⚠ 需要等于 100%')"
              :type="difficultyRatioSum===100?'success':'error'" :closable="false" show-icon />
          </div>
        </el-form>

        <el-form-item style="margin-top:20px;">
          <el-button type="primary" size="large"
            :disabled="typeRatioSum!==100 || difficultyRatioSum!==100" @click="handleSave">
            <el-icon><Check /></el-icon>保存配置
          </el-button>
        </el-form-item>
      </el-card>

      <div v-else-if="categories.length > 0" class="ratio-placeholder">
        <el-empty description="请从左侧分类树中选择一个分类" :image-size="120" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Check, DataAnalysis, Histogram, EditPen, CircleCheck, Document, TrendCharts, Reading, Notebook, MagicStick, Connection, Compass, Microphone, Headset, Collection, Folder } from '@element-plus/icons-vue'
import { adminGetRatioInfo, adminSaveRatio, adminGetCategoryTree } from '../api'

const categories = ref([])
const selectedCategory = ref(null)
const selectedCategoryName = ref('')
const subjectName = ref('')
const ratioLoading = ref(false)
const availableTypes = ref([])

const typeRatios = reactive({})
const difficultyRatios = reactive({ '1': 40, '2': 40, '3': 20 })

// 题型中文映射
const TYPE_LABELS = {
  SINGLE_CHOICE: '单选题', MULTI_CHOICE: '多选题', INDEFINITE_CHOICE: '不定项选', JUDGE: '判断题',
  FILL_BLANK: '填空题', CLOZE: '完形填空', MAP_FILL: '填图题', DICTATION: '默写题',
  SHORT_ANSWER: '简答题', TERM_EXPLAIN: '名词解释', TRANSLATION: '翻译题',
  ESSAY: '论述题', MATERIAL_ANALYSIS: '材料分析', WRITING_BIG: '大作文', WRITING_SMALL: '小作文', CONTINUATION: '读后续写',
  CALCULATION: '计算题', PROOF: '证明题', DERIVATION: '推导题',
  EXPERIMENT: '实验题', DRAWING: '绘图题', LISTENING: '听力题', ORAL: '口语表达',
  OPEN_ENDED: '开放题', RESEARCH: '研究课题', CHART_ANALYSIS: '图表分析'
}

const TYPE_ICONS = {
  SINGLE_CHOICE: 'Histogram', MULTI_CHOICE: 'Connection', INDEFINITE_CHOICE: 'Compass', JUDGE: 'CircleCheck',
  FILL_BLANK: 'EditPen', CLOZE: 'Reading', MAP_FILL: 'Document', DICTATION: 'Notebook',
  SHORT_ANSWER: 'Document', TERM_EXPLAIN: 'MagicStick', TRANSLATION: 'Reading',
  ESSAY: 'Reading', MATERIAL_ANALYSIS: 'Document', WRITING_BIG: 'EditPen', WRITING_SMALL: 'EditPen', CONTINUATION: 'Reading',
  CALCULATION: 'Histogram', PROOF: 'Connection', DERIVATION: 'TrendCharts',
  EXPERIMENT: 'MagicStick', DRAWING: 'EditPen', LISTENING: 'Headset', ORAL: 'Microphone',
  OPEN_ENDED: 'Compass', RESEARCH: 'MagicStick', CHART_ANALYSIS: 'TrendCharts'
}

const COLORS = ['#6366f1','#10b981','#f59e0b','#ef4444','#8b5cf6','#ec4899','#06b6d4','#84cc16','#f97316','#14b8a6','#a855f7','#e11d48','#0ea5e9']

const typeRatioItems = computed(() => {
  return availableTypes.value.map((code, i) => ({
    code,
    label: TYPE_LABELS[code] || code,
    icon: TYPE_ICONS[code] || 'Document',
    color: COLORS[i % COLORS.length]
  }))
})

const difficultyItems = [
  { code: '1', label: '简单（基础知识）', icon: 'CircleCheck', color: '#10b981' },
  { code: '2', label: '中等（理解运用）', icon: 'Histogram', color: '#f59e0b' },
  { code: '3', label: '困难（综合分析）', icon: 'TrendCharts', color: '#ef4444' }
]

const typeRatioSum = computed(() => Object.values(typeRatios).reduce((s, v) => s + (Number(v) || 0), 0))
const difficultyRatioSum = computed(() => Object.values(difficultyRatios).reduce((s, v) => s + (Number(v) || 0), 0))

const countNodes = (nodes) => {
  let count = 0
  for (const n of (nodes || [])) { count++; if (n.children) count += countNodes(n.children) }
  return count
}
const categoryCount = computed(() => countNodes(categories.value))

const handleNodeClick = (data) => {
  selectedCategory.value = data.id
  selectedCategoryName.value = data.name
  loadRatioInfo()
}

const loadRatioInfo = async () => {
  if (!selectedCategory.value) return
  ratioLoading.value = true
  try {
    const res = await adminGetRatioInfo(selectedCategory.value)
    const data = res.data
    subjectName.value = data.subjectName || ''
    availableTypes.value = data.availableTypes || []

    // 重置
    for (const k of Object.keys(typeRatios)) delete typeRatios[k]
    difficultyRatios['1'] = 40; difficultyRatios['2'] = 40; difficultyRatios['3'] = 20

    // 填充已有值
    const ratio = data.ratio
    if (ratio && ratio.typeRatioMap) {
      for (const [k, v] of Object.entries(ratio.typeRatioMap)) {
        typeRatios[k] = Number(v) || 0
      }
    }
    if (ratio && ratio.difficultyRatioMap) {
      for (const [k, v] of Object.entries(ratio.difficultyRatioMap)) {
        difficultyRatios[k] = Number(v) || 0
      }
    }
    // 确保所有题型有默认值
    for (const t of availableTypes.value) {
      if (typeRatios[t] === undefined) typeRatios[t] = 0
    }
  } catch (e) { /* handled */ }
  finally { ratioLoading.value = false }
}

const handleSave = async () => {
  await adminSaveRatio({
    categoryId: selectedCategory.value,
    typeRatios: JSON.stringify({ ...typeRatios }),
    difficultyRatios: JSON.stringify({ ...difficultyRatios })
  })
  ElMessage.success('保存成功')
}

onMounted(async () => {
  const res = await adminGetCategoryTree()
  categories.value = res.data || []
})
</script>

<style scoped>
.ratios-layout { display: flex; gap: 24px; align-items: flex-start; }
.ratios-left { width: 300px; flex-shrink: 0; }
.ratios-right { flex: 1; min-width: 0; }
.admin-toolbar { display: flex; align-items: center; justify-content: space-between; margin-bottom: 16px; }
.admin-title { font-size: 18px; font-weight: 600; margin: 0; }
.tree-card { border-radius: 14px !important; }
.tree-header { display: flex; align-items: center; justify-content: space-between; padding-bottom: 12px; border-bottom: 1px solid #ebeef5; margin-bottom: 8px; font-size: 15px; font-weight: 600; color: #303133; }
.tree-count { font-size: 12px; color: #909399; background: #f0f2f5; padding: 2px 10px; border-radius: 10px; }
.tree-node { display: flex; align-items: center; gap: 8px; padding: 4px 8px; border-radius: 6px; transition: background 0.2s; cursor: pointer; font-size: 14px; color: #303133; }
.tree-node:hover { background: #f0f2f5; }
.tree-node.active { background: #ede9fe; color: #6366f1; font-weight: 600; }

.ratio-card { border-radius: 16px !important; }
.ratio-header { display: flex; align-items: center; gap: 8px; font-size: 16px; font-weight: 600; flex-wrap: wrap; }
.section-title { display: flex; align-items: center; gap: 6px; font-size: 14px; font-weight: 600; color: #303133; margin-bottom: 16px; }
.ratio-placeholder { display: flex; align-items: center; justify-content: center; min-height: 400px; background: #fff; border-radius: 14px; box-shadow: 0 2px 12px rgba(0,0,0,0.06); }
.ratio-row { margin-bottom: 16px; }
.ratio-label-row { display: flex; align-items: center; gap: 8px; margin-bottom: 8px; }
.ratio-icon { display: inline-flex; align-items: center; justify-content: center; width: 24px; height: 24px; border-radius: 6px; color: #fff; }
.ratio-name { font-size: 13px; font-weight: 500; color: #303133; }
.ratio-code { font-size: 11px; color: #909399; background: #f0f2f5; padding: 1px 6px; border-radius: 4px; font-family: monospace; }
.ratio-total { margin-bottom: 12px; }

@media (max-width: 900px) {
  .ratios-layout { flex-direction: column; }
  .ratios-left { width: 100%; }
}
</style>
