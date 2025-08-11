<template>
  <div class="post-message-container">
    <el-row :gutter="20">
      <el-col :span="16" :offset="4">
        <el-card>
          <template #header>
            <div class="card-header">
              <h2>发布留言</h2>
            </div>
          </template>
          
          <el-form
            ref="messageFormRef"
            :model="messageForm"
            :rules="messageRules"
            label-width="80px"
            @submit.prevent="handleSubmit"
          >
            <el-form-item label="留言内容" prop="content">
              <el-input
                v-model="messageForm.content"
                type="textarea"
                :rows="6"
                placeholder="请输入您的留言内容..."
                maxlength="500"
                show-word-limit
                resize="none"
              />
            </el-form-item>
            
            <el-form-item>
              <el-button
                type="primary"
                :loading="loading"
                @click="handleSubmit"
                size="large"
              >
                {{ loading ? '发布中...' : '发布留言' }}
              </el-button>
              <el-button @click="$router.push('/messages')" size="large">
                取消
              </el-button>
            </el-form-item>
          </el-form>
        </el-card>
        
        <el-card class="tips-card" style="margin-top: 20px;">
          <template #header>
            <div class="card-header">
              <h4>发布提示</h4>
            </div>
          </template>
          <ul class="tips-list">
            <li>留言内容应该积极向上，遵守社区规范</li>
            <li>支持 Markdown 格式，可以添加表情符号</li>
            <li>发布后其他用户可以查看和点赞</li>
            <li>热门留言会被缓存到 Redis，提高访问速度</li>
          </ul>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { messagesApi } from '../api/messages'
import { ElMessage } from 'element-plus'

const router = useRouter()

const messageFormRef = ref()
const loading = ref(false)

const messageForm = reactive({
  content: ''
})

const messageRules = {
  content: [
    { required: true, message: '请输入留言内容', trigger: 'blur' },
    { min: 5, max: 500, message: '留言内容长度在 5 到 500 个字符', trigger: 'blur' }
  ]
}

const handleSubmit = async () => {
  if (!messageFormRef.value) return
  
  try {
    await messageFormRef.value.validate()
    loading.value = true
    
    await messagesApi.createMessage({
      content: messageForm.content
    })
    
    ElMessage.success('留言发布成功')
    router.push('/messages')
  } catch (error) {
    console.error('发布留言失败:', error)
    if (error.response?.status === 401) {
      ElMessage.error('请先登录')
      router.push('/login')
    } else {
      ElMessage.error('发布留言失败，请重试')
    }
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.post-message-container {
  max-width: 1200px;
  margin: 0 auto;
}

.card-header {
  text-align: center;
}

.card-header h2 {
  margin: 0;
  color: #409eff;
}

.tips-card .card-header h4 {
  margin: 0;
  color: #666;
}

.tips-list {
  margin: 0;
  padding-left: 20px;
  color: #666;
  line-height: 1.6;
}

.tips-list li {
  margin-bottom: 8px;
}
</style>
