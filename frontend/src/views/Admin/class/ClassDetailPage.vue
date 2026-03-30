<template>
  <el-card class="detail-card">
    <template #header>
      <div class="card-header">
        <span>班级详情</span>
        <el-button link @click="handleBack">
          <el-icon><ArrowLeft /></el-icon>
          返回
        </el-button>
      </div>
    </template>

    <el-descriptions :column="2" border v-loading="loading">
      <el-descriptions-item label="班级ID">
        {{ clazz.classId }}
      </el-descriptions-item>
      <el-descriptions-item label="班级名称">
        {{ clazz.className }}
      </el-descriptions-item>
      <el-descriptions-item label="年级">
        {{ clazz.grade }}
      </el-descriptions-item>
      <el-descriptions-item label="班主任">
        {{ clazz.headteacherName }}
      </el-descriptions-item>
      <el-descriptions-item label="教室位置">
        {{ clazz.classroom }}
      </el-descriptions-item>
      <el-descriptions-item label="学生人数">
        {{ clazz.studentCount }}
      </el-descriptions-item>
      <el-descriptions-item label="状态">
        <el-tag :type="clazz.status === 1 ? 'success' : 'danger'">
          {{ clazz.status === 1 ? '启用' : '禁用' }}
        </el-tag>
      </el-descriptions-item>
      <el-descriptions-item label="创建时间">
        {{ clazz.createTime }}
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
const clazz = ref({})

const loadClass = async () => {
  const classId = route.params.classId
  if (!classId) {
    ElMessage.error('班级ID不能为空')
    handleBack()
    return
  }

  loading.value = true
  try {
    const response = await api.classes.getById(classId)
    if (response.code === 200 && response.data) {
      clazz.value = response.data
    } else {
      ElMessage.error('获取班级信息失败')
      handleBack()
    }
  } catch (error) {
    console.error('获取班级信息失败', error)
    ElMessage.error('获取班级信息失败')
    handleBack()
  } finally {
    loading.value = false
  }
}

const handleBack = () => {
  router.back()
}

onMounted(() => {
  loadClass()
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
