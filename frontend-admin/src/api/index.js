import request from './request'

// 认证相关
export const login = (data) => request.post('/auth/login', data)

// 获取分类列表（用于下拉选项）
export const getCategories = () => request.get('/exam/categories')

// 管理员 - 分类
export const adminGetCategories = (params) => request.get('/admin/category/page', { params })
export const adminGetCategoryTree = () => request.get('/admin/category/tree')
export const adminGetCategory = (id) => request.get(`/admin/category/${id}`)
export const adminSaveCategory = (data) => request.post('/admin/category', data)
export const adminUpdateCategory = (data) => request.put('/admin/category', data)
export const adminDeleteCategory = (id) => request.delete(`/admin/category/${id}`)
export const adminUploadImage = (file) => {
  const formData = new FormData()
  formData.append('file', file)
  return request.post('/admin/upload/category', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

// 管理员 - 题型比例
export const adminGetRatio = (categoryId) => request.get(`/admin/category/ratio/${categoryId}`)
export const adminGetRatioInfo = (categoryId) => request.get(`/admin/category/ratio/info/${categoryId}`)
export const adminSaveRatio = (data) => request.post('/admin/category/ratio', data)

// 管理员 - 题目
export const adminGetQuestions = (params) => request.get('/admin/question/page', { params })
export const adminGetQuestion = (id) => request.get(`/admin/question/${id}`)
export const adminSaveQuestion = (data) => request.post('/admin/question', data)
export const adminUpdateQuestion = (data) => request.put('/admin/question', data)
export const adminDeleteQuestion = (id) => request.delete(`/admin/question/${id}`)
export const adminBatchDeleteQuestions = (ids) => request.delete('/admin/question/batch', { data: ids })

// 管理员 - 试卷
export const adminGetExams = (categoryId) => request.get('/admin/exam/list', { params: { categoryId } })
export const adminGetExamDetail = (id) => request.get(`/admin/exam/${id}`)
export const adminCreateFixedExam = (data) => request.post('/admin/exam/fixed', data)
export const adminDeleteExam = (id) => request.delete(`/admin/exam/${id}`)

// 管理员 - 考试记录
export const adminGetRecords = (params) => request.get('/admin/exam/records', { params })
