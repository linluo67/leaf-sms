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
            <div class="stat-value">{{ stats.totalStudents }}</div>
            <div class="stat-trend">
              <span class="trend-text">在读</span>
              <span class="trend-value">{{ stats.totalStudents }}</span>
            </div>
          </div>
        </div>
      </el-col>

      <el-col :xs="24" :sm="12" :lg="6" class="mb-20">
        <div class="stat-card success">
            <div class="stat-content">
              <div class="stat-title">班级总数</div>
              <div class="stat-value">{{ stats.totalClasses }}</div>
              <div class="stat-trend">
                <span class="trend-text">学生</span>
                <span class="trend-value">{{ stats.totalStudents }}</span>
              </div>
            </div>
          </div>
        </el-col>

        <el-col :xs="24" :sm="12" :lg="6" class="mb-20">
          <div class="stat-card warning">
            <div class="stat-content">
              <div class="stat-title">待审核变动</div>
              <div class="stat-value">{{ stats.totalChanges }}</div>
              <div class="stat-trend">
                <span class="trend-text">本月</span>
                <span class="trend-value">{{ stats.totalChanges }}</span>
              </div>
            </div>
          </div>
        </el-col>

        <el-col :xs="24" :sm="12" :lg="6" class="mb-20">
          <div class="stat-card info">
            <div class="stat-content">
              <div class="stat-title">用户总数</div>
              <div class="stat-value">{{ stats.totalUsers }}</div>
              <div class="stat-trend">
                <span class="trend-text">活跃</span>
                <span class="trend-value">{{ stats.totalUsers }}</span>
              </div>
            </div>
          </div>
        </el-col>
      </el-row>

      <el-row :gutter="20" class="charts-row">
        <el-col :xs="24" :lg="12" class="mb-20">
          <el-card class="chart-card">
            <template #header><span>年级分布</span></template>
            <el-empty v-if="gradeDistribution.length === 0" description="暂无数据" :image-size="80" />
            <v-chart v-else :option="pieChartOption" style="height: 300px;" />
          </el-card>
        </el-col>

        <el-col :xs="24" :lg="12" class="mb-20">
          <el-card class="chart-card">
            <template #header><span>学籍变动统计</span></template>
            <el-empty v-if="changeTypeDistribution.length === 0" description="暂无数据" :image-size="80" />
            <v-chart v-else :option="barChartOption" style="height: 300px;" />
          </el-card>
        </el-col>
      </el-row>
    </el-card>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { PieChart, BarChart } from 'echarts/charts'
import {
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent
} from 'echarts/components'
import VChart from 'vue-echarts'
import { Refresh } from '@element-plus/icons-vue'
import api from '../../services/api'

use([
  CanvasRenderer,
  PieChart,
  BarChart,
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent
])

const stats = ref({
  totalStudents: 0,
  totalClasses: 0,
  totalUsers: 0,
  totalChanges: 0
})

const loading = ref(false)

const gradeDistribution = ref([])
const changeTypeDistribution = ref([])

const pieChartOption = computed(() => {
  return {
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b}: {c}人 ({d}%)'
    },
    legend: {
      show: false
    },
    series: [
      {
        name: '年级',
        type: 'pie',
        radius: ['50%', '80%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 10,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: {
          show: true,
          formatter: '{b}\n{c}人 ({d}%)',
          fontSize: 10,
          fontWeight: 'bold'
        },
        emphasis: {
          label: {
            show: true,
            fontSize: 12,
            fontWeight: 'bold'
          }
        },
        labelLine: {
          show: true,
          length: 10,
          length2: 5
        },
        data: gradeDistribution.value.map((item, index) => ({
          value: item.count,
          name: item.name,
          itemStyle: {
            color: getGradeColor(index)
          }
        }))
      }
    ]
  }
})

const barChartOption = computed(() => {
  return {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: changeTypeDistribution.value.map(item => item.name),
      axisLabel: {
        interval: 0,
        rotate: 0
      }
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        name: '变动数量',
        type: 'bar',
        data: changeTypeDistribution.value.map(item => item.count),
        itemStyle: {
          borderRadius: [5, 5, 0, 0],
          color: function(params) {
            const colors = ['#409eff', '#67c23a', '#e6a23c', '#f56c6c', '#909399']
            return colors[params.dataIndex % colors.length]
          }
        },
        emphasis: {
          itemStyle: {
            color: function(params) {
              const colors = ['#66b1ff', '#85ce61', '#f0c78a', '#f78989', '#a6adb5']
              return colors[params.dataIndex % colors.length]
            }
          }
        }
      }
    ]
  }
})

const getGradeColor = (index) => {
  const colors = ['#409eff', '#67c23a', '#e6a23c', '#f56c6c', '#909399']
  return colors[index % colors.length]
}

const refreshData = async () => {
  await loadDashboardData()
}

const loadDashboardData = async () => {
  try {
    loading.value = true

    const response = await api.dashboard.getStats()

    if (response && response.data) {
      const data = response.data

      stats.value = {
        totalStudents: data.totalStudents || 0,
        totalClasses: data.totalClasses || 0,
        totalUsers: data.totalUsers || 0,
        totalChanges: data.totalChanges || 0
      }

      await loadGradeDistribution()
      await loadChangeTypeDistribution()

    } else {
      stats.value = {
        totalStudents: 0,
        totalClasses: 0,
        totalUsers: 0,
        totalChanges: 0
      }

      gradeDistribution.value = []
      changeTypeDistribution.value = []

      ElMessage.warning('仪表盘数据为空，请检查数据配置')
    }

  } catch (error) {
    if (error.response && error.response.status === 401) {
      ElMessage.warning('登录过期，请重新登录')
    } else if (error.code === 'NETWORK_ERROR' || !error.response) {
      ElMessage.error('网络连接失败，请检查网络连接')
    } else if (error.response && error.response.status === 500) {
      ElMessage.error('服务器内部错误，请联系管理员')
    } else {
      ElMessage.error('加载仪表盘数据失败，请稍后重试')
    }

    stats.value = {
      totalStudents: 0,
      totalClasses: 0,
      totalUsers: 0,
      totalChanges: 0
    }

    gradeDistribution.value = []
    changeTypeDistribution.value = []
  } finally {
    loading.value = false
  }
}

const loadGradeDistribution = async () => {
  try {
    const response = await api.students.getStatistics()
    if (response && response.code === 200 && response.data) {
      const data = response.data
      const distribution = data.gradeDistribution || []
      
      gradeDistribution.value = distribution.map(item => ({
        name: item.grade,
        count: item.count
      }))
    } else {
      gradeDistribution.value = []
    }
  } catch (error) {
    gradeDistribution.value = []
    console.error('加载年级分布失败', error)
  }
}

const loadChangeTypeDistribution = async () => {
  try {
    const response = await api.changes.getStatistics()
    if (response && response.code === 200 && response.data) {
      const data = response.data
      const distribution = data.changeTypeStatistics || []
      
      changeTypeDistribution.value = distribution.map(item => ({
        name: item.changeType,
        count: item.count
      }))
    } else {
      changeTypeDistribution.value = []
    }
  } catch (error) {
    changeTypeDistribution.value = []
    console.error('加载变动统计失败', error)
  }
}

onMounted(() => {
  loadDashboardData()
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

  .pie-chart-container {
    height: 250px;
  }
}
</style>
