<template>
  <div class="page-container">
    <h2 class="page-title">财产清单</h2>
    <el-card style="margin-bottom: 20px">
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center">
          <span>财产列表</span>
          <el-button type="primary" @click="openAddDialog"><el-icon><Plus /></el-icon> 添加财产</el-button>
        </div>
      </template>
      <el-table :data="propertyList" stripe>
        <el-table-column prop="propertyType" label="类型" width="120">
          <template #default="{ row }">
            <el-tag size="small">{{ getPropertyTypeLabel(row.propertyType) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" min-width="200">
          <template #default="{ row }">{{ getPropertyDescription(row) }}</template>
        </el-table-column>
        <el-table-column prop="value" label="价值" width="140">
          <template #default="{ row }">{{ formatCurrency(row.value) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row, $index }">
            <el-button link type="primary" @click="openEditDialog(row, $index)">编辑</el-button>
            <el-button link type="danger" @click="handleDelete(row, $index)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
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
          版本 {{ v.version }} - {{ v.changeDescription || '更新' }}
        </el-timeline-item>
      </el-timeline>
      <el-empty v-else description="暂无版本记录" />
    </el-card>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑财产' : '添加财产'" width="560px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="财产类型" prop="propertyType">
          <el-select v-model="form.propertyType" placeholder="请选择财产类型" style="width: 100%" @change="onTypeChange">
            <el-option v-for="opt in propertyTypeOptions" :key="opt.value" :label="opt.label" :value="opt.value" />
          </el-select>
        </el-form-item>
        <template v-if="form.propertyType === 'REAL_ESTATE'">
          <el-form-item label="地址" prop="address"><el-input v-model="form.address" placeholder="请输入地址" /></el-form-item>
          <el-form-item label="面积(㎡)" prop="area"><el-input-number v-model="form.area" :min="0" :precision="2" style="width: 100%" /></el-form-item>
          <el-form-item label="产权证号" prop="certificateNo"><el-input v-model="form.certificateNo" placeholder="请输入产权证号" /></el-form-item>
        </template>
        <template v-if="form.propertyType === 'DEPOSIT'">
          <el-form-item label="银行" prop="bank"><el-input v-model="form.bank" placeholder="请输入银行名称" /></el-form-item>
          <el-form-item label="账号" prop="accountNo"><el-input v-model="form.accountNo" placeholder="请输入账号" /></el-form-item>
        </template>
        <template v-if="form.propertyType === 'EQUITY'">
          <el-form-item label="公司名称" prop="companyName"><el-input v-model="form.companyName" placeholder="请输入公司名称" /></el-form-item>
          <el-form-item label="持股比例(%)" prop="shareRatio"><el-input-number v-model="form.shareRatio" :min="0" :max="100" :precision="2" style="width: 100%" /></el-form-item>
        </template>
        <template v-if="form.propertyType === 'OTHER'">
          <el-form-item label="描述" prop="description"><el-input v-model="form.description" type="textarea" :rows="2" placeholder="请输入描述" /></el-form-item>
        </template>
        <el-form-item label="价值(元)" prop="value">
          <el-input-number v-model="form.value" :min="0" :precision="2" style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getPropertyList, addProperty, updateProperty, deleteProperty, getPropertyVersions } from '@/api/case'
import { propertyTypeOptions, formatCurrency } from '@/utils'

const route = useRoute()
const caseId = route.params.id as string
const propertyList = ref<any[]>([])
const versions = ref<any[]>([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const editIndex = ref(-1)
const formRef = ref()
const saving = ref(false)

const form = reactive({
  propertyType: '',
  address: '',
  area: null as number | null,
  certificateNo: '',
  bank: '',
  accountNo: '',
  amount: null as number | null,
  companyName: '',
  shareRatio: null as number | null,
  description: '',
  value: null as number | null,
})

const rules = {
  propertyType: [{ required: true, message: '请选择财产类型', trigger: 'change' }],
  value: [{ required: true, message: '请输入价值', trigger: 'blur' }],
}

function getPropertyTypeLabel(type: string) {
  return propertyTypeOptions.find(o => o.value === type)?.label || type
}

function getPropertyDescription(row: any) {
  switch (row.propertyType) {
    case 'REAL_ESTATE': return `${row.address || ''} ${row.area ? row.area + '㎡' : ''}`
    case 'DEPOSIT': return `${row.bank || ''} ${row.accountNo || ''}`
    case 'EQUITY': return `${row.companyName || ''} ${row.shareRatio ? row.shareRatio + '%' : ''}`
    case 'OTHER': return row.description || ''
    default: return ''
  }
}

function resetForm() {
  form.propertyType = ''
  form.address = ''
  form.area = null
  form.certificateNo = ''
  form.bank = ''
  form.accountNo = ''
  form.amount = null
  form.companyName = ''
  form.shareRatio = null
  form.description = ''
  form.value = null
}

function onTypeChange() {
  form.address = ''
  form.area = null
  form.certificateNo = ''
  form.bank = ''
  form.accountNo = ''
  form.amount = null
  form.companyName = ''
  form.shareRatio = null
  form.description = ''
}

function openAddDialog() {
  isEdit.value = false
  editIndex.value = -1
  resetForm()
  dialogVisible.value = true
}

function openEditDialog(row: any, index: number) {
  isEdit.value = true
  editIndex.value = index
  Object.assign(form, { ...row })
  dialogVisible.value = true
}

async function handleSave() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  saving.value = true
  try {
    if (isEdit.value && propertyList.value[editIndex.value]?.id) {
      await updateProperty(caseId, propertyList.value[editIndex.value].id, { ...form })
    } else {
      await addProperty(caseId, { ...form })
    }
    ElMessage.success('保存成功')
    dialogVisible.value = false
    await loadData()
  } catch {
    ElMessage.error('保存失败')
  } finally {
    saving.value = false
  }
}

async function handleDelete(row: any, index: number) {
  await ElMessageBox.confirm('确定删除该财产记录？', '提示', { type: 'warning' })
  try {
    if (row.id) {
      await deleteProperty(caseId, row.id)
    }
    propertyList.value.splice(index, 1)
    ElMessage.success('删除成功')
  } catch {
    ElMessage.error('删除失败')
  }
}

async function loadData() {
  try {
    const res: any = await getPropertyList(caseId)
    propertyList.value = res.data?.list || res.list || res.data || []
  } catch { /* empty */ }
}

async function loadVersions() {
  try {
    const res: any = await getPropertyVersions(caseId)
    versions.value = res.data?.list || res.list || res.data || []
  } catch { /* empty */ }
}

onMounted(() => {
  loadData()
  loadVersions()
})
</script>
