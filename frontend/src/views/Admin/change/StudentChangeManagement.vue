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
        <span>学籍变动列表</span>
        <el-button type="primary" @click="handleAdd">
          <el-icon>
            <Plus />
          </el-icon>
          新增变动
        </el-button>
      </div>
    </template>

    <el-table :data="changeList" stripe v-loading="loading" border style="width: 100%">
      <el-table-column prop="studentName" label="学生姓名" width="100" />
      <el-table-column prop="studentNo" label="学籍号" width="120" />
      <el-table-column prop="changeType" label="变动类型" width="120" />
      <el-table-column prop="changeReason" label="变动原因" min-width="200" />
      <el-table-column prop="status" label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="getStatusType(row.status)">
            {{ row.status }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="reviewerName" label="审核人" width="100" />
      <el-table-column prop="reviewComment" label="审核意见" min-width="200" />
      <el-table-column prop="createTime" label="申请时间" width="120" />
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" @click="handleView(row)">查看</el-button>
          <el-button v-if="row.status === '待审核'" link type="success" @click="handleReview(row)">审核</el-button>
          <el-button v-if="row.status === '待审核'" link type="danger" @click="handleReject(row)">驳回</el-button>
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

  <el-dialog v-model="reviewDialogVisible" title="审核学籍变动" width="500px">
    <el-form :model="reviewForm" label-width="100px">
      <el-form-item label="审核结果">
        <el-radio-group v-model="reviewForm.status">
          <el-radio label="已通过">通过</el-radio>
          <el-radio label="已驳回">驳回</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="审核意见">
        <el-input v-model="reviewForm.reviewComment" type="textarea" :rows="4" placeholder="请输入审核意见" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="reviewDialogVisible = false">取消</el-button>
      <el-button type="primary" @click="handleReviewSubmit" :loading="reviewLoading">
        确定
      </el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import api from '@/services/api'

const router = useRouter()

const loading = ref(false)
const reviewLoading = ref(false)
const changeList = ref([])
const reviewDialogVisible = ref(false)

const searchForm = reactive({
  studentName: '',
  changeType: '',
  status: ''
})

const reviewForm = reactive({
  changeId: '',
  status: '已通过',
  reviewComment: ''
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

const handleAdd = () => {
  router.push('/admin/changes/add')
}

const handleView = (row) => {
  router.push(`/admin/changes/${row.changeId}`)
}

const handleReview = (row) => {
  reviewForm.changeId = row.changeId
  reviewForm.status = '已通过'
  reviewForm.reviewComment = ''
  reviewDialogVisible.value = true
}

const handleReviewSubmit = async () => {
  reviewLoading.value = true
  try {
    const response = await api.changes.review(reviewForm.changeId, {
      status: reviewForm.status,
      reviewComment: reviewForm.reviewComment
    })
    if (response.code === 200) {
      ElMessage.success('审核成功')
      reviewDialogVisible.value = false
      fetchChanges()
    }
  } catch (error) {
    ElMessage.error('审核失败')
  } finally {
    reviewLoading.value = false
  }
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
