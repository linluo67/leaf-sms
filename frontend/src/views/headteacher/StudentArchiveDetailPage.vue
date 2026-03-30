<template>
  <el-card class="detail-card">
    <template #header>
      <div class="card-header">
        <span>学生档案</span>
        <el-button link @click="handleBack">
          <el-icon><ArrowLeft /></el-icon>
          返回
        </el-button>
      </div>
    </template>

    <el-descriptions :column="2" border v-loading="loading">
      <el-descriptions-item label="学籍号">
        {{ student.studentNo }}
      </el-descriptions-item>
      <el-descriptions-item label="姓名">
        {{ student.name }}
      </el-descriptions-item>
      <el-descriptions-item label="性别">
        {{ student.gender === 'M' ? '男' : '女' }}
      </el-descriptions-item>
      <el-descriptions-item label="出生日期">
        {{ student.birthDate }}
      </el-descriptions-item>
      <el-descriptions-item label="民族">
        {{ student.nation }}
      </el-descriptions-item>
      <el-descriptions-item label="身份证号">
        {{ student.idCard }}
      </el-descriptions-item>
      <el-descriptions-item label="年级">
        {{ student.grade }}
      </el-descriptions-item>
      <el-descriptions-item label="班级">
        {{ student.className }}
      </el-descriptions-item>
      <el-descriptions-item label="入学日期">
        {{ student.enrollmentDate }}
      </el-descriptions-item>
      <el-descriptions-item label="状态">
        <el-tag :type="getStatusType(student.status)">
          {{ getStatusText(student.status) }}
        </el-tag>
      </el-descriptions-item>
      <el-descriptions-item label="联系电话">
        {{ student.phone }}
      </el-descriptions-item>
      <el-descriptions-item label="家庭住址">
        {{ student.householdAddress }}
      </el-descriptions-item>
      <el-descriptions-item label="监护人姓名">
        {{ student.guardianName }}
      </el-descriptions-item>
      <el-descriptions-item label="监护人电话">
        {{ student.guardianPhone }}
      </el-descriptions-item>
      <el-descriptions-item label="监护人关系">
        {{ student.guardianRelation }}
      </el-descriptions-item>
      <el-descriptions-item label="创建时间">
        {{ student.createTime }}
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
const student = ref({})

const loadStudent = async () => {
  const studentId = route.params.studentId
  if (!studentId) {
    ElMessage.error('学生ID不能为空')
    handleBack()
    return
  }

  loading.value = true
  try {
    const response = await api.students.getById(studentId)
    if (response.code === 200 && response.data) {
      student.value = response.data
    } else {
      ElMessage.error('获取学生信息失败')
      handleBack()
    }
  } catch (error) {
    console.error('获取学生信息失败', error)
    ElMessage.error('获取学生信息失败')
    handleBack()
  } finally {
    loading.value = false
  }
}

const getStatusType = (status) => {
  const statusMap = {
    '在读': 'success',
    '休学': 'warning',
    '毕业': 'info',
    '转出': 'danger'
  }
  return statusMap[status] || 'info'
}

const getStatusText = (status) => {
  return status || '未知'
}

const handleBack = () => {
  router.back()
}

onMounted(() => {
  loadStudent()
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
