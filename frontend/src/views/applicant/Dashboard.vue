<template>
  <div class="page-container">
    <h2 class="page-title">申请人工作台</h2>
    <div class="card-row">
      <el-card class="stat-card" shadow="hover">
        <div class="stat-value">{{ stats.total }}</div>
        <div class="stat-label">全部案件</div>
      </el-card>
      <el-card class="stat-card" shadow="hover">
        <div class="stat-value" style="color: #e6a23c">{{ stats.draft }}</div>
        <div class="stat-label">草稿</div>
      </el-card>
      <el-card class="stat-card" shadow="hover">
        <div class="stat-value" style="color: #409eff">{{ stats.submitted }}</div>
        <div class="stat-label">审核中</div>
      </el-card>
      <el-card class="stat-card" shadow="hover">
        <div class="stat-value" style="color: #67c23a">{{ stats.completed }}</div>
        <div class="stat-label">已完成</div>
      </el-card>
    </div>

    <el-card style="margin-bottom: 20px">
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center">
          <span>我的案件</span>
          <el-button type="primary" @click="$router.push('/applicant/case/new')">
            <el-icon><Plus /></el-icon> 新建案件
          </el-button>
        </div>
      </template>
      <el-table :data="caseStore.caseList" stripe>
        <el-table-column prop="caseNumber" label="案件编号" width="160" />
        <el-table-column prop="title" label="案件标题" min-width="200" />
        <el-table-column prop="status" label="状态" width="120">
          <template #default="{ row }">
            <el-tag :type="getStatusTagType(row.status)" size="small">{{ getStatusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="180" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="$router.push(`/applicant/case/${row.id}`)">查看</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-card>
      <template #header><span>快捷操作</span></template>
      <div style="display: flex; gap: 12px; flex-wrap: wrap">
        <el-button @click="$router.push('/applicant/case/new')">
          <el-icon><DocumentAdd /></el-icon> 创建新案件
        </el-button>
        <el-button @click="loadCases">
          <el-icon><Refresh /></el-icon> 刷新列表
        </el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useCaseStore } from '@/stores/case'
import { getStatusTagType, getStatusLabel } from '@/utils'

const caseStore = useCaseStore()

const stats = computed(() => {
  const list = caseStore.caseList || []
  return {
    total: list.length,
    draft: list.filter((c: any) => c.status === 'DRAFT').length,
    submitted: list.filter((c: any) => ['SUBMITTED', 'UNDER_REVIEW'].includes(c.status)).length,
    completed: list.filter((c: any) => c.status === 'COMPLETED').length,
  }
})

async function loadCases() {
  await caseStore.fetchCaseList()
}

onMounted(() => {
  loadCases()
})
</script>
