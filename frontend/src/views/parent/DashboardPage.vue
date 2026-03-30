<template>
  <el-card class="dashboard-card">
    <template #header>
      <div class="card-header">
        <span>控制台</span>
        <el-button link @click="refreshData" :loading="loading">
          <el-icon>
            <Refresh />
          </el-icon>
          刷新
        </el-button>
      </div>
    </template>

    <el-row :gutter="20" class="stats-row">
      <el-col :xs="24" :sm="12" :lg="6" class="mb-20">
        <div class="stat-card primary">
          <el-icon class="stat-icon">
            <ChatDotRound />
          </el-icon>
          <div class="stat-content">
            <div class="stat-title">未读消息</div>
            <div class="stat-value">{{ stats.unreadMessages }}</div>
            <div class="stat-trend">
              <span class="trend-text">近期成绩</span>
              <span class="trend-value">{{ stats.recentGrades }}</span>
            </div>
          </div>
            </div>
          </el-col>

          <el-col :xs="24" :sm="12" :lg="6" class="mb-20">
            <div class="stat-card success">
              <el-icon class="stat-icon">
                <Document />
              </el-icon>
              <div class="stat-content">
                <div class="stat-title">近期成绩</div>
                <div class="stat-value">{{ stats.recentGrades }}</div>
                <div class="stat-trend">
                  <span class="trend-text">出勤率</span>
                  <span class="trend-value">{{ stats.attendanceRate }}</span>
                </div>
              </div>
            </div>
          </el-col>

          <el-col :xs="24" :sm="12" :lg="6" class="mb-20">
            <div class="stat-card warning">
              <el-icon class="stat-icon">
                <Calendar />
              </el-icon>
              <div class="stat-content">
                <div class="stat-title">出勤率</div>
                <div class="stat-value">{{ stats.attendanceRate }}</div>
                <div class="stat-trend">
                  <span class="trend-text">子女状态</span>
                  <span class="trend-value">{{ stats.childStatus }}</span>
                </div>
              </div>
            </div>
          </el-col>

          <el-col :xs="24" :sm="12" :lg="6" class="mb-20">
            <div class="stat-card info">
              <el-icon class="stat-icon">
                <User />
              </el-icon>
              <div class="stat-content">
                <div class="stat-title">子女状态</div>
                <div class="stat-value">{{ stats.childStatus }}</div>
                <div class="stat-trend">
                  <span class="trend-text">未读消息</span>
                  <span class="trend-value">{{ stats.unreadMessages }}</span>
                </div>
              </div>
            </div>
          </el-col>
        </el-row>

        <el-row :gutter="20" class="charts-row">
          <el-col :xs="24" :lg="12" class="mb-20">
            <el-card class="chart-card">
              <template #header>
                <div class="chart-header">
                  <span>子女信息</span>
                  <el-button link type="primary" @click="goToChildProfile">
                    查看详情
                  </el-button>
                </div>
              </template>
              <div class="child-info" v-if="childInfo.name">
                <div class="info-item">
                  <span class="label">姓名：</span>
                  <span class="value">{{ childInfo.name }}</span>
                </div>
                <div class="info-item">
                  <span class="label">学籍号：</span>
                  <span class="value">{{ childInfo.studentNo }}</span>
                </div>
                <div class="info-item">
                  <span class="label">班级：</span>
                  <span class="value">{{ childInfo.className }}</span>
                </div>
                <div class="info-item">
                  <span class="label">状态：</span>
                  <el-tag :type="childInfo.status === '在读' ? 'success' : 'warning'">
                    {{ childInfo.status }}
                  </el-tag>
                </div>
              </div>
              <el-empty v-else description="暂无子女信息" />
            </el-card>
          </el-col>

          <el-col :xs="24" :lg="12" class="mb-20">
            <el-card class="chart-card">
              <template #header>
                <div class="chart-header">
                  <span>未读消息</span>
                  <el-button link type="primary" @click="goToMessages">
                    查看全部
                  </el-button>
                </div>
              </template>
              <el-timeline>
                <el-timeline-item
                  v-for="item in messageList"
                  :key="item.id"
                  :timestamp="item.time"
                  :type="item.type"
                >
                  {{ item.content }}
                </el-timeline-item>
              </el-timeline>
            </el-card>
          </el-col>
        </el-row>
    </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { User, Document, ChatDotRound, Calendar, Refresh } from '@element-plus/icons-vue'
import api from '@/services/api'

const router = useRouter()

const stats = ref({
  unreadMessages: 0,
  recentGrades: 0,
  attendanceRate: '0%',
  childStatus: '在读'
})

const loading = ref(false)
const childInfo = ref({})
const recentGrades = ref([])
const recentAttendance = ref([])
const messageList = ref([])

const refreshData = async () => {
  await fetchDashboardData()
}

const fetchDashboardData = async () => {
  try {
    loading.value = true
    const response = await api.dashboard.getParentStats()
    if (response.code === 200) {
      const data = response.data
      stats.value = {
        unreadMessages: data.unreadMessages || 0,
        recentGrades: data.recentGrades || 0,
        attendanceRate: data.attendanceRate || '0%',
        childStatus: childInfo.value.status || '在读'
      }

      if (data.childInfo) {
        childInfo.value = data.childInfo
      }

      if (data.recentGrades) {
        recentGrades.value = data.recentGrades
      }

      if (data.recentAttendance) {
        recentAttendance.value = data.recentAttendance
      }

      if (data.messageList) {
        messageList.value = data.messageList
      }
    }
  } catch (error) {
    console.error('获取数据失败', error)
  } finally {
    loading.value = false
  }
}

const goToChildProfile = () => {
  router.push('/parent/child-changes')
}

const goToMessages = () => {
  router.push('/parent/messages')
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

.chart-card {
  border-radius: 4px;
  border: 1px solid #ebeef5;
  min-height: 350px;
  display: flex;
  flex-direction: column;
}

.chart-card:hover {
  border-color: #c6e2ff;
}

.chart-card :deep(.el-card__body) {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 12px 0;
}

.chart-header {
  font-weight: 600;
  font-size: 16px;
  color: #303133;
  flex-shrink: 0;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.child-info {
  padding: 20px;
  width: 100%;
}

.info-item {
  margin-bottom: 15px;
  display: flex;
  align-items: center;
}

.info-item .label {
  font-weight: 600;
  color: #303133;
  margin-right: 10px;
  width: 80px;
}

.info-item .value {
  color: #606266;
}

.stats-row {
  margin-bottom: 0;
}

.charts-row {
  margin-top: 0;
}

@media (max-width: 1200px) {
  .stats-row .el-col {
    margin-bottom: 0px;
  }

  .charts-row .el-col {
    margin-bottom: 0px;
  }
}

@media (max-width: 768px) {
  .stats-row .el-col {
    width: 100%;
  }

  .charts-row .el-col {
    width: 100%;
  }
}
</style>
