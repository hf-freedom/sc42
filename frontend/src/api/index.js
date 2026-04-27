import axios from 'axios'

const api = axios.create({
  baseURL: '/api',
  timeout: 10000
})

export const bookApi = {
  getAll: () => api.get('/books'),
  getById: (id) => api.get(`/books/${id}`),
  create: (data) => api.post('/books', data),
  getStatus: (id) => api.get(`/books/${id}/status`)
}

export const userApi = {
  getAll: () => api.get('/users'),
  getById: (id) => api.get(`/users/${id}`),
  create: (data) => api.post('/users', data),
  getInfo: (id) => api.get(`/users/${id}/info`),
  checkBlacklist: (id) => api.get(`/users/${id}/blacklist/check`),
  addToBlacklist: (id, reason) => api.post(`/users/${id}/blacklist/add`, { reason }),
  removeFromBlacklist: (id) => api.post(`/users/${id}/blacklist/remove`)
}

export const borrowApi = {
  getAll: () => api.get('/borrows'),
  getById: (id) => api.get(`/borrows/${id}`),
  getByUserId: (userId) => api.get(`/borrows/user/${userId}`),
  getActiveByUserId: (userId) => api.get(`/borrows/user/${userId}/active`),
  borrow: (userId, bookId) => api.post('/borrows/borrow', { userId, bookId }),
  return: (id, isDamaged) => api.post(`/borrows/${id}/return`, { isDamaged }),
  renew: (id) => api.post(`/borrows/${id}/renew`),
  getOverdue: () => api.get('/borrows/overdue'),
  getCount: (userId) => api.get(`/borrows/user/${userId}/count`)
}

export const reservationApi = {
  getAll: () => api.get('/reservations'),
  getById: (id) => api.get(`/reservations/${id}`),
  getByUserId: (userId) => api.get(`/reservations/user/${userId}`),
  getPendingByUserId: (userId) => api.get(`/reservations/user/${userId}/pending`),
  reserve: (userId, bookId) => api.post('/reservations', { userId, bookId }),
  cancel: (id) => api.post(`/reservations/${id}/cancel`),
  hasPending: (bookId) => api.get(`/reservations/book/${bookId}/has-pending`)
}

export const fineApi = {
  getAll: () => api.get('/fines'),
  getById: (id) => api.get(`/fines/${id}`),
  getByUserId: (userId) => api.get(`/fines/user/${userId}`),
  getPendingByUserId: (userId) => api.get(`/fines/user/${userId}/pending`),
  pay: (id) => api.post(`/fines/${id}/pay`),
  hasPending: (userId) => api.get(`/fines/user/${userId}/has-pending`)
}

export const compensationApi = {
  getAll: () => api.get('/compensations'),
  getById: (id) => api.get(`/compensations/${id}`),
  getByUserId: (userId) => api.get(`/compensations/user/${userId}`),
  getPendingByUserId: (userId) => api.get(`/compensations/user/${userId}/pending`),
  create: (data) => api.post('/compensations', data),
  pay: (id) => api.post(`/compensations/${id}/pay`),
  hasPending: (userId) => api.get(`/compensations/user/${userId}/has-pending`)
}

export const flowLogApi = {
  getAll: () => api.get('/flow-logs'),
  getByUserId: (userId) => api.get(`/flow-logs/user/${userId}`)
}

export const initApi = {
  initData: () => api.post('/init')
}

export default api