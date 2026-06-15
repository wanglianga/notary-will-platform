import { defineStore } from 'pinia'
import { ref } from 'vue'
import { getCaseList, getMyCases, getCaseDetail } from '@/api/case'
import { useAuthStore } from './auth'

export const useCaseStore = defineStore('case', () => {
  const caseList = ref<any[]>([])
  const currentCase = ref<Record<string, any> | null>(null)
  const loading = ref(false)

  async function fetchCaseList(params?: Record<string, any>) {
    loading.value = true
    try {
      const authStore = useAuthStore()
      const res: any = authStore.role === 'APPLICANT' ? await getMyCases() : await getCaseList(params)
      caseList.value = res.data?.list || res.list || res.data || []
    } finally {
      loading.value = false
    }
  }

  async function fetchCaseDetail(id: string) {
    loading.value = true
    try {
      const res: any = await getCaseDetail(id)
      currentCase.value = res.data || res
    } finally {
      loading.value = false
    }
  }

  function clearCurrentCase() {
    currentCase.value = null
  }

  return { caseList, currentCase, loading, fetchCaseList, fetchCaseDetail, clearCurrentCase }
})
