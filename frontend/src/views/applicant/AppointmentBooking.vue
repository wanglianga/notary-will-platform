<template>
  <div class="page-container">
    <h2 class="page-title">预约办理</h2>
    <el-card style="margin-bottom: 20px">
      <template #header><span>可选时间段</span></template>
      <el-table :data="slots" stripe>
        <el-table-column prop="date" label="日期" width="140" />
        <el-table-column prop="startTime" label="开始时间" width="120" />
        <el-table-column prop="endTime" label="结束时间" width="120" />
        <el-table-column prop="notaryName" label="公证员" width="120" />
        <el-table-column prop="available" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.available ? 'success' : 'info'" size="small">
              {{ row.available ? '可预约' : '已约满' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120">
          <template #default="{ row }">
            <el-button v-if="row.available" link type="primary" @click="handleBook(row)">预约</el-button>
            <span v-else style="color: #909399">-</span>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-card v-if="currentAppointment">
      <template #header><span>当前预约</span></template>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="预约日期">{{ currentAppointment.date }}</el-descriptions-item>
        <el-descriptions-item label="预约时间">{{ currentAppointment.startTime }} - {{ currentAppointment.endTime }}</el-descriptions-item>
        <el-descriptions-item label="公证员">{{ currentAppointment.notaryName }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag type="success" size="small">已预约</el-tag>
        </el-descriptions-item>
      </el-descriptions>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getAvailableSlots, bookAppointment } from '@/api/case'

const route = useRoute()
const caseId = route.params.id as string
const slots = ref<any[]>([])
const currentAppointment = ref<any>(null)

async function loadSlots() {
  try {
    const res: any = await getAvailableSlots(caseId)
    slots.value = res.data?.list || res.list || res.data || []
  } catch { /* empty */ }
}

async function handleBook(slot: any) {
  await ElMessageBox.confirm(`确认预约 ${slot.date} ${slot.startTime}-${slot.endTime}？`, '确认预约', { type: 'info' })
  try {
    await bookAppointment(caseId, { slotId: slot.id })
    ElMessage.success('预约成功')
    currentAppointment.value = slot
    await loadSlots()
  } catch {
    ElMessage.error('预约失败')
  }
}

onMounted(() => {
  loadSlots()
})
</script>
