<template>
  <div class="page-container">
    <h2 class="page-title">费用管理</h2>

    <el-card style="margin-bottom: 20px">
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center">
          <span>费用列表</span>
          <el-button type="primary" @click="openAddFeeDialog"><el-icon><Plus /></el-icon> 添加费用</el-button>
        </div>
      </template>
      <el-table :data="feeList" stripe>
        <el-table-column prop="feeType" label="费用类型" width="140">
          <template #default="{ row }">{{ getFeeTypeLabel(row.feeType) }}</template>
        </el-table-column>
        <el-table-column prop="amount" label="金额" width="160">
          <template #default="{ row }">{{ formatCurrency(row.amount) }}</template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusTagType(row.status)" size="small">{{ getStatusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="receiptNumber" label="收据编号" width="160" />
        <el-table-column prop="paidBy" label="付款人" width="120" />
        <el-table-column prop="paidAt" label="付款时间" width="180" />
      </el-table>
    </el-card>

    <el-card style="margin-bottom: 20px">
      <template #header><span>记录付款</span></template>
      <el-form :model="paymentForm" label-width="100px" style="max-width: 500px" inline>
        <el-form-item label="收据编号">
          <el-input v-model="paymentForm.receiptNumber" placeholder="请输入收据编号" />
        </el-form-item>
        <el-form-item label="付款人">
          <el-input v-model="paymentForm.paidBy" placeholder="请输入付款人" />
        </el-form-item>
        <el-form-item>
          <el-button type="success" :loading="paying" @click="handlePayment">确认收款</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card style="margin-bottom: 20px">
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center">
          <span>打印收据</span>
        </div>
      </template>
      <el-button type="primary" :disabled="!feeList.some(f => f.status === 'PAID')" @click="handlePrintReceipt">
        <el-icon><Printer /></el-icon> 打印收据
      </el-button>
    </el-card>

    <el-card>
      <template #header><span>退款处理</span></template>
      <el-form :model="refundForm" label-width="100px" style="max-width: 500px">
        <el-form-item label="退款原因">
          <el-input v-model="refundForm.reason" type="textarea" :rows="3" placeholder="请输入退款原因" />
        </el-form-item>
        <el-form-item label="退款金额">
          <el-input-number v-model="refundForm.amount" :min="0" :precision="2" style="width: 200px" />
        </el-form-item>
        <el-form-item>
          <el-button type="danger" :loading="refunding" @click="handleRefund">确认退款</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-dialog v-model="addFeeDialogVisible" title="添加费用" width="500px">
      <el-form :model="addFeeForm" :rules="addFeeRules" ref="addFeeFormRef" label-width="100px">
        <el-form-item label="费用类型" prop="feeType">
          <el-select v-model="addFeeForm.feeType" placeholder="请选择费用类型" style="width: 100%">
            <el-option v-for="opt in feeTypeOptions" :key="opt.value" :label="opt.label" :value="opt.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="金额" prop="amount">
          <el-input-number v-model="addFeeForm.amount" :min="0" :precision="2" style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addFeeDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="addingFee" @click="handleAddFee">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getFeeList, addFee, recordPayment, refundPayment } from '@/api/cashier'
import { getStatusTagType, getStatusLabel, getFeeTypeLabel, formatCurrency, feeTypeOptions } from '@/utils'

const route = useRoute()
const caseId = route.params.id as string
const feeList = ref<any[]>([])
const paying = ref(false)
const refunding = ref(false)
const addingFee = ref(false)
const addFeeDialogVisible = ref(false)
const addFeeFormRef = ref()

const paymentForm = reactive({
  receiptNumber: '',
  paidBy: '',
})

const refundForm = reactive({
  reason: '',
  amount: null as number | null,
})

const addFeeForm = reactive({
  feeType: '',
  amount: null as number | null,
})

const addFeeRules = {
  feeType: [{ required: true, message: '请选择费用类型', trigger: 'change' }],
  amount: [{ required: true, message: '请输入金额', trigger: 'blur' }],
}

async function loadFees() {
  try {
    const res: any = await getFeeList(caseId)
    feeList.value = res.data?.list || res.list || res.data || []
  } catch { /* empty */ }
}

function openAddFeeDialog() {
  addFeeForm.feeType = ''
  addFeeForm.amount = null
  addFeeDialogVisible.value = true
}

async function handleAddFee() {
  const valid = await addFeeFormRef.value?.validate().catch(() => false)
  if (!valid) return
  addingFee.value = true
  try {
    await addFee(caseId, { ...addFeeForm })
    ElMessage.success('费用已添加')
    addFeeDialogVisible.value = false
    loadFees()
  } catch {
    ElMessage.error('添加失败')
  } finally {
    addingFee.value = false
  }
}

async function handlePayment() {
  if (!paymentForm.receiptNumber || !paymentForm.paidBy) {
    ElMessage.warning('请填写完整付款信息')
    return
  }
  await ElMessageBox.confirm('确认已收到款项？', '确认收款', { type: 'info' })
  paying.value = true
  try {
    await recordPayment(caseId, { ...paymentForm })
    ElMessage.success('收款记录已保存')
    loadFees()
  } catch {
    ElMessage.error('操作失败')
  } finally {
    paying.value = false
  }
}

function handlePrintReceipt() {
  window.print()
}

async function handleRefund() {
  if (!refundForm.reason || !refundForm.amount) {
    ElMessage.warning('请填写完整退款信息')
    return
  }
  await ElMessageBox.confirm(`确认退款 ${formatCurrency(refundForm.amount!)}？`, '确认退款', { type: 'warning' })
  refunding.value = true
  try {
    await refundPayment(caseId, { ...refundForm })
    ElMessage.success('退款已处理')
    loadFees()
  } catch {
    ElMessage.error('退款失败')
  } finally {
    refunding.value = false
  }
}

onMounted(() => {
  loadFees()
})
</script>
