<template>
  <div class="page-container">
    <h2 class="page-title">见证人</h2>
    <el-card>
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center">
          <span>见证人列表</span>
          <el-button type="primary" @click="openAddDialog"><el-icon><Plus /></el-icon> 添加见证人</el-button>
        </div>
      </template>
      <el-table :data="witnessList" stripe>
        <el-table-column prop="name" label="姓名" width="120" />
        <el-table-column prop="idType" label="证件类型" width="120">
          <template #default="{ row }">{{ getIdTypeLabel(row.idType) }}</template>
        </el-table-column>
        <el-table-column prop="idNumber" label="证件号码" width="180">
          <template #default="{ row }">{{ maskIdNumber(row.idNumber) }}</template>
        </el-table-column>
        <el-table-column prop="phone" label="联系电话" width="140" />
        <el-table-column prop="relation" label="与立遗嘱人关系" width="140">
          <template #default="{ row }">{{ getRelationLabel(row.relation) }}</template>
        </el-table-column>
        <el-table-column prop="declaredNoConflict" label="无利益冲突声明" width="140">
          <template #default="{ row }">
            <el-tag :type="row.declaredNoConflict ? 'success' : 'danger'" size="small">
              {{ row.declaredNoConflict ? '已声明' : '未声明' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row, $index }">
            <el-button link type="primary" @click="openEditDialog(row, $index)">编辑</el-button>
            <el-button link type="danger" @click="handleDelete(row, $index)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑见证人' : '添加见证人'" width="500px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="130px">
        <el-form-item label="姓名" prop="name"><el-input v-model="form.name" placeholder="请输入姓名" /></el-form-item>
        <el-form-item label="证件类型" prop="idType">
          <el-select v-model="form.idType" placeholder="请选择证件类型" style="width: 100%">
            <el-option v-for="opt in idTypeOptions" :key="opt.value" :label="opt.label" :value="opt.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="证件号码" prop="idNumber"><el-input v-model="form.idNumber" placeholder="请输入证件号码" /></el-form-item>
        <el-form-item label="联系电话" prop="phone"><el-input v-model="form.phone" placeholder="请输入联系电话" /></el-form-item>
        <el-form-item label="与立遗嘱人关系" prop="relation">
          <el-select v-model="form.relation" placeholder="请选择关系" style="width: 100%">
            <el-option v-for="opt in relationOptions" :key="opt.value" :label="opt.label" :value="opt.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="无利益冲突声明">
          <el-checkbox v-model="form.declaredNoConflict">声明无利益冲突</el-checkbox>
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
import { getWitnessList, addWitness, updateWitness, deleteWitness } from '@/api/case'
import { maskIdNumber, idTypeOptions, relationOptions } from '@/utils'

const route = useRoute()
const caseId = route.params.id as string
const witnessList = ref<any[]>([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const editIndex = ref(-1)
const formRef = ref()
const saving = ref(false)

const form = reactive({
  name: '',
  idType: '',
  idNumber: '',
  phone: '',
  relation: '',
  declaredNoConflict: false,
})

const rules = {
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  idType: [{ required: true, message: '请选择证件类型', trigger: 'change' }],
  idNumber: [{ required: true, message: '请输入证件号码', trigger: 'blur' }],
}

function getIdTypeLabel(type: string) {
  return idTypeOptions.find(o => o.value === type)?.label || type
}

function getRelationLabel(relation: string) {
  return relationOptions.find(o => o.value === relation)?.label || relation
}

function resetForm() {
  form.name = ''
  form.idType = ''
  form.idNumber = ''
  form.phone = ''
  form.relation = ''
  form.declaredNoConflict = false
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
    if (isEdit.value && witnessList.value[editIndex.value]?.id) {
      await updateWitness(caseId, witnessList.value[editIndex.value].id, { ...form })
    } else {
      await addWitness(caseId, { ...form })
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
  await ElMessageBox.confirm('确定删除该见证人？', '提示', { type: 'warning' })
  try {
    if (row.id) await deleteWitness(caseId, row.id)
    witnessList.value.splice(index, 1)
    ElMessage.success('删除成功')
  } catch {
    ElMessage.error('删除失败')
  }
}

async function loadData() {
  try {
    const res: any = await getWitnessList(caseId)
    witnessList.value = res.data?.list || res.list || res.data || []
  } catch { /* empty */ }
}

onMounted(() => {
  loadData()
})
</script>
