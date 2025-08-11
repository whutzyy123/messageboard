<template>
  <div class="home">
    <el-row :gutter="20">
      <el-col :span="24">
        <el-card class="welcome-card">
          <template #header>
            <div class="card-header">
              <h1>欢迎来到在线留言板系统</h1>
            </div>
          </template>
          <div class="welcome-content">
            <p>这是一个现代化的前后端分离留言板系统，支持用户注册、登录、发布留言等功能。</p>
            <p>系统采用 Vue 3 + Spring Boot 架构，集成了 Redis 缓存和 Kafka 消息队列。</p>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <el-row :gutter="20" class="feature-row">
      <el-col :span="8">
        <el-card class="feature-card">
          <template #header>
            <div class="card-header">
              <el-icon><ChatDotRound /></el-icon>
              <span>发布留言</span>
            </div>
          </template>
          <div class="feature-content">
            <p>用户可以发布自己的留言，分享想法和观点。</p>
            <el-button type="primary" @click="$router.push('/post')" v-if="isLoggedIn">
              发布留言
            </el-button>
            <el-button type="primary" @click="$router.push('/login')" v-else>
              登录后发布
            </el-button>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="8">
        <el-card class="feature-card">
          <template #header>
            <div class="card-header">
              <el-icon><View /></el-icon>
              <span>浏览留言</span>
            </div>
          </template>
          <div class="feature-content">
            <p>浏览所有用户发布的留言，发现有趣的内容。</p>
            <el-button type="success" @click="$router.push('/messages')">
              查看留言
            </el-button>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="8">
        <el-card class="feature-card">
          <template #header>
            <div class="card-header">
              <el-icon><Star /></el-icon>
              <span>热门留言</span>
            </div>
          </template>
          <div class="feature-content">
            <p>Redis 缓存热门留言，提供更快的访问速度。</p>
            <el-button type="warning" @click="$router.push('/messages')">
              查看热门
            </el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <el-row :gutter="20" class="stats-row">
      <el-col :span="24">
        <el-card>
          <template #header>
            <div class="card-header">
              <h3>系统统计</h3>
            </div>
          </template>
          <el-row :gutter="20">
            <el-col :span="6">
              <div class="stat-item">
                <h4>用户数量</h4>
                <p class="stat-number">{{ stats.userCount || 0 }}</p>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="stat-item">
                <h4>留言数量</h4>
                <p class="stat-number">{{ stats.messageCount || 0 }}</p>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="stat-item">
                <h4>今日留言</h4>
                <p class="stat-number">{{ stats.todayCount || 0 }}</p>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="stat-item">
                <h4>热门留言</h4>
                <p class="stat-number">{{ stats.hotCount || 0 }}</p>
              </div>
            </el-col>
          </el-row>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useAuthStore } from '../stores/auth'
import { messagesApi } from '../api/messages'

const authStore = useAuthStore()
const isLoggedIn = computed(() => authStore.isLoggedIn)

const stats = ref({
  userCount: 0,
  messageCount: 0,
  todayCount: 0,
  hotCount: 0
})

onMounted(async () => {
  try {
    // 这里可以调用后端 API 获取统计数据
    // const response = await messagesApi.getStats()
    // stats.value = response.data
  } catch (error) {
    console.error('获取统计数据失败:', error)
  }
})
</script>

<style scoped>
.home {
  max-width: 1200px;
  margin: 0 auto;
}

.welcome-card {
  margin-bottom: 30px;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 10px;
}

.welcome-content {
  text-align: center;
  font-size: 16px;
  line-height: 1.6;
}

.feature-row {
  margin-bottom: 30px;
}

.feature-card {
  height: 200px;
  display: flex;
  flex-direction: column;
}

.feature-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  text-align: center;
}

.feature-content p {
  margin-bottom: 20px;
  color: #666;
}

.stats-row {
  margin-bottom: 30px;
}

.stat-item {
  text-align: center;
  padding: 20px;
}

.stat-item h4 {
  margin: 0 0 10px 0;
  color: #666;
  font-size: 14px;
}

.stat-number {
  margin: 0;
  font-size: 24px;
  font-weight: bold;
  color: #409eff;
}
</style>
