<template>
  <div class="page-container">
    <h2 class="page-title">新建遗嘱案件</h2>
    <el-card>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="120px" style="max-width: 600px">
        <el-form-item label="案件标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入案件标题" />
        </el-form-item>
        <el-form-item label="遗嘱类型" prop="willType">
          <el-select v-model="form.willType" placeholder="请选择遗嘱类型" style="width: 100%">
            <el-option label="自书遗嘱" value="SELF_WRITTEN" />
            <el-option label="代书遗嘱" value="PROXY_WRITTEN" />
            <el-option label="打印遗嘱" value="PRINTED" />
            <el-option label="录音录像遗嘱" value="AUDIO_VIDEO" />
            <el-option label="口头遗嘱" value="ORAL" />
            <el-option label="公证遗嘱" value="NOTARIZED" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注" prop="remarks">
          <el-input v-model="form.remarks" type="textarea" :rows="3" placeholder="请输入备注信息" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="submitting" @click="handleSubmit">提交创建</el-button>
          <el-button @click="$router.back()">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { createCase } from '@/api/case'

const router = useRouter()
const formRef = ref()
const submitting = ref(false)

const form = reactive({
  title: '',
  willType: '',
  remarks: '',
})

const rules = {
  title: [{ required: true, message: '请输入案件标题', trigger: 'blur' }],
  willType: [{ required: true, message: '请选择遗嘱类型', trigger: 'change' }],
}

async function handleSubmit() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  submitting.value = true
  try {
    const res: any = await createCase(form)
    ElMessage.success('案件创建成功')
    const caseId = res.data?.id || res.id
    router.push(`/applicant/case/${caseId}`)
  } catch {
    ElMessage.error('创建失败')
  } finally {
    submitting.value = false
  }
}
</script>
