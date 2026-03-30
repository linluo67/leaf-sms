import Server from '../utils/Server'

const AuthService = {
  login(data, autoLogin = false) {
    return Server.post('/api/auth/login', data, { autoLogin })
  },

  logout() {
    return Server.post('/api/auth/logout')
  },

  register(data) {
    return Server.post('/api/auth/register', data)
  },

  getCurrentUser() {
    return Server.get('/api/auth/me')
  },

  updateUserInfo(data) {
    return Server.put('/api/auth/me', data)
  },

  changePassword(data) {
    return Server.put('/api/auth/password', data)
  },

  updateNotificationSettings(data) {
    return Server.put('/api/auth/notification-settings', data)
  }
}

const DashboardService = {
  getStats() {
    return Server.get('/api/dashboard/admin/stats')
  },

  getTeacherStats() {
    return Server.get('/api/dashboard/teacher/stats')
  },

  getAcademicStats() {
    return Server.get('/api/dashboard/academic/stats')
  },

  getHeadTeacherStats() {
    return Server.get('/api/dashboard/headteacher/stats')
  },

  getParentStats() {
    return Server.get('/api/dashboard/parent/stats')
  }
}

const StudentService = {
  getList(params) {
    return Server.get('/api/v1/students', {
      page: params.page || 1,
      size: params.size || 10,
      name: params.name,
      studentNo: params.studentNo,
      grade: params.grade,
      classId: params.classId,
      status: params.status
    })
  },

  getById(id) {
    return Server.get(`/api/v1/students/${id}`)
  },

  add(data) {
    return Server.post('/api/v1/students', data)
  },

  update(id, data) {
    return Server.put(`/api/v1/students/${id}`, data)
  },

  delete(id) {
    return Server.delete(`/api/v1/students/${id}`)
  },

  getStatistics() {
    return Server.get('/api/v1/students/statistics')
  },

  import(formData) {
    return Server.post('/api/v1/students/import', formData, { headers: { 'Content-Type': 'multipart/form-data' } })
  },

  export(params) {
    return Server.get('/api/v1/students/export', params, { responseType: 'blob' })
  }
}

const ClazzService = {
  getList(params) {
    return Server.get('/api/v1/classes', {
      page: params.page || 1,
      size: params.size || 10,
      grade: params.grade,
      className: params.className
    })
  },

  getById(id) {
    return Server.get(`/api/v1/classes/${id}`)
  },

  add(data) {
    return Server.post('/api/v1/classes', data)
  },

  update(id, data) {
    return Server.put(`/api/v1/classes/${id}`, data)
  },

  delete(id) {
    return Server.delete(`/api/v1/classes/${id}`)
  },

  listAll() {
    return Server.get('/api/v1/classes/all')
  },

  export(params) {
    return Server.get('/api/v1/classes/export', params, { responseType: 'blob' })
  }
}

const StudentChangeService = {
  getList(params) {
    return Server.get('/api/v1/changes', {
      page: params.page || 1,
      size: params.size || 10,
      changeType: params.changeType,
      status: params.status,
      startDate: params.startDate,
      endDate: params.endDate
    })
  },

  getById(id) {
    return Server.get(`/api/v1/changes/${id}`)
  },

  add(data) {
    return Server.post('/api/v1/changes', data)
  },

  review(id, data) {
    return Server.put(`/api/v1/changes/${id}/review`, data)
  },

  getStatistics() {
    return Server.get('/api/v1/changes/statistics')
  },

  getTrend(period) {
    return Server.get('/api/v1/changes/trend', { period })
  },

  export(params) {
    return Server.get('/api/v1/changes/export', params, { responseType: 'blob' })
  }
}

const UserService = {
  getList(params) {
    return Server.get('/api/v1/users', {
      page: params.page || 1,
      size: params.size || 10,
      keyword: params.keyword,
      roleCode: params.roleCode,
      status: params.status
    })
  },

  getById(id) {
    return Server.get(`/api/v1/users/${id}`)
  },

  add(data) {
    return Server.post('/api/v1/users', data)
  },

  update(id, data) {
    return Server.put(`/api/v1/users/${id}`, data)
  },

  delete(id) {
    return Server.delete(`/api/v1/users/${id}`)
  },

  updateStatus(id, status) {
    return Server.put(`/api/v1/users/${id}/status`, { status })
  },

  resetPassword(id, newPassword) {
    return Server.put(`/api/v1/users/${id}/reset-password`, newPassword)
  },

  sendResetCode(data) {
    return Server.post('/api/v1/users/send-reset-code', data)
  }
}

const LogService = {
  getList(params) {
    return Server.get('/api/operation-logs', {
      page: params.page || 1,
      size: params.size || 10,
      startDate: params.startDate,
      endDate: params.endDate,
      operationType: params.operationType
    })
  },

  getStats(params) {
    return Server.get('/api/operation-logs/stats', params)
  },

  getByType(operationType) {
    return Server.get(`/api/operation-logs/type/${operationType}`)
  },

  clearLogs() {
    return Server.delete('/api/operation-logs')
  },

  logOperation(data) {
    return Server.post('/api/operation-logs', data)
  }
}

const GradeService = {
  getList(params) {
    return Server.get('/api/v1/grades/with-student', {
      page: params.page || 1,
      size: params.size || 10,
      classId: params.classId,
      subject: params.subject,
      examName: params.examName
    })
  },

  getById(id) {
    return Server.get(`/api/v1/grades/${id}`)
  },

  add(data) {
    return Server.post('/api/v1/grades', data)
  },

  update(id, data) {
    return Server.put(`/api/v1/grades/${id}`, data)
  },

  delete(id) {
    return Server.delete(`/api/v1/grades/${id}`)
  },

  getStatistics(params) {
    return Server.get('/api/v1/grades/statistics', params)
  },

  export(params) {
    return Server.get('/api/v1/grades/export', params, { responseType: 'blob' })
  }
}

const AttendanceService = {
  getList(params) {
    return Server.get('/api/v1/attendance/with-student', {
      page: params.page || 1,
      size: params.size || 10,
      classId: params.classId,
      startDate: params.startDate,
      endDate: params.endDate,
      status: params.status
    })
  },

  getById(id) {
    return Server.get(`/api/v1/attendance/${id}`)
  },

  add(data) {
    return Server.post('/api/v1/attendance', data)
  },

  update(id, data) {
    return Server.put(`/api/v1/attendance/${id}`, data)
  },

  delete(id) {
    return Server.delete(`/api/v1/attendance/${id}`)
  },

  getStatistics(params) {
    return Server.get('/api/v1/attendance/statistics', params)
  },

  export(params) {
    return Server.get('/api/v1/attendance/export', params, { responseType: 'blob' })
  }
}

const MessageService = {
  getList(params) {
    return Server.get('/api/v1/messages', {
      page: params.page || 1,
      size: params.size || 10,
      isRead: params.isRead
    })
  },

  getUnreadCount() {
    return Server.get('/api/v1/messages/unread-count')
  },

  getById(id) {
    return Server.get(`/api/v1/messages/${id}`)
  },

  add(data) {
    return Server.post('/api/v1/messages', data)
  },

  markRead(id) {
    return Server.put(`/api/v1/messages/${id}/read`)
  },

  markAllRead() {
    return Server.put('/api/v1/messages/read-all')
  },

  delete(id) {
    return Server.delete(`/api/v1/messages/${id}`)
  }
}

export default {
  auth: AuthService,
  dashboard: DashboardService,
  students: StudentService,
  classes: ClazzService,
  changes: StudentChangeService,
  users: UserService,
  logs: LogService,
  grades: GradeService,
  attendance: AttendanceService,
  messages: MessageService
}
