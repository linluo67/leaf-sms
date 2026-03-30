<template>
  <el-card class="search-card">
    <el-form :model="searchForm" class="search-form">
      <el-row :gutter="10">
        <el-col :span="4">
          <el-form-item label="用户名">
            <el-input v-model="searchForm.username" placeholder="请输入用户名" clearable />
          </el-form-item>
        </el-col>
        <el-col :span="4">
          <el-form-item label="真实姓名">
            <el-input v-model="searchForm.realName" placeholder="请输入真实姓名" clearable />
          </el-form-item>
        </el-col>
        <el-col :span="4">
          <el-form-item label="角色">
            <el-select v-model="searchForm.roleCode" placeholder="请选择角色" clearable style="width: 100%">
              <el-option label="系统管理员" value="ADMIN" />
              <el-option label="教务管理员" value="ACADEMIC" />
              <el-option label="班主任" value="HEADTEACHER" />
              <el-option label="任课教师" value="TEACHER" />
              <el-option label="家长" value="PARENT" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="4">
          <el-form-item label="状态">
            <el-select v-model="searchForm.status" placeholder="请选择状态" clearable style="width: 100%">
              <el-option label="启用" value="1" />
              <el-option label="禁用" value="0" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item class="button-group">
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
        </el-col>
      </el-row>
    </el-form>
  </el-card>

  <el-card class="table-card">
    <template #header>
      <div class="card-header">
        <span>用户列表</span>
        <el-button type="primary" @click="handleAdd">
          <el-icon>
            <Plus />
          </el-icon>
          新增用户
        </el-button>
      </div>
    </template>

    <el-table :data="userList" stripe v-loading="loading" border style="width: 100%">
      <el-table-column prop="username" label="用户名" min-width="180" />
      <el-table-column prop="realName" label="真实姓名" min-width="150" />
      <el-table-column prop="roleCode" label="角色" min-width="150">
        <template #default="{ row }">
          <el-tag :type="getRoleType(row.roleCode)">
            {{ getRoleName(row.roleCode) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="phone" label="联系电话" min-width="180" />
      <el-table-column prop="email" label="邮箱" min-width="220" />
      <el-table-column prop="status" label="状态" width="80" align="center">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'danger'">
            {{ row.status === 1 ? '启用' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="lastLoginTime" label="最后登录" min-width="200" />
      <el-table-column prop="createTime" label="创建时间" min-width="200" />
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" @click="handleEdit(row)">
            编辑
          </el-button>
          <el-button link type="warning" @click="handleToggleStatus(row)">
            {{ row.status === 1 ? '禁用' : '启用' }}
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
import api from '@/services/api'

const router = useRouter()

const loading = ref(false)
const userList = ref([])

const searchForm = reactive({
  username: '',
  realName: '',
  roleCode: '',
  status: ''
})

const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

const fetchUsers = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.page,
      size: pagination.size,
      ...searchForm
    }
    const response = await api.users.getList(params)
    if (response.code === 200) {
      userList.value = response.data.records || []
      pagination.total = response.data.total || 0
    } else {
      userList.value = []
      pagination.total = 0
    }
  } catch (error) {
    console.error('获取用户列表失败', error)
    ElMessage.error('获取用户列表失败')
    userList.value = []
    pagination.total = 0
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.page = 1
  fetchUsers()
}

const handleReset = () => {
  Object.assign(searchForm, {
    username: '',
    realName: '',
    roleCode: '',
    status: ''
  })
  handleSearch()
}

const handleAdd = () => {
  router.push('/admin/users/add')
}

const handleEdit = (row) => {
  router.push(`/admin/users/${row.userId}/edit`)
}

const handleToggleStatus = async (row) => {
  try {
    const action = row.status === 1 ? '禁用' : '启用'
    await ElMessageBox.confirm(`确定要${action}该用户吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const newStatus = row.status === 1 ? 0 : 1
    const response = await api.users.updateStatus(row.userId, newStatus)
    if (response.code === 200) {
      ElMessage.success(`${action}成功`)
      fetchUsers()
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败')
    }
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该用户吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const response = await api.users.delete(row.userId)
    if (response.code === 200) {
      ElMessage.success('删除成功')
      fetchUsers()
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const handleSizeChange = (val) => {
  pagination.size = val
  fetchUsers()
}

const handlePageChange = (val) => {
  pagination.page = val
  fetchUsers()
}

const getRoleType = (roleCode) => {
  const roleMap = {
    'ADMIN': 'danger',
    'ACADEMIC': 'warning',
    'HEADTEACHER': 'success',
    'TEACHER': 'primary',
    'PARENT': 'info'
  }
  return roleMap[roleCode] || 'info'
}

const getRoleName = (roleCode) => {
  const roleMap = {
    'ADMIN': '系统管理员',
    'ACADEMIC': '教务管理员',
    'HEADTEACHER': '班主任',
    'TEACHER': '任课教师',
    'PARENT': '家长'
  }
  return roleMap[roleCode] || '未知'
}

onMounted(() => {
  fetchUsers()
})
</script>

<style scoped>
:deep(.search-card) {
  margin-bottom: 40px !important;
}

.search-form {
  margin-bottom: 0;
}

.search-form :deep(.el-form-item) {
  margin-bottom: 0;
}

.button-group {
  display: flex;
  gap: 10px;
}

.table-card {
  min-height: 500px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 600;
  font-size: 16px;
  color: #303133;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}
</style>
