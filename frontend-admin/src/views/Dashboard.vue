<template>
  <div class="dashboard-page">
    <h2 class="dashboard-title"><el-icon :size="22" color="#6366f1"><Setting /></el-icon>管理后台</h2>
    <el-row :gutter="20">
      <el-col :xs="12" :sm="8" :md="6" :lg="4" :xl="4" v-for="item in menuItems" :key="item.path">
        <div class="dash-card" @click="$router.push(item.path)">
          <div class="dash-icon" :style="{ background: item.color }"><el-icon :size="28"><component :is="item.icon" /></el-icon></div>
          <h3 class="dash-name">{{ item.name }}</h3>
          <p class="dash-desc">{{ item.desc }}</p>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
const menuItems = [
  { name: '分类管理', desc: '管理考试分类', icon: 'Collection', path: '/categories', color: 'linear-gradient(135deg, #6366f1, #818cf8)' },
  { name: '题库管理', desc: '增删改查题目', icon: 'Document', path: '/questions', color: 'linear-gradient(135deg, #10b981, #34d399)' },
  { name: '比例配置', desc: '设置题型占比', icon: 'DataAnalysis', path: '/ratios', color: 'linear-gradient(135deg, #f59e0b, #fbbf24)' },
  { name: '试卷管理', desc: '管理固定试卷', icon: 'Tickets', path: '/exams', color: 'linear-gradient(135deg, #ef4444, #f87171)' },
  { name: '考试记录', desc: '查看答题记录', icon: 'Notebook', path: '/records', color: 'linear-gradient(135deg, #8b5cf6, #a78bfa)' },
]
</script>

<style scoped>
.dashboard-page { max-width: 1400px; margin: 0 auto; }
@media (min-width: 1600px) { .dashboard-page { max-width: 1600px; } }
@media (min-width: 1920px) { .dashboard-page { max-width: 1800px; } }
.dashboard-title { display: flex; align-items: center; gap: 10px; font-size: 20px; font-weight: 600; margin: 0 0 28px; }
.dash-card { background: #fff; border-radius: 16px; padding: 28px 20px 24px; cursor: pointer; text-align: center; transition: all 0.3s cubic-bezier(0.4,0,0.2,1); margin-bottom: 20px; box-shadow: 0 2px 8px rgba(0,0,0,0.06); }
.dash-card:hover { transform: translateY(-6px); box-shadow: 0 12px 32px rgba(0,0,0,0.12); }
.dash-icon { width: 60px; height: 60px; border-radius: 16px; display: inline-flex; align-items: center; justify-content: center; color: #fff; margin-bottom: 16px; }
.dash-name { font-size: 16px; font-weight: 600; color: #303133; margin: 0 0 6px; }
.dash-desc { font-size: 13px; color: #909399; margin: 0; }
</style>
