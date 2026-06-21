import request from './request'
import { useUserStore } from '../stores/user'

// 认证相关
export const login = (data) => request.post('/auth/login', data)
export const register = (data) => request.post('/auth/register', data)
export const getWechatAuthUrl = () => request.get('/auth/wechat/url')
export const wechatLogin = (data) => request.post('/auth/wechat/login', data)

// 考试相关
export const getCategories = (eduSystem) => request.get('/exam/categories', { params: { eduSystem } })
export const getCategoryRatio = (id) => request.get(`/exam/category/${id}/ratio`)
export const generateExam = (data) => request.post('/exam/generate', data)
export const generateExamBySeed = (data) => request.post('/exam/generate-by-seed', data)
export const getExamBySeed = (seed) => request.get(`/exam/seed/${seed}`)
export const getExamDetail = (examId) => request.get(`/exam/${examId}`)
export const startExam = (examId) => request.post(`/exam/${examId}/start`)
export const submitExam = (data) => request.post('/exam/submit', data)
export const getExamHistory = () => request.get('/exam/history')
export const getExamRecord = (userExamId) => request.get(`/exam/record/${userExamId}`)

// 用户偏好
export const updateEducationSystem = (educationSystem) => request.put('/user/education-system', { educationSystem })
export const saveGradeSelection = (path) => request.put('/user/grade-selection', { path })
export const getGradeSelection = () => request.get('/user/grade-selection')
export const clearGradeSelection = () => request.delete('/user/grade-selection')

// ===================== AI 流式接口 =====================

/**
 * 流式答案检查 —— 通过 SSE 逐块接收 AI 判题结果
 * @param {Object} data - 请求参数 (CheckAnswerRequest)
 * @param {Function} onChunk - 每收到一个文本块时回调 (chunk: string)
 * @param {Function} onDone - 流结束时回调
 * @param {Function} onError - 出错时回调 (error: Error)
 * @returns {AbortController} 可用于中断请求
 */
export const checkAnswerStream = (data, onChunk, onDone, onError) => {
  return createSseStream('/ai/check-answer/stream', data, onChunk, onDone, onError)
}

/**
 * 流式试题讲解 —— 通过 SSE 逐块接收 AI 讲解内容
 * @param {Object} data - 请求参数 (ExplainQuestionRequest)
 * @param {Function} onChunk - 每收到一个文本块时回调 (chunk: string)
 * @param {Function} onDone - 流结束时回调
 * @param {Function} onError - 出错时回调 (error: Error)
 * @returns {AbortController} 可用于中断请求
 */
export const explainQuestionStream = (data, onChunk, onDone, onError) => {
  return createSseStream('/ai/explain-question/stream', data, onChunk, onDone, onError)
}

/**
 * 创建 SSE 流式请求的通用方法
 */
function createSseStream(url, body, onChunk, onDone, onError) {
  const abortController = new AbortController()
  const userStore = useUserStore()

  fetch(`/api${url}`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      ...(userStore.token ? { 'Authorization': `Bearer ${userStore.token}` } : {})
    },
    body: JSON.stringify(body),
    signal: abortController.signal
  }).then(async response => {
    if (!response.ok) {
      const text = await response.text()
      throw new Error(`HTTP ${response.status}: ${text}`)
    }

    const reader = response.body.getReader()
    const decoder = new TextDecoder()
    let buffer = ''

    while (true) {
      const { done, value } = await reader.read()
      if (done) break

      buffer += decoder.decode(value, { stream: true })
      const lines = buffer.split('\n')
      buffer = lines.pop() || ''

      for (const line of lines) {
        if (line.startsWith('data:')) {
          const data = line.slice(5).trim()
          if (data) {
            onChunk?.(data)
          }
        }
      }
    }

    // 处理缓冲区剩余内容
    if (buffer.startsWith('data:')) {
      const data = buffer.slice(5).trim()
      if (data) {
        onChunk?.(data)
      }
    }

    onDone?.()
  }).catch(err => {
    if (err.name === 'AbortError') return
    onError?.(err)
  })

  return abortController
}
