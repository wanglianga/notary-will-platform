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
