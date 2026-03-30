<template>
  <el-card class="search-card">
    <el-form :inline="true" :model="searchForm" class="search-form">
      <el-form-item label="学生姓名">
        <el-input v-model="searchForm.studentName" placeholder="请输入学生姓名" clearable />
      </el-form-item>
      <el-form-item label="变动类型">
        <el-select v-model="searchForm.changeType" placeholder="请选择变动类型" clearable>
          <el-option label="转入" value="转入" />
          <el-option label="转出" value="转出" />
          <el-option label="休学" value="休学" />
          <el-option label="复学" value="复学" />
          <el-option label="毕业" value="毕业" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
          <el-option label="待审核" value="待审核" />
          <el-option label="已通过" value="已通过" />
          <el-option label="已驳回" value="已驳回" />
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
        <span>学籍变动记录</span>
      </div>
    </template>

    <el-table :data="changeList" stripe v-loading="loading" border style="width: 100%">
      <el-table-column prop="studentName" label="学生姓名" min-width="150" />
      <el-table-column prop="studentNo" label="学籍号" min-width="200" />
      <el-table-column prop="changeType" label="变动类型" min-width="150" />
      <el-table-column prop="changeReason" label="变动原因" min-width="300" />
      <el-table-column prop="status" label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="getStatusType(row.status)">
            {{ row.status }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="reviewerName" label="审核人" min-width="150" />
      <el-table-column prop="reviewComment" label="审核意见" min-width="300" />
      <el-table-column prop="createTime" label="申请时间" min-width="200" />
      <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleView(row)">
              查看
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
const changeList = ref([])

const searchForm = reactive({
  studentName: '',
  changeType: '',
  status: ''
})

const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

const fetchChanges = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.page,
      size: pagination.size,
      ...searchForm
    }
    const response = await api.changes.list(params)
    if (response.code === 200) {
      changeList.value = response.data.records || []
      pagination.total = response.data.total || 0
    } else {
      changeList.value = []
      pagination.total = 0
    }
  } catch (error) {
    console.error('获取变动列表失败', error)
    changeList.value = []
    pagination.total = 0
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.page = 1
  fetchChanges()
}

const handleReset = () => {
  Object.assign(searchForm, {
    studentName: '',
    changeType: '',
    status: ''
  })
  handleSearch()
}

const handleView = (row) => {
  router.push(`/headteacher/student-changes/${row.changeId}`)
}

const handleSizeChange = (val) => {
  pagination.size = val
  fetchChanges()
}

const handlePageChange = (val) => {
  pagination.page = val
  fetchChanges()
}

const getStatusType = (status) => {
  const statusMap = {
    '待审核': 'warning',
    '已通过': 'success',
    '已驳回': 'danger'
  }
  return statusMap[status] || 'info'
}

onMounted(() => {
  fetchChanges()
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
