export function maskIdNumber(idNumber: string): string {
  if (!idNumber || idNumber.length < 7) return idNumber || ''
  return idNumber.slice(0, 3) + '****' + idNumber.slice(-4)
}

export function getStatusTagType(status: string): string {
  const map: Record<string, string> = {
    DRAFT: 'info',
    SUBMITTED: '',
    UNDER_REVIEW: 'warning',
    REVIEW_PASSED: 'success',
    REVIEW_FAILED: 'danger',
    APPOINTMENT_SCHEDULED: '',
    INTERVIEWING: 'warning',
    WITNESS_SIGNING: '',
    ARCHIVED: 'success',
    CLOSED: 'info',
    PAID: 'success',
    UNPAID: 'danger',
    REFUNDED: 'info',
    SUPPLEMENT_REQUIRED: 'warning',
  }
  return map[status] || 'info'
}

export function getStatusLabel(status: string): string {
  const map: Record<string, string> = {
    DRAFT: '草稿',
    SUBMITTED: '已提交',
    UNDER_REVIEW: '审核中',
    REVIEW_PASSED: '审核通过',
    REVIEW_FAILED: '审核未通过',
    APPOINTMENT_SCHEDULED: '已预约',
    INTERVIEWING: '谈话中',
    WITNESS_SIGNING: '见证签署',
    ARCHIVED: '已归档',
    CLOSED: '已结案',
    PAID: '已支付',
    UNPAID: '未支付',
    REFUNDED: '已退款',
    SUPPLEMENT_REQUIRED: '需补件',
  }
  return map[status] || status
}

export const idTypeOptions = [
  { label: '居民身份证', value: 'ID_CARD' },
  { label: '护照', value: 'PASSPORT' },
  { label: '军官证', value: 'MILITARY_ID' },
  { label: '港澳通行证', value: 'HK_MACAO_PASS' },
  { label: '台湾通行证', value: 'TW_PASS' },
  { label: '其他', value: 'OTHER' },
]

export const genderOptions = [
  { label: '男', value: 'MALE' },
  { label: '女', value: 'FEMALE' },
]

export const relationOptions = [
  { label: '配偶', value: 'SPOUSE' },
  { label: '父亲', value: 'FATHER' },
  { label: '母亲', value: 'MOTHER' },
  { label: '儿子', value: 'SON' },
  { label: '女儿', value: 'DAUGHTER' },
  { label: '兄弟', value: 'BROTHER' },
  { label: '姐妹', value: 'SISTER' },
  { label: '其他', value: 'OTHER' },
]

export const propertyTypeOptions = [
  { label: '不动产', value: 'REAL_ESTATE' },
  { label: '存款', value: 'DEPOSIT' },
  { label: '股权', value: 'EQUITY' },
  { label: '其他', value: 'OTHER' },
]

export const mentalStatusOptions = [
  { label: '正常', value: 'NORMAL' },
  { label: '受损', value: 'IMPAIRED' },
  { label: '其他', value: 'OTHER' },
]

export const feeTypeOptions = [
  { label: '公证费', value: 'NOTARY_FEE' },
  { label: '登记费', value: 'REGISTRATION_FEE' },
  { label: '副本费', value: 'COPY_FEE' },
  { label: '重新谈话费', value: 'RE_INTERVIEW_FEE' },
  { label: '其他', value: 'OTHER' },
]

export function getFeeTypeLabel(feeType: string): string {
  return feeTypeOptions.find(o => o.value === feeType)?.label || feeType
}

export function formatCurrency(amount: number): string {
  return '¥' + amount.toFixed(2).replace(/\B(?=(\d{3})+(?!\d))/g, ',')
}

export const interviewTriggerOptions = [
  { label: '申请人年龄较大', value: 'ELDERLY' },
  { label: '表达不清', value: 'UNCLEAR_EXPRESSION' },
  { label: '亲属陪同过度', value: 'EXCESSIVE_COMPANION' },
  { label: '疑似受胁迫', value: 'SUSPECTED_COERCION' },
]

export function getInterviewTriggerLabel(trigger: string): string {
  return interviewTriggerOptions.find(o => o.value === trigger)?.label || trigger
}

export const continueDecisionOptions = [
  { label: '继续办理', value: 'CONTINUE' },
  { label: '暂停办理', value: 'SUSPEND' },
  { label: '终止办理', value: 'TERMINATE' },
]

export function getContinueDecisionLabel(decision: string): string {
  return continueDecisionOptions.find(o => o.value === decision)?.label || decision
}

export const supplementMaterialTypeOptions = [
  { label: '房产证', value: 'PROPERTY_CERTIFICATE' },
  { label: '银行存单', value: 'BANK_CERTIFICATE' },
  { label: '亲属关系证明', value: 'KINSHIP_CERTIFICATE' },
  { label: '医学证明', value: 'MEDICAL_CERTIFICATE' },
  { label: '其他', value: 'OTHER' },
]

export function getSupplementMaterialTypeLabel(type: string): string {
  return supplementMaterialTypeOptions.find(o => o.value === type)?.label || type
}

export const supplementStatusLabels: Record<string, string> = {
  PENDING: '待补件',
  SUBMITTED: '已提交',
  APPROVED: '已审核',
}

export const supplementStatusTagTypes: Record<string, string> = {
  PENDING: 'danger',
  SUBMITTED: 'warning',
  APPROVED: 'success',
}
