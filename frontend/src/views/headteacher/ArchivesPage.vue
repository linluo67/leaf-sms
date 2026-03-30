<template>
  <el-card class="search-card">
    <el-form :inline="true" :model="searchForm" class="search-form">
      <el-form-item label="学生姓名">
        <el-input v-model="searchForm.studentName" placeholder="请输入学生姓名" clearable />
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
      </el-form-item>
    </el-form>
  </el-card>

  <el-card class="table-card">
    <template #header>
      <div class="card-header">
        <span>学籍档案</span>
      </div>
    </template>

    <el-table :data="archiveList" stripe v-loading="loading" border style="width: 100%">
      <el-table-column prop="studentNo" label="学籍号" min-width="180" />
      <el-table-column prop="name" label="姓名" min-width="150" />
      <el-table-column prop="gender" label="性别" width="60">
        <template #default="{ row }">
          {{ row.gender === 'M' ? '男' : '女' }}
        </template>
      </el-table-column>
      <el-table-column prop="birthDate" label="出生日期" min-width="180" />
      <el-table-column prop="idCard" label="身份证号" min-width="250" />
      <el-table-column prop="address" label="家庭住址" min-width="300" />
      <el-table-column prop="enrollDate" label="入学日期" min-width="180" />
      <el-table-column label="操作" width="150" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" @click="handleView(row)">
            查看详情
          </el-button>
          <el-button link type="success" @click="handlePrint(row)">
            打印档案
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
import { ElMessage } from 'element-plus'
import { Search, Refresh } from '@element-plus/icons-vue'
import api from '@/services/api'

const loading = ref(false)
const archiveList = ref([])

const searchForm = reactive({
  studentName: '',
  studentNo: ''
})

const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

const fetchArchives = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.page,
      size: pagination.size,
      ...searchForm
    }
    const response = await api.students.list(params)
    if (response.code === 200) {
      archiveList.value = response.data.records || []
      pagination.total = response.data.total || 0
    } else {
      archiveList.value = []
      pagination.total = 0
    }
  } catch (error) {
    console.error('获取学籍档案失败', error)
    archiveList.value = []
    pagination.total = 0
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.page = 1
  fetchArchives()
}

const handleReset = () => {
  Object.assign(searchForm, {
    studentName: '',
    studentNo: ''
  })
  handleSearch()
}

const handleView = () => {
  ElMessage.info('查看档案详情功能开发中')
}

const handlePrint = () => {
  ElMessage.info('打印档案功能开发中')
}

const handleSizeChange = (val) => {
  pagination.size = val
  fetchArchives()
}

const handlePageChange = (val) => {
  pagination.page = val
  fetchArchives()
}

onMounted(() => {
  fetchArchives()
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
