<template>
  <el-card class="edit-card">
    <template #header>
      <div class="card-header">
        <span>编辑班级</span>
        <el-button link @click="handleBack">
          <el-icon><ArrowLeft /></el-icon>
          返回
        </el-button>
      </div>
    </template>

    <el-form :model="form" :rules="rules" ref="formRef" label-width="100px" v-loading="loading">
      <el-form-item label="年级" prop="grade">
        <el-select v-model="form.grade" placeholder="请选择年级" style="width: 100%">
          <el-option label="初一" value="初一" />
          <el-option label="初二" value="初二" />
          <el-option label="初三" value="初三" />
        </el-select>
      </el-form-item>

      <el-form-item label="班级名称" prop="className">
        <el-input v-model="form.className" placeholder="请输入班级名称" />
      </el-form-item>

      <el-form-item label="班主任" prop="headteacherName">
        <el-input v-model="form.headteacherName" placeholder="请输入班主任姓名" />
      </el-form-item>

      <el-form-item label="状态" prop="status">
        <el-radio-group v-model="form.status">
          <el-radio :label="1">启用</el-radio>
          <el-radio :label="0">禁用</el-radio>
        </el-radio-group>
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

const form = reactive({
  classId: '',
  className: '',
  grade: '',
  headteacherName: '',
  status: 1
})

const rules = {
  grade: [
    { required: true, message: '请选择年级', trigger: 'change' }
  ],
  className: [
    { required: true, message: '请输入班级名称', trigger: 'blur' }
  ],
  headteacherName: [
    { required: true, message: '请输入班主任姓名', trigger: 'blur' }
  ]
}

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
      const clazz = response.data
      Object.assign(form, {
        classId: clazz.classId,
        className: clazz.className,
        grade: clazz.grade,
        headteacherName: clazz.headteacherName,
        status: clazz.status
      })
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

const handleSubmit = async () => {
  try {
    await formRef.value.validate()
  } catch (error) {
    return
  }

  submitting.value = true
  try {
    const response = await api.classes.update(form.classId, form)
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
  loadClass()
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
