import request from './index'

export function getCaseList(params?: Record<string, any>) {
  return request.get('/cases', { params })
}

export function getMyCases() {
  return request.get('/cases/my')
}

export function getCaseDetail(id: string) {
  return request.get(`/cases/${id}`)
}

export function createCase(data: Record<string, any>) {
  return request.post('/cases', data)
}

export function updateCase(id: string, data: Record<string, any>) {
  return request.put(`/cases/${id}`, data)
}

export function getIdentityInfo(caseId: string) {
  return request.get(`/cases/${caseId}/identity`)
}

export function updateIdentityInfo(caseId: string, data: Record<string, any>) {
  return request.put(`/cases/${caseId}/identity`, data)
}

export function getPropertyList(caseId: string) {
  return request.get(`/cases/${caseId}/properties`)
}

export function addProperty(caseId: string, data: Record<string, any>) {
  return request.post(`/cases/${caseId}/properties`, data)
}

export function updateProperty(caseId: string, propertyId: string, data: Record<string, any>) {
  return request.put(`/cases/${caseId}/properties/${propertyId}`, data)
}

export function deleteProperty(caseId: string, propertyId: string) {
  return request.delete(`/cases/${caseId}/properties/${propertyId}`)
}

export function getPropertyVersions(caseId: string) {
  return request.get(`/cases/${caseId}/properties/versions`)
}

export function getKinshipList(caseId: string) {
  return request.get(`/cases/${caseId}/kinship`)
}

export function addKinship(caseId: string, data: Record<string, any>) {
  return request.post(`/cases/${caseId}/kinship`, data)
}

export function updateKinship(caseId: string, kinshipId: string, data: Record<string, any>) {
  return request.put(`/cases/${caseId}/kinship/${kinshipId}`, data)
}

export function deleteKinship(caseId: string, kinshipId: string) {
  return request.delete(`/cases/${caseId}/kinship/${kinshipId}`)
}

export function getHealthDeclaration(caseId: string) {
  return request.get(`/cases/${caseId}/health`)
}

export function updateHealthDeclaration(caseId: string, data: Record<string, any>) {
  return request.put(`/cases/${caseId}/health`, data)
}

export function getHealthVersions(caseId: string) {
  return request.get(`/cases/${caseId}/health/versions`)
}

export function getBeneficiaryList(caseId: string) {
  return request.get(`/cases/${caseId}/beneficiaries`)
}

export function addBeneficiary(caseId: string, data: Record<string, any>) {
  return request.post(`/cases/${caseId}/beneficiaries`, data)
}

export function updateBeneficiary(caseId: string, beneficiaryId: string, data: Record<string, any>) {
  return request.put(`/cases/${caseId}/beneficiaries/${beneficiaryId}`, data)
}

export function deleteBeneficiary(caseId: string, beneficiaryId: string) {
  return request.delete(`/cases/${caseId}/beneficiaries/${beneficiaryId}`)
}

export function getWitnessList(caseId: string) {
  return request.get(`/cases/${caseId}/witnesses`)
}

export function addWitness(caseId: string, data: Record<string, any>) {
  return request.post(`/cases/${caseId}/witnesses`, data)
}

export function updateWitness(caseId: string, witnessId: string, data: Record<string, any>) {
  return request.put(`/cases/${caseId}/witnesses/${witnessId}`, data)
}

export function deleteWitness(caseId: string, witnessId: string) {
  return request.delete(`/cases/${caseId}/witnesses/${witnessId}`)
}

export function getAvailableSlots(caseId: string) {
  return request.get(`/cases/${caseId}/appointment/slots`)
}

export function bookAppointment(caseId: string, data: Record<string, any>) {
  return request.post(`/cases/${caseId}/appointment`, data)
}

export function getMaterialList(caseId: string) {
  return request.get(`/cases/${caseId}/materials`)
}

export function uploadMaterial(caseId: string, data: FormData) {
  return request.post(`/cases/${caseId}/materials`, data, {
    headers: { 'Content-Type': 'multipart/form-data' },
  })
}

export function getSupplementRequests(caseId: string) {
  return request.get(`/cases/${caseId}/materials/supplements`)
}
