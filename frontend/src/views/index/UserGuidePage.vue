<template>
  <div class="user-guide-page">
    <!-- 简洁的头部区域 -->
    <header class="page-header">
      <div class="container">
        <h1>使用指南</h1>
        <p>Leaf SMS 学生学籍管理系统快速入门指南</p>
      </div>
    </header>

    <main class="page-content">
      <div class="container">
        <div class="guide-sections">
          <!-- 快速入门部分 -->
          <section class="guide-section">
            <div class="section-header">
              <h2>快速入门</h2>
              <p>新手用户快速上手Leaf SMS 学生学籍管理系统</p>
            </div>
            <div class="guide-content">
              <el-collapse v-model="activeNames" accordion>
                <el-collapse-item name="register" title="注册账号">
                  <div class="collapse-content">
                    <p>注册 LEAF-SMS 学生学籍管理系统账号的要求如下：</p>
                    <ul>
                      <li>用户名：3-20个字符，支持中文、英文、数字和下划线</li>
                      <li>密码：6-100个字符，支持明文存储（按业务需求）</li>
                      <li>邮箱：用于登录和系统通知</li>
                    </ul>
                  </div>
                </el-collapse-item>
                
                <el-collapse-item name="login" title="登录系统">
                  <div class="collapse-content">
                    <p>在登录页面输入用户名/邮箱和密码即可登录。支持记住登录状态和自动登录功能，登录失败超过5次会暂时锁定账户。</p>
                  </div>
                </el-collapse-item>
                
                <el-collapse-item name="product" title="产品规格配置">
                  <div class="collapse-content">
                    <p>系统支持灵活的班级与课程管理，方便教务工作高效开展：</p>
                    <ul>
                      <li>班级管理：创建不同年级的班级，设置班级状态与描述</li>
                      <li>课程管理：为每个班级配置不同的课程，安排任课教师</li>
                      <li>教学安排：精细化控制每个班级的教学计划和课程安排</li>
                    </ul>
                  </div>
                </el-collapse-item>

                <el-collapse-item name="review" title="业务评论管理">
                  <div class="collapse-content">
                    <p>LEAF-SMS 集成了学籍变动管理模块，支持对学生学籍进行全流程管理：</p>
                    <ul>
                      <li>变动申请：管理员可以发起学生学籍变动申请</li>
                      <li>变动审核：教务管理员对学籍变动申请进行审核与管理</li>
                      <li>变动记录：系统自动记录所有学籍变动历史，支持查询统计</li>
                    </ul>
                  </div>
                </el-collapse-item>
              </el-collapse>
            </div>
          </section>
          <section class="guide-section">
            <div class="section-header">
              <h2>发展历程</h2>
              <p>LEAF-SMS 学生学籍管理系统的成长历程</p>
            </div>
            <div class="guide-content">
              <div class="timeline">
                <div class="timeline-item">
                  <div class="timeline-content">
                    <h3>项目启动 · 2025年6月</h3>
                    <p>深入分析中学学籍管理系统痛点，确立系统架构与技术选型，采用 Vue 3 + Spring Boot 3 现代化技术栈。</p>
                  </div>
                </div>
                <div class="timeline-item">
                  <div class="timeline-content">
                    <h3>核心开发 · 2025年9月</h3>
                    <p>完成前后端核心功能开发，实现学生信息管理、学籍变动管理、班级管理等关键核心业务模块。</p>
                  </div>
                </div>
                <div class="timeline-item">
                  <div class="timeline-content">
                    <h3>正式发布 · 2026年1月</h3>
                    <p>LEAF-SMS 学生学籍管理系统正式上线并持续迭代优化，为用户提供稳定可靠的学籍管理服务。</p>
                  </div>
                </div>
              </div>
            </div>
          </section>
        </div>
      </div>
    </main>
  </div>
</template>

<script>
import { ref } from 'vue';

export default {
  name: 'UserGuidePage',
  setup() {
    const activeNames = ref(['register']);
    
    return {
      activeNames
    };
  }
}
</script>

<style scoped>
.user-guide-page {
  min-height: 100vh;
  background-color: #f5f7fa;
}

.page-header {
  background: #2d3748;
  color: white;
  padding: 40px 0;
  text-align: center;
}

.page-header h1 {
  font-size: 32px;
  margin-bottom: 12px;
  font-weight: 600;
}

.page-header p {
  font-size: 16px;
  max-width: 600px;
  margin: 0 auto;
}

.page-content {
  padding: 40px 0 60px;
}

.guide-sections {
  display: flex;
  flex-direction: column;
  gap: 40px;
}

.guide-section {
  background: white;
  border-radius: 4px;
  padding: 0;
}

.section-header {
  background: #2d3748;
  padding: 20px 24px;
  color: white;
}

.section-header h2 {
  font-size: 20px;
  margin: 0;
  font-weight: 600;
}

.guide-content {
  padding: 24px;
}

.timeline {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.timeline-item {
  border-left: 2px solid #2d3748;
  padding-left: 20px;
  position: relative;
}

.timeline-item::before {
  content: '';
  position: absolute;
  left: -7px;
  top: 0;
  width: 12px;
  height: 12px;
  background: #2d3748;
  border-radius: 50%;
}

.timeline-content h3 {
  font-size: 16px;
  margin-bottom: 8px;
  color: #2d3748;
}

.timeline-content p {
  font-size: 14px;
  color: #666;
  line-height: 1.6;
}

.collapse-content {
  padding: 12px 0;
}

.collapse-content p {
  color: #606266;
  line-height: 1.6;
  margin: 0;
  font-size: 14px;
}

.container {
  max-width: 1000px;
  margin: 0 auto;
  padding: 0 20px;
}

@media (max-width: 768px) {
  .page-header h1 {
    font-size: 24px;
  }
  
  .page-header p {
    font-size: 14px;
  }
  
  .section-header {
    padding: 16px 20px;
  }
  
  .section-header h2 {
    font-size: 18px;
  }
  
  .guide-content {
    padding: 20px;
  }
}
</style>