<template>
  <el-card class="dashboard-card">
    <template #header>
      <div class="card-header">
        <span>控制台</span>
        <el-button link @click="refreshData" :loading="loading">
          <el-icon><Refresh /></el-icon>
          刷新
        </el-button>
      </div>
    </template>

    <el-row :gutter="20" class="stats-row">
      <el-col :xs="24" :sm="12" :lg="6" class="mb-20">
        <div class="stat-card primary">
          <div class="stat-content">
            <div class="stat-title">学生总数</div>
            <div class="stat-value">{{ stats[0].value }}</div>
            <div class="stat-trend">
              <span class="trend-text">在读</span>
              <span class="trend-value">{{ stats[3].value }}</span>
            </div>
          </div>
        </div>
      </el-col>

      <el-col :xs="24" :sm="12" :lg="6" class="mb-20">
        <div class="stat-card success">
            <div class="stat-content">
              <div class="stat-title">班级总数</div>
              <div class="stat-value">{{ stats[1].value }}</div>
              <div class="stat-trend">
                <span class="trend-text">学生</span>
                <span class="trend-value">{{ stats[0].value }}</span>
              </div>
            </div>
          </div>
        </el-col>

        <el-col :xs="24" :sm="12" :lg="6" class="mb-20">
          <div class="stat-card warning">
            <div class="stat-content">
              <div class="stat-title">待审核变动</div>
              <div class="stat-value">{{ stats[2].value }}</div>
              <div class="stat-trend">
                <span class="trend-text">本月</span>
                <span class="trend-value">{{ stats[2].value }}</span>
              </div>
            </div>
          </div>
        </el-col>

        <el-col :xs="24" :sm="12" :lg="6" class="mb-20">
          <div class="stat-card info">
            <div class="stat-content">
              <div class="stat-title">在读学生</div>
              <div class="stat-value">{{ stats[3].value }}</div>
              <div class="stat-trend">
                <span class="trend-text">班级</span>
                <span class="trend-value">{{ stats[1].value }}</span>
              </div>
            </div>
          </div>
        </el-col>
      </el-row>
    </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Refresh } from '@element-plus/icons-vue'
import api from '@/services/api'

const loading = ref(false)

const stats = ref([
  { key: 'totalStudents', label: '学生总数', value: 0, type: 'primary' },
  { key: 'totalClasses', label: '班级总数', value: 0, type: 'success' },
  { key: 'pendingChanges', label: '待审核变动', value: 0, type: 'warning' },
  { key: 'readingStudents', label: '在读学生', value: 0, type: 'info' }
])

const refreshData = async () => {
  await fetchDashboardData()
}

const fetchDashboardData = async () => {
  try {
    loading.value = true
    const response = await api.dashboard.getAcademicStats()
    if (response.code === 200) {
      const data = response.data
      stats.value[0].value = data.totalStudents || 0
      stats.value[1].value = data.totalClasses || 0
      stats.value[2].value = data.pendingChanges || 0
      stats.value[3].value = data.readingStudents || 0
    }
  } catch (error) {
    console.error('获取数据失败', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchDashboardData()
})
</script>

<style scoped>
.dashboard-card {
  margin-bottom: 0px;
  border-radius: 4px;
  border: 1px solid #ebeef5;
}

.dashboard-card :deep(.el-card__body) {
  padding: 12px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 600;
  font-size: 16px;
  color: #303133;
}

.stat-card {
  height: 100%;
  display: flex;
  align-items: center;
  border: 1px solid #ebeef5;
  border-radius: 2px;
  background-color: #fff;
  padding: 12px;
}

.stat-card:hover {
  border-color: #c6e2ff;
}

.stat-card.primary {
  border-left: 4px solid #409eff;
}

.stat-card.success {
  border-left: 4px solid #67c23a;
}

.stat-card.warning {
  border-left: 4px solid #e6a23c;
}

.stat-card.info {
  border-left: 4px solid #909399;
}

.mb-20 {
  margin-bottom: 20px;
}

.stat-icon {
  margin-right: 12px;
  width: 40px;
  height: 40px;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #409eff;
  flex-shrink: 0;
  color: #fff;
  font-size: 24px;
}

.stat-content {
  flex: 1;
}

.stat-title {
  font-size: 13px;
  color: #909399;
  margin-bottom: 6px;
  font-weight: 500;
}

.stat-value {
  font-size: 20px;
  font-weight: 600;
  color: #303133;
  line-height: 1.2;
  margin-bottom: 6px;
}

.stat-trend {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 11px;
}

.trend-text {
  color: #909399;
}

.trend-value {
  font-weight: 500;
}

.stats-row {
  margin-bottom: 0;
}

@media (max-width: 1200px) {
  .stats-row .el-col {
    margin-bottom: 0px;
  }
}

@media (max-width: 768px) {
  .stats-row .el-col {
    width: 100%;
  }
}
</style>
