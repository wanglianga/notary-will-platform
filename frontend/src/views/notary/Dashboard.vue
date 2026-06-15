<template>
  <div class="page-container">
    <h2 class="page-title">公证员工作台</h2>
    <div class="card-row">
      <el-card class="stat-card" shadow="hover">
        <div class="stat-value" style="color: #409eff">{{ stats.todayAppointments }}</div>
        <div class="stat-label">今日预约</div>
      </el-card>
      <el-card class="stat-card" shadow="hover">
        <div class="stat-value" style="color: #e6a23c">{{ stats.pendingCases }}</div>
        <div class="stat-label">待办案件</div>
      </el-card>
      <el-card class="stat-card" shadow="hover">
        <div class="stat-value" style="color: #67c23a">{{ stats.completedToday }}</div>
        <div class="stat-label">今日完成</div>
      </el-card>
    </div>

    <el-card style="margin-bottom: 20px">
      <template #header><span>今日预约</span></template>
      <el-table :data="todayAppointments" stripe>
        <el-table-column prop="appointmentTime" label="预约时间" width="160" />
        <el-table-column prop="applicantName" label="申请人" width="120" />
        <el-table-column prop="caseNumber" label="案件编号" width="160" />
        <el-table-column prop="willType" label="遗嘱类型" width="120" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusTagType(row.status)" size="small">{{ getStatusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="$router.push(`/notary/case/${row.caseId}`)">办理</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-card>
      <template #header><span>待办案件</span></template>
      <el-table :data="pendingCases" stripe>
        <el-table-column prop="caseNumber" label="案件编号" width="160" />
        <el-table-column prop="applicantName" label="申请人" width="120" />
        <el-table-column prop="status" label="状态" width="120">
          <template #default="{ row }">
            <el-tag :type="getStatusTagType(row.status)" size="small">{{ getStatusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="$router.push(`/notary/case/${row.id}`)">办理</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getNotaryAppointments, getNotaryCaseList } from '@/api/notary'
import { getStatusTagType, getStatusLabel } from '@/utils'

const todayAppointments = ref<any[]>([])
const pendingCases = ref<any[]>([])
const stats = ref({ todayAppointments: 0, pendingCases: 0, completedToday: 0 })

async function loadData() {
  try {
    const [apptRes, caseRes]: [any, any] = await Promise.all([
      getNotaryAppointments({ date: new Date().toISOString().split('T')[0] }),
      getNotaryCaseList({ status: 'IN_PROGRESS' }),
    ])
    todayAppointments.value = apptRes.data?.list || apptRes.list || apptRes.data || []
    pendingCases.value = caseRes.data?.list || caseRes.list || caseRes.data || []
    stats.value = {
      todayAppointments: todayAppointments.value.length,
      pendingCases: pendingCases.value.length,
      completedToday: 0,
    }
  } catch { /* empty */ }
}

onMounted(() => {
  loadData()
})
</script>
