<template>
  <div class="page-container">
    <h2 class="page-title">审核队列</h2>
    <el-card>
      <template #header>
        <div style="display: flex; gap: 12px; align-items: center; flex-wrap: wrap">
          <el-select v-model="filterStatus" placeholder="状态筛选" clearable style="width: 160px" @change="loadData">
            <el-option label="待审核" value="PENDING" />
            <el-option label="审核中" value="IN_PROGRESS" />
            <el-option label="需补件" value="SUPPLEMENT_REQUIRED" />
            <el-option label="已通过" value="APPROVED" />
            <el-option label="已驳回" value="REJECTED" />
          </el-select>
          <el-input v-model="searchKeyword" placeholder="搜索案件编号/申请人" clearable style="width: 240px" @clear="loadData" @keyup.enter="loadData" />
          <el-button type="primary" @click="loadData"><el-icon><Search /></el-icon> 搜索</el-button>
        </div>
      </template>
      <el-table :data="queueList" stripe>
        <el-table-column prop="caseNumber" label="案件编号" width="160" />
        <el-table-column prop="applicantName" label="申请人" width="120" />
        <el-table-column prop="willType" label="遗嘱类型" width="120" />
        <el-table-column prop="submittedAt" label="提交时间" width="180" />
        <el-table-column prop="status" label="状态" width="120">
          <template #default="{ row }">
            <el-tag :type="getStatusTagType(row.status)" size="small">{{ getStatusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="reviewerName" label="审核员" width="120" />
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="$router.push(`/reviewer/case/${row.id}`)">审核</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
        v-if="total > pageSize"
        style="margin-top: 16px; justify-content: flex-end"
        :current-page="currentPage"
        :page-size="pageSize"
        :total="total"
        layout="prev, pager, next"
        @current-change="handlePageChange"
      />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getReviewQueue } from '@/api/review'
import { getStatusTagType, getStatusLabel } from '@/utils'

const filterStatus = ref('')
const searchKeyword = ref('')
const queueList = ref<any[]>([])
const currentPage = ref(1)
const pageSize = 20
const total = ref(0)

async function loadData() {
  try {
    const params: Record<string, any> = {
      page: currentPage.value,
      size: pageSize,
    }
    if (filterStatus.value) params.status = filterStatus.value
    if (searchKeyword.value) params.keyword = searchKeyword.value
    const res: any = await getReviewQueue(params)
    queueList.value = res.data?.list || res.list || res.data || []
    total.value = res.data?.total || 0
  } catch { /* empty */ }
}

function handlePageChange(page: number) {
  currentPage.value = page
  loadData()
}

onMounted(() => {
  loadData()
})
</script>
