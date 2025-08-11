import axios from 'axios'

const API_BASE_URL = '/api'

export const authApi = {
  // 用户注册
  register: (userData) => {
    return axios.post(`${API_BASE_URL}/auth/register`, userData)
  },
  
  // 用户登录
  login: (credentials) => {
    return axios.post(`${API_BASE_URL}/auth/login`, credentials)
  }
}
