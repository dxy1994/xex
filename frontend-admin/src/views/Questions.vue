<template>
  <div class="questions-layout">
    <!-- 左侧：筛选 + 表格 -->
    <div class="questions-left">
      <div class="admin-toolbar">
        <h3 class="admin-title">题库管理</h3>
        <el-button type="primary" :icon="Plus" round @click="openNew">新增题目</el-button>
      </div>

      <el-card class="filter-card" shadow="never">
        <el-form inline>
          <el-form-item label="分类"><el-tree-select v-model="filter.categoryId" :data="categoryTree" :props="{ label: 'name', value: 'id', children: 'children' }" check-strictly clearable placeholder="全部分类" style="width:200px;" /></el-form-item>
          <el-form-item label="题型"><el-select v-model="filter.type" clearable placeholder="全部题型" style="width:160px;"><el-option label="单选题" value="SINGLE_CHOICE" /><el-option label="多选题" value="MULTI_CHOICE" /><el-option label="判断题" value="JUDGE" /><el-option label="填空题" value="FILL_BLANK" /><el-option label="简答题" value="SHORT_ANSWER" /><el-option label="计算题" value="CALCULATION" /></el-select></el-form-item>
          <el-form-item><el-button type="primary" :icon="Search" @click="loadData(1)">搜索</el-button><el-button :icon="Refresh" @click="filter.categoryId=null;filter.type='';loadData(1)">重置</el-button></el-form-item>
        </el-form>
      </el-card>

      <el-card class="table-card">
        <el-table :data="list" border stripe v-loading="loading" empty-text="暂无题目数据" highlight-current-row @row-click="handleRowClick" :row-class-name="rowClassName">
          <el-table-column prop="id" label="ID" width="70" align="center" />
          <el-table-column prop="content" label="题目内容" min-width="220" show-overflow-tooltip />
          <el-table-column label="分类" width="110"><template #default="{row}"><el-tag size="small" effect="plain">{{ getCategoryName(row.categoryId) }}</el-tag></template></el-table-column>
          <el-table-column label="题型" width="90" align="center"><template #default="{row}"><el-tag :type="typeTagMap[row.type]" size="small" effect="dark" round>{{ typeNameMap[row.type] }}</el-tag></template></el-table-column>
          <el-table-column label="难度" width="110" align="center"><template #default="{row}"><el-rate v-model="row.difficulty" disabled :max="3" size="small" /></template></el-table-column>
          <el-table-column prop="score" label="分值" width="70" align="center" />
          <el-table-column label="操作" width="100" fixed="right"><template #default="{row}"><el-button type="danger" link :icon="Delete" @click.stop="handleDelete(row.id)">删除</el-button></template></el-table-column>
        </el-table>
        <el-pagination class="table-pagination" :current-page="pageNum" :page-size="pageSize" :total="total" layout="total, prev, pager, next" @current-change="loadData" />
      </el-card>
    </div>

    <!-- 右侧：编辑面板 -->
    <transition name="panel-slide">
      <div v-if="selectedQuestion !== null" class="questions-right">
        <div class="panel-header">
          <div class="panel-title">
            <el-icon :size="18" color="#6366f1"><Edit /></el-icon>
            <span>{{ form.id ? '编辑题目' : '新增题目' }}</span>
          </div>
          <el-button :icon="Close" circle size="small" @click="closePanel" />
        </div>
        <div class="panel-body">
          <el-form :model="form" label-width="80px" label-position="top">
            <el-form-item label="所属分类" required><el-tree-select v-model="form.categoryId" :data="categoryTree" :props="{ label: 'name', value: 'id', children: 'children' }" check-strictly style="width:100%" placeholder="请选择分类" /></el-form-item>
            <el-form-item label="题型" required><el-select v-model="form.type" style="width:100%" @change="onTypeChange" filterable><el-option label="单选题" value="SINGLE_CHOICE" /><el-option label="多选题" value="MULTI_CHOICE" /><el-option label="判断题" value="JUDGE" /><el-option label="填空题" value="FILL_BLANK" /><el-option label="简答题" value="SHORT_ANSWER" /><el-option label="计算题" value="CALCULATION" /><el-option label="论述题" value="ESSAY" /><el-option label="实验题" value="EXPERIMENT" /></el-select></el-form-item>
            <el-row :gutter="12">
              <el-col :span="12"><el-form-item label="难度"><el-rate v-model="form.difficulty" :max="3" /></el-form-item></el-col>
              <el-col :span="12"><el-form-item label="分值"><el-input-number v-model="form.score" :min="1" :max="100" /></el-form-item></el-col>
            </el-row>
            <el-form-item label="题目内容" required><el-input v-model="form.content" type="textarea" :rows="3" placeholder="请输入题目内容" /></el-form-item>
            <el-form-item v-if="showOptions" label="选项">
              <div v-for="(opt,i) in optionsList" :key="i" class="option-row"><span class="option-tag">{{ String.fromCharCode(65+i) }}</span><el-input v-model="optionsList[i]" :placeholder="'选项 '+String.fromCharCode(65+i)" size="small" /><el-button v-if="optionsList.length>2" type="danger" :icon="Delete" circle size="small" @click="optionsList.splice(i,1)" /></div>
              <el-button size="small" :icon="Plus" @click="optionsList.push('')">添加选项</el-button>
            </el-form-item>
            <el-form-item label="正确答案" required>
              <template v-if="isChoiceType"><el-input v-model="form.answer" placeholder="输入正确选项字母（如 A 或 ABD）" /></template>
              <el-radio-group v-else-if="form.type==='JUDGE'" v-model="form.answer"><el-radio value="true" size="large">正确</el-radio><el-radio value="false" size="large">错误</el-radio></el-radio-group>
              <el-input v-else v-model="form.answer" type="textarea" :rows="2" placeholder="请输入正确答案" />
            </el-form-item>
            <el-form-item label="题目解析"><el-input v-model="form.analysis" type="textarea" :rows="2" placeholder="选填" /></el-form-item>
            <el-form-item label="知识点标签"><el-input v-model="form.knowledgeTags" placeholder="多个标签用逗号分隔，如：拼音,声调" /></el-form-item>
          </el-form>
        </div>
        <div class="panel-footer">
          <el-button @click="closePanel">取消</el-button>
          <el-button type="primary" @click="handleSave">保存</el-button>
        </div>
      </div>
    </transition>

    <!-- 右侧占位提示 -->
    <div v-if="selectedQuestion === null" class="questions-right questions-right--empty">
      <el-empty description="点击表格行查看题目详情，或点击「新增题目」创建" :image-size="120" />
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Edit, Delete, Search, Refresh, Close } from '@element-plus/icons-vue'
import { adminGetQuestions, adminSaveQuestion, adminUpdateQuestion, adminDeleteQuestion, adminGetCategoryTree } from '../api'

const list=ref([]), categoryTree=ref([]), loading=ref(false), pageNum=ref(1), pageSize=ref(10), total=ref(0), optionsList=ref(['','','',''])
const filter=reactive({ categoryId:null, type:'' })
const form=reactive({ id:null, categoryId:null, type:'SINGLE_CHOICE', difficulty:1, content:'', options:'', answer:'', analysis:'', knowledgeTags:'', score:5 })
const selectedQuestion = ref(null)
const typeTagMap = {
  SINGLE_CHOICE: 'primary', MULTI_CHOICE: 'primary', INDEFINITE_CHOICE: 'primary',
  JUDGE: 'warning', FILL_BLANK: 'success', CLOZE: 'success', MAP_FILL: 'success', DICTATION: 'success',
  SHORT_ANSWER: 'info', TERM_EXPLAIN: 'info', TRANSLATION: 'info',
  ESSAY: 'danger', MATERIAL_ANALYSIS: 'danger', WRITING_BIG: 'danger', WRITING_SMALL: 'danger', CONTINUATION: 'danger',
  CALCULATION: '', PROOF: '', DERIVATION: '',
  EXPERIMENT: '', DRAWING: '', LISTENING: '', ORAL: '',
  OPEN_ENDED: 'info', RESEARCH: 'danger', CHART_ANALYSIS: ''
}
const typeNameMap = {
  SINGLE_CHOICE: '单选题', MULTI_CHOICE: '多选题', INDEFINITE_CHOICE: '不定项选',
  JUDGE: '判断题', FILL_BLANK: '填空题', CLOZE: '完形填空', MAP_FILL: '填图题', DICTATION: '默写题',
  SHORT_ANSWER: '简答题', TERM_EXPLAIN: '名词解释', TRANSLATION: '翻译题',
  ESSAY: '论述题', MATERIAL_ANALYSIS: '材料分析', WRITING_BIG: '大作文', WRITING_SMALL: '小作文', CONTINUATION: '读后续写',
  CALCULATION: '计算题', PROOF: '证明题', DERIVATION: '推导题',
  EXPERIMENT: '实验题', DRAWING: '绘图题', LISTENING: '听力题', ORAL: '口语表达',
  OPEN_ENDED: '开放题', RESEARCH: '研究课题', CHART_ANALYSIS: '图表分析'
}

// 选择题类型判断
const showOptions = computed(() => ['SINGLE_CHOICE', 'MULTI_CHOICE', 'INDEFINITE_CHOICE'].includes(form.type))
const isChoiceType = showOptions

// 扁平化树以查找分类名称
const flattenTree = (nodes, map = {}) => {
  for (const n of (nodes || [])) {
    map[n.id] = n.name
    if (n.children) flattenTree(n.children, map)
  }
  return map
}
const getCategoryName=(id)=>flattenTree(categoryTree.value)[id]||''

const loadData=async(p=1)=>{pageNum.value=p;loading.value=true;const res=await adminGetQuestions({pageNum:p,pageSize:pageSize.value,categoryId:filter.categoryId,type:filter.type});list.value=res.data.records;total.value=Number(res.data.total);loading.value=false}
const onTypeChange=()=>{if(!showOptions.value)optionsList.value=[''];else optionsList.value=['','','','']}

const handleRowClick = (row) => {
  selectedQuestion.value = row.id
  Object.assign(form, row)
  try { optionsList.value = row.options ? JSON.parse(row.options) : ['','','',''] } catch { optionsList.value = [] }
  // 将数据库 JSON 数组转为逗号分隔字符串回显
  if (form.knowledgeTags) {
    try { const arr = JSON.parse(form.knowledgeTags); form.knowledgeTags = Array.isArray(arr) ? arr.join(',') : form.knowledgeTags } catch { /* 保持原样 */ }
  }
}

const openNew = () => {
  selectedQuestion.value = -1
  Object.assign(form, { id:null, categoryId:null, type:'SINGLE_CHOICE', difficulty:1, content:'', options:'', answer:'', analysis:'', knowledgeTags:'', score:5 })
  optionsList.value = ['','','','']
}

const closePanel = () => { selectedQuestion.value = null }

const rowClassName = ({ row }) => {
  return selectedQuestion.value === row.id ? 'row-selected' : ''
}

const handleSave=async()=>{
  const data={...form}
  if(isChoiceType.value) data.options=JSON.stringify(optionsList.value.filter(o=>o.trim()))
  else data.options=null
  // 知识点标签：逗号分隔字符串 → JSON数组字符串
  if(data.knowledgeTags && data.knowledgeTags.trim()) {
    data.knowledgeTags = JSON.stringify(data.knowledgeTags.split(',').map(t => t.trim()).filter(t => t))
  } else {
    data.knowledgeTags = null
  }
  if(data.id) await adminUpdateQuestion(data)
  else await adminSaveQuestion(data)
  ElMessage.success('保存成功')
  selectedQuestion.value = null
  loadData(pageNum.value)
}
const handleDelete=async(id)=>{await ElMessageBox.confirm('确定删除该题目吗？','确认删除',{type:'warning'});await adminDeleteQuestion(id);ElMessage.success('删除成功');if(selectedQuestion.value===id)selectedQuestion.value=null;loadData(pageNum.value)}

onMounted(async()=>{const res=await adminGetCategoryTree();categoryTree.value=res.data||[];loadData()})
</script>

<style scoped>
.questions-layout { display: flex; gap: 24px; align-items: flex-start; }
.questions-left { flex: 1; min-width: 0; }
.questions-right {
  width: 440px; flex-shrink: 0;
  background: #fff; border-radius: 14px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.06);
  display: flex; flex-direction: column;
  max-height: calc(100vh - 120px);
  position: sticky; top: 80px;
}
.questions-right--empty {
  display: flex; align-items: center; justify-content: center;
  min-height: 400px; box-shadow: 0 2px 12px rgba(0,0,0,0.06);
}
.panel-header {
  display: flex; align-items: center; justify-content: space-between;
  padding: 16px 20px; border-bottom: 1px solid #ebeef5;
}
.panel-title { display: flex; align-items: center; gap: 8px; font-size: 16px; font-weight: 600; color: #303133; }
.panel-body { flex: 1; overflow-y: auto; padding: 20px; }
.panel-footer {
  display: flex; justify-content: flex-end; gap: 10px;
  padding: 16px 20px; border-top: 1px solid #ebeef5;
}
.admin-toolbar { display: flex; align-items: center; justify-content: space-between; margin-bottom: 16px; }
.admin-title { font-size: 18px; font-weight: 600; margin: 0; }
.filter-card { border-radius: 14px !important; margin-bottom: 16px; }
.table-card { border-radius: 14px !important; }
.table-pagination { margin-top: 16px; }
.option-row { display: flex; align-items: center; gap: 8px; margin-bottom: 8px; }
.option-tag { display: inline-flex; align-items: center; justify-content: center; width: 28px; height: 28px; border-radius: 6px; background: #f0f2f5; font-weight: 700; font-size: 13px; color: #6366f1; flex-shrink: 0; }

/* 选中行高亮 */
:deep(.row-selected) { background-color: #ede9fe !important; }
:deep(.row-selected:hover > td) { background-color: #ddd6fe !important; }

/* 右侧面板滑入动画 */
.panel-slide-enter-active { transition: all 0.3s ease-out; }
.panel-slide-leave-active { transition: all 0.2s ease-in; }
.panel-slide-enter-from { opacity: 0; transform: translateX(20px); }
.panel-slide-leave-to { opacity: 0; transform: translateX(20px); }

/* 响应式：窄屏时上下布局 */
@media (max-width: 1100px) {
  .questions-layout { flex-direction: column; }
  .questions-right, .questions-right--empty { width: 100%; max-height: none; position: static; }
}
</style>
