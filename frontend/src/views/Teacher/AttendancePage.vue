<template>
  <el-card class="search-card">
    <el-form :inline="true" :model="searchForm" class="search-form">
      <el-form-item label="班级">
        <el-select v-model="searchForm.classId" placeholder="请选择班级" clearable filterable>
          <el-option v-for="clazz in classList" :key="clazz.classId" :label="clazz.className" :value="clazz.classId" />
        </el-select>
      </el-form-item>
      <el-form-item label="日期范围">
        <el-date-picker
          v-model="dateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          value-format="YYYY-MM-DD"
        />
      </el-form-item>
      <el-form-item label="学生姓名">
        <el-input v-model="searchForm.studentName" placeholder="请输入学生姓名" clearable />
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
          <el-option label="正常" value="正常" />
          <el-option label="迟到" value="迟到" />
          <el-option label="早退" value="早退" />
          <el-option label="请假" value="请假" />
          <el-option label="缺勤" value="缺勤" />
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
        <span>考勤记录</span>
      </div>
    </template>

    <el-table :data="attendanceList" stripe v-loading="loading" border style="width: 100%">
      <el-table-column prop="date" label="日期" min-width="150" />
      <el-table-column prop="className" label="班级" min-width="150" />
      <el-table-column prop="studentName" label="学生姓名" min-width="120" />
      <el-table-column prop="studentNo" label="学籍号" min-width="150" />
      <el-table-column prop="status" label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="getStatusType(row.status)">
            {{ row.status }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="remark" label="备注" min-width="250" />
      <el-table-column prop="createTime" label="记录时间" min-width="180" />
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
import { Search, Refresh } from '@element-plus/icons-vue'
import api from '@/services/api'

const loading = ref(false)
const dateRange = ref([])
const attendanceList = ref([])
const classList = ref([])

const searchForm = reactive({
  classId: '',
  studentName: '',
  status: ''
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

const fetchAttendance = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.page,
      size: pagination.size,
      ...searchForm
    }
    if (dateRange.value && dateRange.value.length === 2) {
      params.startDate = dateRange.value[0]
      params.endDate = dateRange.value[1]
    }
    const response = await api.attendance.list(params)
    if (response.code === 200) {
      attendanceList.value = response.data.records || []
      pagination.total = response.data.total || 0
    } else {
      attendanceList.value = []
      pagination.total = 0
    }
  } catch (error) {
    console.error('获取考勤记录失败', error)
    attendanceList.value = []
    pagination.total = 0
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.page = 1
  fetchAttendance()
}

const handleReset = () => {
  dateRange.value = []
  Object.assign(searchForm, {
    classId: '',
    studentName: '',
    status: ''
  })
  handleSearch()
}

const handleSizeChange = (val) => {
  pagination.size = val
  fetchAttendance()
}

const handlePageChange = (val) => {
  pagination.page = val
  fetchAttendance()
}

const getStatusType = (status) => {
  const statusMap = {
    '正常': 'success',
    '迟到': 'warning',
    '早退': 'warning',
    '请假': 'info',
    '缺勤': 'danger'
  }
  return statusMap[status] || 'info'
}

onMounted(() => {
  fetchClasses()
  fetchAttendance()
})
</script>

<style scoped>
.search-card {
  margin-bottom: 20px;
}

.search-form {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  margin-bottom: 0;
}

.search-form :deep(.el-form-item) {
  margin-bottom: 16px;
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
