<template>
  <div class="page-container">
    <h2 class="page-title">身份信息</h2>
    <el-card>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="120px" style="max-width: 600px">
        <el-form-item label="姓名" prop="name">
          <el-input v-model="form.name" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="证件类型" prop="idType">
          <el-select v-model="form.idType" placeholder="请选择证件类型" style="width: 100%">
            <el-option v-for="opt in idTypeOptions" :key="opt.value" :label="opt.label" :value="opt.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="证件号码" prop="idNumber">
          <el-input v-model="form.idNumber" placeholder="请输入证件号码" />
        </el-form-item>
        <el-form-item label="性别" prop="gender">
          <el-radio-group v-model="form.gender">
            <el-radio v-for="opt in genderOptions" :key="opt.value" :value="opt.value">{{ opt.label }}</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="出生日期" prop="birthDate">
          <el-date-picker v-model="form.birthDate" type="date" placeholder="请选择出生日期" style="width: 100%" value-format="YYYY-MM-DD" />
        </el-form-item>
        <el-form-item label="国籍" prop="nationality">
          <el-input v-model="form.nationality" placeholder="请输入国籍" />
        </el-form-item>
        <el-form-item label="住址" prop="address">
          <el-input v-model="form.address" type="textarea" :rows="2" placeholder="请输入住址" />
        </el-form-item>
        <el-form-item label="联系电话" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="saving" @click="handleSave">保存</el-button>
          <el-button @click="$router.back()">返回</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getIdentityInfo, updateIdentityInfo } from '@/api/case'
import { idTypeOptions, genderOptions } from '@/utils'

const route = useRoute()
const formRef = ref()
const saving = ref(false)
const caseId = route.params.id as string

const form = reactive({
  name: '',
  idType: '',
  idNumber: '',
  gender: '',
  birthDate: '',
  nationality: '',
  address: '',
  phone: '',
})

const rules = {
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  idType: [{ required: true, message: '请选择证件类型', trigger: 'change' }],
  idNumber: [{ required: true, message: '请输入证件号码', trigger: 'blur' }],
  gender: [{ required: true, message: '请选择性别', trigger: 'change' }],
  birthDate: [{ required: true, message: '请选择出生日期', trigger: 'change' }],
}

async function loadData() {
  try {
    const res: any = await getIdentityInfo(caseId)
    const data = res.data || res
    Object.assign(form, data)
  } catch { /* empty */ }
}

async function handleSave() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  saving.value = true
  try {
    await updateIdentityInfo(caseId, { ...form })
    ElMessage.success('保存成功')
  } catch {
    ElMessage.error('保存失败')
  } finally {
    saving.value = false
  }
}

onMounted(() => {
  loadData()
})
</script>
