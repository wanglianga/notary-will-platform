<template>
  <div class="page-container">
    <h2 class="page-title">案件审核</h2>

    <el-card style="margin-bottom: 20px">
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center">
          <span>案件基本信息</span>
          <el-tag :type="getStatusTagType(caseData.status)" size="large">{{ getStatusLabel(caseData.status) }}</el-tag>
        </div>
      </template>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="案件编号">{{ caseData.caseNumber }}</el-descriptions-item>
        <el-descriptions-item label="申请人">{{ caseData.applicantName }}</el-descriptions-item>
      </el-descriptions>
    </el-card>

    <el-card style="margin-bottom: 20px">
      <template #header><span>身份证件审核</span></template>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="证件类型">{{ caseData.identity?.idType }}</el-descriptions-item>
        <el-descriptions-item label="证件号码">{{ caseData.identity?.idNumber }}</el-descriptions-item>
        <el-descriptions-item label="有效期">{{ caseData.identity?.expiryDate || '未提供' }}</el-descriptions-item>
      </el-descriptions>
      <div style="margin-top: 12px; display: flex; gap: 8px; align-items: center">
        <span>审核结果：</span>
        <el-radio-group v-model="identityReview.result" size="small">
          <el-radio-button value="APPROVED">通过</el-radio-button>
          <el-radio-button value="REJECTED">驳回</el-radio-button>
          <el-radio-button value="SUPPLEMENT_REQUIRED">需补件</el-radio-button>
        </el-radio-group>
        <el-input v-model="identityReview.comment" placeholder="审核意见" style="width: 300px" size="small" />
        <el-button type="primary" size="small" @click="submitMaterialReview('identity', identityReview)">提交</el-button>
      </div>
    </el-card>

    <el-card style="margin-bottom: 20px">
      <template #header><span>财产证明审核</span></template>
      <el-table :data="caseData.properties || []" stripe size="small">
        <el-table-column prop="propertyType" label="类型" width="100" />
        <el-table-column prop="description" label="描述" min-width="200" />
        <el-table-column prop="value" label="价值" width="120" />
        <el-table-column label="审核" width="360">
          <template #default="{ row }">
            <el-radio-group v-model="propertyReviews[row.id || row._idx]" size="small">
              <el-radio-button :value="`${row.id || row._idx}_APPROVED`">通过</el-radio-button>
              <el-radio-button :value="`${row.id || row._idx}_REJECTED`">驳回</el-radio-button>
              <el-radio-button :value="`${row.id || row._idx}_SUPPLEMENT_REQUIRED`">需补件</el-radio-button>
            </el-radio-group>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-card style="margin-bottom: 20px">
      <template #header><span>亲属关系审核</span></template>
      <el-table :data="caseData.kinship || []" stripe size="small">
        <el-table-column prop="relativeName" label="姓名" width="100" />
        <el-table-column prop="relation" label="关系" width="100" />
        <el-table-column prop="idNumber" label="证件号码" width="160" />
        <el-table-column label="审核" width="360">
          <template #default="{ row }">
            <el-radio-group v-model="kinshipReviews[row.id]" size="small">
              <el-radio-button :value="`${row.id}_APPROVED`">通过</el-radio-button>
              <el-radio-button :value="`${row.id}_REJECTED`">驳回</el-radio-button>
              <el-radio-button :value="`${row.id}_SUPPLEMENT_REQUIRED`">需补件</el-radio-button>
            </el-radio-group>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-card style="margin-bottom: 20px">
      <template #header><span>健康状况声明审核 🔒</span></template>
      <el-descriptions :column="1" border>
        <el-descriptions-item label="精神状态">{{ caseData.health?.mentalStatus }}</el-descriptions-item>
        <el-descriptions-item label="慢性疾病">{{ caseData.health?.chronicDiseases }}</el-descriptions-item>
        <el-descriptions-item label="用药情况">{{ caseData.health?.medicationInfo }}</el-descriptions-item>
      </el-descriptions>
      <div style="margin-top: 12px; display: flex; gap: 8px; align-items: center">
        <span>审核结果：</span>
        <el-radio-group v-model="healthReview.result" size="small">
          <el-radio-button value="APPROVED">通过</el-radio-button>
          <el-radio-button value="REJECTED">驳回</el-radio-button>
          <el-radio-button value="SUPPLEMENT_REQUIRED">需补件</el-radio-button>
        </el-radio-group>
        <el-input v-model="healthReview.comment" placeholder="审核意见" style="width: 300px" size="small" />
        <el-button type="primary" size="small" @click="submitMaterialReview('health', healthReview)">提交</el-button>
      </div>
    </el-card>

    <el-card style="margin-bottom: 20px">
      <template #header><span>高风险谈话摘要</span></template>
      <el-alert title="材料审核员只能看到需要补充的证明，不能替代公证员做意愿判断" type="warning" :closable="false" style="margin-bottom: 16px" />
      <template v-if="caseData.highRiskInterviewSummary">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="触发原因">{{ getInterviewTriggerLabel(caseData.highRiskInterviewSummary.triggerReason) }}</el-descriptions-item>
          <el-descriptions-item label="是否继续办理">{{ getContinueDecisionLabel(caseData.highRiskInterviewSummary.continueDecision) }}</el-descriptions-item>
          <el-descriptions-item label="录像编号">{{ caseData.highRiskInterviewSummary.videoRecordingNumber }}</el-descriptions-item>
        </el-descriptions>
      </template>
      <template v-else>
        <span>该案件无高风险谈话记录</span>
      </template>
    </el-card>

    <el-card style="margin-bottom: 20px">
      <template #header><span>生成补件清单</span></template>
      <el-form :model="supplementForm" label-width="100px" style="max-width: 500px">
        <el-form-item label="补件名称">
          <el-select v-model="supplementForm.materialType" placeholder="请选择补件类型" style="width: 100%">
            <el-option v-for="opt in supplementMaterialTypeOptions" :key="opt.value" :label="opt.label" :value="opt.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="补件说明">
          <el-input v-model="supplementForm.description" type="textarea" :rows="3" placeholder="请输入需要补充的材料说明" />
        </el-form-item>
        <el-form-item label="截止日期">
          <el-date-picker v-model="supplementForm.deadline" type="date" placeholder="选择截止日期" style="width: 100%" value-format="YYYY-MM-DD" />
        </el-form-item>
        <el-form-item label="有效期限（天）">
          <el-input-number v-model="supplementForm.validityPeriodDays" :min="1" style="width: 100%" />
        </el-form-item>
        <el-form-item label="可替代材料">
          <el-input v-model="supplementForm.alternativeMaterials" type="textarea" :rows="2" placeholder="请输入可替代的材料" />
        </el-form-item>
        <el-form-item label="预约保留时间（天）">
          <el-input-number v-model="supplementForm.reservationRetentionDays" :min="1" style="width: 100%" />
        </el-form-item>
        <el-form-item label="是否需要重新谈话">
          <el-switch v-model="supplementForm.requiresReInterview" />
        </el-form-item>
        <el-form-item>
          <el-button type="warning" @click="handleGenerateSupplement">生成补件清单</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card style="margin-bottom: 20px">
      <template #header><span>补件版本对比</span></template>
      <el-table :data="supplementVersions" stripe size="small">
        <el-table-column prop="versionNumber" label="版本号" width="80" />
        <el-table-column label="补件类型" width="120">
          <template #default="{ row }">{{ getSupplementMaterialTypeLabel(row.materialType) }}</template>
        </el-table-column>
        <el-table-column prop="description" label="说明" min-width="160" />
        <el-table-column prop="changeSummary" label="变更摘要" min-width="160" />
        <el-table-column prop="submittedBy" label="提交人" width="100" />
        <el-table-column label="审核结果" width="100">
          <template #default="{ row }">
            <el-tag :type="supplementStatusTagTypes[row.reviewResult]" size="small">{{ supplementStatusLabels[row.reviewResult] || row.reviewResult }}</el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-card>
      <template #header><span>案件审核决定</span></template>
      <div style="display: flex; gap: 12px; align-items: center">
        <el-button type="success" size="large" @click="handleDecision('APPROVED')">审核通过</el-button>
        <el-button type="danger" size="large" @click="handleDecision('REJECTED')">审核驳回</el-button>
        <el-input v-model="decisionComment" placeholder="审核决定意见" style="width: 400px" size="large" />
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getReviewDetail, reviewMaterial, submitReviewDecision, requestSupplementWithValidity, getSupplementVersionsByCase } from '@/api/review'
import { getStatusTagType, getStatusLabel, getInterviewTriggerLabel, getContinueDecisionLabel, supplementMaterialTypeOptions, supplementStatusLabels, supplementStatusTagTypes, getSupplementMaterialTypeLabel } from '@/utils'

const route = useRoute()
const caseId = route.params.id as string
const caseData = ref<any>({})
const decisionComment = ref('')

const identityReview = reactive({ result: '', comment: '' })
const healthReview = reactive({ result: '', comment: '' })
const propertyReviews = ref<Record<string, string>>({})
const kinshipReviews = ref<Record<string, string>>({})

const supplementForm = reactive({
  description: '',
  deadline: '',
  materialType: '',
  validityPeriodDays: 30,
  alternativeMaterials: '',
  reservationRetentionDays: 15,
  requiresReInterview: false,
})

const supplementVersions = ref<any[]>([])

async function loadDetail() {
  try {
    const res: any = await getReviewDetail(caseId)
    caseData.value = res.data || res
  } catch { /* empty */ }
}

async function submitMaterialReview(materialType: string, review: { result: string; comment: string }) {
  if (!review.result) {
    ElMessage.warning('请选择审核结果')
    return
  }
  try {
    await reviewMaterial(caseId, materialType, review)
    ElMessage.success('审核意见已提交')
  } catch {
    ElMessage.error('提交失败')
  }
}

async function handleGenerateSupplement() {
  if (!supplementForm.description) {
    ElMessage.warning('请输入补件说明')
    return
  }
  try {
    await requestSupplementWithValidity(caseId, { ...supplementForm })
    ElMessage.success('补件清单已生成')
  } catch {
    ElMessage.error('生成失败')
  }
}

async function handleDecision(decision: string) {
  const action = decision === 'APPROVED' ? '通过' : '驳回'
  await ElMessageBox.confirm(`确定${action}该案件？`, '审核决定', { type: 'warning' })
  try {
    await submitReviewDecision(caseId, { decision, comment: decisionComment.value })
    ElMessage.success(`案件已${action}`)
    loadDetail()
  } catch {
    ElMessage.error('操作失败')
  }
}

async function loadSupplementVersions() {
  try {
    const res: any = await getSupplementVersionsByCase(caseId)
    supplementVersions.value = res.data?.list || res.list || res.data || []
  } catch { /* empty */ }
}

onMounted(() => {
  loadDetail()
  loadSupplementVersions()
})
</script>
