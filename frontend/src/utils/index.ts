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
  { label: '其他', value: 'OTHER' },
]

export function getFeeTypeLabel(feeType: string): string {
  return feeTypeOptions.find(o => o.value === feeType)?.label || feeType
}

export function formatCurrency(amount: number): string {
  return '¥' + amount.toFixed(2).replace(/\B(?=(\d{3})+(?!\d))/g, ',')
}
