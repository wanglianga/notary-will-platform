<template>
  <div class="page-container">
    <h2 class="page-title">材料上传</h2>
    <el-card style="margin-bottom: 20px">
      <template #header><span>上传材料</span></template>
      <el-upload
        :action="`/api/cases/${caseId}/materials`"
        :headers="uploadHeaders"
        multiple
        :on-success="handleUploadSuccess"
        :on-error="handleUploadError"
        drag
      >
        <el-icon :size="60" style="color: #c0c4cc"><UploadFilled /></el-icon>
        <div style="margin-top: 8px">将文件拖到此处，或<em>点击上传</em></div>
      </el-upload>
    </el-card>

    <el-card style="margin-bottom: 20px">
      <template #header><span>已上传材料</span></template>
      <el-table :data="materialList" stripe>
        <el-table-column prop="fileName" label="文件名" min-width="200" />
        <el-table-column prop="fileType" label="类型" width="120" />
        <el-table-column prop="fileSize" label="大小" width="100">
          <template #default="{ row }">{{ (row.fileSize / 1024).toFixed(1) }} KB</template>
        </el-table-column>
        <el-table-column prop="uploadedAt" label="上传时间" width="180" />
        <el-table-column prop="status" label="状态" width="120">
          <template #default="{ row }">
            <el-tag :type="getStatusTagType(row.status)" size="small">{{ getStatusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-card>
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center">
          <span>补件要求</span>
          <el-button size="small" @click="loadSupplements"><el-icon><Refresh /></el-icon> 刷新</el-button>
        </div>
      </template>
      <el-table :data="supplementRequests" stripe>
        <el-table-column prop="materialType" label="需补材料类型" width="160" />
        <el-table-column prop="description" label="说明" min-width="200" />
        <el-table-column prop="deadline" label="截止日期" width="140" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'PENDING' ? 'warning' : 'success'" size="small">
              {{ row.status === 'PENDING' ? '待补件' : '已补件' }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getMaterialList, getSupplementRequests } from '@/api/case'
import { getStatusTagType, getStatusLabel } from '@/utils'

const route = useRoute()
const caseId = route.params.id as string
const materialList = ref<any[]>([])
const supplementRequests = ref<any[]>([])

const uploadHeaders = computed(() => ({
  Authorization: `Bearer ${localStorage.getItem('token')}`,
}))

function handleUploadSuccess() {
  ElMessage.success('上传成功')
  loadMaterials()
}

function handleUploadError() {
  ElMessage.error('上传失败')
}

async function loadMaterials() {
  try {
    const res: any = await getMaterialList(caseId)
    materialList.value = res.data?.list || res.list || res.data || []
  } catch { /* empty */ }
}

async function loadSupplements() {
  try {
    const res: any = await getSupplementRequests(caseId)
    supplementRequests.value = res.data?.list || res.list || res.data || []
  } catch { /* empty */ }
}

onMounted(() => {
  loadMaterials()
  loadSupplements()
})
</script>
