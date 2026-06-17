import request from './index'

export function getFeeList(caseId: string) {
  return request.get(`/cashier/cases/${caseId}/fees`)
}

export function addFee(caseId: string, data: Record<string, any>) {
  return request.post(`/cashier/cases/${caseId}/fees`, data)
}

export function recordPayment(caseId: string, data: Record<string, any>) {
  return request.post(`/cashier/cases/${caseId}/payment`, data)
}

export function refundPayment(caseId: string, data: Record<string, any>) {
  return request.post(`/cashier/cases/${caseId}/refund`, data)
}

export function getPaymentStatistics(params?: Record<string, any>) {
  return request.get('/cashier/statistics', { params })
}

export function getPendingPayments(params?: Record<string, any>) {
  return request.get('/cashier/pending', { params })
}

export function checkReInterviewFee(caseId: string) {
  return request.get(`/cashier/cases/${caseId}/re-interview-check`)
}

export function generateReInterviewFee(caseId: string, data: Record<string, any>) {
  return request.post(`/cashier/cases/${caseId}/re-interview-fee`, data)
}
