import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/login',
      name: 'Login',
      component: () => import('@/views/Login.vue'),
      meta: { requiresAuth: false },
    },
    {
      path: '/',
      redirect: '/login',
    },
    {
      path: '/applicant',
      component: () => import('@/layouts/MainLayout.vue'),
      meta: { requiresAuth: true, role: 'APPLICANT' },
      children: [
        { path: '', name: 'ApplicantDashboard', component: () => import('@/views/applicant/Dashboard.vue') },
        { path: 'case/new', name: 'NewCase', component: () => import('@/views/applicant/NewCase.vue') },
        { path: 'case/:id', name: 'CaseDetail', component: () => import('@/views/applicant/CaseDetail.vue') },
        { path: 'case/:id/identity', name: 'IdentityInfo', component: () => import('@/views/applicant/IdentityInfo.vue') },
        { path: 'case/:id/property', name: 'PropertyInventory', component: () => import('@/views/applicant/PropertyInventory.vue') },
        { path: 'case/:id/kinship', name: 'KinshipRelations', component: () => import('@/views/applicant/KinshipRelations.vue') },
        { path: 'case/:id/health', name: 'HealthDeclaration', component: () => import('@/views/applicant/HealthDeclaration.vue') },
        { path: 'case/:id/beneficiaries', name: 'Beneficiaries', component: () => import('@/views/applicant/Beneficiaries.vue') },
        { path: 'case/:id/witnesses', name: 'Witnesses', component: () => import('@/views/applicant/Witnesses.vue') },
        { path: 'case/:id/appointment', name: 'AppointmentBooking', component: () => import('@/views/applicant/AppointmentBooking.vue') },
        { path: 'case/:id/materials', name: 'MaterialUpload', component: () => import('@/views/applicant/MaterialUpload.vue') },
      ],
    },
    {
      path: '/reviewer',
      component: () => import('@/layouts/MainLayout.vue'),
      meta: { requiresAuth: true, role: 'REVIEWER' },
      children: [
        { path: '', name: 'ReviewerDashboard', component: () => import('@/views/reviewer/Dashboard.vue') },
        { path: 'queue', name: 'ReviewQueue', component: () => import('@/views/reviewer/ReviewQueue.vue') },
        { path: 'case/:id', name: 'CaseReview', component: () => import('@/views/reviewer/CaseReview.vue') },
      ],
    },
    {
      path: '/notary',
      component: () => import('@/layouts/MainLayout.vue'),
      meta: { requiresAuth: true, role: 'NOTARY' },
      children: [
        { path: '', name: 'NotaryDashboard', component: () => import('@/views/notary/Dashboard.vue') },
        { path: 'appointments', name: 'AppointmentManagement', component: () => import('@/views/notary/AppointmentManagement.vue') },
        { path: 'case/:id', name: 'CaseProcessing', component: () => import('@/views/notary/CaseProcessing.vue') },
        { path: 'case/:id/high-risk-interview', name: 'HighRiskInterview', component: () => import('@/views/notary/HighRiskInterview.vue') },
      ],
    },
    {
      path: '/archivist',
      component: () => import('@/layouts/MainLayout.vue'),
      meta: { requiresAuth: true, role: 'ARCHIVIST' },
      children: [
        { path: '', name: 'ArchiveDashboard', component: () => import('@/views/archive/Dashboard.vue') },
        { path: 'case/:id', name: 'CaseArchive', component: () => import('@/views/archive/CaseArchive.vue') },
      ],
    },
    {
      path: '/cashier',
      component: () => import('@/layouts/MainLayout.vue'),
      meta: { requiresAuth: true, role: 'CASHIER' },
      children: [
        { path: '', name: 'CashierDashboard', component: () => import('@/views/cashier/Dashboard.vue') },
        { path: 'case/:id', name: 'FeeManagement', component: () => import('@/views/cashier/FeeManagement.vue') },
      ],
    },
  ],
})

router.beforeEach((to) => {
  const token = localStorage.getItem('token')
  const userRole = localStorage.getItem('userRole')

  if (to.meta.requiresAuth !== false && !token) {
    return '/login'
  }

  if (to.meta.requiresAuth === false && token) {
    if (userRole) {
      const rolePathMap: Record<string, string> = {
        APPLICANT: '/applicant',
        REVIEWER: '/reviewer',
        NOTARY: '/notary',
        ARCHIVIST: '/archivist',
        CASHIER: '/cashier',
      }
      return rolePathMap[userRole] || '/login'
    }
  }

  if (to.meta.role && to.meta.role !== userRole) {
    return '/login'
  }

  return true
})

export default router
