<template>
  <el-card class="search-card">
    <el-form :inline="true" :model="searchForm" class="search-form">
      <el-form-item label="状态">
        <el-select v-model="searchForm.isRead" placeholder="请选择状态" clearable>
          <el-option label="未读" :value="false" />
          <el-option label="已读" :value="true" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSearch" :loading="loading">
          <el-icon>
            <Search />
          </el-icon>
          查询
        </el-button>
        <el-button @click="handleReset">
          <el-icon>
            <Refresh />
          </el-icon>
          重置
        </el-button>
        <el-button type="success" @click="handleMarkAllRead">
          <el-icon>
            <Check />
          </el-icon>
          全部标为已读
        </el-button>
      </el-form-item>
    </el-form>
  </el-card>

  <el-card class="table-card">
      <template #header>
        <div class="card-header">
          <span>消息通知</span>
        </div>
      </template>

      <el-table :data="messageList" stripe v-loading="loading" border style="width: 100%">
        <el-table-column prop="title" label="标题" min-width="250" />
        <el-table-column prop="content" label="内容" min-width="350" show-overflow-tooltip />
        <el-table-column prop="isRead" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="!row.isRead ? 'danger' : 'info'" size="small">
              {{ !row.isRead ? '未读' : '已读' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="发送时间" min-width="180" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleView(row)">
              查看
            </el-button>
            <el-button v-if="!row.isRead" link type="success" @click="handleMarkRead(row)">
              标为已读
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="pagination.page"
        v-model:page-size="pagination.size"
        :total="pagination.total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handlePageChange"
        class="pagination"
      />
    </el-card>

    <el-dialog v-model="viewDialogVisible" title="消息详情" width="600px">
      <div class="message-detail">
        <div class="detail-item">
          <span class="label">标题：</span>
          <span class="value">{{ currentMessage.title }}</span>
        </div>
        <div class="detail-item">
          <span class="label">内容：</span>
          <div class="value content">{{ currentMessage.content }}</div>
        </div>
        <div class="detail-item">
          <span class="label">发送时间：</span>
          <span class="value">{{ currentMessage.createTime }}</span>
        </div>
      </div>
      <template #footer>
        <el-button @click="viewDialogVisible = false">关闭</el-button>
        <el-button type="primary" @click="handleMarkRead(currentMessage)">
          标为已读
        </el-button>
      </template>
    </el-dialog>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Search, Refresh, Check } from '@element-plus/icons-vue'
import api from '@/services/api'

const loading = ref(false)
const viewDialogVisible = ref(false)
const messageList = ref([])
const currentMessage = ref({})

const searchForm = reactive({
  isRead: null
})

const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

const fetchMessages = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.page,
      size: pagination.size,
      ...searchForm
    }
    const response = await api.messages.list(params)
    if (response.code === 200) {
      messageList.value = response.data.records || []
      pagination.total = response.data.total || 0
    } else {
      messageList.value = []
      pagination.total = 0
    }
  } catch (error) {
    console.error('获取消息列表失败', error)
    messageList.value = []
    pagination.total = 0
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.page = 1
  fetchMessages()
}

const handleReset = () => {
  searchForm.isRead = null
  handleSearch()
}

const handleView = (row) => {
  currentMessage.value = row
  viewDialogVisible.value = true
}

const handleMarkRead = async (row) => {
  try {
    const response = await api.messages.markRead(row.messageId)
    if (response.code === 200) {
      ElMessage.success('标记成功')
      row.isRead = true
      if (viewDialogVisible.value) {
        viewDialogVisible.value = false
      }
      fetchMessages()
    }
  } catch (error) {
    ElMessage.error('标记失败')
  }
}

const handleMarkAllRead = async () => {
  try {
    const response = await api.messages.markAllRead()
    if (response.code === 200) {
      ElMessage.success('全部标记成功')
      fetchMessages()
    }
  } catch (error) {
    ElMessage.error('标记失败')
  }
}

const handleSizeChange = (val) => {
  pagination.size = val
  fetchMessages()
}

const handlePageChange = (val) => {
  pagination.page = val
  fetchMessages()
}

onMounted(() => {
  fetchMessages()
})
</script>

<style scoped>
.search-card {
  margin-bottom: 20px;
}

.search-form {
  margin-bottom: 0;
}

.table-card {
  min-height: 500px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

.message-detail {
  padding: 20px;
}

.detail-item {
  margin-bottom: 15px;
}

.detail-item .label {
  font-weight: bold;
  color: #606266;
}

.detail-item .value {
  color: #303133;
}

.detail-item .value.content {
  white-space: pre-wrap;
  line-height: 1.6;
}
</style>
