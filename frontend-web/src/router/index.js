import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '../stores/user'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: () => import('../views/Home.vue')
  },
  {
    path: '/login',
    redirect: '/'
  },
  {
    path: '/register',
    redirect: '/'
  },
  {
    path: '/auth/wechat/callback',
    name: 'WeChatAuthCallback',
    component: () => import('../views/WeChatAuthCallback.vue')
  },
  {
    path: '/dashboard',
    component: () => import('../layouts/MainLayout.vue'),
    meta: {},
    children: [
      {
        path: '',
        name: 'Dashboard',
        component: () => import('../views/Dashboard.vue')
      },
      {
        path: 'exam/config',
        name: 'ExamConfig',
        component: () => import('../views/ExamConfig.vue')
      },
      {
        path: 'exam/:id',
        name: 'TakingExam',
        component: () => import('../views/TakingExam.vue')
      },
      {
        path: 'exam/result/:userExamId',
        name: 'ExamResult',
        component: () => import('../views/ExamResult.vue')
      },
      {
        path: 'history',
        name: 'History',
        component: () => import('../views/History.vue'),
        meta: { requiresAuth: true }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  if (to.meta.requiresAuth && !userStore.token) {
    next('/')
  } else {
    next()
  }
})

export default router
