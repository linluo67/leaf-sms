<template>
  <el-watermark :content="watermarkText" :font="{ color: 'rgba(0, 0, 0, 0.15)', fontSize: 16 }" :z-index="9"
    :rotate="-15" :gap="[100, 100]">
    <div class="headteacher-layout">
      <header class="headteacher-header">
        <div class="header-left">
          <el-button class="mobile-toggle" @click="showDrawer = true" v-if="isMobile">
            <el-icon><Menu /></el-icon>
          </el-button>
          <h1 class="logo">Leaf SMS <span class="logo-sub">- 班主任工作台</span></h1>
        </div>
        <div class="header-right">
          <el-dropdown @command="handleCommand" trigger="click">
            <span class="user-info">
              <el-avatar :size="32" :src="userAvatar">
                <el-icon>
                  <User />
                </el-icon>
              </el-avatar>
              <span class="username">{{ store.state.user?.realName || store.state.user?.username || '班主任' }}</span>
              <el-icon class="el-icon--right"><arrow-down /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人资料</el-dropdown-item>
                <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </header>

      <div class="headteacher-container">
        <aside class="headteacher-sidebar" v-if="!isMobile">
          <el-menu :default-active="activeMenu" class="headteacher-menu" router unique-opened>
            <el-menu-item index="/headteacher">
              <el-icon>
                <Monitor />
              </el-icon>
              <template #title>工作台</template>
            </el-menu-item>

            <el-sub-menu index="my-class">
              <template #title>
                <el-icon>
                  <School />
                </el-icon>
                <span>我的班级</span>
              </template>
              <el-menu-item index="/headteacher/students">
                <el-icon>
                  <UserFilled />
                </el-icon>
                <template #title>学生列表</template>
              </el-menu-item>
              <el-menu-item index="/headteacher/attendance">
                <el-icon>
                  <Calendar />
                </el-icon>
                <template #title>考勤管理</template>
              </el-menu-item>
              <el-menu-item index="/headteacher/performance">
                <el-icon>
                  <TrendCharts />
                </el-icon>
                <template #title>成绩管理</template>
              </el-menu-item>
            </el-sub-menu>

            <el-sub-menu index="student-info">
              <template #title>
                <el-icon>
                  <Document />
                </el-icon>
                <span>学生信息</span>
              </template>
              <el-menu-item index="/headteacher/student-archives">
                <el-icon>
                  <Folder />
                </el-icon>
                <template #title>学籍档案</template>
              </el-menu-item>
              <el-menu-item index="/headteacher/student-changes">
                <el-icon>
                  <List />
                </el-icon>
                <template #title>变动记录</template>
              </el-menu-item>
            </el-sub-menu>

            <el-menu-item index="/headteacher/messages">
              <el-icon>
                <ChatDotRound />
              </el-icon>
              <template #title>消息通知</template>
            </el-menu-item>
          </el-menu>
        </aside>

        <el-drawer v-model="showDrawer" direction="ltr" size="240px" :with-header="false" class="mobile-drawer"
          :body-style="{ padding: '0', backgroundColor: '#2c3e50' }" v-if="isMobile">
          <div class="drawer-header">
            <h2 class="drawer-logo">Leaf SMS</h2>
          </div>
          <el-menu :default-active="activeMenu" class="headteacher-menu" router unique-opened @select="showDrawer = false">
            <el-menu-item index="/headteacher">
              <el-icon>
                <Monitor />
              </el-icon>
              <template #title>工作台</template>
            </el-menu-item>

            <el-sub-menu index="my-class">
              <template #title>
                <el-icon>
                  <School />
                </el-icon>
                <span>我的班级</span>
              </template>
              <el-menu-item index="/headteacher/students">
                <el-icon>
                  <UserFilled />
                </el-icon>
                <template #title>学生列表</template>
              </el-menu-item>
              <el-menu-item index="/headteacher/attendance">
                <el-icon>
                  <Calendar />
                </el-icon>
                <template #title>考勤管理</template>
              </el-menu-item>
              <el-menu-item index="/headteacher/performance">
                <el-icon>
                  <TrendCharts />
                </el-icon>
                <template #title>成绩管理</template>
              </el-menu-item>
            </el-sub-menu>

            <el-sub-menu index="student-info">
              <template #title>
                <el-icon>
                  <Document />
                </el-icon>
                <span>学生信息</span>
              </template>
              <el-menu-item index="/headteacher/student-archives">
                <el-icon>
                  <Folder />
                </el-icon>
                <template #title>学籍档案</template>
              </el-menu-item>
              <el-menu-item index="/headteacher/student-changes">
                <el-icon>
                  <List />
                </el-icon>
                <template #title>变动记录</template>
              </el-menu-item>
            </el-sub-menu>

            <el-menu-item index="/headteacher/messages">
              <el-icon>
                <ChatDotRound />
              </el-icon>
              <template #title>消息通知</template>
            </el-menu-item>
          </el-menu>
        </el-drawer>

        <main class="headteacher-main">
          <router-view />
        </main>
      </div>
    </div>
  </el-watermark>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, nextTick } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { User, UserFilled, ArrowDown, Monitor, Document, School, Calendar, TrendCharts, Folder, List, ChatDotRound, Menu } from '@element-plus/icons-vue'
import store from '@/utils/store.js'

const router = useRouter()
const route = useRoute()

const isMobile = ref(false)
const showDrawer = ref(false)

const checkIfMobile = () => {
  isMobile.value = window.innerWidth <= 768
  if (!isMobile.value) {
    showDrawer.value = false
  }
}

const activeMenu = computed(() => route.path)

const userAvatar = computed(() => {
  return store.state.user?.avatar || ''
})

const watermarkText = computed(() => {
  const user = store.state.user
  if (user?.email) {
    return user.email
  }
  return 'Leaf SMS'
})

const handleCommand = async (command) => {
  try {
    if (command === 'logout') {
      await ElMessageBox.confirm(
        '确定要退出登录吗？',
        '提示',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }
      )

      await store.logout()
      ElMessage.success('已退出登录')
      router.push('/login')
    } else if (command === 'profile') {
      router.push('/headteacher/profile')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败，请重试')
    }
  }
}

onMounted(async () => {
  checkIfMobile()
  window.addEventListener('resize', checkIfMobile)
  try {
    if (!store.state.user) {
      await store.fetchCurrentUser()
    }

    await nextTick()
  } catch (error) {
    ElMessage.error('初始化失败，请刷新页面重试')
  }
})

onUnmounted(() => {
  window.removeEventListener('resize', checkIfMobile)
})
</script>

<style scoped>
.headteacher-layout {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background-color: #f0f2f5;
}

.headteacher-header {
  height: 64px;
  background-color: #1a2e2a;
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  border-bottom: 1px solid #2a4a3a;
  position: relative;
  z-index: 1001;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.mobile-toggle {
  background: transparent;
  border: none;
  color: #fff;
  font-size: 24px;
  padding: 8px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
}

.mobile-toggle:hover {
  background-color: rgba(255, 255, 255, 0.1);
}

.header-left .logo {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
  color: #fff;
  letter-spacing: 0.5px;
}

.logo-sub {
  font-size: 14px;
  font-weight: 400;
  opacity: 0.8;
}

.header-right .user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
  color: #fff;
  padding: 8px 12px;
  border-radius: 8px;
  transition: background-color 0.3s;
}

.header-right .user-info:hover {
  background-color: rgba(255, 255, 255, 0.1);
}

.username {
  margin: 0 10px;
  font-weight: 500;
}

.headteacher-container {
  flex: 1;
  display: flex;
  overflow: hidden;
  position: relative;
}

.headteacher-sidebar {
  width: 240px;
  background-color: #1a2e2a;
  border-right: 1px solid #2a4a3a;
  transition: width 0.3s;
  flex-shrink: 0;
}

.headteacher-menu {
  height: 100%;
  border-right: none;
  background-color: #1a2e2a;
}

.mobile-drawer {
  --el-drawer-padding-primary: 0 !important;
}

.mobile-drawer :deep(.el-drawer) {
  background-color: #1a2e2a;
}

.drawer-header {
  height: 64px;
  display: flex;
  align-items: center;
  padding: 0 20px;
  background-color: #1a2e2a;
  border-bottom: 1px solid #2a4a3a;
}

.drawer-logo {
  margin: 0;
  font-size: 18px;
  color: #fff;
  font-weight: 600;
}

.headteacher-menu :deep(.el-menu) {
  background-color: #1a2e2a;
  border-right: none;
}

.headteacher-menu :deep(.el-menu-item),
.headteacher-menu :deep(.el-sub-menu__title) {
  height: 50px;
  line-height: 50px;
  color: #b0b0b0;
  margin: 0;
  border-radius: 0;
}

.headteacher-menu :deep(.el-menu-item:hover),
.headteacher-menu :deep(.el-sub-menu__title:hover) {
  background-color: #2a4a3a;
  color: #fff;
}

.headteacher-menu :deep(.el-menu-item.is-active) {
  background-color: #3a6a5a;
  color: #fff;
  font-weight: 500;
}

.headteacher-menu :deep(.el-sub-menu .el-menu-item) {
  background-color: #1a2e2a;
  padding-left: 50px !important;
}

.headteacher-menu :deep(.el-sub-menu .el-menu-item:hover) {
  background-color: #2a4a3a;
}

.headteacher-menu :deep(.el-sub-menu .el-menu-item.is-active) {
  background-color: #3a6a5a;
}

.headteacher-menu :deep(.el-menu-item .el-icon),
.headteacher-menu :deep(.el-sub-menu__title .el-icon) {
  font-size: 18px;
  margin-right: 12px;
  color: inherit;
}

.headteacher-main {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
  background-color: #f0f2f5;
  width: 100%;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

.headteacher-main :deep(.el-card) {
  border-radius: 8px;
  border: 1px solid #e6e8eb;
  overflow: hidden;
  margin-bottom: 20px;
  box-shadow: 0 2px 12px 0 rgba(0,0,0,0.05);
}

.headteacher-main :deep(.el-card__header) {
  padding: 16px 20px;
  border-bottom: 1px solid #e6e8eb;
  background-color: #fafbfc;
}

.headteacher-main :deep(.el-card__body) {
  padding: 20px;
}

.headteacher-main :deep(.el-form-item) {
  margin-bottom: 16px;
}

.headteacher-main :deep(.search-form .el-form-item) {
  margin-bottom: 0;
  margin-right: 16px;
}

.headteacher-main :deep(.search-form .el-form-item:last-child) {
  margin-right: 0;
}

@media (max-width: 1024px) {
  .headteacher-sidebar {
    width: 200px;
  }
}

@media (max-width: 768px) {
  .headteacher-header {
    height: 60px;
    padding: 0 16px;
  }

  .header-left .logo {
    font-size: 18px;
  }

  .logo-sub {
    display: none;
  }

  .username {
    display: none;
  }

  .headteacher-main {
    padding: 12px;
  }

  .headteacher-main :deep(.el-card__body) {
    padding: 12px;
  }
}

@media (max-width: 480px) {
  .headteacher-header {
    padding: 0 12px;
  }

  .header-left .logo {
    font-size: 16px;
  }

  .headteacher-main {
    padding: 10px;
  }
}
</style>

<style>
.mobile-drawer .el-drawer__body {
  padding: 0 !important;
  background-color: #047857 !important;
}
</style>
