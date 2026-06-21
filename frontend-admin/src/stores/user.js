import { defineStore } from 'pinia'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: localStorage.getItem('admin_token') || '',
    userId: localStorage.getItem('admin_userId') || '',
    username: localStorage.getItem('admin_username') || '',
    nickname: localStorage.getItem('admin_nickname') || '',
    userSource: localStorage.getItem('admin_userSource') || ''
  }),
  actions: {
    setUserInfo(data) {
      this.token = data.token
      this.userId = data.userId
      this.username = data.username
      this.nickname = data.nickname || data.username
      this.userSource = data.userSource
      localStorage.setItem('admin_token', data.token)
      localStorage.setItem('admin_userId', data.userId)
      localStorage.setItem('admin_username', data.username)
      localStorage.setItem('admin_nickname', data.nickname || data.username)
      localStorage.setItem('admin_userSource', data.userSource)
    },
    logout() {
      this.token = ''
      this.userId = ''
      this.username = ''
      this.nickname = ''
      this.userSource = ''
      localStorage.removeItem('admin_token')
      localStorage.removeItem('admin_userId')
      localStorage.removeItem('admin_username')
      localStorage.removeItem('admin_nickname')
      localStorage.removeItem('admin_userSource')
    }
  }
})
