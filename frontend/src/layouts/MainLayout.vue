<template>
  <el-container class="main-layout">
    <el-aside :width="isCollapse ? '64px' : '220px'" class="sidebar">
      <div class="logo">
        <el-icon :size="28"><Stamp /></el-icon>
        <span v-show="!isCollapse" class="logo-text">公证遗嘱平台</span>
      </div>
      <el-menu
        :default-active="activeMenu"
        :collapse="isCollapse"
        :router="true"
        background-color="#1d1e3a"
        text-color="#b0b3d6"
        active-text-color="#409eff"
      >
        <template v-for="item in menuItems" :key="item.path">
          <el-menu-item :index="item.path">
            <el-icon><component :is="item.icon" /></el-icon>
            <template #title>{{ item.title }}</template>
          </el-menu-item>
        </template>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="header">
        <div class="header-left">
          <el-icon class="collapse-btn" @click="isCollapse = !isCollapse">
            <Fold v-if="!isCollapse" />
            <Expand v-else />
          </el-icon>
          <el-breadcrumb separator="/">
            <el-breadcrumb-item v-for="item in breadcrumbs" :key="item.path" :to="item.path">
              {{ item.title }}
            </el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="header-right">
          <el-tag :type="roleTagType" effect="dark" size="small">{{ roleLabel }}</el-tag>
          <el-dropdown @command="handleCommand">
            <span class="user-info">
              <el-icon><User /></el-icon>
              {{ userName }}
              <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      <el-main class="main-content">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import {
  Stamp, Fold, Expand, User, ArrowDown,
  HomeFilled, DocumentAdd, Document, UserFilled,
  House, Calendar, Reading, Money, Box, View, List,
} from '@element-plus/icons-vue'

const route = useRoute()
const authStore = useAuthStore()
const isCollapse = ref(false)

const activeMenu = computed(() => {
  const path = route.path
  const segments = path.split('/')
  if (segments.length >= 3) {
    return '/' + segments[1] + '/' + segments[2]
  }
  return '/' + segments[1]
})

const roleLabelMap: Record<string, string> = {
  APPLICANT: '申请人',
  REVIEWER: '审核员',
  NOTARY: '公证员',
  ARCHIVIST: '档案员',
  CASHIER: '收费员',
}

const roleTagTypeMap: Record<string, string> = {
  APPLICANT: '',
  REVIEWER: 'warning',
  NOTARY: 'success',
  ARCHIVIST: 'info',
  CASHIER: 'danger',
}

const roleLabel = computed(() => roleLabelMap[authStore.role] || authStore.role)
const roleTagType = computed(() => roleTagTypeMap[authStore.role] || 'info')
const userName = computed(() => authStore.userInfo?.name || authStore.userInfo?.username || '用户')

const menuConfig: Record<string, Array<{ path: string; title: string; icon: any }>> = {
  APPLICANT: [
    { path: '/applicant', title: '工作台', icon: HomeFilled },
    { path: '/applicant/case/new', title: '新建案件', icon: DocumentAdd },
  ],
  REVIEWER: [
    { path: '/reviewer', title: '工作台', icon: HomeFilled },
    { path: '/reviewer/queue', title: '审核队列', icon: List },
  ],
  NOTARY: [
    { path: '/notary', title: '工作台', icon: HomeFilled },
    { path: '/notary/appointments', title: '预约管理', icon: Calendar },
  ],
  ARCHIVIST: [
    { path: '/archivist', title: '工作台', icon: HomeFilled },
  ],
  CASHIER: [
    { path: '/cashier', title: '工作台', icon: HomeFilled },
  ],
}

const menuItems = computed(() => menuConfig[authStore.role] || [])

const breadcrumbs = computed(() => {
  const items: Array<{ path: string; title: string }> = []
  const rolePathMap: Record<string, string> = {
    APPLICANT: '/applicant',
    REVIEWER: '/reviewer',
    NOTARY: '/notary',
    ARCHIVIST: '/archivist',
    CASHIER: '/cashier',
  }
  const roleHome = { path: rolePathMap[authStore.role] || `/${authStore.role}`, title: roleLabel.value + '首页' }
  items.push(roleHome)

  const nameMap: Record<string, string> = {
    NewCase: '新建案件',
    CaseDetail: '案件详情',
    IdentityInfo: '身份信息',
    PropertyInventory: '财产清单',
    KinshipRelations: '亲属关系',
    HealthDeclaration: '健康状况声明',
    Beneficiaries: '受益人',
    Witnesses: '见证人',
    AppointmentBooking: '预约办理',
    MaterialUpload: '材料上传',
    ReviewQueue: '审核队列',
    CaseReview: '案件审核',
    AppointmentManagement: '预约管理',
    CaseProcessing: '案件办理',
    HighRiskInterview: '高风险谈话',
    CaseArchive: '档案管理',
    FeeManagement: '费用管理',
  }

  if (route.name && route.name !== 'ApplicantDashboard' && route.name !== 'ReviewerDashboard'
    && route.name !== 'NotaryDashboard' && route.name !== 'ArchiveDashboard' && route.name !== 'CashierDashboard') {
    items.push({
      path: route.path,
      title: nameMap[route.name as string] || (route.name as string),
    })
  }

  return items
})

function handleCommand(command: string) {
  if (command === 'logout') {
    authStore.logout()
  }
}
</script>

<style scoped>
.main-layout {
  height: 100vh;
}
.sidebar {
  background-color: #1d1e3a;
  transition: width 0.3s;
  overflow-x: hidden;
}
.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  color: #fff;
  font-size: 16px;
  font-weight: 600;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
}
.logo-text {
  white-space: nowrap;
}
.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #fff;
  border-bottom: 1px solid #e8e8e8;
  padding: 0 20px;
  height: 60px;
}
.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}
.collapse-btn {
  font-size: 20px;
  cursor: pointer;
  color: #606266;
}
.collapse-btn:hover {
  color: #409eff;
}
.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}
.user-info {
  display: flex;
  align-items: center;
  gap: 4px;
  cursor: pointer;
  color: #606266;
  font-size: 14px;
}
.user-info:hover {
  color: #409eff;
}
.main-content {
  background: #f0f2f5;
  overflow-y: auto;
}
.el-menu {
  border-right: none;
}
</style>
