<template>
  <div class="dashboard-page" :class="{ 'has-grade-layout': currentLevel.length > 0 }">
    <!-- 面包屑导航 -->
    <div class="breadcrumb-bar">
      <span class="breadcrumb-item" :class="{ active: currentLevel.length === 0 }" @click="goToLevel([])">
        <el-icon><HomeFilled /></el-icon> 全部年级
      </span>
      <template v-for="(item, idx) in currentLevel" :key="item.id">
        <span class="breadcrumb-sep">/</span>
        <span class="breadcrumb-item" :class="{ active: idx === currentLevel.length - 1 }" @click="goToLevel(currentLevel.slice(0, idx + 1))">
          {{ item.name }}
        </span>
      </template>
    </div>

    <!-- ===== 状态1：年级选择（按小学/初中分组） ===== -->
    <template v-if="currentLevel.length === 0">
      <!-- 小学 -->
      <template v-if="primaryGrades.length > 0">
        <div class="section-header">
          <h2><el-icon><School /></el-icon> 小学</h2>
          <span class="section-count">{{ primaryGrades.length }} 个年级</span>
        </div>
        <el-row :gutter="24">
          <el-col :xs="24" :sm="12" :md="8" :lg="6" :xl="4" v-for="cat in primaryGrades" :key="cat.id">
            <div class="category-card" @click="handleCardClick(cat)">
              <div class="card-icon" :class="{ 'has-image': cat.imageUrl }">
                <img v-if="cat.imageUrl" :src="cat.imageUrl" class="card-icon-img" />
                <el-icon v-else :size="32" :style="{ background: iconColors[(cat.sortOrder || cat.id) % iconColors.length] }" class="card-icon-fallback">
                  <Notebook />
                </el-icon>
              </div>
              <div class="card-body">
                <h3 class="card-name">{{ cat.name }}</h3>
                <p class="card-desc">{{ cat.description || '暂无描述' }}</p>
                <div v-if="cat.children && cat.children.length > 0" class="card-badge">
                  {{ cat.children.length }} 门课程
                </div>
              </div>
              <div class="card-action">
                <span>选择课程</span>
                <el-icon><ArrowRight /></el-icon>
              </div>
            </div>
          </el-col>
        </el-row>
      </template>

      <!-- 初中 -->
      <template v-if="middleGrades.length > 0">
        <div class="section-header" style="margin-top: 32px;">
          <h2><el-icon><Collection /></el-icon> 初中</h2>
          <span class="section-count">{{ middleGrades.length }} 个年级</span>
        </div>
        <el-row :gutter="24">
          <el-col :xs="24" :sm="12" :md="8" :lg="6" :xl="4" v-for="cat in middleGrades" :key="cat.id">
            <div class="category-card" @click="handleCardClick(cat)">
              <div class="card-icon" :class="{ 'has-image': cat.imageUrl }">
                <img v-if="cat.imageUrl" :src="cat.imageUrl" class="card-icon-img" />
                <el-icon v-else :size="32" :style="{ background: iconColors[(cat.sortOrder || cat.id) % iconColors.length] }" class="card-icon-fallback">
                  <Notebook />
                </el-icon>
              </div>
              <div class="card-body">
                <h3 class="card-name">{{ cat.name }}</h3>
                <p class="card-desc">{{ cat.description || '暂无描述' }}</p>
                <div v-if="cat.children && cat.children.length > 0" class="card-badge">
                  {{ cat.children.length }} 门课程
                </div>
              </div>
              <div class="card-action">
                <span>选择课程</span>
                <el-icon><ArrowRight /></el-icon>
              </div>
            </div>
          </el-col>
        </el-row>
      </template>

      <el-empty v-if="!loading && sortedGrades.length === 0" description="暂无可选年级" :image-size="100" />
    </template>

    <!-- ===== 状态2：进入年级（双栏布局） ===== -->
    <template v-else>
      <div class="grade-layout">
        <!-- 左侧：树形目录 -->
        <aside class="tree-panel">
          <div class="tree-panel-header">
            <h3><el-icon><Reading /></el-icon> 课程目录</h3>
            <span class="tree-panel-gradename">{{ currentParent?.name }}</span>
          </div>
          <div class="tree-panel-body">
            <el-tree
              ref="treeRef"
              :data="courseTreeData"
              :props="{ children: 'children', label: 'name' }"
              node-key="id"
              highlight-current
              :expand-on-click-node="false"
              :default-expanded-keys="defaultExpandedKeys"
              @node-click="handleTreeNodeClick"
            >
              <template #default="{ node, data }">
                <div class="tree-node-content" :class="'depth-' + (node.level - 1)">
                  <el-icon v-if="node.level === 1" :size="18" color="#6366f1"><Notebook /></el-icon>
                  <el-icon v-else-if="node.level === 2" :size="16" color="#10b981"><Files /></el-icon>
                  <el-icon v-else-if="node.level === 3" :size="16" color="#f59e0b"><Collection /></el-icon>
                  <el-icon v-else :size="16" color="#8b5cf6"><Document /></el-icon>
                  <span class="tree-node-label">{{ data.name }}</span>
                  <span v-if="data.children && data.children.length" class="tree-node-count">{{ data.children.length }}</span>
                </div>
              </template>
            </el-tree>
            <el-empty v-if="courseTreeData.length === 0 && !loading" description="该年级暂无课程" :image-size="60" />
          </div>
        </aside>

        <!-- 右侧：内容区域 -->
        <section class="content-panel">
          <!-- 选中了节点 -->
          <template v-if="activeTreeNode">
            <div class="detail-card">
              <div class="detail-header">
                <div class="detail-title-row">
                  <span class="detail-depth-tag" :style="{ background: depthColor }">{{ depthLabel }}</span>
                  <h2>{{ activeTreeNode.name }}</h2>
                </div>
                <div v-if="activeTreeNode.imageUrl" class="detail-cover">
                  <el-image :src="activeTreeNode.imageUrl" fit="cover" class="detail-cover-img" />
                </div>
                <p v-if="activeTreeNode.description" class="detail-desc">{{ activeTreeNode.description }}</p>
              </div>

              <!-- 操作按钮 -->
              <div class="detail-action">
                <el-button type="primary" size="large" class="generate-btn" @click="handleGenerateExam(activeTreeNode)">
                  <el-icon><MagicStick /></el-icon>
                  {{ actionLabel }}
                </el-button>
              </div>
            </div>

            <!-- 子节点列表 -->
            <div v-if="activeTreeNode.children && activeTreeNode.children.length" class="children-section">
              <div class="children-header">
                <h3>{{ childrenTitle }}</h3>
                <span class="children-count">共 {{ activeTreeNode.children.length }} 项</span>
              </div>
              <div class="children-grid">
                <div
                  v-for="child in activeTreeNode.children"
                  :key="child.id"
                  class="child-item"
                  @click="navigateToChild(child)"
                >
                  <div class="child-item-icon">
                    <el-icon :size="20"><ArrowRight /></el-icon>
                  </div>
                  <div class="child-item-body">
                    <span class="child-item-name">{{ child.name }}</span>
                    <span v-if="child.children && child.children.length" class="child-item-hint">{{ child.children.length }} 个子项</span>
                    <span v-else class="child-item-hint leaf">可生成试卷</span>
                  </div>
                </div>
              </div>
            </div>
          </template>

          <!-- 未选中节点：默认引导 -->
          <template v-else>
            <div class="default-content">
              <div class="default-icon-wrap">
                <div class="default-icon">
                  <el-icon :size="48" color="#c0c4cc"><Reading /></el-icon>
                </div>
              </div>
              <h3>浏览课程分类</h3>
              <p>从左侧树形目录中选择一个课程<br />选择不同层级可生成对应范围的试卷</p>
              <div class="default-hints">
                <div class="hint-item">
                  <el-icon :size="18" color="#6366f1"><Notebook /></el-icon>
                  <span>选课程 → 综合试题</span>
                </div>
                <div class="hint-item">
                  <el-icon :size="18" color="#10b981"><Files /></el-icon>
                  <span>选分册 → 单册试题</span>
                </div>
                <div class="hint-item">
                  <el-icon :size="18" color="#f59e0b"><Collection /></el-icon>
                  <span>选章节 → 章节试题</span>
                </div>
              </div>

              <!-- 种子号入口 -->
              <el-divider style="max-width: 360px; margin: 28px auto 20px;" />
              <div class="seed-entry">
                <h4><el-icon :size="18" color="#10b981"><Share /></el-icon>加入种子号试卷</h4>
                <p>输入朋友分享的种子号，生成相同的试卷</p>
                <div class="seed-entry-form">
                  <el-input v-model="seedQuery" placeholder="输入种子号，如：A3K9M2" size="large" maxlength="10" clearable @keyup.enter="handleSeedQuery" />
                  <el-button type="success" size="large" :icon="Search" :loading="seedLoading" @click="handleSeedQuery">查询试卷</el-button>
                </div>
              </div>
            </div>
          </template>
        </section>
      </div>
    </template>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getCategories, saveGradeSelection, getGradeSelection, getExamBySeed, generateExamBySeed } from '../api'
import { useUserStore } from '../stores/user'
import {
  Reading, Collection, ArrowRight, HomeFilled,
  School, Notebook, Document, Files, MagicStick, Share, Search
} from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()
const treeData = ref([])
const loading = ref(true)
const currentLevel = ref([])       // 面包屑路径
const activeTreeNode = ref(null)   // 当前选中的树节点
const activeNodeLevel = ref(0)     // 当前选中节点的层级（1=课程,2=分册,3=章节,4=小章节）
const treeRef = ref(null)
const seedQuery = ref('')         // 种子号查询输入
const seedLoading = ref(false)    // 种子号查询加载状态

// ===== 缓存层级路径（登录用户→Redis，游客→localStorage），最多保存5级 =====
const getGuestPathKey = () => `lastPath_${userStore.userId || 'guest'}`

const persistGradeSelection = async (path) => {
  if (!path || path.length === 0) return
  if (userStore.token) {
    try { await saveGradeSelection(path) } catch { /* 静默失败 */ }
  }
  // 游客降级到 localStorage
  localStorage.setItem(getGuestPathKey(), path.join(','))
}

const restoreGradeSelection = async () => {
  // 优先从 localStorage 读取（即时、无网络延迟）
  const cached = localStorage.getItem(getGuestPathKey())
  if (cached) {
    const ids = cached.split(',').map(Number).filter(id => !Number.isNaN(id))
    if (ids.length > 0) return ids
  }
  // 登录用户降级到 Redis 获取
  if (userStore.token) {
    try {
      const res = await getGradeSelection()
      if (res.data?.path && res.data.path.length > 0) return res.data.path
    } catch { /* 静默失败 */ }
  }
  return []
}

/** 构建从年级到当前节点的完整 ID 路径 */
const buildNodePath = (node, currentNodeId) => {
  const gradeId = currentLevel.value[0]?.id
  if (!gradeId) return []
  const ids = [gradeId]
  const ancestors = []
  let p = node.parent
  while (p && p.level > 0) {
    ancestors.unshift(p.data.id)
    p = p.parent
  }
  ids.push(...ancestors, currentNodeId)
  return ids.slice(0, 5)
}

/** 在树中按 ID 查找节点 */
const findNodeById = (nodes, targetId) => {
  for (const n of nodes) {
    if (n.id === targetId) return n
    if (n.children && n.children.length) {
      const found = findNodeById(n.children, targetId)
      if (found) return found
    }
  }
  return null
}

// ===== 排序后的年级列表 =====
const sortedGrades = computed(() => {
  return [...treeData.value].sort((a, b) => (a.sortOrder ?? 0) - (b.sortOrder ?? 0))
})

// ===== 按小学/初中分组 =====
const primaryGrades = computed(() => sortedGrades.value.filter(c => c.stageLabel === '小学'))
const middleGrades = computed(() => sortedGrades.value.filter(c => c.stageLabel === '初中'))

// ===== 当前父节点 =====
const currentParent = computed(() => {
  if (currentLevel.value.length === 0) return null
  return currentLevel.value[currentLevel.value.length - 1]
})

// ===== 课程树数据（当前年级下的课程子树） =====
const courseTreeData = computed(() => {
  if (currentLevel.value.length === 0) return []
  const grade = currentLevel.value[currentLevel.value.length - 1]
  // 对子节点排序
  return [...(grade.children || [])].sort((a, b) => (a.sortOrder ?? 0) - (b.sortOrder ?? 0))
})

// ===== 默认展开所有一级节点（课程），展示至少两级 =====
const defaultExpandedKeys = computed(() => {
  return courseTreeData.value.map(item => item.id)
})

// ===== 卡片渐变色 =====
const iconColors = [
  'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
  'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)',
  'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)',
  'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)',
  'linear-gradient(135deg, #fa709a 0%, #fee140 100%)',
  'linear-gradient(135deg, #a18cd1 0%, #fbc2eb 100%)',
  'linear-gradient(135deg, #fccb90 0%, #d57eeb 100%)',
  'linear-gradient(135deg, #e0c3fc 0%, #8ec5fc 100%)',
]

// ===== 根据节点层级获取标签 =====
const depthLabel = computed(() => {
  const map = { 1: '课程', 2: '分册', 3: '章节', 4: '知识点' }
  return map[activeNodeLevel.value] || '分类'
})

const depthColor = computed(() => {
  const map = { 1: '#6366f1', 2: '#10b981', 3: '#f59e0b', 4: '#8b5cf6' }
  return map[activeNodeLevel.value] || '#909399'
})

const actionLabel = computed(() => {
  const hasChildren = activeTreeNode.value?.children && activeTreeNode.value.children.length > 0
  if (activeNodeLevel.value === 1) return '生成综合试题'
  if (activeNodeLevel.value === 2) return '生成单册试题'
  if (activeNodeLevel.value === 3) return '生成章节试题'
  return '开始考试'
})

const childrenTitle = computed(() => {
  const map = { 1: '课程分册', 2: '大章节', 3: '小章节' }
  return map[activeNodeLevel.value] || '子分类'
})

// ===== 事件处理 =====
const handleCardClick = (cat) => {
  // 进入年级
  currentLevel.value = [cat]
  activeTreeNode.value = null
  activeNodeLevel.value = 0
  // 持久化年级路径到 Redis（登录用户）或 localStorage（游客）
  persistGradeSelection([cat.id])
}

const goToLevel = (level) => {
  currentLevel.value = level
  activeTreeNode.value = null
  activeNodeLevel.value = 0
}

const handleTreeNodeClick = (data, node) => {
  activeTreeNode.value = data
  activeNodeLevel.value = node.level
  treeRef.value?.setCurrentKey(data.id)
  // 若有子节点则展开，展示上下册及后续子分类
  if (data.children && data.children.length > 0) {
    node.expand()
  }
  // 持久化完整层级路径
  persistGradeSelection(buildNodePath(node, data.id))
}

const navigateToChild = (child) => {
  activeTreeNode.value = child
  // 根据 child 在课程树中的位置推断层级
  activeNodeLevel.value = findNodeDepth(courseTreeData.value, child.id, 1)
  nextTick(() => {
    if (treeRef.value) {
      treeRef.value.setCurrentKey(child.id)
      // 展开所有祖先节点，使子节点在树中可见
      const node = treeRef.value.getNode(child.id)
      if (node) {
        let parent = node.parent
        while (parent && parent.level > 0) {
          parent.expand()
          parent = parent.parent
        }
        // 持久化完整层级路径
        persistGradeSelection(buildNodePath(node, child.id))
      }
    }
  })
}

// 在树中查找节点深度
const findNodeDepth = (nodes, targetId, depth) => {
  for (const n of nodes) {
    if (n.id === targetId) return depth
    if (n.children && n.children.length) {
      const found = findNodeDepth(n.children, targetId, depth + 1)
      if (found > 0) return found
    }
  }
  return -1
}

const handleGenerateExam = (node) => {
  router.push({
    path: '/dashboard/exam/config',
    query: { categoryId: node.id, categoryName: node.name }
  })
}

const handleSeedQuery = async () => {
  const seed = seedQuery.value.trim()
  if (!seed) {
    ElMessage.warning('请输入种子号')
    return
  }
  seedLoading.value = true
  try {
    // 先查询种子号对应的试卷信息
    const res = await getExamBySeed(seed)
    if (res.data && res.data.exam) {
      const exam = res.data.exam
      const categoryName = res.data.categoryName || '未知分类'
      // 跳转到考试配置页面，带上种子号和分类信息
      router.push({
        path: '/dashboard/exam/config',
        query: {
          categoryId: exam.categoryId,
          categoryName: categoryName,
          seed: seed
        }
      })
    }
  } catch (e) {
    // 如果找不到已有试卷，尝试用默认分类生成
    ElMessage.info('未找到该种子号的试卷记录，请先选择分类后输入种子号生成')
  } finally {
    seedLoading.value = false
  }
}

onMounted(async () => {
  try {
    const res = await getCategories(userStore.educationSystem)
    treeData.value = res.data || []
    // 尝试恢复层级路径（Redis → localStorage 降级）
    const path = await restoreGradeSelection()
    if (path.length > 0) {
      const gradeId = path[0]
      const grade = findNodeById(treeData.value, gradeId)
      if (grade) {
        currentLevel.value = [grade]
        // 若路径更深，恢复树节点选中状态
        if (path.length > 1) {
          await nextTick()
          const deepestId = path[path.length - 1]
          const node = treeRef.value?.getNode(deepestId)
          if (node) {
            // 展开所有祖先节点
            let parent = node.parent
            while (parent && parent.level > 0) {
              parent.expand()
              parent = parent.parent
            }
            treeRef.value.setCurrentKey(deepestId)
            activeTreeNode.value = node.data
            activeNodeLevel.value = node.level
          }
        }
      }
    }
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
/* ===== 页面容器 ===== */
.dashboard-page {
  max-width: 100%;
}
.dashboard-page.has-grade-layout {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 0;
}

/* ===== 面包屑 ===== */
.breadcrumb-bar {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 24px;
  padding: 10px 16px;
  background: #f8f9fb;
  border-radius: 10px;
  font-size: 14px;
  color: #606266;
  flex-wrap: wrap;
}
.breadcrumb-item {
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 4px;
  color: #409eff;
  font-weight: 500;
  transition: color 0.2s;
}
.breadcrumb-item:hover { color: #6366f1; }
.breadcrumb-item.active {
  color: #303133;
  cursor: default;
  font-weight: 600;
}
.breadcrumb-sep { color: #c0c4cc; }

/* ===== 年级卡片区 ===== */
.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
}
.section-header h2 {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
  display: flex;
  align-items: center;
  gap: 8px;
  margin: 0;
}
.section-count {
  font-size: 13px;
  color: #909399;
  background: #f0f2f5;
  padding: 4px 12px;
  border-radius: 12px;
}

.category-card {
  background: #fff;
  border-radius: 16px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  margin-bottom: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}
.category-card:hover {
  transform: translateY(-6px);
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.12);
}
.card-icon {
  height: 80px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  overflow: hidden;
}
.card-icon.has-image {
  height: 160px;
}
.card-icon-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.card-icon-fallback {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}
.card-body {
  padding: 20px;
  text-align: center;
}
.card-name {
  font-size: 17px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 8px;
}
.card-desc {
  font-size: 13px;
  color: #909399;
  margin: 0 0 8px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  min-height: 36px;
}
.card-badge {
  display: inline-block;
  font-size: 12px;
  color: #6366f1;
  background: #f0f0ff;
  border-radius: 8px;
  padding: 2px 8px;
}
.card-action {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  padding: 12px 20px 16px;
  font-size: 14px;
  color: #6366f1;
  font-weight: 500;
}
.category-card:hover .card-action .el-icon {
  transform: translateX(4px);
  transition: transform 0.3s;
}

/* ===== 双栏布局 ===== */
.grade-layout {
  display: flex;
  gap: 24px;
  flex: 1;
  min-height: 0;
}

/* ===== 左侧树面板 ===== */
.tree-panel {
  width: 300px;
  flex-shrink: 0;
  background: #fff;
  border-radius: 14px;
  box-shadow: 0 1px 6px rgba(0, 0, 0, 0.06);
  overflow: hidden;
  border: 1px solid #f0f2f5;
  display: flex;
  flex-direction: column;
}
.tree-panel-header {
  padding: 16px 20px;
  border-bottom: 1px solid #f0f2f5;
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.tree-panel-header h3 {
  margin: 0;
  font-size: 15px;
  font-weight: 600;
  color: #303133;
  display: flex;
  align-items: center;
  gap: 8px;
}
.tree-panel-gradename {
  font-size: 12px;
  color: #909399;
  background: #f5f7fa;
  padding: 2px 10px;
  border-radius: 8px;
}
.tree-panel-body {
  flex: 1;
  overflow-y: auto;
  padding: 8px 0;
}
.tree-panel-body :deep(.el-tree) {
  background: transparent;
}
.tree-panel-body :deep(.el-tree-node__content) {
  height: 40px;
  padding: 0 16px;
  border-radius: 0;
}
.tree-panel-body :deep(.el-tree-node__content:hover) {
  background: #f5f7fa;
}
.tree-panel-body :deep(.el-tree-node.is-current > .el-tree-node__content) {
  background: #eef0ff;
  color: #6366f1;
}

/* 树节点样式 */
.tree-node-content {
  display: flex;
  align-items: center;
  gap: 8px;
  flex: 1;
  min-width: 0;
}
.tree-node-label {
  font-size: 14px;
  font-weight: 500;
  color: #303133;
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.tree-node-content.depth-0 .tree-node-label {
  font-weight: 600;
  font-size: 15px;
}
.tree-node-content.depth-1 .tree-node-label {
  font-weight: 500;
}
.tree-node-content.depth-2 .tree-node-label,
.tree-node-content.depth-3 .tree-node-label {
  font-size: 13px;
  color: #606266;
}
.tree-node-count {
  font-size: 11px;
  color: #909399;
  background: #f0f2f5;
  padding: 1px 7px;
  border-radius: 10px;
  flex-shrink: 0;
}
.tree-panel-body :deep(.el-tree-node.is-current) .tree-node-label {
  color: #6366f1;
}
.tree-panel-body :deep(.el-tree-node.is-current) .tree-node-count {
  background: #e0e7ff;
  color: #6366f1;
}

/* ===== 右侧内容面板 ===== */
.content-panel {
  flex: 1;
  min-width: 0;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

/* 详情卡片 */
.detail-card {
  background: #fff;
  border-radius: 14px;
  padding: 28px 32px;
  box-shadow: 0 1px 6px rgba(0, 0, 0, 0.06);
  border: 1px solid #f0f2f5;
}
.detail-header {
  margin-bottom: 24px;
}
.detail-title-row {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 10px;
}
.detail-depth-tag {
  display: inline-block;
  font-size: 12px;
  font-weight: 500;
  color: #fff;
  padding: 3px 12px;
  border-radius: 6px;
  white-space: nowrap;
}
.detail-title-row h2 {
  margin: 0;
  font-size: 22px;
  font-weight: 700;
  color: #1e293b;
}
.detail-desc {
  margin: 0;
  font-size: 14px;
  color: #909399;
  line-height: 1.6;
}
.detail-cover {
  margin-bottom: 16px;
  border-radius: 12px;
  overflow: hidden;
}
.detail-cover-img {
  width: 100%;
  max-height: 220px;
  object-fit: cover;
  display: block;
}
.detail-action {
  padding-top: 4px;
}
.generate-btn {
  height: 48px;
  padding: 0 32px;
  font-size: 16px;
  font-weight: 600;
  border-radius: 12px;
  background: linear-gradient(135deg, #3b82f6 0%, #6366f1 100%);
  border: none;
  box-shadow: 0 4px 16px rgba(59, 130, 246, 0.25);
  display: inline-flex;
  align-items: center;
  gap: 8px;
}
.generate-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(59, 130, 246, 0.35);
}

/* 子节点列表 */
.children-section {
  background: #fff;
  border-radius: 14px;
  padding: 24px 32px;
  box-shadow: 0 1px 6px rgba(0, 0, 0, 0.06);
  border: 1px solid #f0f2f5;
  flex-shrink: 0;
}
.children-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
}
.children-header h3 {
  margin: 0;
  font-size: 17px;
  font-weight: 600;
  color: #303133;
}
.children-count {
  font-size: 13px;
  color: #909399;
  background: #f0f2f5;
  padding: 3px 12px;
  border-radius: 10px;
}
.children-grid {
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.child-item {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 14px 18px;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s ease;
  border: 1px solid transparent;
  background: #fafbfc;
}
.child-item:hover {
  background: #f0f2ff;
  border-color: #c7d2fe;
  transform: translateX(4px);
}
.child-item-icon {
  width: 36px;
  height: 36px;
  border-radius: 10px;
  background: #eef0ff;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #6366f1;
  flex-shrink: 0;
  transition: all 0.2s;
}
.child-item:hover .child-item-icon {
  background: #6366f1;
  color: #fff;
}
.child-item-body {
  display: flex;
  align-items: center;
  gap: 12px;
  flex: 1;
  min-width: 0;
}
.child-item-name {
  font-size: 15px;
  font-weight: 500;
  color: #303133;
}
.child-item-hint {
  font-size: 12px;
  color: #909399;
  background: #f0f2f5;
  padding: 2px 10px;
  border-radius: 8px;
}
.child-item-hint.leaf {
  color: #10b981;
  background: #ecfdf5;
}

/* 默认引导内容 */
.default-content {
  text-align: center;
  padding: 60px 40px;
  background: #fff;
  border-radius: 14px;
  box-shadow: 0 1px 6px rgba(0, 0, 0, 0.06);
  border: 1px solid #f0f2f5;
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}
.default-icon-wrap {
  margin-bottom: 24px;
}
.default-icon {
  width: 100px;
  height: 100px;
  border-radius: 24px;
  background: #f8f9fb;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto;
}
.default-content h3 {
  font-size: 20px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 10px;
}
.default-content p {
  font-size: 14px;
  color: #909399;
  line-height: 1.7;
  margin: 0 0 32px;
}
.default-hints {
  display: flex;
  gap: 16px;
  justify-content: center;
  flex-wrap: wrap;
}
.hint-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 18px;
  background: #f8f9fb;
  border-radius: 10px;
  font-size: 13px;
  color: #606266;
  font-weight: 500;
}

/* 种子号入口 */
.seed-entry { text-align: center; }
.seed-entry h4 { font-size: 16px; font-weight: 600; color: #303133; margin: 0 0 8px; display: flex; align-items: center; justify-content: center; gap: 8px; }
.seed-entry p { font-size: 13px; color: #909399; margin: 0 0 16px; }
.seed-entry-form { display: flex; gap: 12px; justify-content: center; max-width: 440px; margin: 0 auto; }
.seed-entry-form .el-input { flex: 1; max-width: 240px; }

/* ===== 响应式 ===== */
@media (max-width: 1024px) {
  .grade-layout {
    flex-direction: column;
    flex: none;
  }
  .tree-panel {
    width: 100%;
    flex-shrink: 0;
    max-height: 320px;
  }
  .content-panel {
    overflow-y: visible;
    flex: none;
  }
}

@media (max-width: 768px) {
  .detail-title-row {
    flex-wrap: wrap;
  }
  .detail-title-row h2 {
    font-size: 18px;
  }
  .children-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
  .default-hints {
    flex-direction: column;
    align-items: center;
  }
}
</style>
