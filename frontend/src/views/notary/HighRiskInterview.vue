<template>
  <div class="page-container">
    <h2 class="page-title">高风险谈话管理</h2>

    <el-alert
      type="warning"
      :closable="false"
      show-icon
      style="margin-bottom: 20px"
    >
      <template #title>材料审核员只能看到需要补充的证明，不能替代公证员做意愿判断</template>
    </el-alert>

    <el-card>
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center">
          <span>高风险谈话记录</span>
          <el-button type="primary" @click="openCreateDialog">发起高风险谈话</el-button>
        </div>
      </template>

      <el-table :data="interviews" stripe style="width: 100%" @row-click="openDetailDialog">
        <el-table-column prop="triggerReason" label="触发原因" min-width="140">
          <template #default="{ row }">
            {{ getInterviewTriggerLabel(row.triggerReason) }}
          </template>
        </el-table-column>
        <el-table-column label="陪同人信息" min-width="140">
          <template #default="{ row }">
            <span v-if="row.companionName">{{ row.companionName }}（{{ row.companionRelation }}）</span>
            <span v-else style="color: #999">—</span>
          </template>
        </el-table-column>
        <el-table-column prop="qaSummary" label="问答摘要" min-width="180" show-overflow-tooltip />
        <el-table-column prop="videoRecordingNumber" label="录像编号" min-width="120" />
        <el-table-column label="处理决定" min-width="120">
          <template #default="{ row }">
            <el-tag :type="getDecisionTagType(row.continueDecision)">
              {{ getContinueDecisionLabel(row.continueDecision) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" min-width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusTagType(row.status)">
              {{ getStatusLabel(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click.stop="openDetailDialog(row)">查看</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="createDialogVisible" title="发起高风险谈话" width="640px" destroy-on-close>
      <el-form :model="createForm" label-width="110px" style="max-width: 560px">
        <el-form-item label="触发原因" required>
          <el-select v-model="createForm.triggerReason" placeholder="请选择触发原因" style="width: 100%">
            <el-option
              v-for="opt in interviewTriggerOptions"
              :key="opt.value"
              :label="opt.label"
              :value="opt.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item v-if="showCompanionFields" label="陪同人姓名" required>
          <el-input v-model="createForm.companionName" placeholder="请输入陪同人姓名" />
        </el-form-item>
        <el-form-item v-if="showCompanionFields" label="陪同人关系" required>
          <el-input v-model="createForm.companionRelation" placeholder="请输入与申请人的关系" />
        </el-form-item>
        <el-form-item label="问答摘要" required>
          <el-input v-model="createForm.qaSummary" type="textarea" :rows="4" placeholder="请记录谈话问答摘要" />
        </el-form-item>
        <el-form-item label="录像编号">
          <el-input v-model="createForm.videoRecordingNumber" placeholder="请输入录像编号" />
        </el-form-item>
        <el-form-item label="处理决定" required>
          <el-select v-model="createForm.continueDecision" placeholder="请选择处理决定" style="width: 100%">
            <el-option
              v-for="opt in continueDecisionOptions"
              :key="opt.value"
              :label="opt.label"
              :value="opt.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item v-if="showContinueReasonField" label="决定原因" required>
          <el-input v-model="createForm.continueReason" type="textarea" :rows="3" placeholder="请说明暂停/终止的原因" />
        </el-form-item>
        <el-form-item label="公证员意见" required>
          <el-input v-model="createForm.notaryOpinion" type="textarea" :rows="3" placeholder="请输入公证员意见" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="createDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleCreate">确认发起</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="detailDialogVisible" title="高风险谈话详情" width="640px" destroy-on-close>
      <el-descriptions :column="1" border v-if="currentDetail">
        <el-descriptions-item label="触发原因">
          {{ getInterviewTriggerLabel(currentDetail.triggerReason) }}
        </el-descriptions-item>
        <el-descriptions-item v-if="currentDetail.companionName" label="陪同人姓名">
          {{ currentDetail.companionName }}
        </el-descriptions-item>
        <el-descriptions-item v-if="currentDetail.companionRelation" label="陪同人关系">
          {{ currentDetail.companionRelation }}
        </el-descriptions-item>
        <el-descriptions-item label="问答摘要">
          {{ currentDetail.qaSummary }}
        </el-descriptions-item>
        <el-descriptions-item label="录像编号">
          {{ currentDetail.videoRecordingNumber || '—' }}
        </el-descriptions-item>
        <el-descriptions-item label="处理决定">
          <el-tag :type="getDecisionTagType(currentDetail.continueDecision)">
            {{ getContinueDecisionLabel(currentDetail.continueDecision) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item v-if="currentDetail.continueReason" label="决定原因">
          {{ currentDetail.continueReason }}
        </el-descriptions-item>
        <el-descriptions-item label="公证员意见">
          {{ currentDetail.notaryOpinion }}
        </el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusTagType(currentDetail.status)">
            {{ getStatusLabel(currentDetail.status) }}
          </el-tag>
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  getHighRiskInterviews,
  createHighRiskInterview,
} from '@/api/notary'
import {
  interviewTriggerOptions,
  continueDecisionOptions,
  getInterviewTriggerLabel,
  getContinueDecisionLabel,
  getStatusTagType,
  getStatusLabel,
} from '@/utils'

const route = useRoute()
const caseId = route.params.id as string

const interviews = ref<any[]>([])
const submitting = ref(false)
const createDialogVisible = ref(false)
const detailDialogVisible = ref(false)
const currentDetail = ref<any>(null)

const createForm = reactive({
  triggerReason: '',
  companionName: '',
  companionRelation: '',
  qaSummary: '',
  videoRecordingNumber: '',
  continueDecision: '',
  continueReason: '',
  notaryOpinion: '',
})

const showCompanionFields = computed(() =>
  ['EXCESSIVE_COMPANION', 'SUSPECTED_COERCION'].includes(createForm.triggerReason)
)

const showContinueReasonField = computed(() =>
  ['SUSPEND', 'TERMINATE'].includes(createForm.continueDecision)
)

function getDecisionTagType(decision: string): string {
  const map: Record<string, string> = {
    CONTINUE: 'success',
    SUSPEND: 'warning',
    TERMINATE: 'danger',
  }
  return map[decision] || 'info'
}

function resetCreateForm() {
  createForm.triggerReason = ''
  createForm.companionName = ''
  createForm.companionRelation = ''
  createForm.qaSummary = ''
  createForm.videoRecordingNumber = ''
  createForm.continueDecision = ''
  createForm.continueReason = ''
  createForm.notaryOpinion = ''
}

function openCreateDialog() {
  resetCreateForm()
  createDialogVisible.value = true
}

function openDetailDialog(row: any) {
  currentDetail.value = row
  detailDialogVisible.value = true
}

async function loadInterviews() {
  try {
    const res: any = await getHighRiskInterviews(caseId)
    interviews.value = res.data || res || []
  } catch {
    ElMessage.error('加载高风险谈话记录失败')
  }
}

async function handleCreate() {
  if (!createForm.triggerReason) {
    ElMessage.warning('请选择触发原因')
    return
  }
  if (showCompanionFields.value && !createForm.companionName) {
    ElMessage.warning('请输入陪同人姓名')
    return
  }
  if (showCompanionFields.value && !createForm.companionRelation) {
    ElMessage.warning('请输入陪同人关系')
    return
  }
  if (!createForm.qaSummary) {
    ElMessage.warning('请输入问答摘要')
    return
  }
  if (!createForm.continueDecision) {
    ElMessage.warning('请选择处理决定')
    return
  }
  if (showContinueReasonField.value && !createForm.continueReason) {
    ElMessage.warning('请输入决定原因')
    return
  }
  if (!createForm.notaryOpinion) {
    ElMessage.warning('请输入公证员意见')
    return
  }

  submitting.value = true
  try {
    const data: Record<string, any> = {
      triggerReason: createForm.triggerReason,
      qaSummary: createForm.qaSummary,
      continueDecision: createForm.continueDecision,
      notaryOpinion: createForm.notaryOpinion,
    }
    if (createForm.videoRecordingNumber) {
      data.videoRecordingNumber = createForm.videoRecordingNumber
    }
    if (showCompanionFields.value) {
      data.companionName = createForm.companionName
      data.companionRelation = createForm.companionRelation
    }
    if (showContinueReasonField.value) {
      data.continueReason = createForm.continueReason
    }
    await createHighRiskInterview(caseId, data)
    ElMessage.success('高风险谈话已发起')
    createDialogVisible.value = false
    await loadInterviews()
  } catch {
    ElMessage.error('发起失败')
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  loadInterviews()
})
</script>
