<template>
  <el-card class="detail-card">
    <template #header>
      <div class="card-header">
        <span>用户详情</span>
        <el-button link @click="handleBack">
          <el-icon><ArrowLeft /></el-icon>
          返回
        </el-button>
      </div>
    </template>

    <el-descriptions :column="2" border v-loading="loading">
      <el-descriptions-item label="用户ID">
        {{ user.userId }}
      </el-descriptions-item>
      <el-descriptions-item label="用户名">
        {{ user.username }}
      </el-descriptions-item>
      <el-descriptions-item label="真实姓名">
        {{ user.realName }}
      </el-descriptions-item>
      <el-descriptions-item label="性别">
        {{ user.gender === 'M' ? '男' : '女' }}
      </el-descriptions-item>
      <el-descriptions-item label="角色">
        <el-tag :type="getRoleType(user.roleCode)">
          {{ getRoleName(user.roleCode) }}
        </el-tag>
      </el-descriptions-item>
      <el-descriptions-item label="状态">
        <el-tag :type="user.status === 1 ? 'success' : 'danger'">
          {{ user.status === 1 ? '启用' : '禁用' }}
        </el-tag>
      </el-descriptions-item>
      <el-descriptions-item label="联系电话">
        {{ user.phone }}
      </el-descriptions-item>
      <el-descriptions-item label="邮箱">
        {{ user.email }}
      </el-descriptions-item>
      <el-descriptions-item label="最后登录时间">
        {{ user.lastLoginTime }}
      </el-descriptions-item>
      <el-descriptions-item label="创建时间">
        {{ user.createTime }}
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
const user = ref({})

const loadUser = async () => {
  const userId = route.params.userId
  if (!userId) {
    ElMessage.error('用户ID不能为空')
    handleBack()
    return
  }

  loading.value = true
  try {
    const response = await api.users.getById(userId)
    if (response.code === 200 && response.data) {
      user.value = response.data
    } else {
      ElMessage.error('获取用户信息失败')
      handleBack()
    }
  } catch (error) {
    console.error('获取用户信息失败', error)
    ElMessage.error('获取用户信息失败')
    handleBack()
  } finally {
    loading.value = false
  }
}

const getRoleType = (roleCode) => {
  const roleMap = {
    'ADMIN': 'danger',
    'ACADEMIC': 'warning',
    'HEADTEACHER': 'primary',
    'TEACHER': 'success',
    'PARENT': 'info'
  }
  return roleMap[roleCode] || 'info'
}

const getRoleName = (roleCode) => {
  const roleMap = {
    'ADMIN': '系统管理员',
    'ACADEMIC': '教务管理员',
    'HEADTEACHER': '班主任',
    'TEACHER': '任课教师',
    'PARENT': '家长'
  }
  return roleMap[roleCode] || '未知'
}

const handleBack = () => {
  router.back()
}

onMounted(() => {
  loadUser()
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
