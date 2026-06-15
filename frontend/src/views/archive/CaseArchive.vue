<template>
  <div class="page-container">
    <h2 class="page-title">档案管理</h2>

    <el-card style="margin-bottom: 20px">
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center">
          <span>案件信息</span>
          <el-tag :type="archiveData.archiveStatus === 'SEALED' ? '' : 'warning'" size="large">
            {{ archiveData.archiveStatus === 'SEALED' ? '已封存' : '已启封' }}
          </el-tag>
        </div>
      </template>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="案件编号">{{ archiveData.caseNumber }}</el-descriptions-item>
        <el-descriptions-item label="申请人">{{ archiveData.applicantName }}</el-descriptions-item>
        <el-descriptions-item label="封存时间">{{ archiveData.sealedAt || '未封存' }}</el-descriptions-item>
        <el-descriptions-item label="物理位置">{{ archiveData.physicalLocation || '-' }}</el-descriptions-item>
        <el-descriptions-item label="电子路径">{{ archiveData.electronicPath || '-' }}</el-descriptions-item>
        <el-descriptions-item label="启封时间">{{ archiveData.unsealedAt || '-' }}</el-descriptions-item>
      </el-descriptions>
    </el-card>

    <el-card v-if="archiveData.archiveStatus !== 'SEALED'" style="margin-bottom: 20px">
      <template #header><span>封存操作</span></template>
      <el-form :model="sealForm" label-width="100px" style="max-width: 500px">
        <el-form-item label="物理位置">
          <el-input v-model="sealForm.physicalLocation" placeholder="请输入物理存放位置" />
        </el-form-item>
        <el-form-item label="电子路径">
          <el-input v-model="sealForm.electronicPath" placeholder="请输入电子存储路径" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="sealing" @click="handleSeal">确认封存</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card style="margin-bottom: 20px">
      <template #header><span>封存材料清单</span></template>
      <el-table :data="sealedMaterials" stripe>
        <el-table-column prop="materialType" label="材料类型" width="160" />
        <el-table-column prop="description" label="描述" min-width="200" />
        <el-table-column prop="sealedAt" label="封存时间" width="180" />
      </el-table>
    </el-card>

    <el-card>
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center">
          <span>启封申请</span>
          <el-button v-if="archiveData.archiveStatus === 'SEALED'" type="warning" @click="openUnsealDialog">申请启封</el-button>
        </div>
      </template>
      <el-table :data="unsealRequests" stripe>
        <el-table-column prop="requestedBy" label="申请人" width="120" />
        <el-table-column prop="reason" label="启封原因" min-width="200" />
        <el-table-column prop="requestedAt" label="申请时间" width="180" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'PENDING' ? 'warning' : row.status === 'APPROVED' ? 'success' : 'danger'" size="small">
              {{ row.status === 'PENDING' ? '待审批' : row.status === 'APPROVED' ? '已批准' : '已拒绝' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160" v-if="archiveData.archiveStatus === 'SEALED'">
          <template #default="{ row }">
            <el-button v-if="row.status === 'PENDING'" link type="success" @click="handleUnsealApprove(row)">批准</el-button>
            <el-button v-if="row.status === 'PENDING'" link type="danger" @click="handleUnsealReject(row)">拒绝</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="unsealDialogVisible" title="申请启封" width="500px">
      <el-form :model="unsealForm" label-width="100px">
        <el-form-item label="启封原因">
          <el-input v-model="unsealForm.reason" type="textarea" :rows="3" placeholder="请输入启封原因" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="unsealDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="unsealing" @click="handleUnsealRequest">提交申请</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getArchiveDetail, sealCase, unsealCase, getUnsealRequests } from '@/api/archive'

const route = useRoute()
const caseId = route.params.id as string
const archiveData = ref<any>({})
const sealedMaterials = ref<any[]>([])
const unsealRequests = ref<any[]>([])
const sealing = ref(false)
const unsealing = ref(false)
const unsealDialogVisible = ref(false)

const sealForm = reactive({
  physicalLocation: '',
  electronicPath: '',
})

const unsealForm = reactive({
  reason: '',
})

async function loadDetail() {
  try {
    const res: any = await getArchiveDetail(caseId)
    archiveData.value = res.data || res
    sealedMaterials.value = res.data?.materials || res.materials || []
    const unsealRes: any = await getUnsealRequests({ caseId })
    unsealRequests.value = unsealRes.data?.list || unsealRes.list || unsealRes.data || []
  } catch { /* empty */ }
}

async function handleSeal() {
  if (!sealForm.physicalLocation && !sealForm.electronicPath) {
    ElMessage.warning('请至少填写一个存放位置')
    return
  }
  await ElMessageBox.confirm('确认封存该案件？', '封存确认', { type: 'warning' })
  sealing.value = true
  try {
    await sealCase(caseId, { ...sealForm })
    ElMessage.success('案件已封存')
    loadDetail()
  } catch {
    ElMessage.error('封存失败')
  } finally {
    sealing.value = false
  }
}

function openUnsealDialog() {
  unsealForm.reason = ''
  unsealDialogVisible.value = true
}

async function handleUnsealRequest() {
  if (!unsealForm.reason) {
    ElMessage.warning('请输入启封原因')
    return
  }
  unsealing.value = true
  try {
    await unsealCase(caseId, { ...unsealForm })
    ElMessage.success('启封申请已提交')
    unsealDialogVisible.value = false
    loadDetail()
  } catch {
    ElMessage.error('申请失败')
  } finally {
    unsealing.value = false
  }
}

async function handleUnsealApprove(row: any) {
  await ElMessageBox.confirm('确认批准该启封申请？', '批准确认', { type: 'warning' })
  try {
    await unsealCase(caseId, { requestId: row.id, action: 'APPROVE' })
    ElMessage.success('已批准启封')
    loadDetail()
  } catch {
    ElMessage.error('操作失败')
  }
}

async function handleUnsealReject(row: any) {
  await ElMessageBox.confirm('确认拒绝该启封申请？', '拒绝确认', { type: 'warning' })
  try {
    await unsealCase(caseId, { requestId: row.id, action: 'REJECT' })
    ElMessage.success('已拒绝启封')
    loadDetail()
  } catch {
    ElMessage.error('操作失败')
  }
}

onMounted(() => {
  loadDetail()
})
</script>
