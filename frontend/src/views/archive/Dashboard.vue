<template>
  <div class="page-container">
    <h2 class="page-title">档案管理工作台</h2>
    <div class="card-row">
      <el-card class="stat-card" shadow="hover">
        <div class="stat-value">{{ stats.sealed }}</div>
        <div class="stat-label">已封存</div>
      </el-card>
      <el-card class="stat-card" shadow="hover">
        <div class="stat-value" style="color: #e6a23c">{{ stats.unsealed }}</div>
        <div class="stat-label">已启封</div>
      </el-card>
      <el-card class="stat-card" shadow="hover">
        <div class="stat-value" style="color: #f56c6c">{{ stats.unsealRequests }}</div>
        <div class="stat-label">启封申请</div>
      </el-card>
    </div>

    <el-card>
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center">
          <span>案件列表</span>
          <el-select v-model="filterStatus" placeholder="状态筛选" clearable style="width: 160px" @change="loadData">
            <el-option label="已封存" value="SEALED" />
            <el-option label="已启封" value="UNSEALED" />
          </el-select>
        </div>
      </template>
      <el-table :data="caseList" stripe>
        <el-table-column prop="caseNumber" label="案件编号" width="160" />
        <el-table-column prop="applicantName" label="申请人" width="120" />
        <el-table-column prop="sealedAt" label="封存时间" width="180" />
        <el-table-column prop="physicalLocation" label="物理位置" width="160" />
        <el-table-column prop="archiveStatus" label="档案状态" width="120">
          <template #default="{ row }">
            <el-tag :type="row.archiveStatus === 'SEALED' ? '' : 'warning'" size="small">
              {{ row.archiveStatus === 'SEALED' ? '已封存' : '已启封' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="$router.push(`/archive/case/${row.id}`)">管理</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getArchiveList, getUnsealRequests } from '@/api/archive'

const caseList = ref<any[]>([])
const filterStatus = ref('')
const stats = ref({ sealed: 0, unsealed: 0, unsealRequests: 0 })

async function loadData() {
  try {
    const params: Record<string, any> = {}
    if (filterStatus.value) params.status = filterStatus.value
    const [listRes, unsealRes]: [any, any] = await Promise.all([
      getArchiveList(params),
      getUnsealRequests(),
    ])
    caseList.value = listRes.data?.list || listRes.list || listRes.data || []
    const unsealList = unsealRes.data?.list || unsealRes.list || unsealRes.data || []
    stats.value = {
      sealed: caseList.value.filter((c: any) => c.archiveStatus === 'SEALED').length,
      unsealed: caseList.value.filter((c: any) => c.archiveStatus === 'UNSEALED').length,
      unsealRequests: unsealList.length,
    }
  } catch { /* empty */ }
}

onMounted(() => {
  loadData()
})
</script>
