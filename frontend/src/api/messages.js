import axios from 'axios'
import { useAuthStore } from '../stores/auth'

const API_BASE_URL = '/api'

// 创建 axios 实例
const apiClient = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10000
})

// 请求拦截器，添加 token
apiClient.interceptors.request.use(
  (config) => {
    const authStore = useAuthStore()
    const token = authStore.getToken()
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

export const messagesApi = {
  // 获取留言列表
  getMessages: (params = {}) => {
    return apiClient.get('/messages', { params })
  },
  
  // 发布留言
  createMessage: (messageData) => {
    return apiClient.post('/messages', messageData)
  },
  
  // 点赞留言
  likeMessage: (messageId) => {
    return apiClient.post(`/messages/${messageId}/like`)
  },
  
  // 获取热门留言
  getHotMessages: () => {
    return apiClient.get('/messages/hot')
  }
}
