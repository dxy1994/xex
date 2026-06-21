import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  plugins: [vue()],
  server: {
    host: '127.0.0.1', // 强制 Vite 只监听 IPv4
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://127.0.0.1:8080', // 后端代理也强制走 IPv4
        changeOrigin: true
      },
      '/uploads': {
        target: 'http://127.0.0.1:8080',
        changeOrigin: true
      }
    }
  }
})
