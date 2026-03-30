<template>
  <el-card class="search-card">
    <el-form :inline="true" :model="searchForm" class="search-form">
      <el-form-item label="姓名">
        <el-input v-model="searchForm.name" placeholder="请输入姓名" clearable />
      </el-form-item>
      <el-form-item label="学籍号">
        <el-input v-model="searchForm.studentNo" placeholder="请输入学籍号" clearable />
      </el-form-item>
      <el-form-item label="年级">
        <el-select v-model="searchForm.grade" placeholder="请选择年级" clearable>
          <el-option label="初一" value="初一" />
          <el-option label="初二" value="初二" />
          <el-option label="初三" value="初三" />
        </el-select>
      </el-form-item>
      <el-form-item label="班级">
        <el-select v-model="searchForm.classId" placeholder="请选择班级" clearable filterable>
          <el-option v-for="clazz in classList" :key="clazz.classId" :label="clazz.className" :value="clazz.classId" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
          <el-option label="在读" value="在读" />
          <el-option label="休学" value="休学" />
          <el-option label="毕业" value="毕业" />
          <el-option label="转出" value="转出" />
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
        <el-button type="success" @click="handleImport">
          <el-icon>
            <Upload />
          </el-icon>
          批量导入
        </el-button>
        <el-button type="warning" @click="handleExport">
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
        <span>学生列表</span>
        <el-button type="primary" @click="handleAdd">
          <el-icon>
            <Plus />
          </el-icon>
          新增学生
        </el-button>
      </div>
    </template>

    <el-table :data="studentList" stripe v-loading="loading" border style="width: 100%">
      <el-table-column prop="studentNo" label="学籍号" min-width="200" />
      <el-table-column prop="name" label="姓名" min-width="150" />
      <el-table-column prop="gender" label="性别" width="60" align="center">
        <template #default="{ row }">
          {{ row.gender === 'M' ? '男' : '女' }}
        </template>
      </el-table-column>
      <el-table-column prop="grade" label="年级" min-width="120" align="center" />
      <el-table-column prop="className" label="班级" min-width="200" />
      <el-table-column prop="phone" label="联系电话" min-width="180" />
      <el-table-column prop="status" label="状态" width="80" align="center">
        <template #default="{ row }">
          <el-tag :type="getStatusType(row.status)">
            {{ row.status }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="入学时间" min-width="200" />
      <el-table-column label="操作" width="250" fixed="right">
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

  <el-dialog v-model="importDialogVisible" title="批量导入学生" width="500px">
    <el-upload
      ref="uploadRef"
      class="upload-demo"
      drag
      action="/api/v1/students/import"
      :auto-upload="false"
      :on-change="handleFileChange"
      :limit="1"
      accept=".xlsx,.xls"
    >
      <el-icon class="el-icon--upload"><upload-filled /></el-icon>
      <div class="el-upload__text">
        将文件拖到此处，或<em>点击上传</em>
      </div>
      <template #tip>
        <div class="el-upload__tip">
          只能上传 xlsx/xls 文件，且不超过 5MB
        </div>
      </template>
    </el-upload>
    <template #footer>
      <el-button @click="importDialogVisible = false">取消</el-button>
      <el-button type="primary" @click="handleImportSubmit" :loading="importLoading">
        确定导入
      </el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRouter } from 'vue-router'
import { Search, Refresh, Plus, Upload, Download, UploadFilled } from '@element-plus/icons-vue'
import api from '@/services/api'

const router = useRouter()

const loading = ref(false)
const importLoading = ref(false)
const importDialogVisible = ref(false)
const studentList = ref([])
const classList = ref([])
const uploadRef = ref(null)
const fileList = ref([])

const searchForm = reactive({
  name: '',
  studentNo: '',
  grade: '',
  classId: '',
  status: ''
})

const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

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

const fetchClasses = async () => {
  try {
    const response = await api.classes.listAll()
    if (response.code === 200) {
      classList.value = response.data
    }
  } catch (error) {
    console.error('获取班级列表失败', error)
  }
}

const handleSearch = () => {
  pagination.page = 1
  fetchStudents()
}

const handleReset = () => {
  Object.assign(searchForm, {
    name: '',
    studentNo: '',
    grade: '',
    classId: '',
    status: ''
  })
  handleSearch()
}

const handleAdd = () => {
  router.push('/academic/students/add')
}

const handleView = (row) => {
  router.push(`/academic/students/${row.studentId}`)
}

const handleEdit = (row) => {
  router.push(`/academic/students/${row.studentId}/edit`)
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该学生吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const response = await api.students.delete(row.studentId)
    if (response.code === 200) {
      ElMessage.success('删除成功')
      fetchStudents()
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const handleImport = () => {
  importDialogVisible.value = true
}

const handleFileChange = (file) => {
  fileList.value = [file]
}

const handleImportSubmit = async () => {
  if (fileList.value.length === 0) {
    ElMessage.warning('请选择要导入的文件')
    return
  }
  
  importLoading.value = true
  try {
    const formData = new FormData()
    formData.append('file', fileList.value[0].raw)
    
    await api.students.import(formData)
    ElMessage.success('导入成功')
    importDialogVisible.value = false
    fileList.value = []
    fetchStudents()
  } catch (error) {
    ElMessage.error('导入失败')
  } finally {
    importLoading.value = false
  }
}

const handleExport = async () => {
  try {
    const params = { ...searchForm }
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

:deep(.search-card .el-card__body) {
  padding: 20px 20px 30px 20px !important;
}

:deep(.search-form) {
  margin-bottom: 0;
  display: flex;
  flex-wrap: wrap;
  row-gap: 25px;
}

:deep(.search-form .el-form-item) {
  margin-bottom: 0 !important;
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

.upload-demo {
  text-align: center;
}

.el-icon--upload {
  font-size: 67px;
  color: #409eff;
  margin: 40px 0 16px;
}

.el-upload__text {
  font-size: 14px;
  color: #606266;
}

.el-upload__tip {
  font-size: 12px;
  color: #909399;
  margin-top: 7px;
}
</style>
