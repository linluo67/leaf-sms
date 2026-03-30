<template>
  <el-row :gutter="20" class="stats-row">
    <el-col :xs="24" :lg="24" class="mb-20">
      <el-card class="report-card" shadow="hover">
        <template #header>
          <div class="card-header">
            <span>学籍变动统计</span>
            <el-button link type="primary" @click="exportChangeReport">
              <el-icon><Download /></el-icon>
              导出
            </el-button>
          </div>
        </template>
        <div class="change-row">
          <div v-for="change in changeStats" :key="change.type" class="change-item">
            <div class="change-icon">
              <el-icon :size="24">
                <component :is="change.icon" />
              </el-icon>
            </div>
            <div class="change-info">
              <div class="change-value">{{ change.count }}</div>
              <div class="change-label">{{ change.type }}</div>
            </div>
          </div>
        </div>
      </el-card>
    </el-col>
  </el-row>

    <el-row :gutter="20" class="charts-row">
      <el-col :xs="24" :sm="12" :lg="12" class="mb-20">
        <el-card class="report-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span>在校生统计</span>
              <el-button link type="primary" @click="exportStudentReport">
                <el-icon><Download /></el-icon>
                导出
              </el-button>
            </div>
          </template>
          <div class="stat-grid">
            <div class="stat-item">
              <div class="stat-value">{{ studentStats.total }}</div>
              <div class="stat-label">学生总数</div>
            </div>
            <div class="stat-item">
              <div class="stat-value primary">{{ studentStats.reading }}</div>
              <div class="stat-label">在读</div>
            </div>
            <div class="stat-item">
              <div class="stat-value warning">{{ studentStats.suspension }}</div>
              <div class="stat-label">休学</div>
            </div>
            <div class="stat-item">
              <div class="stat-value info">{{ studentStats.graduated }}</div>
              <div class="stat-label">毕业</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :xs="24" :sm="12" :lg="12" class="mb-20">
        <el-card class="report-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span>年级分布</span>
              <el-button link type="primary" @click="exportGradeReport">
                <el-icon><Download /></el-icon>
                导出
              </el-button>
            </div>
          </template>
          <div class="grade-chart">
            <v-chart :option="gradeChartOption" autoresize />
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="charts-row">
      <el-col :xs="24" :lg="12" class="mb-20">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>班级统计</span>
              <el-button link type="primary" @click="exportClassReport">
                <el-icon><Download /></el-icon>
                导出
              </el-button>
            </div>
          </template>
          <el-table :data="classStats" stripe border style="width: 100%">
            <el-table-column prop="grade" label="年级" min-width="100" />
            <el-table-column prop="className" label="班级" min-width="150" />
            <el-table-column prop="studentCount" label="学生数" min-width="100" align="center" />
            <el-table-column prop="headTeacherName" label="班主任" min-width="120" />
          </el-table>
        </el-card>
      </el-col>

      <el-col :xs="24" :lg="12" class="mb-20">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>近期变动趋势</span>
              <el-select v-model="trendPeriod" @change="fetchTrendData" style="width: 120px">
                <el-option label="最近7天" value="7" />
                <el-option label="最近30天" value="30" />
                <el-option label="最近90天" value="90" />
              </el-select>
            </div>
          </template>
          <div class="trend-chart">
            <div v-for="(item, index) in trendData" :key="index" class="trend-item">
              <div class="trend-date">{{ item.date }}</div>
              <div class="trend-bar">
                <div class="bar-item" :style="{ height: item.inCount * 10 + 'px', background: '#67c23a' }"></div>
                <div class="bar-item" :style="{ height: item.outCount * 10 + 'px', background: '#e6a23c' }"></div>
              </div>
              <div class="trend-legend">
                <span class="legend-item"><span class="legend-dot" style="background: #67c23a"></span>转入</span>
                <span class="legend-item"><span class="legend-dot" style="background: #e6a23c"></span>转出</span>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Download, User, School, Document } from '@element-plus/icons-vue'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { PieChart } from 'echarts/charts'
import {
  TitleComponent,
  TooltipComponent,
  LegendComponent
} from 'echarts/components'
import VChart from 'vue-echarts'
import api from '@/services/api'

use([
  CanvasRenderer,
  PieChart,
  TitleComponent,
  TooltipComponent,
  LegendComponent
])

const trendPeriod = ref('7')

const studentStats = ref({
  total: 0,
  reading: 0,
  suspension: 0,
  graduated: 0
})

const gradeStats = ref([
  { name: '初一', count: 0, percentage: 0, color: '#409eff' },
  { name: '初二', count: 0, percentage: 0, color: '#67c23a' },
  { name: '初三', count: 0, percentage: 0, color: '#e6a23c' }
])

const gradeChartOption = ref({
  tooltip: {
    trigger: 'item',
    formatter: '{a} <br/>{b}: {c}人 ({d}%)'
  },
  legend: {
    orient: 'horizontal',
    bottom: '0%',
    left: 'center'
  },
  series: [
    {
      name: '年级分布',
      type: 'pie',
      radius: ['40%', '70%'],
      avoidLabelOverlap: false,
      itemStyle: {
        borderRadius: 10,
        borderColor: '#fff',
        borderWidth: 2
      },
      label: {
        show: false,
        position: 'center'
      },
      emphasis: {
        label: {
          show: true,
          fontSize: 20,
          fontWeight: 'bold'
        }
      },
      labelLine: {
        show: false
      },
      data: []
    }
  ]
})

const changeStats = ref([
  { type: '转入', count: 0, icon: User },
  { type: '转出', count: 0, icon: School },
  { type: '休学', count: 0, icon: Document },
  { type: '复学', count: 0, icon: User },
  { type: '毕业', count: 0, icon: School }
])

const classStats = ref([])
const trendData = ref([])

const fetchReportData = async () => {
  try {
    const response = await api.changes.getStatistics()
    if (response.code === 200) {
      const data = response.data
      studentStats.value = data.studentStats || studentStats.value
      
      if (data.gradeStats) {
        const total = data.gradeStats.reduce((sum, g) => sum + g.count, 0)
        gradeStats.value = data.gradeStats.map(g => ({
          ...g,
          percentage: total > 0 ? Math.round((g.count / total) * 100) : 0
        }))
        
        gradeChartOption.value.series[0].data = data.gradeStats.map(g => ({
          name: g.name,
          value: g.count,
          itemStyle: {
            color: g.color
          }
        }))
      }
      
      if (data.changeStats) {
        changeStats.value = changeStats.value.map(item => ({
          ...item,
          count: data.changeStats[item.type] || 0
        }))
      }
      
      if (data.classStats) {
        classStats.value = data.classStats
      }
    }
  } catch (error) {
    ElMessage.error('获取统计数据失败')
  }
}

const fetchTrendData = async () => {
  try {
    const response = await api.changes.getTrend(trendPeriod.value)
    if (response.code === 200) {
      trendData.value = response.data || []
    }
  } catch (error) {
    console.error('获取趋势数据失败', error)
  }
}

const exportStudentReport = async () => {
  try {
    await api.students.export({ type: 'student' })
    ElMessage.success('导出成功')
  } catch (error) {
    ElMessage.error('导出失败')
  }
}

const exportGradeReport = async () => {
  try {
    await api.students.export({ type: 'grade' })
    ElMessage.success('导出成功')
  } catch (error) {
    ElMessage.error('导出失败')
  }
}

const exportChangeReport = async () => {
  try {
    await api.changes.export({ type: 'change' })
    ElMessage.success('导出成功')
  } catch (error) {
    ElMessage.error('导出失败')
  }
}

const exportClassReport = async () => {
  try {
    await api.classes.export({ type: 'class' })
    ElMessage.success('导出成功')
  } catch (error) {
    ElMessage.error('导出失败')
  }
}

onMounted(() => {
  fetchReportData()
  fetchTrendData()
})
</script>

<style scoped>
.stats-row,
.charts-row {
  margin-bottom: 0;
}

.mb-20 {
  margin-bottom: 20px;
}

.report-card {
  margin-bottom: 0;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.report-card :deep(.el-card__body) {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 600;
  font-size: 16px;
  color: #303133;
}

.stat-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.stat-item {
  text-align: center;
  padding: 20px 15px;
  background: #f8f9fa;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.stat-item:hover {
  background: #e8f4ff;
  transform: translateY(-2px);
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: #303133;
  margin-bottom: 8px;
  line-height: 1;
}

.stat-value.primary {
  color: #409eff;
}

.stat-value.warning {
  color: #e6a23c;
}

.stat-value.info {
  color: #909399;
}

.stat-label {
  font-size: 14px;
  color: #606266;
  font-weight: 500;
}

.grade-chart {
  padding: 20px 0;
  height: 300px;
}

.grade-chart :deep(.echarts) {
  width: 100% !important;
  height: 100% !important;
}

.change-row {
  display: flex;
  flex-direction: row;
  gap: 20px;
  justify-content: space-between;
}

.change-item {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
  transition: all 0.3s ease;
  min-width: 0;
}

.change-item:hover {
  background: #e8f4ff;
  transform: translateY(-2px);
}

.change-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 56px;
  height: 56px;
  border-radius: 50%;
  background: #667eea;
  color: #fff;
  flex-shrink: 0;
}

.change-info {
  flex: 1;
}

.change-value {
  font-size: 28px;
  font-weight: 700;
  color: #303133;
  margin-bottom: 6px;
  line-height: 1;
}

.change-label {
  font-size: 14px;
  color: #606266;
  font-weight: 500;
}

.trend-chart {
  padding: 20px 0;
  max-height: 400px;
  overflow-y: auto;
}

.trend-item {
  margin-bottom: 24px;
}

.trend-item:last-child {
  margin-bottom: 0;
}

.trend-date {
  font-size: 13px;
  color: #909399;
  margin-bottom: 10px;
  font-weight: 500;
}

.trend-bar {
  display: flex;
  align-items: flex-end;
  gap: 12px;
  height: 120px;
  padding: 12px;
  background: #f8f9fa;
  border-radius: 6px;
}

.bar-item {
  flex: 1;
  min-width: 24px;
  border-radius: 4px 4px 0 0;
  transition: height 0.3s ease;
}

.trend-legend {
  display: flex;
  gap: 24px;
  font-size: 13px;
  color: #909399;
  margin-top: 8px;
  font-weight: 500;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 6px;
}

.legend-dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
}

@media (max-width: 768px) {
  .change-row {
    flex-direction: column;
  }

  .change-item {
    min-width: auto;
  }

  .stat-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 12px;
  }

  .stat-item {
    padding: 15px 10px;
  }

  .stat-value {
    font-size: 24px;
  }

  .change-icon {
    width: 48px;
    height: 48px;
  }

  .change-value {
    font-size: 24px;
  }

  .grade-chart {
    height: 250px;
  }
}
</style>
