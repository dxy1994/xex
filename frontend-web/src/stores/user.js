import { defineStore } from 'pinia'
import { clearGradeSelection } from '../api'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: localStorage.getItem('token') || '',
    userId: localStorage.getItem('userId') || '',
    username: localStorage.getItem('username') || '',
    nickname: localStorage.getItem('nickname') || '',
    userType: localStorage.getItem('userType') || '',
    userSource: localStorage.getItem('userSource') || '',
    educationSystem: localStorage.getItem('educationSystem') || '63'
  }),
  actions: {
    setUserInfo(data) {
      this.token = data.token
      this.userId = data.userId
      this.username = data.username
      this.nickname = data.nickname || data.username
      this.userType = data.userType
      this.userSource = data.userSource
      // 学制优先保留本地缓存（本地操作即时生效，后端同步可能滞后）
      if (data.educationSystem && !localStorage.getItem('educationSystem')) {
        this.educationSystem = data.educationSystem
        localStorage.setItem('educationSystem', data.educationSystem)
      }
      localStorage.setItem('token', data.token)
      localStorage.setItem('userId', data.userId)
      localStorage.setItem('username', data.username)
      localStorage.setItem('nickname', data.nickname || data.username)
      localStorage.setItem('userType', data.userType)
      localStorage.setItem('userSource', data.userSource)
    },
    setEducationSystem(value) {
      this.educationSystem = value
      localStorage.setItem('educationSystem', value)
    },
    logout() {
      const uid = this.userId || 'guest'
      // 清理 Redis 年级选择缓存（需在 token 失效前调用）
      if (this.token) {
        try { clearGradeSelection() } catch { /* 忽略网络错误 */ }
      }
      // 清理 localStorage 中的层级路径缓存
      localStorage.removeItem(`lastPath_${uid}`)
      // 清理旧版年级缓存（兼容升级）
      localStorage.removeItem(`lastGradeId_${uid}`)
      localStorage.removeItem(`lastCategoryPath_${uid}`)
      this.token = ''
      this.userId = ''
      this.username = ''
      this.nickname = ''
      this.userType = ''
      this.userSource = ''
      // 不清理 educationSystem，保留用户偏好
      localStorage.removeItem('token')
      localStorage.removeItem('userId')
      localStorage.removeItem('username')
      localStorage.removeItem('nickname')
      localStorage.removeItem('userType')
      localStorage.removeItem('userSource')
    }
  }
})
