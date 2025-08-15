<template>
  <div id="app">
    <el-container>
      <el-header>
        <el-menu
          :default-active="activeIndex"
          class="el-menu-demo"
          mode="horizontal"
          router
          background-color="#545c64"
          text-color="#fff"
          active-text-color="#ffd04b"
        >
          <el-menu-item index="/">
            <el-icon><House /></el-icon>
            在线留言板
          </el-menu-item>
          <div class="flex-grow" />
          <el-menu-item v-if="!isLoggedIn" index="/login">
            <el-icon><User /></el-icon>
            登录
          </el-menu-item>
          <el-menu-item v-if="!isLoggedIn" index="/register">
            <el-icon><Plus /></el-icon>
            注册
          </el-menu-item>
          <el-menu-item v-if="isLoggedIn" @click="logout">
            <el-icon><SwitchButton /></el-icon>
            退出登录
          </el-menu-item>
        </el-menu>
      </el-header>
      
      <el-main>
        <router-view />
      </el-main>
      
      <el-footer>
        <div class="footer-content">
          <p>&copy; 2025 在线留言板系统. All rights reserved.</p>
        </div>
      </el-footer>
    </el-container>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from './stores/auth'
import { House, User, Plus, SwitchButton } from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

const activeIndex = computed(() => route.path)
const isLoggedIn = computed(() => authStore.isLoggedIn)

const logout = () => {
  authStore.logout()
  router.push('/login')
}
</script>

<style>
#app {
  font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', '微软雅黑', Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: #2c3e50;
  height: 100vh;
}

.el-container {
  height: 100%;
}

.el-header {
  padding: 0;
  background-color: #545c64;
}

.el-menu-demo {
  border-bottom: none;
}

.flex-grow {
  flex-grow: 1;
}

.el-main {
  padding: 20px;
  background-color: #f5f5f5;
  min-height: calc(100vh - 120px);
}

.el-footer {
  background-color: #545c64;
  color: #fff;
  text-align: center;
  padding: 20px;
}

.footer-content {
  margin: 0;
}

.footer-content p {
  margin: 0;
  font-size: 14px;
}
</style>
