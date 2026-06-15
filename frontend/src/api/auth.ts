import request from './index'

export function login(data: { username: string; password: string }) {
  return request.post('/auth/login', data)
}

export function getUserInfo() {
  return request.get('/auth/me')
}

export function logout() {
  return request.post('/auth/logout')
}
