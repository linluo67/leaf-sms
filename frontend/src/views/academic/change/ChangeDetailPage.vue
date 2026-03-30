<template>
  <el-card class="detail-card">
    <template #header>
      <div class="card-header">
        <span>学籍变动详情</span>
        <el-button link @click="handleBack">
          <el-icon><ArrowLeft /></el-icon>
          返回
        </el-button>
      </div>
    </template>

    <el-descriptions :column="2" border v-loading="loading">
      <el-descriptions-item label="变动ID">
        {{ change.changeId }}
      </el-descriptions-item>
      <el-descriptions-item label="学生姓名">
        {{ change.studentName }}
      </el-descriptions-item>
      <el-descriptions-item label="学籍号">
        {{ change.studentNo }}
      </el-descriptions-item>
      <el-descriptions-item label="变动类型">
        <el-tag :type="getChangeTypeColor(change.changeType)">
          {{ change.changeType }}
        </el-tag>
      </el-descriptions-item>
      <el-descriptions-item label="变动原因" :span="2">
        {{ change.changeReason }}
      </el-descriptions-item>
      <el-descriptions-item label="状态">
        <el-tag :type="getStatusType(change.status)">
          {{ change.status }}
        </el-tag>
      </el-descriptions-item>
      <el-descriptions-item label="审核人">
        {{ change.reviewerName || '-' }}
      </el-descriptions-item>
      <el-descriptions-item label="审核意见" :span="2">
        {{ change.reviewComment || '-' }}
      </el-descriptions-item>
      <el-descriptions-item label="申请时间">
        {{ change.createTime }}
      </el-descriptions-item>
      <el-descriptions-item label="审核时间">
        {{ change.reviewTime || '-' }}
      </el-descriptions-item>
    </el-descriptions>
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowLeft } from '@element-plus/icons-vue'
import api from '@/services/api'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const change = ref({})

const loadChange = async () => {
  const changeId = route.params.changeId
  if (!changeId) {
    ElMessage.error('变动ID不能为空')
    handleBack()
    return
  }

  loading.value = true
  try {
    const response = await api.changes.getById(changeId)
    if (response.code === 200 && response.data) {
      change.value = response.data
    } else {
      ElMessage.error('获取学籍变动信息失败')
      handleBack()
    }
  } catch (error) {
    console.error('获取学籍变动信息失败', error)
    ElMessage.error('获取学籍变动信息失败')
    handleBack()
  } finally {
    loading.value = false
  }
}

const getChangeTypeColor = (type) => {
  const colorMap = {
    '转入': 'success',
    '转出': 'danger',
    '休学': 'warning',
    '复学': 'primary',
    '毕业': 'info'
  }
  return colorMap[type] || 'info'
}

const getStatusType = (status) => {
  const statusMap = {
    '待审核': 'warning',
    '已通过': 'success',
    '已驳回': 'danger'
  }
  return statusMap[status] || 'info'
}

const handleBack = () => {
  router.back()
}

onMounted(() => {
  loadChange()
})
</script>

<style scoped>
.detail-card {
  max-width: 1000px;
  margin: 0 auto;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 600;
  font-size: 16px;
  color: #303133;
}
</style>
