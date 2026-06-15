import request from './index'

export function getArchiveList(params?: Record<string, any>) {
  return request.get('/archive/cases', { params })
}

export function getArchiveDetail(caseId: string) {
  return request.get(`/archive/cases/${caseId}`)
}

export function sealCase(caseId: string, data: Record<string, any>) {
  return request.post(`/archive/cases/${caseId}/seal`, data)
}

export function unsealCase(caseId: string, data: Record<string, any>) {
  return request.post(`/archive/cases/${caseId}/unseal`, data)
}

export function getUnsealRequests(params?: Record<string, any>) {
  return request.get('/archive/unseal-requests', { params })
}
