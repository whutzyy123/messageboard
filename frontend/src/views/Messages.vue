<template>
  <div class="messages-container">
    <el-row :gutter="20" class="search-row">
      <el-col :span="24">
        <el-card>
          <el-row :gutter="20">
            <el-col :span="8">
              <el-input
                v-model="searchKeyword"
                placeholder="搜索留言内容..."
                prefix-icon="Search"
                clearable
                @input="handleSearch"
              />
            </el-col>
            <el-col :span="4">
              <el-select v-model="sortBy" placeholder="排序方式" @change="handleSearch">
                <el-option label="最新发布" value="createdAt" />
                <el-option label="最多点赞" value="likes" />
                <el-option label="热门留言" value="hot" />
              </el-select>
            </el-col>
            <el-col :span="4">
              <el-button type="primary" @click="handleSearch">
                搜索
              </el-button>
            </el-col>
          </el-row>
        </el-card>
      </el-col>
    </el-row>
    
    <el-row :gutter="20" class="messages-row">
      <el-col :span="24">
        <el-card>
          <template #header>
            <div class="card-header">
              <h3>留言列表</h3>
              <el-button type="primary" @click="$router.push('/post')" v-if="isLoggedIn">
                发布留言
              </el-button>
            </div>
          </template>
          
          <div v-if="loading" class="loading-container">
            <el-skeleton :rows="5" animated />
          </div>
          
          <div v-else-if="messages.length === 0" class="empty-container">
            <el-empty description="暂无留言" />
          </div>
          
          <div v-else class="messages-list">
            <div
              v-for="message in messages"
              :key="message.id"
              class="message-item"
            >
              <div class="message-header">
                <div class="user-info">
                  <el-avatar :size="40" :src="message.user?.avatar">
                    {{ message.user?.username?.charAt(0)?.toUpperCase() }}
                  </el-avatar>
                  <div class="user-details">
                    <span class="username">{{ message.user?.username }}</span>
                    <span class="time">{{ formatTime(message.createdAt) }}</span>
                  </div>
                </div>
                <div class="message-actions">
                  <el-button
                    type="text"
                    size="small"
                    @click="handleLike(message)"
                    :disabled="!isLoggedIn"
                  >
                    <el-icon><ThumbsUp /></el-icon>
                    <span>{{ message.likesCount || 0 }}</span>
                  </el-button>
                </div>
              </div>
              
              <div class="message-content">
                <p>{{ message.content }}</p>
              </div>
              
              <div class="message-footer">
                <el-tag v-if="message.isHot" type="danger" size="small">
                  热门
                </el-tag>
                <el-tag v-if="message.likesCount > 10" type="warning" size="small">
                  热门
                </el-tag>
              </div>
            </div>
          </div>
          
          <div class="pagination-container">
            <el-pagination
              v-model:current-page="currentPage"
              v-model:page-size="pageSize"
              :page-sizes="[10, 20, 50]"
              :total="total"
              layout="total, sizes, prev, pager, next, jumper"
              @size-change="handleSizeChange"
              @current-change="handleCurrentChange"
            />
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useAuthStore } from '../stores/auth'
import { messagesApi } from '../api/messages'
import { ElMessage } from 'element-plus'

const authStore = useAuthStore()
const isLoggedIn = computed(() => authStore.isLoggedIn)

const loading = ref(false)
const messages = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const searchKeyword = ref('')
const sortBy = ref('createdAt')

const fetchMessages = async () => {
  try {
    loading.value = true
    const params = {
      page: currentPage.value - 1,
      size: pageSize.value,
      keyword: searchKeyword.value,
      sort: sortBy.value
    }
    
    const response = await messagesApi.getMessages(params)
    messages.value = response.data.content || []
    total.value = response.data.totalElements || 0
  } catch (error) {
    console.error('获取留言失败:', error)
    ElMessage.error('获取留言失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  currentPage.value = 1
  fetchMessages()
}

const handleSizeChange = (size) => {
  pageSize.value = size
  currentPage.value = 1
  fetchMessages()
}

const handleCurrentChange = (page) => {
  currentPage.value = page
  fetchMessages()
}

const handleLike = async (message) => {
  try {
    await messagesApi.likeMessage(message.id)
    message.likesCount = (message.likesCount || 0) + 1
    ElMessage.success('点赞成功')
  } catch (error) {
    console.error('点赞失败:', error)
    ElMessage.error('点赞失败')
  }
}

const formatTime = (timeStr) => {
  if (!timeStr) return ''
  const date = new Date(timeStr)
  const now = new Date()
  const diff = now - date
  
  if (diff < 60000) return '刚刚'
  if (diff < 3600000) return `${Math.floor(diff / 60000)}分钟前`
  if (diff < 86400000) return `${Math.floor(diff / 3600000)}小时前`
  if (diff < 2592000000) return `${Math.floor(diff / 86400000)}天前`
  
  return date.toLocaleDateString()
}

onMounted(() => {
  fetchMessages()
})
</script>

<style scoped>
.messages-container {
  max-width: 1200px;
  margin: 0 auto;
}

.search-row {
  margin-bottom: 20px;
}

.messages-row {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header h3 {
  margin: 0;
}

.loading-container {
  padding: 20px 0;
}

.empty-container {
  padding: 40px 0;
}

.messages-list {
  margin-bottom: 20px;
}

.message-item {
  border: 1px solid #ebeef5;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 20px;
  background-color: #fafafa;
}

.message-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-details {
  display: flex;
  flex-direction: column;
}

.username {
  font-weight: bold;
  color: #409eff;
}

.time {
  font-size: 12px;
  color: #999;
}

.message-actions {
  display: flex;
  gap: 10px;
}

.message-content {
  margin-bottom: 15px;
}

.message-content p {
  margin: 0;
  line-height: 1.6;
  color: #333;
}

.message-footer {
  display: flex;
  gap: 10px;
}

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}
</style>
