<template>
  <div class="categories-layout">
    <!-- 左侧：树形表格 -->
    <div class="categories-left">
      <div class="admin-toolbar">
        <h3 class="admin-title">分类管理</h3>
        <el-button type="primary" :icon="Plus" round @click="openNew()">新增分类</el-button>
      </div>
      <el-card class="table-card">
        <el-table
          :data="treeData"
          border
          v-loading="loading"
          empty-text="暂无分类数据"
          row-key="id"
          :tree-props="{ children: 'children' }"
          default-expand-all
          highlight-current-row
          @row-click="handleRowClick"
          :row-class-name="rowClassName"
        >
          <el-table-column prop="id" label="ID" width="80" align="center" />
          <el-table-column prop="name" label="名称" min-width="160" />
          <el-table-column prop="description" label="描述" min-width="180" show-overflow-tooltip />
          <el-table-column prop="sortOrder" label="排序" width="80" align="center" />
          <el-table-column label="操作" width="180" fixed="right">
            <template #default="{ row }">
              <el-button type="success" link :icon="Plus" size="small" @click.stop="openNew(row.id)">新增子分类</el-button>
              <el-button type="danger" link :icon="Delete" size="small" @click.stop="handleDelete(row.id)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-card>
    </div>

    <!-- 右侧：编辑面板 -->
    <transition name="panel-slide">
      <div v-if="selectedId !== null" class="categories-right">
        <div class="panel-header">
          <div class="panel-title">
            <el-icon :size="18" color="#6366f1"><Edit /></el-icon>
            <span>{{ form.id ? '编辑分类' : '新增分类' }}</span>
          </div>
          <el-button :icon="Close" circle size="small" @click="closePanel" />
        </div>
        <div class="panel-body">
          <el-form :model="form" label-width="80px" label-position="top">
            <el-form-item label="父分类">
              <el-tree-select
                v-model="form.parentId"
                :data="parentOptions"
                :props="{ label: 'name', value: 'id', children: 'children' }"
                check-strictly
                placeholder="无（顶级分类）"
                clearable
                style="width: 100%"
              />
            </el-form-item>
            <el-form-item label="分类名称" required>
              <el-input v-model="form.name" placeholder="请输入分类名称" />
            </el-form-item>
            <el-form-item label="分类描述">
              <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入描述（选填）" />
            </el-form-item>
            <el-form-item label="封面图片">
              <div class="image-upload-area">
                <div v-if="form.imageUrl" class="image-preview">
                  <el-image :src="form.imageUrl" fit="cover" class="preview-img" />
                  <div class="image-preview-mask">
                    <el-button :icon="Delete" circle size="small" type="danger" @click="form.imageUrl = ''" />
                  </div>
                </div>
                <el-upload
                  v-else
                  class="upload-box"
                  :show-file-list="false"
                  :before-upload="beforeImageUpload"
                  :http-request="handleImageUpload"
                  accept="image/*"
                >
                  <el-icon :size="28"><Plus /></el-icon>
                  <span>上传封面</span>
                </el-upload>
              </div>
              <div class="image-tip">支持 JPG/PNG，建议尺寸 400×300，不超过 5MB</div>
            </el-form-item>
            <el-form-item label="排序">
              <el-input-number v-model="form.sortOrder" :min="0" />
            </el-form-item>
          </el-form>
        </div>
        <div class="panel-footer">
          <el-button @click="closePanel">取消</el-button>
          <el-button type="primary" @click="handleSave">保存</el-button>
        </div>
      </div>
    </transition>

    <!-- 右侧占位 -->
    <div v-if="selectedId === null" class="categories-right categories-right--empty">
      <el-empty description="点击表格行编辑分类，或点击「新增分类」创建" :image-size="120" />
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Edit, Delete, Close, Upload, Picture } from '@element-plus/icons-vue'
import { adminGetCategoryTree, adminSaveCategory, adminUpdateCategory, adminDeleteCategory, adminUploadImage } from '../api'

const treeData = ref([])
const loading = ref(false)
const selectedId = ref(null)
const form = reactive({ id: null, parentId: null, name: '', description: '', imageUrl: '', sortOrder: 0 })

// 父分类下拉选项：全部节点均可选
const parentOptions = computed(() => treeData.value)

const loadData = async () => {
  loading.value = true
  try {
    const res = await adminGetCategoryTree()
    treeData.value = res.data || []
  } finally {
    loading.value = false
  }
}

const handleRowClick = (row) => {
  selectedId.value = row.id
  Object.assign(form, {
    id: row.id,
    parentId: row.parentId ?? null,
    name: row.name,
    description: row.description,
    imageUrl: row.imageUrl || '',
    sortOrder: row.sortOrder
  })
}

const openNew = (parentId = null) => {
  selectedId.value = -1
  Object.assign(form, { id: null, parentId, name: '', description: '', sortOrder: 0 })
}

const closePanel = () => { selectedId.value = null }

const rowClassName = ({ row }) => {
  return selectedId.value === row.id ? 'row-selected' : ''
}

const handleSave = async () => {
  if (!form.name) {
    ElMessage.warning('请输入分类名称')
    return
  }
  const payload = { ...form, parentId: form.parentId || null }
  if (form.id) {
    await adminUpdateCategory(payload)
  } else {
    await adminSaveCategory(payload)
  }
  ElMessage.success('保存成功')
  selectedId.value = null
  loadData()
}

const handleDelete = async (id) => {
  await ElMessageBox.confirm('确定删除该分类吗？删除后不可恢复。', '确认删除', { type: 'warning' })
  const res = await adminDeleteCategory(id)
  if (res.code !== 200 && res.code !== 0) {
    ElMessage.error(res.msg || '删除失败')
    return
  }
  ElMessage.success('删除成功')
  if (selectedId.value === id) selectedId.value = null
  loadData()
}

// 图片上传前校验
const beforeImageUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  if (!isImage) {
    ElMessage.error('仅支持上传图片文件')
    return false
  }
  const isLt5M = file.size / 1024 / 1024 < 5
  if (!isLt5M) {
    ElMessage.error('图片大小不能超过 5MB')
    return false
  }
  return true
}

// 自定义图片上传
const handleImageUpload = async (options) => {
  try {
    const res = await adminUploadImage(options.file)
    form.imageUrl = res.data
    ElMessage.success('封面上传成功')
  } catch {
    ElMessage.error('封面上传失败')
  }
}

onMounted(() => loadData())
</script>

<style scoped>
.categories-layout { display: flex; gap: 24px; align-items: flex-start; }
.categories-left { flex: 1; min-width: 0; }
.categories-right {
  width: 420px; flex-shrink: 0;
  background: #fff; border-radius: 14px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.06);
  display: flex; flex-direction: column;
  max-height: calc(100vh - 120px);
  position: sticky; top: 80px;
}
.categories-right--empty {
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
.table-card { border-radius: 14px !important; }

/* 选中行高亮 */
:deep(.row-selected) { background-color: #ede9fe !important; }
:deep(.row-selected:hover > td) { background-color: #ddd6fe !important; }

/* 滑入动画 */
.panel-slide-enter-active { transition: all 0.3s ease-out; }
.panel-slide-leave-active { transition: all 0.2s ease-in; }
.panel-slide-enter-from { opacity: 0; transform: translateX(20px); }
.panel-slide-leave-to { opacity: 0; transform: translateX(20px); }

/* 图片上传 */
.image-upload-area {
  width: 100%;
}
.upload-box {
  width: 100%;
  height: 140px;
  border: 2px dashed #dcdfe6;
  border-radius: 12px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8px;
  cursor: pointer;
  transition: all 0.25s;
  color: #909399;
  font-size: 13px;
}
.upload-box:hover {
  border-color: #6366f1;
  color: #6366f1;
  background: #f8f9ff;
}
.image-preview {
  position: relative;
  width: 100%;
  border-radius: 12px;
  overflow: hidden;
}
.preview-img {
  width: 100%;
  height: 160px;
  display: block;
}
.image-preview-mask {
  position: absolute;
  top: 8px;
  right: 8px;
  opacity: 0;
  transition: opacity 0.25s;
}
.image-preview:hover .image-preview-mask {
  opacity: 1;
}
.image-tip {
  margin-top: 6px;
  font-size: 12px;
  color: #c0c4cc;
}

@media (max-width: 1100px) {
  .categories-layout { flex-direction: column; }
  .categories-right, .categories-right--empty { width: 100%; max-height: none; position: static; }
}
</style>
