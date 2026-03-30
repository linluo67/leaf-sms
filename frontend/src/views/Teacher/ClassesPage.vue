<template>
  <el-card class="search-card">
    <el-form :inline="true" :model="searchForm" class="search-form">
      <el-form-item label="年级">
        <el-select v-model="searchForm.grade" placeholder="请选择年级" clearable>
          <el-option label="初一" value="初一" />
          <el-option label="初二" value="初二" />
          <el-option label="初三" value="初三" />
        </el-select>
      </el-form-item>
      <el-form-item label="科目">
        <el-select v-model="searchForm.subject" placeholder="请选择科目" clearable>
          <el-option label="语文" value="语文" />
          <el-option label="数学" value="数学" />
          <el-option label="英语" value="英语" />
          <el-option label="物理" value="物理" />
          <el-option label="化学" value="化学" />
          <el-option label="生物" value="生物" />
          <el-option label="历史" value="历史" />
          <el-option label="地理" value="地理" />
          <el-option label="政治" value="政治" />
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
      </el-form-item>
    </el-form>
  </el-card>

  <el-card class="table-card">
    <template #header>
      <div class="card-header">
        <span>任教班级</span>
      </div>
    </template>

    <el-table :data="classList" stripe v-loading="loading" border style="width: 100%">
      <el-table-column prop="grade" label="年级" min-width="150" />
      <el-table-column prop="className" label="班级名称" min-width="250" />
      <el-table-column prop="subject" label="科目" min-width="150" />
      <el-table-column prop="studentCount" label="学生数" min-width="150" align="center" />
      <el-table-column prop="headTeacherName" label="班主任" min-width="180" />
      <el-table-column label="操作" width="150" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" @click="handleViewStudents(row)">
            查看学生
          </el-button>
          <el-button link type="primary" @click="handleViewGrades(row)">
            查看成绩
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
import { Search, Refresh } from '@element-plus/icons-vue'
import api from '@/services/api'

const router = useRouter()
const loading = ref(false)
const classList = ref([])

const searchForm = reactive({
  grade: '',
  subject: ''
})

const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

const fetchClasses = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.page,
      size: pagination.size,
      ...searchForm
    }
    const response = await api.classes.list(params)
    if (response.code === 200) {
      classList.value = response.data.records || []
      pagination.total = response.data.total || 0
    } else {
      classList.value = []
      pagination.total = 0
    }
  } catch (error) {
    console.error('获取班级列表失败', error)
    classList.value = []
    pagination.total = 0
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.page = 1
  fetchClasses()
}

const handleReset = () => {
  Object.assign(searchForm, {
    grade: '',
    subject: ''
  })
  handleSearch()
}

const handleViewStudents = (row) => {
  router.push(`/teacher/students?classId=${row.classId}`)
}

const handleViewGrades = (row) => {
  router.push(`/teacher/grades?classId=${row.classId}`)
}

const handleSizeChange = (val) => {
  pagination.size = val
  fetchClasses()
}

const handlePageChange = (val) => {
  pagination.page = val
  fetchClasses()
}

onMounted(() => {
  fetchClasses()
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
