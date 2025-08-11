<template>
  <div id="app">
    <el-container>
      <el-header>
        <nav class="navbar">
          <div class="nav-brand">
            <h2>在线留言板系统</h2>
          </div>
          <div class="nav-menu">
            <el-menu
              :default-active="activeIndex"
              mode="horizontal"
              router
              background-color="#545c64"
              text-color="#fff"
              active-text-color="#ffd04b"
            >
              <el-menu-item index="/">首页</el-menu-item>
              <el-menu-item index="/messages">留言列表</el-menu-item>
              <el-menu-item v-if="!isLoggedIn" index="/login">登录</el-menu-item>
              <el-menu-item v-if="!isLoggedIn" index="/register">注册</el-menu-item>
              <el-menu-item v-if="isLoggedIn" index="/post">发布留言</el-menu-item>
              <el-menu-item v-if="isLoggedIn" @click="logout">退出登录</el-menu-item>
            </el-menu>
          </div>
        </nav>
      </el-header>
      
      <el-main>
        <router-view />
      </el-main>
      
      <el-footer>
        <p>&copy; 2024 在线留言板系统. All rights reserved.</p>
      </el-footer>
    </el-container>
  </div>
</template>

<script setup>
import { computed, ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from './stores/auth'

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

<style scoped>
.navbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  background-color: #545c64;
  color: white;
}

.nav-brand h2 {
  margin: 0;
  color: #ffd04b;
}

.nav-menu {
  flex: 1;
  display: flex;
  justify-content: flex-end;
}

.el-header {
  padding: 0;
  height: auto;
}

.el-main {
  min-height: calc(100vh - 120px);
  padding: 20px;
}

.el-footer {
  text-align: center;
  padding: 20px;
  background-color: #f5f5f5;
}
</style>
