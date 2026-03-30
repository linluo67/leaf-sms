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
      <el-form-item label="班级名称">
        <el-input v-model="searchForm.className" placeholder="请输入班级名称" clearable />
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
        <span>班级列表</span>
        <el-button type="primary" @click="handleAdd">
          <el-icon>
            <Plus />
          </el-icon>
          新增班级
        </el-button>
      </div>
    </template>

    <el-table :data="classList" stripe v-loading="loading" border style="width: 100%">
      <el-table-column prop="grade" label="年级" min-width="150" />
      <el-table-column prop="className" label="班级名称" min-width="250" />
      <el-table-column prop="headTeacherName" label="班主任" min-width="180" />
      <el-table-column prop="studentCount" label="学生人数" min-width="150" align="center" />
      <el-table-column prop="status" label="状态" width="80" align="center">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'danger'">
            {{ row.status === 1 ? '启用' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" min-width="200" />
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" @click="handleView(row)">
            查看
          </el-button>
          <el-button link type="primary" @click="handleEdit(row)">
            编辑
          </el-button>
          <el-button link type="danger" @click="handleDelete(row)">
            删除
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
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRouter } from 'vue-router'
import { Search, Refresh, Plus } from '@element-plus/icons-vue'
import api from '@/services/api'

const router = useRouter()

const loading = ref(false)
const classList = ref([])

const searchForm = reactive({
  grade: '',
  className: ''
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
    className: ''
  })
  handleSearch()
}

const handleAdd = () => {
  router.push('/academic/classes/add')
}

const handleView = (row) => {
  router.push(`/academic/classes/${row.classId}`)
}

const handleEdit = (row) => {
  router.push(`/academic/classes/${row.classId}/edit`)
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该班级吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const response = await api.classes.delete(row.classId)
    if (response.code === 200) {
      ElMessage.success('删除成功')
      fetchClasses()
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
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
