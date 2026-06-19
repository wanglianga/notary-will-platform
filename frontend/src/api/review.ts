import request from './index'

export function getReviewQueue(params?: Record<string, any>) {
  return request.get('/review/queue', { params })
}

export function getReviewDetail(caseId: string) {
  return request.get(`/review/case/${caseId}`)
}

export function reviewMaterial(caseId: string, materialId: string, data: Record<string, any>) {
  return request.put(`/review/case/${caseId}/material/${materialId}`, data)
}

export function submitReviewDecision(caseId: string, data: Record<string, any>) {
  return request.post(`/review/case/${caseId}/decision`, data)
}

export function generateSupplementList(caseId: string, data: Record<string, any>) {
  return request.post(`/review/case/${caseId}/supplement`, data)
}

export function getHighRiskInterviewSummary(caseId: string) {
  return request.get(`/cases/${caseId}/high-risk-interviews/reviewer-view`)
}

export function requestSupplementWithValidity(caseId: string, data: Record<string, any>) {
  return request.post(`/cases/${caseId}/materials/supplement-request-with-validity`, data)
}

export function getSupplementVersions(caseId: string, supplementItemId: string) {
  return request.get(`/cases/${caseId}/materials/supplements/${supplementItemId}/versions`)
}

export function getSupplementVersionsByCase(caseId: string) {
  return request.get(`/cases/${caseId}/materials/supplement-versions`)
}

export function submitSupplementVersion(caseId: string, supplementItemId: string, data: Record<string, any>) {
  return request.post(`/cases/${caseId}/materials/supplements/${supplementItemId}/versions`, data)
}

export function reviewSupplementVersion(caseId: string, versionId: string, data: Record<string, any>) {
  return request.post(`/cases/${caseId}/materials/supplement-versions/${versionId}/review`, data)
}
