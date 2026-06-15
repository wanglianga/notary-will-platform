<template>
  <div class="page-container">
    <h2 class="page-title">案件详情</h2>
    <el-card v-if="caseStore.currentCase" style="margin-bottom: 20px">
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center">
          <span>案件信息</span>
          <el-tag :type="getStatusTagType(caseStore.currentCase.status)" size="large">
            {{ getStatusLabel(caseStore.currentCase.status) }}
          </el-tag>
        </div>
      </template>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="案件编号">{{ caseStore.currentCase.caseNumber }}</el-descriptions-item>
        <el-descriptions-item label="遗嘱类型">{{ caseStore.currentCase.willType }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ caseStore.currentCase.createdAt }}</el-descriptions-item>
        <el-descriptions-item label="更新时间">{{ caseStore.currentCase.updatedAt }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ caseStore.currentCase.remarks || '无' }}</el-descriptions-item>
      </el-descriptions>
    </el-card>

    <el-card>
      <template #header><span>案件流程</span></template>
      <el-row :gutter="16">
        <el-col :span="6" v-for="step in steps" :key="step.path">
          <el-card shadow="hover" class="step-card" @click="$router.push(step.path)">
            <el-icon :size="32" :color="step.color"><component :is="step.icon" /></el-icon>
            <div class="step-title">{{ step.title }}</div>
          </el-card>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useCaseStore } from '@/stores/case'
import { getStatusTagType, getStatusLabel } from '@/utils'

const route = useRoute()
const caseStore = useCaseStore()
const caseId = computed(() => route.params.id as string)

const steps = computed(() => [
  { title: '身份信息', icon: 'User', color: '#409eff', path: `/applicant/case/${caseId.value}/identity` },
  { title: '财产清单', icon: 'Money', color: '#67c23a', path: `/applicant/case/${caseId.value}/property` },
  { title: '亲属关系', icon: 'Connection', color: '#e6a23c', path: `/applicant/case/${caseId.value}/kinship` },
  { title: '健康状况 🔒', icon: 'FirstAidKit', color: '#f56c6c', path: `/applicant/case/${caseId.value}/health` },
  { title: '受益人', icon: 'Star', color: '#9b59b6', path: `/applicant/case/${caseId.value}/beneficiaries` },
  { title: '见证人', icon: 'View', color: '#3498db', path: `/applicant/case/${caseId.value}/witnesses` },
  { title: '预约办理', icon: 'Calendar', color: '#1abc9c', path: `/applicant/case/${caseId.value}/appointment` },
  { title: '材料上传', icon: 'Upload', color: '#e74c3c', path: `/applicant/case/${caseId.value}/materials` },
])

onMounted(() => {
  caseStore.fetchCaseDetail(caseId.value)
})
</script>

<style scoped>
.step-card {
  text-align: center;
  cursor: pointer;
  margin-bottom: 16px;
  transition: transform 0.2s;
}
.step-card:hover {
  transform: translateY(-4px);
}
.step-title {
  margin-top: 8px;
  font-size: 14px;
  color: #606266;
}
</style>
