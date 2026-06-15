<template>
  <div class="page-container">
    <h2 class="page-title">收费管理工作台</h2>
    <div class="card-row">
      <el-card class="stat-card" shadow="hover">
        <div class="stat-value">{{ formatCurrency(stats.totalRevenue) }}</div>
        <div class="stat-label">总收入</div>
      </el-card>
      <el-card class="stat-card" shadow="hover">
        <div class="stat-value" style="color: #f56c6c">{{ stats.pendingPayments }}</div>
        <div class="stat-label">待收款</div>
      </el-card>
      <el-card class="stat-card" shadow="hover">
        <div class="stat-value" style="color: #67c23a">{{ stats.paidCount }}</div>
        <div class="stat-label">已收款</div>
      </el-card>
      <el-card class="stat-card" shadow="hover">
        <div class="stat-value" style="color: #909399">{{ stats.refundCount }}</div>
        <div class="stat-label">已退款</div>
      </el-card>
    </div>

    <el-card>
      <template #header><span>待收款案件</span></template>
      <el-table :data="pendingList" stripe>
        <el-table-column prop="caseNumber" label="案件编号" width="160" />
        <el-table-column prop="applicantName" label="申请人" width="120" />
        <el-table-column prop="feeType" label="费用类型" width="120">
          <template #default="{ row }">{{ getFeeTypeLabel(row.feeType) }}</template>
        </el-table-column>
        <el-table-column prop="amount" label="金额" width="140">
          <template #default="{ row }">{{ formatCurrency(row.amount) }}</template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusTagType(row.status)" size="small">{{ getStatusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="$router.push(`/cashier/case/${row.caseId}`)">管理</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getPaymentStatistics, getPendingPayments } from '@/api/cashier'
import { getStatusTagType, getStatusLabel, getFeeTypeLabel, formatCurrency } from '@/utils'

const pendingList = ref<any[]>([])
const stats = ref({ totalRevenue: 0, pendingPayments: 0, paidCount: 0, refundCount: 0 })

async function loadData() {
  try {
    const [statRes, pendingRes]: [any, any] = await Promise.all([
      getPaymentStatistics(),
      getPendingPayments(),
    ])
    stats.value = statRes.data || stats.value
    pendingList.value = pendingRes.data?.list || pendingRes.list || pendingRes.data || []
  } catch { /* empty */ }
}

onMounted(() => {
  loadData()
})
</script>
