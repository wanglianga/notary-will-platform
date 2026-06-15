import request from './index'

export function getNotaryAppointments(params?: Record<string, any>) {
  return request.get('/notary/appointments', { params })
}

export function getNotaryCaseList(params?: Record<string, any>) {
  return request.get('/notary/cases', { params })
}

export function getNotaryCaseDetail(caseId: string) {
  return request.get(`/notary/cases/${caseId}`)
}

export function createInterviewRecord(caseId: string, data: Record<string, any>) {
  return request.post(`/notary/cases/${caseId}/interview`, data)
}

export function updateRiskAssessment(caseId: string, data: Record<string, any>) {
  return request.put(`/notary/cases/${caseId}/risk`, data)
}

export function confirmWitnessSigning(caseId: string, data: Record<string, any>) {
  return request.post(`/notary/cases/${caseId}/witness-signing`, data)
}

export function uploadVideoMetadata(caseId: string, data: Record<string, any>) {
  return request.post(`/notary/cases/${caseId}/video`, data)
}

export function signOffCase(caseId: string, data: Record<string, any>) {
  return request.post(`/notary/cases/${caseId}/signoff`, data)
}

export function manageSlots(data: Record<string, any>) {
  return request.post('/notary/slots', data)
}
