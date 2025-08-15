<template>
  <div class="login-container">
    <el-card class="login-card">
      <div class="login-header">
        <h2>用户登录</h2>
      </div>
      <el-form ref="loginFormRef" :model="loginForm" :rules="rules" @submit.prevent>
        <el-form-item prop="username">
          <el-input v-model="loginForm.username" placeholder="用户名" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="loginForm.password" placeholder="密码" type="password" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" @click="handleLogin" style="width:100%">登录</el-button>
        </el-form-item>
        <div style="text-align:center;">
          还没有账号？<router-link to="/register">去注册</router-link>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { ElMessage } from 'element-plus'

const router = useRouter()
const authStore = useAuthStore()

const loginFormRef = ref()
const loading = ref(false)

const loginForm = reactive({ username: '', password: '' })
const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const handleLogin = async () => {
  await loginFormRef.value?.validate()
  loading.value = true
  const res = await authStore.login({ ...loginForm })
  loading.value = false
  if (res.success) {
    ElMessage.success('登录成功')
    router.push('/home')
  } else {
    ElMessage.error(res.message || '登录失败')
  }
}
</script>

<style scoped>
.login-container { min-height: 70vh; display:flex; align-items:center; justify-content:center; padding:24px; }
.login-card { width: 360px; }
.login-header { text-align:center; margin-bottom: 12px; }
</style>
