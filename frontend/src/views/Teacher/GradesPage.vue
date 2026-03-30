<template>
  <el-card class="search-card">
    <el-form :inline="true" :model="searchForm" class="search-form">
      <el-form-item label="班级">
        <el-select v-model="searchForm.classId" placeholder="请选择班级" clearable filterable>
          <el-option v-for="clazz in classList" :key="clazz.classId" :label="clazz.className" :value="clazz.classId" />
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
      <el-form-item label="学期">
        <el-select v-model="searchForm.semester" placeholder="请选择学期" clearable>
          <el-option label="2024-2025第一学期" value="2024-1" />
          <el-option label="2024-2025第二学期" value="2024-2" />
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
        <el-button type="success" @click="handleAdd">
          <el-icon>
            <Plus />
          </el-icon>
          录入成绩
        </el-button>
      </el-form-item>
    </el-form>
  </el-card>

  <el-card class="table-card">
      <template #header>
        <div class="card-header">
          <span>成绩录入</span>
        </div>
      </template>

      <el-table :data="gradeList" stripe v-loading="loading" border style="width: 100%">
        <el-table-column prop="studentName" label="学生姓名" min-width="120" />
        <el-table-column prop="studentNo" label="学籍号" min-width="150" />
        <el-table-column prop="className" label="班级" min-width="150" />
        <el-table-column prop="subject" label="科目" min-width="100" />
        <el-table-column prop="examName" label="考试名称" min-width="180" />
        <el-table-column prop="score" label="分数" width="80" align="center">
          <template #default="{ row }">
            <el-input-number v-model="row.score" :min="0" :max="100" :precision="1" size="small" />
          </template>
        </el-table-column>
        <el-table-column prop="examDate" label="考试日期" min-width="150" />
        <el-table-column prop="remark" label="备注" min-width="250">
          <template #default="{ row }">
            <el-input v-model="row.remark" placeholder="备注" size="small" />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleSave(row)">
              保存
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
import { Search, Refresh, Plus } from '@element-plus/icons-vue'
import api from '@/services/api'

const loading = ref(false)
const gradeList = ref([])
const classList = ref([])

const searchForm = reactive({
  classId: '',
  subject: '',
  semester: ''
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

const fetchGrades = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.page,
      size: pagination.size,
      ...searchForm
    }
    const response = await api.grades.list(params)
    if (response.code === 200) {
      gradeList.value = response.data.records || []
      pagination.total = response.data.total || 0
    } else {
      gradeList.value = []
      pagination.total = 0
    }
  } catch (error) {
    console.error('获取成绩列表失败', error)
    gradeList.value = []
    pagination.total = 0
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.page = 1
  fetchGrades()
}

const handleReset = () => {
  Object.assign(searchForm, {
    classId: '',
    subject: '',
    semester: ''
  })
  handleSearch()
}

const handleAdd = () => {
  ElMessage.info('批量录入成绩功能开发中')
}

const handleSave = async (row) => {
  try {
    const response = await api.grades.update(row.id, {
      score: row.score,
      remark: row.remark
    })
    if (response.code === 200) {
      ElMessage.success('保存成功')
    }
  } catch (error) {
    ElMessage.error('保存失败')
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该成绩记录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const response = await api.grades.delete(row.id)
    if (response.code === 200) {
      ElMessage.success('删除成功')
      fetchGrades()
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const handleSizeChange = (val) => {
  pagination.size = val
  fetchGrades()
}

const handlePageChange = (val) => {
  pagination.page = val
  fetchGrades()
}

onMounted(() => {
  fetchClasses()
  fetchGrades()
})
</script>

<style scoped>
.search-card {
  margin-bottom: 20px;
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
