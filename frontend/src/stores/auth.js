import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login as loginApi, register as registerApi } from '../api/auth'

export const useAuthStore = defineStore('auth', () => {
  // 安全地获取 token
  const token = ref(localStorage.getItem('token') || '')
  
  // 安全地获取和解析 user 数据
  let initialUser = null
  try {
    const userData = localStorage.getItem('user')
    if (userData && userData !== 'undefined' && userData !== 'null') {
      initialUser = JSON.parse(userData)
    }
  } catch (error) {
    console.warn('Failed to parse user data from localStorage:', error)
    localStorage.removeItem('user')
  }
  
  const user = ref(initialUser)

  const isLoggedIn = computed(() => !!token.value)
  const isAuthenticated = computed(() => !!token.value)

  const login = async (credentials) => {
    try {
      const response = await loginApi(credentials)
      if (response.success) {
        token.value = response.data.token
        user.value = response.data.user
        localStorage.setItem('token', response.data.token)
        localStorage.setItem('user', JSON.stringify(response.data.user))
        return { success: true }
      } else {
        return { success: false, message: response.message }
      }
    } catch (error) {
      console.error('Login error:', error)
      return { success: false, message: '登录失败，请稍后重试' }
    }
  }

  const register = async (userData) => {
    try {
      const response = await registerApi(userData)
      if (response.success) {
        return { success: true, message: '注册成功，请登录' }
      } else {
        return { success: false, message: response.message }
      }
    } catch (error) {
      console.error('Register error:', error)
      return { success: false, message: '注册失败，请稍后重试' }
    }
  }

  const logout = () => {
    token.value = ''
    user.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('user')
  }

  // 清理无效的localStorage数据
  const clearInvalidData = () => {
    try {
      const tokenData = localStorage.getItem('token')
      const userData = localStorage.getItem('user')
      
      if (tokenData && tokenData === 'undefined') {
        localStorage.removeItem('token')
      }
      if (userData && userData === 'undefined') {
        localStorage.removeItem('user')
      }
    } catch (error) {
      console.warn('Failed to clear invalid localStorage data:', error)
    }
  }

  // 初始化时清理无效数据
  clearInvalidData()

  return {
    token,
    user,
    isLoggedIn,
    isAuthenticated,
    login,
    register,
    logout
  }
})
