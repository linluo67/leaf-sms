<template>
  <el-card class="edit-card">
    <template #header>
      <div class="card-header">
        <span>编辑学生</span>
        <el-button link @click="handleBack">
          <el-icon><ArrowLeft /></el-icon>
          返回
        </el-button>
      </div>
    </template>

    <el-form :model="form" :rules="rules" ref="formRef" label-width="100px" v-loading="loading">
      <el-form-item label="学籍号" prop="studentNo">
        <el-input v-model="form.studentNo" placeholder="请输入学籍号" disabled />
      </el-form-item>

      <el-form-item label="姓名" prop="name">
        <el-input v-model="form.name" placeholder="请输入姓名" />
      </el-form-item>

      <el-form-item label="性别" prop="gender">
        <el-radio-group v-model="form.gender">
          <el-radio label="M">男</el-radio>
          <el-radio label="F">女</el-radio>
        </el-radio-group>
      </el-form-item>

      <el-form-item label="年级" prop="grade">
        <el-select v-model="form.grade" placeholder="请选择年级" style="width: 100%">
          <el-option label="初一" value="初一" />
          <el-option label="初二" value="初二" />
          <el-option label="初三" value="初三" />
        </el-select>
      </el-form-item>

      <el-form-item label="班级" prop="classId">
        <el-select v-model="form.classId" placeholder="请选择班级" style="width: 100%" filterable>
          <el-option v-for="clazz in classList" :key="clazz.classId" :label="clazz.className" :value="clazz.classId" />
        </el-select>
      </el-form-item>

      <el-form-item label="联系电话" prop="phone">
        <el-input v-model="form.phone" placeholder="请输入联系电话" />
      </el-form-item>

      <el-form-item label="家长ID" prop="parentId">
        <el-input v-model="form.parentId" placeholder="请输入家长ID" />
      </el-form-item>

      <el-form-item label="状态" prop="studentStatus">
        <el-select v-model="form.studentStatus" placeholder="请选择状态" style="width: 100%">
          <el-option label="在读" value="在读" />
          <el-option label="休学" value="休学" />
          <el-option label="毕业" value="毕业" />
          <el-option label="转出" value="转出" />
        </el-select>
      </el-form-item>

      <el-form-item>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">
          保存
        </el-button>
        <el-button @click="handleBack">取消</el-button>
      </el-form-item>
    </el-form>
  </el-card>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowLeft } from '@element-plus/icons-vue'
import api from '@/services/api'

const route = useRoute()
const router = useRouter()

const formRef = ref(null)
const loading = ref(false)
const submitting = ref(false)
const classList = ref([])

const form = reactive({
  studentId: '',
  studentNo: '',
  name: '',
  gender: 'M',
  grade: '',
  classId: '',
  phone: '',
  parentId: '',
  studentStatus: '在读'
})

const rules = {
  name: [
    { required: true, message: '请输入姓名', trigger: 'blur' }
  ],
  gender: [
    { required: true, message: '请选择性别', trigger: 'change' }
  ],
  grade: [
    { required: true, message: '请选择年级', trigger: 'change' }
  ],
  classId: [
    { required: true, message: '请选择班级', trigger: 'change' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  studentStatus: [
    { required: true, message: '请选择状态', trigger: 'change' }
  ]
}

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
      const student = response.data
      Object.assign(form, {
        studentId: student.studentId,
        studentNo: student.studentNo,
        name: student.name,
        gender: student.gender,
        grade: student.grade,
        classId: student.classId,
        phone: student.phone,
        parentId: student.parentId,
        studentStatus: student.studentStatus
      })
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

const loadClasses = async () => {
  try {
    const response = await api.classes.listAll()
    if (response.code === 200) {
      classList.value = response.data || []
    }
  } catch (error) {
    console.error('获取班级列表失败', error)
  }
}

const handleSubmit = async () => {
  try {
    await formRef.value.validate()
  } catch (error) {
    return
  }

  submitting.value = true
  try {
    const response = await api.students.update(form.studentId, form)
    if (response.code === 200) {
      ElMessage.success('保存成功')
      handleBack()
    } else {
      ElMessage.error(response.message || '保存失败')
    }
  } catch (error) {
    console.error('保存失败', error)
    ElMessage.error('保存失败')
  } finally {
    submitting.value = false
  }
}

const handleBack = () => {
  router.back()
}

onMounted(() => {
  loadClasses()
  loadStudent()
})
</script>

<style scoped>
.edit-card {
  max-width: 800px;
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
