<template>
  <div class="page-container">
    <h2 class="page-title">案件办理</h2>

    <el-steps :active="activeStep" finish-status="success" align-center style="margin-bottom: 24px">
      <el-step title="材料审阅" />
      <el-step title="面谈记录" />
      <el-step title="风险评估" />
      <el-step title="见证签署" />
      <el-step title="完结签发" />
    </el-steps>

    <el-card v-if="activeStep === 0" style="margin-bottom: 20px">
      <template #header><span>申请人材料（只读）</span></template>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="案件编号">{{ caseData.caseNumber }}</el-descriptions-item>
        <el-descriptions-item label="申请人">{{ caseData.applicantName }}</el-descriptions-item>
        <el-descriptions-item label="证件号码">{{ caseData.identity?.idNumber }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ caseData.identity?.phone }}</el-descriptions-item>
      </el-descriptions>
      <div style="margin-top: 16px; text-align: right">
        <el-button type="primary" @click="activeStep = 1">下一步：面谈</el-button>
      </div>
    </el-card>

    <el-card v-if="activeStep === 1" style="margin-bottom: 20px">
      <template #header><span>面谈记录 🔒</span></template>
      <el-form :model="interviewForm" label-width="100px" style="max-width: 600px">
        <el-form-item label="面谈日期">
          <el-date-picker v-model="interviewForm.date" type="date" placeholder="选择日期" style="width: 100%" value-format="YYYY-MM-DD" />
        </el-form-item>
        <el-form-item label="面谈内容">
          <el-input v-model="interviewForm.content" type="textarea" :rows="6" placeholder="请记录面谈内容" />
        </el-form-item>
        <el-form-item label="风险提示">
          <el-switch v-model="interviewForm.riskAlert" active-text="存在风险" inactive-text="无风险" />
        </el-form-item>
        <el-form-item v-if="interviewForm.riskAlert" label="风险内容">
          <el-input v-model="interviewForm.riskAlertContent" type="textarea" :rows="3" placeholder="请描述风险内容" />
        </el-form-item>
        <el-form-item>
          <el-button @click="activeStep = 0">上一步</el-button>
          <el-button type="primary" :loading="saving" @click="saveInterview">保存并下一步</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card v-if="activeStep === 2" style="margin-bottom: 20px">
      <template #header><span>风险评估</span></template>
      <el-alert v-if="caseData.riskAlerts && caseData.riskAlerts.length" type="warning" :closable="false" style="margin-bottom: 16px">
        <template #title>存在风险提示</template>
        <ul>
          <li v-for="(alert, i) in caseData.riskAlerts" :key="i">{{ alert }}</li>
        </ul>
      </el-alert>
      <el-form :model="riskForm" label-width="100px" style="max-width: 600px">
        <el-form-item label="风险等级">
          <el-select v-model="riskForm.riskLevel" placeholder="请选择" style="width: 100%">
            <el-option label="低风险" value="LOW" />
            <el-option label="中风险" value="MEDIUM" />
            <el-option label="高风险" value="HIGH" />
          </el-select>
        </el-form-item>
        <el-form-item label="风险说明">
          <el-input v-model="riskForm.riskDescription" type="textarea" :rows="3" placeholder="请输入风险说明" />
        </el-form-item>
        <el-form-item>
          <el-button @click="activeStep = 1">上一步</el-button>
          <el-button type="primary" :loading="saving" @click="saveRiskAssessment">保存并下一步</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card v-if="activeStep === 3" style="margin-bottom: 20px">
      <template #header><span>见证签署</span></template>
      <el-form :model="witnessForm" label-width="120px" style="max-width: 600px">
        <el-form-item label="见证人到场确认">
          <el-checkbox v-model="witnessForm.witnessPresent">确认见证人已到场</el-checkbox>
        </el-form-item>
        <el-form-item label="签署详情">
          <el-input v-model="witnessForm.signingDetails" type="textarea" :rows="3" placeholder="请记录签署详情" />
        </el-form-item>
        <el-divider />
        <el-form-item label="见证视频 🔒">
          <el-input v-model="videoForm.videoUrl" placeholder="视频URL" style="margin-bottom: 8px" />
          <el-input-number v-model="videoForm.duration" :min="0" placeholder="视频时长(秒)" style="width: 100%" />
        </el-form-item>
        <el-form-item>
          <el-button @click="activeStep = 2">上一步</el-button>
          <el-button type="primary" :loading="saving" @click="saveWitnessSigning">保存并下一步</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card v-if="activeStep === 4">
      <template #header><span>完结签发</span></template>
      <el-result icon="success" title="案件办理完毕" sub-title="请确认所有流程已完成，签发公证文书">
        <template #extra>
          <el-button type="primary" size="large" :loading="saving" @click="handleSignOff">确认签发</el-button>
        </template>
      </el-result>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getNotaryCaseDetail, createInterviewRecord, updateRiskAssessment,
  confirmWitnessSigning, uploadVideoMetadata, signOffCase,
} from '@/api/notary'

const route = useRoute()
const caseId = route.params.id as string
const caseData = ref<any>({})
const activeStep = ref(0)
const saving = ref(false)

const interviewForm = reactive({
  date: '',
  content: '',
  riskAlert: false,
  riskAlertContent: '',
})

const riskForm = reactive({
  riskLevel: '',
  riskDescription: '',
})

const witnessForm = reactive({
  witnessPresent: false,
  signingDetails: '',
})

const videoForm = reactive({
  videoUrl: '',
  duration: null as number | null,
})

async function loadDetail() {
  try {
    const res: any = await getNotaryCaseDetail(caseId)
    caseData.value = res.data || res
  } catch { /* empty */ }
}

async function saveInterview() {
  saving.value = true
  try {
    await createInterviewRecord(caseId, { ...interviewForm })
    ElMessage.success('面谈记录已保存')
    activeStep.value = 2
  } catch {
    ElMessage.error('保存失败')
  } finally {
    saving.value = false
  }
}

async function saveRiskAssessment() {
  saving.value = true
  try {
    await updateRiskAssessment(caseId, { ...riskForm })
    ElMessage.success('风险评估已保存')
    activeStep.value = 3
  } catch {
    ElMessage.error('保存失败')
  } finally {
    saving.value = false
  }
}

async function saveWitnessSigning() {
  saving.value = true
  try {
    await confirmWitnessSigning(caseId, { ...witnessForm })
    if (videoForm.videoUrl) {
      await uploadVideoMetadata(caseId, { ...videoForm })
    }
    ElMessage.success('见证签署已保存')
    activeStep.value = 4
  } catch {
    ElMessage.error('保存失败')
  } finally {
    saving.value = false
  }
}

async function handleSignOff() {
  await ElMessageBox.confirm('确认签发该公证文书？此操作不可撤销。', '确认签发', { type: 'warning' })
  saving.value = true
  try {
    await signOffCase(caseId, {})
    ElMessage.success('公证文书已签发')
  } catch {
    ElMessage.error('签发失败')
  } finally {
    saving.value = false
  }
}

onMounted(() => {
  loadDetail()
})
</script>
