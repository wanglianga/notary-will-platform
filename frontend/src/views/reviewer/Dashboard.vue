<template>
  <div class="page-container">
    <h2 class="page-title">审核员工作台</h2>
    <div class="card-row">
      <el-card class="stat-card" shadow="hover">
        <div class="stat-value" style="color: #e6a23c">{{ stats.pending }}</div>
        <div class="stat-label">待审核</div>
      </el-card>
      <el-card class="stat-card" shadow="hover">
        <div class="stat-value" style="color: #409eff">{{ stats.inProgress }}</div>
        <div class="stat-label">审核中</div>
      </el-card>
      <el-card class="stat-card" shadow="hover">
        <div class="stat-value" style="color: #67c23a">{{ stats.approved }}</div>
        <div class="stat-label">已通过</div>
      </el-card>
      <el-card class="stat-card" shadow="hover">
        <div class="stat-value" style="color: #f56c6c">{{ stats.rejected }}</div>
        <div class="stat-label">已驳回</div>
      </el-card>
    </div>

    <el-card>
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center">
          <span>待审核案件</span>
          <el-button @click="loadData"><el-icon><Refresh /></el-icon> 刷新</el-button>
        </div>
      </template>
      <el-table :data="pendingList" stripe>
        <el-table-column prop="caseNumber" label="案件编号" width="160" />
        <el-table-column prop="applicantName" label="申请人" width="120" />
        <el-table-column prop="willType" label="遗嘱类型" width="120" />
        <el-table-column prop="submittedAt" label="提交时间" width="180" />
        <el-table-column prop="status" label="状态" width="120">
          <template #default="{ row }">
            <el-tag :type="getStatusTagType(row.status)" size="small">{{ getStatusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="$router.push(`/reviewer/case/${row.id}`)">审核</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getReviewQueue } from '@/api/review'
import { getStatusTagType, getStatusLabel } from '@/utils'

const pendingList = ref<any[]>([])
const stats = ref({ pending: 0, inProgress: 0, approved: 0, rejected: 0 })

async function loadData() {
  try {
    const res: any = await getReviewQueue({ status: 'PENDING' })
    pendingList.value = res.data?.list || res.list || res.data || []
    stats.value = res.data?.stats || { pending: pendingList.value.length, inProgress: 0, approved: 0, rejected: 0 }
  } catch { /* empty */ }
}

onMounted(() => {
  loadData()
})
</script>
