<template>
  <div class="page-container">
    <h2 class="page-title">预约管理</h2>
    <el-card style="margin-bottom: 20px">
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center">
          <span>新增可用时段</span>
        </div>
      </template>
      <el-form :model="slotForm" label-width="100px" style="max-width: 500px" inline>
        <el-form-item label="日期">
          <el-date-picker v-model="slotForm.date" type="date" placeholder="选择日期" value-format="YYYY-MM-DD" />
        </el-form-item>
        <el-form-item label="开始时间">
          <el-time-select v-model="slotForm.startTime" start="08:00" step="00:30" end="18:00" placeholder="开始时间" />
        </el-form-item>
        <el-form-item label="结束时间">
          <el-time-select v-model="slotForm.endTime" start="08:00" step="00:30" end="18:00" placeholder="结束时间" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleAddSlot">添加时段</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card>
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center">
          <span>预约列表</span>
          <el-date-picker v-model="filterDate" type="date" placeholder="筛选日期" value-format="YYYY-MM-DD" @change="loadAppointments" />
        </div>
      </template>
      <el-table :data="appointments" stripe>
        <el-table-column prop="date" label="日期" width="120" />
        <el-table-column prop="startTime" label="开始时间" width="100" />
        <el-table-column prop="endTime" label="结束时间" width="100" />
        <el-table-column prop="applicantName" label="预约人" width="120" />
        <el-table-column prop="caseNumber" label="案件编号" width="160" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.applicantName ? 'success' : 'info'" size="small">
              {{ row.applicantName ? '已预约' : '空闲' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120">
          <template #default="{ row }">
            <el-button v-if="row.caseId" link type="primary" @click="$router.push(`/notary/case/${row.caseId}`)">办理</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getNotaryAppointments, manageSlots } from '@/api/notary'

const appointments = ref<any[]>([])
const filterDate = ref('')

const slotForm = reactive({
  date: '',
  startTime: '',
  endTime: '',
})

async function loadAppointments() {
  try {
    const params: Record<string, any> = {}
    if (filterDate.value) params.date = filterDate.value
    const res: any = await getNotaryAppointments(params)
    appointments.value = res.data?.list || res.list || res.data || []
  } catch { /* empty */ }
}

async function handleAddSlot() {
  if (!slotForm.date || !slotForm.startTime || !slotForm.endTime) {
    ElMessage.warning('请填写完整时段信息')
    return
  }
  try {
    await manageSlots({ ...slotForm })
    ElMessage.success('时段添加成功')
    loadAppointments()
  } catch {
    ElMessage.error('添加失败')
  }
}

onMounted(() => {
  loadAppointments()
})
</script>
