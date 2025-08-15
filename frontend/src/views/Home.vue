<template>
  <div class="home-container">
    <el-card class="mb-16">
      <template #header>
        <div class="card-header">发布新留言</div>
      </template>
      <el-input
        v-model="newContent"
        type="textarea"
        :rows="3"
        placeholder="说点什么..."
      />
      <div class="mt-12" style="text-align:right;">
        <el-button type="primary" :loading="creating" @click="handleCreate">发布</el-button>
      </div>
    </el-card>

    <el-card>
      <template #header>
        <div class="card-header">
          <el-tabs v-model="activeTab" @tab-change="switchTab">
            <el-tab-pane label="全部留言" name="all" />
            <el-tab-pane label="热门留言" name="hot" />
          </el-tabs>
        </div>
      </template>
      
      <!-- 全部留言 -->
      <div v-if="activeTab === 'all'">
        <div v-if="loading" class="loading-container">
          <el-skeleton :rows="3" animated />
        </div>
        <el-empty v-else-if="messages.length === 0" description="暂无留言" />
        <el-timeline v-else>
          <el-timeline-item
            v-for="item in messages"
            :key="item.id"
          >
            <div class="message-item">
              <div class="meta">
                <span class="username">{{ item.username || '未知用户' }}</span>
                <span class="time">{{ formatTime(item.createdAt || item.created_at) }}</span>
              </div>
              <div class="content">{{ item.content }}</div>
              <div class="actions">
                <el-button
                  :type="item.isLiked ? 'danger' : 'primary'"
                  size="small"
                  @click="handleLike(item.id, item.isLiked)"
                >
                  <el-icon v-if="item.isLiked"><StarFilled /></el-icon>
                  <el-icon v-else><Star /></el-icon>
                  {{ item.isLiked ? '取消点赞' : '点赞' }}
                </el-button>
                <span class="like-count">
                  <el-icon><Star /></el-icon>
                  {{ item.likeCount || 0 }}
                </span>
                <span v-if="item.isHot" class="hot-badge">
                  <el-icon><StarFilled /></el-icon>
                  热门
                </span>
              </div>
            </div>
          </el-timeline-item>
        </el-timeline>
      </div>
      
      <!-- 热门留言 -->
      <div v-else-if="activeTab === 'hot'">
        <div v-if="hotLoading" class="loading-container">
          <el-skeleton :rows="3" animated />
        </div>
        <el-empty v-else-if="hotMessages.length === 0" description="暂无热门留言" />
        <el-timeline v-else>
          <el-timeline-item
            v-for="item in hotMessages"
            :key="item.id"
          >
            <div class="message-item hot-message">
              <div class="meta">
                <span class="username">{{ item.username || '未知用户' }}</span>
                <span class="time">{{ formatTime(item.createdAt || item.created_at) }}</span>
              </div>
              <div class="content">{{ item.content }}</div>
              <div class="actions">
                <el-button
                  :type="item.isLiked ? 'danger' : 'primary'"
                  size="small"
                  @click="handleLike(item.id, item.isLiked)"
                >
                  <el-icon v-if="item.isLiked"><StarFilled /></el-icon>
                  <el-icon v-else><Star /></el-icon>
                  {{ item.isLiked ? '取消点赞' : '点赞' }}
                </el-button>
                <span class="like-count">
                  <el-icon><Star /></el-icon>
                  {{ item.likeCount || 0 }}
                </span>
                <span class="hot-badge">
                  <el-icon><StarFilled /></el-icon>
                  热门
                </span>
              </div>
            </div>
          </el-timeline-item>
        </el-timeline>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { listMessages, createMessage } from '@/api/message'
import { likeMessage, unlikeMessage, getLikeStatus, getHotMessages } from '@/api/messageLike'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '@/stores/auth'
import { useRouter } from 'vue-router'
import { Star, StarFilled, Trophy } from '@element-plus/icons-vue'

const messages = ref([])
const hotMessages = ref([])
const newContent = ref('')
const loading = ref(false)
const creating = ref(false)
const hotLoading = ref(false)
const activeTab = ref('all') // 'all' 或 'hot'

const authStore = useAuthStore()
const router = useRouter()

const isLoggedIn = computed(() => authStore.isLoggedIn)

const fetchMessages = async () => {
  try {
    loading.value = true
    const res = await listMessages()
    console.log('留言列表响应:', res) // 调试日志
    
    // 处理分页数据结构
    if (res && res.data && res.data.content) {
      // 分页数据结构
      messages.value = res.data.content
    } else if (Array.isArray(res)) {
      // 直接数组结构
      messages.value = res
    } else if (res && res.data && Array.isArray(res.data)) {
      // 嵌套数组结构
      messages.value = res.data
    } else {
      // 其他情况，尝试获取数据
      messages.value = res?.data || res || []
    }
    
    console.log('解析后的留言列表:', messages.value) // 调试日志
  } catch (e) {
    console.error('获取留言失败:', e)
    ElMessage.error('获取留言失败')
  } finally {
    loading.value = false
  }
}

const fetchHotMessages = async () => {
  try {
    hotLoading.value = true
    const res = await getHotMessages()
    console.log('热门留言响应:', res)
    
    // 处理分页数据结构
    if (res && res.data && res.data.content) {
      hotMessages.value = res.data.content
    } else if (Array.isArray(res)) {
      hotMessages.value = res
    } else if (res && res.data && Array.isArray(res.data)) {
      hotMessages.value = res.data
    } else {
      hotMessages.value = res?.data || res || []
    }
    
    console.log('解析后的热门留言:', hotMessages.value)
  } catch (e) {
    console.error('获取热门留言失败:', e)
    ElMessage.error('获取热门留言失败')
  } finally {
    hotLoading.value = false
  }
}

const handleLike = async (messageId, isLiked) => {
  if (!isLoggedIn.value) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  
  try {
    if (isLiked) {
      await unlikeMessage(messageId)
      ElMessage.success('取消点赞成功')
    } else {
      await likeMessage(messageId)
      ElMessage.success('点赞成功')
    }
    
    // 刷新留言列表
    if (activeTab.value === 'all') {
      fetchMessages()
    } else {
      fetchHotMessages()
    }
  } catch (e) {
    console.error('点赞操作失败:', e)
    ElMessage.error(e.response?.data?.message || '操作失败')
  }
}

const switchTab = (tab) => {
  activeTab.value = tab
  if (tab === 'hot') {
    fetchHotMessages()
  } else {
    fetchMessages()
  }
}

const handleCreate = async () => {
  if (!isLoggedIn.value) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  
  if (!newContent.value.trim()) {
    ElMessage.warning('请输入留言内容')
    return
  }
  
  try {
    creating.value = true
    // 只发送内容，让后端自动设置用户ID
    await createMessage({ content: newContent.value })
    ElMessage.success('发布成功')
    newContent.value = ''
    fetchMessages()
  } catch (e) {
    console.error(e)
    if (e.response?.status === 401) {
      ElMessage.error('登录已过期，请重新登录')
      authStore.logout()
      router.push('/login')
    } else if (e.response?.status === 400) {
      ElMessage.error(e.response.data?.message || '发布失败，请检查输入')
    }
  } finally {
    creating.value = false
  }
}

const formatTime = (t) => {
  if (!t) return ''
  try { return new Date(t).toLocaleString() } catch { return t }
}

onMounted(fetchMessages)
</script>

<style scoped>
.home-container { max-width: 900px; margin: 0 auto; }
.mb-16 { margin-bottom: 16px; }
.mt-12 { margin-top: 12px; }
.loading-container { padding: 20px; }
.card-header { font-weight: 600; }
.message-item .meta { 
  color: #888; 
  font-size: 12px; 
  margin-bottom: 8px; 
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.message-item .username { 
  font-weight: 500; 
  color: #409EFF; 
}
.message-item .time { 
  color: #999; 
  font-size: 11px; 
}
.message-item .content { 
  white-space: pre-wrap; 
  line-height: 1.6; 
  color: #333;
  font-size: 14px;
}

.message-item .actions {
  margin-top: 12px;
  display: flex;
  align-items: center;
  gap: 12px;
}

.message-item .like-count {
  display: flex;
  align-items: center;
  gap: 4px;
  color: #666;
  font-size: 12px;
}

.message-item .hot-badge {
  display: flex;
  align-items: center;
  gap: 4px;
  background: linear-gradient(45deg, #ff6b6b, #ff8e53);
  color: white;
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 11px;
  font-weight: 500;
}

.message-item.hot-message {
  border-left: 4px solid #ff6b6b;
  padding-left: 16px;
}

.message-item.hot-message .content {
  font-weight: 500;
}
</style>
