import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login as loginApi, getUserInfo } from '@/api/auth'
import router from '@/router'

export const useAuthStore = defineStore('auth', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userRole = ref(localStorage.getItem('userRole') || '')
  const userInfo = ref<Record<string, any>>(null)

  const isLoggedIn = computed(() => !!token.value)
  const role = computed(() => userRole.value)

  function setAuth(data: { token: string; role: string; user?: Record<string, any> }) {
    token.value = data.token
    userRole.value = data.role
    localStorage.setItem('token', data.token)
    localStorage.setItem('userRole', data.role)
    if (data.user) {
      userInfo.value = data.user
      localStorage.setItem('userInfo', JSON.stringify(data.user))
    }
  }

  async function login(username: string, password: string) {
    const res: any = await loginApi({ username, password })
    const data = res.data || res
    setAuth({
      token: data.token,
      role: data.role,
      user: data.user,
    })
    return data
  }

  async function fetchUserInfo() {
    try {
      const res: any = await getUserInfo()
      userInfo.value = res.data || res
    } catch {
      logout()
    }
  }

  function logout() {
    token.value = ''
    userRole.value = ''
    userInfo.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('userRole')
    localStorage.removeItem('userInfo')
    router.push('/login')
  }

  function initAuth() {
    const savedToken = localStorage.getItem('token')
    const savedRole = localStorage.getItem('userRole')
    const savedUser = localStorage.getItem('userInfo')
    if (savedToken) {
      token.value = savedToken
      userRole.value = savedRole || ''
      if (savedUser) {
        try {
          userInfo.value = JSON.parse(savedUser)
        } catch {
          userInfo.value = null
        }
      }
    }
  }

  return { token, userRole, userInfo, isLoggedIn, role, login, logout, fetchUserInfo, initAuth }
})
