<template>
  <el-card class="search-card">
    <el-form :inline="true" :model="searchForm" class="search-form">
      <el-form-item label="班级">
        <el-select v-model="searchForm.classId" placeholder="请选择班级" clearable filterable>
          <el-option v-for="clazz in classList" :key="clazz.classId" :label="clazz.className" :value="clazz.classId" />
        </el-select>
      </el-form-item>
      <el-form-item label="姓名">
        <el-input v-model="searchForm.name" placeholder="请输入姓名" clearable />
      </el-form-item>
      <el-form-item label="学籍号">
        <el-input v-model="searchForm.studentNo" placeholder="请输入学籍号" clearable />
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
        <el-button type="success" @click="handleExport">
          <el-icon>
            <Download />
          </el-icon>
          导出
        </el-button>
      </el-form-item>
    </el-form>
  </el-card>

  <el-card class="table-card">
    <template #header>
      <div class="card-header">
        <span>学生名单</span>
      </div>
    </template>

    <el-table :data="studentList" stripe v-loading="loading" border style="width: 100%">
      <el-table-column prop="studentNo" label="学籍号" min-width="200" />
      <el-table-column prop="name" label="姓名" min-width="150" />
      <el-table-column prop="gender" label="性别" width="60">
        <template #default="{ row }">
          {{ row.gender === 'M' ? '男' : '女' }}
        </template>
      </el-table-column>
      <el-table-column prop="className" label="班级" min-width="200" />
      <el-table-column prop="phone" label="联系电话" min-width="180" />
      <el-table-column prop="status" label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="getStatusType(row.status)">
            {{ row.status }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="150" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" @click="handleView(row)">
            查看
          </el-button>
          <el-button link type="primary" @click="handleViewGrades(row)">
            成绩
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
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Search, Refresh, Download } from '@element-plus/icons-vue'
import api from '@/services/api'

const router = useRouter()
const loading = ref(false)
const studentList = ref([])
const classList = ref([])

const searchForm = reactive({
  classId: '',
  name: '',
  studentNo: ''
})

const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

const fetchClasses = async () => {
  try {
    const response = await api.classes.list({ page: 1, size: 100 })
    if (response.code === 200) {
      classList.value = response.data.records || []
    }
  } catch (error) {
    console.error('获取班级列表失败', error)
    classList.value = []
  }
}

const fetchStudents = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.page,
      size: pagination.size,
      ...searchForm
    }
    const response = await api.students.list(params)
    if (response.code === 200) {
      studentList.value = response.data.records || []
      pagination.total = response.data.total || 0
    } else {
      studentList.value = []
      pagination.total = 0
    }
  } catch (error) {
    console.error('获取学生列表失败', error)
    studentList.value = []
    pagination.total = 0
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.page = 1
  fetchStudents()
}

const handleReset = () => {
  Object.assign(searchForm, {
    classId: '',
    name: '',
    studentNo: ''
  })
  handleSearch()
}

const handleView = (row) => {
  router.push(`/teacher/students/${row.studentId}`)
}

const handleViewGrades = (row) => {
  router.push(`/teacher/grades?studentId=${row.studentId}`)
}

const handleExport = async () => {
  try {
    const params = { ...searchForm, exportType: 'student' }
    await api.students.export(params)
    ElMessage.success('导出成功')
  } catch (error) {
    ElMessage.error('导出失败')
  }
}

const handleSizeChange = (val) => {
  pagination.size = val
  fetchStudents()
}

const handlePageChange = (val) => {
  pagination.page = val
  fetchStudents()
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

onMounted(() => {
  fetchClasses()
  fetchStudents()
})
</script>

<style scoped>
:deep(.search-card) {
  margin-bottom: 40px !important;
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
</style>
