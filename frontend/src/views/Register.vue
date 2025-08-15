<template>
  <div class="register-container">
    <el-card class="register-card">
      <div class="card-header"><h2>用户注册</h2></div>
      <el-form ref="formRef" :model="form" :rules="rules" @submit.prevent>
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="用户名" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" placeholder="密码" type="password" />
        </el-form-item>
        <el-form-item prop="confirmPassword">
          <el-input v-model="form.confirmPassword" placeholder="确认密码" type="password" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" @click="handleRegister" style="width:100%">注册</el-button>
        </el-form-item>
        <div style="text-align:center;">
          已有账号？<router-link to="/login">去登录</router-link>
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

const formRef = ref()
const loading = ref(false)
const form = reactive({ username: '', password: '', confirmPassword: '' })

const validateConfirm = (rule, value, callback) => {
  if (value !== form.password) callback(new Error('两次输入的密码不一致'))
  else callback()
}

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  confirmPassword: [{ required: true, validator: validateConfirm, trigger: 'blur' }]
}

const handleRegister = async () => {
  await formRef.value?.validate()
  loading.value = true
  const res = await authStore.register({ username: form.username, password: form.password })
  loading.value = false
  if (res.success) {
    ElMessage.success('注册成功，请登录')
    router.push('/login')
  } else {
    ElMessage.error(res.message || '注册失败')
  }
}
</script>

<style scoped>
.register-container { min-height: 70vh; display:flex; align-items:center; justify-content:center; padding:24px; }
.register-card { width: 420px; }
.card-header { text-align:center; margin-bottom: 12px; }
</style>
