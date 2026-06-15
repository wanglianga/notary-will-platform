<template>
  <div class="page-container">
    <h2 class="page-title">亲属关系</h2>
    <el-card>
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center">
          <span>亲属列表</span>
          <el-button type="primary" @click="openAddDialog"><el-icon><Plus /></el-icon> 添加亲属</el-button>
        </div>
      </template>
      <el-table :data="kinshipList" stripe>
        <el-table-column prop="relativeName" label="姓名" width="120" />
        <el-table-column prop="relation" label="关系" width="120">
          <template #default="{ row }">{{ getRelationLabel(row.relation) }}</template>
        </el-table-column>
        <el-table-column prop="idNumber" label="证件号码" width="180">
          <template #default="{ row }">{{ maskIdNumber(row.idNumber) }}</template>
        </el-table-column>
        <el-table-column prop="phone" label="联系电话" width="140" />
        <el-table-column prop="isBeneficiary" label="受益人" width="80">
          <template #default="{ row }">
            <el-tag :type="row.isBeneficiary ? 'success' : 'info'" size="small">
              {{ row.isBeneficiary ? '是' : '否' }}
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

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑亲属' : '添加亲属'" width="500px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="姓名" prop="relativeName"><el-input v-model="form.relativeName" placeholder="请输入姓名" /></el-form-item>
        <el-form-item label="关系" prop="relation">
          <el-select v-model="form.relation" placeholder="请选择关系" style="width: 100%">
            <el-option v-for="opt in relationOptions" :key="opt.value" :label="opt.label" :value="opt.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="证件号码" prop="idNumber"><el-input v-model="form.idNumber" placeholder="请输入证件号码" /></el-form-item>
        <el-form-item label="联系电话" prop="phone"><el-input v-model="form.phone" placeholder="请输入联系电话" /></el-form-item>
        <el-form-item label="是否受益人">
          <el-checkbox v-model="form.isBeneficiary">是受益人</el-checkbox>
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
import { getKinshipList, addKinship, updateKinship, deleteKinship } from '@/api/case'
import { maskIdNumber, relationOptions } from '@/utils'

const route = useRoute()
const caseId = route.params.id as string
const kinshipList = ref<any[]>([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const editIndex = ref(-1)
const formRef = ref()
const saving = ref(false)

const form = reactive({
  relativeName: '',
  relation: '',
  idNumber: '',
  phone: '',
  isBeneficiary: false,
})

const rules = {
  relativeName: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  relation: [{ required: true, message: '请选择关系', trigger: 'change' }],
  idNumber: [{ required: true, message: '请输入证件号码', trigger: 'blur' }],
}

function getRelationLabel(relation: string) {
  return relationOptions.find(o => o.value === relation)?.label || relation
}

function resetForm() {
  form.relativeName = ''
  form.relation = ''
  form.idNumber = ''
  form.phone = ''
  form.isBeneficiary = false
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
    if (isEdit.value && kinshipList.value[editIndex.value]?.id) {
      await updateKinship(caseId, kinshipList.value[editIndex.value].id, { ...form })
    } else {
      await addKinship(caseId, { ...form })
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
  await ElMessageBox.confirm('确定删除该亲属记录？', '提示', { type: 'warning' })
  try {
    if (row.id) await deleteKinship(caseId, row.id)
    kinshipList.value.splice(index, 1)
    ElMessage.success('删除成功')
  } catch {
    ElMessage.error('删除失败')
  }
}

async function loadData() {
  try {
    const res: any = await getKinshipList(caseId)
    kinshipList.value = res.data?.list || res.list || res.data || []
  } catch { /* empty */ }
}

onMounted(() => {
  loadData()
})
</script>
