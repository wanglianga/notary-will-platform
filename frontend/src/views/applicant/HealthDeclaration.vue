<template>
  <div class="page-container">
    <h2 class="page-title">健康状况声明 🔒</h2>
    <el-alert type="warning" :closable="false" style="margin-bottom: 20px">
      <template #title>
        <el-icon class="privacy-icon"><Lock /></el-icon>
        此信息为隐私数据，仅授权人员可查看
      </template>
    </el-alert>
    <el-card style="margin-bottom: 20px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="120px" style="max-width: 600px">
        <el-form-item label="精神状态" prop="mentalStatus">
          <el-select v-model="form.mentalStatus" placeholder="请选择精神状态" style="width: 100%">
            <el-option v-for="opt in mentalStatusOptions" :key="opt.value" :label="opt.label" :value="opt.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="慢性疾病" prop="chronicDiseases">
          <el-input v-model="form.chronicDiseases" type="textarea" :rows="3" placeholder="请输入慢性疾病情况，无则填'无'" />
        </el-form-item>
        <el-form-item label="用药情况" prop="medicationInfo">
          <el-input v-model="form.medicationInfo" type="textarea" :rows="3" placeholder="请输入用药情况，无则填'无'" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="saving" @click="handleSave">保存</el-button>
          <el-button @click="$router.back()">返回</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card>
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center">
          <span>版本历史</span>
          <el-button size="small" @click="loadVersions"><el-icon><Refresh /></el-icon> 刷新</el-button>
        </div>
      </template>
      <el-timeline v-if="versions.length">
        <el-timeline-item v-for="(v, i) in versions" :key="i" :timestamp="v.createdAt" placement="top">
          版本 {{ v.version }} - 精神状态: {{ v.mentalStatus || '未填写' }}
        </el-timeline-item>
      </el-timeline>
      <el-empty v-else description="暂无版本记录" />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getHealthDeclaration, updateHealthDeclaration, getHealthVersions } from '@/api/case'
import { mentalStatusOptions } from '@/utils'

const route = useRoute()
const caseId = route.params.id as string
const formRef = ref()
const saving = ref(false)
const versions = ref<any[]>([])

const form = reactive({
  mentalStatus: '',
  chronicDiseases: '',
  medicationInfo: '',
})

const rules = {
  mentalStatus: [{ required: true, message: '请选择精神状态', trigger: 'change' }],
}

async function loadData() {
  try {
    const res: any = await getHealthDeclaration(caseId)
    const data = res.data || res
    Object.assign(form, data)
  } catch { /* empty */ }
}

async function loadVersions() {
  try {
    const res: any = await getHealthVersions(caseId)
    versions.value = res.data?.list || res.list || res.data || []
  } catch { /* empty */ }
}

async function handleSave() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  saving.value = true
  try {
    await updateHealthDeclaration(caseId, { ...form })
    ElMessage.success('保存成功')
    await loadVersions()
  } catch {
    ElMessage.error('保存失败')
  } finally {
    saving.value = false
  }
}

onMounted(() => {
  loadData()
  loadVersions()
})
</script>
