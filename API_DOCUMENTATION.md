# Leaf SMS API 接口文档

本文档详细说明了 Leaf SMS 学生学籍管理系统的所有后端 API 接口。所有接口均以 `/api` 开头。

## 认证接口 (Auth)

用于系统登录、登出及当前用户信息维护。

### 1. 用户注册
- **URL**: `/api/auth/register`
- **Method**: `POST`
- **请求体**:
  ```json
  {
    "email": "user@example.com",
    "password": "password123"
  }
  ```
- **成功响应**: 返回 JWT Token 及用户基本信息。

### 2. 用户登录
- **URL**: `/api/auth/login`
- **Method**: `POST`
- **请求体**:
  ```json
  {
    "email": "chenmh@qq.com",
    "password": "123456"
  }
  ```
- **成功响应**: 返回 JWT Token 及用户基本信息。

### 3. 获取当前登录用户信息
- **URL**: `/api/auth/me`
- **Method**: `GET`
- **Header**: `Authorization: Bearer <Token>`
- **功能**: 获取当前 Token 对应的用户资料。

### 4. 更新当前用户信息
- **URL**: `/api/auth/me`
- **Method**: `PUT`
- **Header**: `Authorization: Bearer <Token>`
- **请求体**: 用户实体对象。

### 5. 修改当前用户密码
- **URL**: `/api/auth/password`
- **Method**: `PUT`
- **Header**: `Authorization: Bearer <Token>`
- **请求体**: `{"oldPassword": "旧密码", "newPassword": "新密码"}`

### 6. 用户登出
- **URL**: `/api/auth/logout`
- **Method**: `POST`
- **功能**: 登出系统。

### 7. 更新通知设置
- **URL**: `/api/auth/notification-settings`
- **Method**: `PUT`
- **Header**: `Authorization: Bearer <Token>`
- **请求体**: 通知设置对象。

---

## 控制台统计接口 (Dashboard)

### 1. 管理员统计
- **URL**: `/api/dashboard/admin/stats`
- **Method**: `GET`
- **功能**: 获取管理员控制台统计数据。
- **响应数据**: totalStudents, totalClasses, totalUsers, totalChanges

### 2. 教务管理员统计
- **URL**: `/api/dashboard/academic/stats`
- **Method**: `GET`
- **Header**: `Authorization: Bearer <Token>`
- **功能**: 获取教务管理员控制台统计数据。
- **响应数据**: totalStudents, totalClasses, pendingChanges

### 3. 班主任统计
- **URL**: `/api/dashboard/headteacher/stats`
- **Method**: `GET`
- **Header**: `Authorization: Bearer <Token>`
- **功能**: 获取班主任控制台统计数据。
- **响应数据**: myClassStudents, attendanceStats

### 4. 任课教师统计
- **URL**: `/api/dashboard/teacher/stats`
- **Method**: `GET`
- **Header**: `Authorization: Bearer <Token>`
- **功能**: 获取任课教师控制台统计数据。
- **响应数据**: totalStudents, totalClasses

### 5. 家长统计
- **URL**: `/api/dashboard/parent/stats`
- **Method**: `GET`
- **Header**: `Authorization: Bearer <Token>`
- **功能**: 获取家长控制台统计数据。
- **响应数据**: totalGrades, totalAttendances, unreadMessages

---

## 用户管理接口 (Users)

### 1. 分页查询用户列表
- **URL**: `/api/v1/users`
- **Method**: `GET`
- **Header**: `Authorization: Bearer <Token>`
- **参数**: `page`, `size`, `keyword`, `roleCode`, `status`

### 2. 获取指定用户
- **URL**: `/api/v1/users/{userId}`
- **Method**: `GET`
- **Header**: `Authorization: Bearer <Token>`

### 3. 创建用户
- **URL**: `/api/v1/users`
- **Method**: `POST`
- **Header**: `Authorization: Bearer <Token>`
- **请求体**: 用户实体对象。

### 4. 更新用户信息
- **URL**: `/api/v1/users/{userId}`
- **Method**: `PUT`
- **Header**: `Authorization: Bearer <Token>`
- **请求体**: 用户实体对象。

### 5. 删除用户
- **URL**: `/api/v1/users/{userId}`
- **Method**: `DELETE`
- **Header**: `Authorization: Bearer <Token>`

### 6. 重置用户密码
- **URL**: `/api/v1/users/{userId}/reset-password`
- **Method**: `PUT`
- **Header**: `Authorization: Bearer <Token>`
- **请求体**: 新密码字符串

### 7. 更新用户状态
- **URL**: `/api/v1/users/{userId}/status`
- **Method**: `PUT`
- **Header**: `Authorization: Bearer <Token>`
- **请求体**: `{"status": 1}` (1-启用，0-禁用)

### 8. 发送重置验证码
- **URL**: `/api/v1/users/send-reset-code`
- **Method**: `POST`
- **请求体**: `{"email": "邮箱地址"}`

---

## 学生管理接口 (Students)

### 1. 分页查询学生列表
- **URL**: `/api/v1/students`
- **Method**: `GET`
- **Header**: `Authorization: Bearer <Token>`
- **参数**: `page`, `size`, `name`, `studentNo`, `grade`, `classId`, `status`

### 2. 获取指定学生
- **URL**: `/api/v1/students/{studentId}`
- **Method**: `GET`
- **Header**: `Authorization: Bearer <Token>`

### 3. 按家长ID查询学生
- **URL**: `/api/v1/students/parent/{parentId}`
- **Method**: `GET`
- **Header**: `Authorization: Bearer <Token>`

### 4. 创建学生
- **URL**: `/api/v1/students`
- **Method**: `POST`
- **Header**: `Authorization: Bearer <Token>`
- **请求体**: 学生实体对象。

### 5. 更新学生信息
- **URL**: `/api/v1/students/{studentId}`
- **Method**: `PUT`
- **Header**: `Authorization: Bearer <Token>`
- **请求体**: 学生实体对象。

### 6. 删除学生
- **URL**: `/api/v1/students/{studentId}`
- **Method**: `DELETE`
- **Header**: `Authorization: Bearer <Token>`

### 7. 获取学生统计
- **URL**: `/api/v1/students/statistics`
- **Method**: `GET`
- **Header**: `Authorization: Bearer <Token>`

### 8. 批量导入学生
- **URL**: `/api/v1/students/import`
- **Method**: `POST`
- **Header**: `Authorization: Bearer <Token>`
- **Content-Type**: `multipart/form-data`
- **请求体**: Excel 文件。

### 9. 导出学生列表
- **URL**: `/api/v1/students/export`
- **Method**: `GET`
- **Header**: `Authorization: Bearer <Token>`
- **参数**: `type`, `grade`
- **响应**: Excel 文件流。

---

## 班级管理接口 (Classes)

### 1. 分页查询班级列表
- **URL**: `/api/v1/classes`
- **Method**: `GET`
- **Header**: `Authorization: Bearer <Token>`
- **参数**: `page`, `size`, `grade`, `className`

### 2. 获取所有班级
- **URL**: `/api/v1/classes/all`
- **Method**: `GET`
- **Header**: `Authorization: Bearer <Token>`

### 3. 获取指定班级
- **URL**: `/api/v1/classes/{classId}`
- **Method**: `GET`
- **Header**: `Authorization: Bearer <Token>`

### 4. 创建班级
- **URL**: `/api/v1/classes`
- **Method**: `POST`
- **Header**: `Authorization: Bearer <Token>`
- **请求体**: 班级实体对象。

### 5. 更新班级信息
- **URL**: `/api/v1/classes/{classId}`
- **Method**: `PUT`
- **Header**: `Authorization: Bearer <Token>`
- **请求体**: 班级实体对象。

### 6. 删除班级
- **URL**: `/api/v1/classes/{classId}`
- **Method**: `DELETE`
- **Header**: `Authorization: Bearer <Token>`

### 7. 导出班级列表
- **URL**: `/api/v1/classes/export`
- **Method**: `GET`
- **Header**: `Authorization: Bearer <Token>`
- **参数**: `type`, `grade`
- **响应**: Excel 文件流。

---

## 学籍变动管理接口 (Student Changes)

### 1. 分页查询变动列表
- **URL**: `/api/v1/changes`
- **Method**: `GET`
- **Header**: `Authorization: Bearer <Token>`
- **参数**: `page`, `size`, `studentName`, `changeType`, `status`

### 2. 获取指定变动
- **URL**: `/api/v1/changes/{changeId}`
- **Method**: `GET`
- **Header**: `Authorization: Bearer <Token>`

### 3. 按学生ID查询变动
- **URL**: `/api/v1/changes/student/{studentId}`
- **Method**: `GET`
- **Header**: `Authorization: Bearer <Token>`

### 4. 创建变动申请
- **URL**: `/api/v1/changes`
- **Method**: `POST`
- **Header**: `Authorization: Bearer <Token>`
- **请求体**: 学籍变动实体对象。

### 5. 审核变动
- **URL**: `/api/v1/changes/{changeId}/review`
- **Method**: `PUT`
- **Header**: `Authorization: Bearer <Token>`
- **请求体**: `{"status": "已通过", "reviewComment": "审核意见"}`

### 6. 获取变动统计
- **URL**: `/api/v1/changes/statistics`
- **Method**: `GET`
- **Header**: `Authorization: Bearer <Token>`
- **响应数据**: total, byType

### 7. 获取变动趋势
- **URL**: `/api/v1/changes/trend`
- **Method**: `GET`
- **Header**: `Authorization: Bearer <Token>`
- **参数**: `period` (时间周期: week/month/quarter/year)

### 8. 导出变动列表
- **URL**: `/api/v1/changes/export`
- **Method**: `GET`
- **Header**: `Authorization: Bearer <Token>`
- **参数**: `type`, `changeType`
- **响应**: Excel 文件流。

---

## 成绩管理接口 (Grades)

### 1. 分页查询成绩列表
- **URL**: `/api/v1/grades`
- **Method**: `GET`
- **Header**: `Authorization: Bearer <Token>`
- **参数**: `page`, `size`, `classId`, `subject`, `examName`, `semester`

### 2. 查询成绩（带学生信息）
- **URL**: `/api/v1/grades/with-student`
- **Method**: `GET`
- **Header**: `Authorization: Bearer <Token>`
- **参数**: `page`, `size`, `classId`, `subject`, `examName`

### 3. 按学生ID查询成绩
- **URL**: `/api/v1/grades/student/{studentId}`
- **Method**: `GET`
- **Header**: `Authorization: Bearer <Token>`

### 4. 获取指定成绩
- **URL**: `/api/v1/grades/{gradeId}`
- **Method**: `GET`
- **Header**: `Authorization: Bearer <Token>`

### 5. 创建成绩
- **URL**: `/api/v1/grades`
- **Method**: `POST`
- **Header**: `Authorization: Bearer <Token>`
- **请求体**: 成绩实体对象。

### 6. 更新成绩
- **URL**: `/api/v1/grades/{gradeId}`
- **Method**: `PUT`
- **Header**: `Authorization: Bearer <Token>`
- **请求体**: 成绩实体对象。

### 7. 删除成绩
- **URL**: `/api/v1/grades/{gradeId}`
- **Method**: `DELETE`
- **Header**: `Authorization: Bearer <Token>`

### 8. 获取成绩统计
- **URL**: `/api/v1/grades/statistics`
- **Method**: `GET`
- **Header**: `Authorization: Bearer <Token>`
- **参数**: `classId`

### 9. 获取班级成绩统计
- **URL**: `/api/v1/grades/class-stats`
- **Method**: `GET`
- **Header**: `Authorization: Bearer <Token>`
- **参数**: `classId`, `subject`, `examName`

### 10. 导出成绩列表
- **URL**: `/api/v1/grades/export`
- **Method**: `GET`
- **Header**: `Authorization: Bearer <Token>`
- **参数**: `classId`, `subject`, `examName`
- **响应**: Excel 文件流。

---

## 考勤管理接口 (Attendance)

### 1. 分页查询考勤列表
- **URL**: `/api/v1/attendance`
- **Method**: `GET`
- **Header**: `Authorization: Bearer <Token>`
- **参数**: `page`, `size`, `classId`, `startDate`, `endDate`, `status`

### 2. 查询考勤（带学生信息）
- **URL**: `/api/v1/attendance/with-student`
- **Method**: `GET`
- **Header**: `Authorization: Bearer <Token>`
- **参数**: `page`, `size`, `classId`, `startDate`, `endDate`, `status`

### 3. 按学生ID查询考勤
- **URL**: `/api/v1/attendance/student/{studentId}`
- **Method**: `GET`
- **Header**: `Authorization: Bearer <Token>`

### 4. 获取指定考勤
- **URL**: `/api/v1/attendance/{attendanceId}`
- **Method**: `GET`
- **Header**: `Authorization: Bearer <Token>`

### 5. 创建考勤
- **URL**: `/api/v1/attendance`
- **Method**: `POST`
- **Header**: `Authorization: Bearer <Token>`
- **请求体**: 考勤实体对象。

### 6. 更新考勤
- **URL**: `/api/v1/attendance/{attendanceId}`
- **Method**: `PUT`
- **Header**: `Authorization: Bearer <Token>`
- **请求体**: 考勤实体对象。

### 7. 删除考勤
- **URL**: `/api/v1/attendance/{attendanceId}`
- **Method**: `DELETE`
- **Header**: `Authorization: Bearer <Token>`

### 8. 获取考勤统计
- **URL**: `/api/v1/attendance/statistics`
- **Method**: `GET`
- **Header**: `Authorization: Bearer <Token>`
- **参数**: `classId`, `startDate`, `endDate`

### 9. 导出考勤列表
- **URL**: `/api/v1/attendance/export`
- **Method**: `GET`
- **Header**: `Authorization: Bearer <Token>`
- **参数**: `classId`, `startDate`, `endDate`, `status`
- **响应**: Excel 文件流。

---

## 消息管理接口 (Messages)

### 1. 分页查询消息列表
- **URL**: `/api/v1/messages`
- **Method**: `GET`
- **Header**: `Authorization: Bearer <Token>`
- **参数**: `page`, `size`, `isRead`

### 2. 获取未读消息数量
- **URL**: `/api/v1/messages/unread-count`
- **Method**: `GET`
- **Header**: `Authorization: Bearer <Token>`

### 3. 获取指定消息
- **URL**: `/api/v1/messages/{messageId}`
- **Method**: `GET`
- **Header**: `Authorization: Bearer <Token>`

### 4. 创建消息
- **URL**: `/api/v1/messages`
- **Method**: `POST`
- **Header**: `Authorization: Bearer <Token>`
- **请求体**: 消息实体对象。

### 5. 标记消息已读
- **URL**: `/api/v1/messages/{messageId}/read`
- **Method**: `PUT`
- **Header**: `Authorization: Bearer <Token>`

### 6. 标记所有消息已读
- **URL**: `/api/v1/messages/read-all`
- **Method**: `PUT`
- **Header**: `Authorization: Bearer <Token>`

### 7. 删除消息
- **URL**: `/api/v1/messages/{messageId}`
- **Method**: `DELETE`
- **Header**: `Authorization: Bearer <Token>`

---

## 操作日志接口 (Operation Logs)

### 1. 分页查询操作日志
- **URL**: `/api/operation-logs`
- **Method**: `GET`
- **参数**: `page`, `size`, `startDate`, `endDate`, `operationType`

### 2. 获取日志统计
- **URL**: `/api/operation-logs/stats`
- **Method**: `GET`
- **参数**: `startDate`, `endDate`

### 3. 按操作类型查询日志
- **URL**: `/api/operation-logs/type/{operationType}`
- **Method**: `GET`

### 4. 清空操作日志
- **URL**: `/api/operation-logs`
- **Method**: `DELETE`

### 5. 创建操作日志
- **URL**: `/api/operation-logs`
- **Method**: `POST`
- **请求体**: `{"operationType": "操作类型", "description": "操作描述", "ipAddress": "IP地址"}`

---

## 数据模型

### 用户表 (users)

| 字段名 | 类型 | 说明 |
|--------|------|------|
| user_id | VARCHAR(20) | 用户ID |
| username | VARCHAR(50) | 用户名 |
| password | VARCHAR(100) | 密码 |
| real_name | VARCHAR(50) | 真实姓名 |
| gender | CHAR(1) | 性别：M-男，F-女 |
| phone | VARCHAR(20) | 联系电话 |
| email | VARCHAR(50) | 邮箱 |
| role_code | VARCHAR(20) | 角色编码：ADMIN-系统管理员，ACADEMIC-教务管理员，HEADTEACHER-班主任，TEACHER-任课教师，PARENT-家长 |
| status | INT | 状态：1-启用，0-禁用 |
| last_login_time | DATETIME | 最后登录时间 |
| create_time | DATETIME | 创建时间 |
| update_time | DATETIME | 更新时间 |

### 学生表 (students)

| 字段名 | 类型 | 说明 |
|--------|------|------|
| student_id | VARCHAR(20) | 学生ID |
| student_no | VARCHAR(30) | 学籍号 |
| name | VARCHAR(50) | 姓名 |
| gender | CHAR(1) | 性别：M-男，F-女 |
| birth_date | DATE | 出生日期 |
| nation | VARCHAR(20) | 民族 |
| native_place | VARCHAR(100) | 籍贯 |
| id_card | VARCHAR(18) | 身份证号 |
| household_address | VARCHAR(200) | 户口所在地 |
| current_address | VARCHAR(200) | 现住址 |
| phone | VARCHAR(20) | 联系电话 |
| grade | VARCHAR(20) | 年级 |
| class_id | VARCHAR(20) | 班级ID |
| class_name | VARCHAR(50) | 班级名称 |
| enrollment_date | DATE | 入学日期 |
| student_status | VARCHAR(20) | 学籍状态：在读/休学/转出/毕业 |
| photo_url | VARCHAR(200) | 照片URL |
| guardian_name | VARCHAR(50) | 监护人姓名 |
| guardian_phone | VARCHAR(20) | 监护人电话 |
| guardian_relation | VARCHAR(20) | 监护关系 |
| parent_id | VARCHAR(20) | 家长用户ID |
| status | INT | 记录状态：1-有效，0-删除 |
| create_time | DATETIME | 创建时间 |
| update_time | DATETIME | 更新时间 |
| create_by | VARCHAR(20) | 创建人 |
| update_by | VARCHAR(20) | 更新人 |

### 班级表 (classes)

| 字段名 | 类型 | 说明 |
|--------|------|------|
| class_id | VARCHAR(20) | 班级ID |
| class_name | VARCHAR(50) | 班级名称 |
| grade | VARCHAR(20) | 年级 |
| headteacher_id | VARCHAR(20) | 班主任ID |
| headteacher_name | VARCHAR(50) | 班主任姓名 |
| classroom | VARCHAR(50) | 教室位置 |
| student_count | INT | 学生人数 |
| status | INT | 状态：1-有效，0-停用 |
| create_time | DATETIME | 创建时间 |
| update_time | DATETIME | 更新时间 |

### 学籍变动表 (student_changes)

| 字段名 | 类型 | 说明 |
|--------|------|------|
| change_id | VARCHAR(20) | 变动ID |
| student_id | VARCHAR(20) | 学生ID |
| student_no | VARCHAR(30) | 学籍号 |
| student_name | VARCHAR(50) | 学生姓名 |
| change_type | VARCHAR(20) | 变动类型：转入/转出/休学/复学/毕业 |
| change_reason | TEXT | 变动原因 |
| change_date | DATE | 变动日期 |
| original_school | VARCHAR(100) | 原学校（转入时） |
| target_school | VARCHAR(100) | 目标学校（转出时） |
| original_class | VARCHAR(50) | 原班级 |
| target_class | VARCHAR(50) | 目标班级（复学时） |
| expected_return_date | DATE | 预计复学日期（休学时） |
| attachments | VARCHAR(500) | 附件材料 |
| status | VARCHAR(20) | 状态：待审核/已通过/已驳回 |
| reviewer_id | VARCHAR(20) | 审核人ID |
| reviewer_name | VARCHAR(50) | 审核人姓名 |
| review_time | DATETIME | 审核时间 |
| review_comment | TEXT | 审核意见 |
| create_time | DATETIME | 创建时间 |
| update_time | DATETIME | 更新时间 |
| create_by | VARCHAR(20) | 创建人 |

### 信息变更日志表 (info_change_logs)

| 字段名 | 类型 | 说明 |
|--------|------|------|
| log_id | VARCHAR(20) | 日志ID |
| student_id | VARCHAR(20) | 学生ID |
| student_no | VARCHAR(30) | 学籍号 |
| field_name | VARCHAR(50) | 变更字段 |
| field_label | VARCHAR(50) | 字段名称 |
| old_value | VARCHAR(500) | 原值 |
| new_value | VARCHAR(500) | 新值 |
| change_time | DATETIME | 变更时间 |
| change_by | VARCHAR(20) | 变更人ID |
| change_by_name | VARCHAR(50) | 变更人姓名 |

### 操作日志表 (operation_logs)

| 字段名 | 类型 | 说明 |
|--------|------|------|
| log_id | VARCHAR(20) | 日志ID |
| user_id | VARCHAR(20) | 用户ID |
| username | VARCHAR(50) | 用户名 |
| operation_type | VARCHAR(50) | 操作类型 |
| operation_desc | VARCHAR(200) | 操作描述 |
| request_method | VARCHAR(10) | 请求方法 |
| request_url | VARCHAR(200) | 请求URL |
| request_params | TEXT | 请求参数 |
| response_result | TEXT | 响应结果 |
| ip_address | VARCHAR(50) | IP地址 |
| operation_time | DATETIME | 操作时间 |
| execution_time | INT | 执行时长(ms) |

### 成绩表 (grades)

| 字段名 | 类型 | 说明 |
|--------|------|------|
| grade_id | VARCHAR(20) | 成绩ID |
| student_id | VARCHAR(20) | 学生ID |
| class_id | VARCHAR(20) | 班级ID |
| subject | VARCHAR(50) | 科目 |
| exam_name | VARCHAR(100) | 考试名称 |
| score | DECIMAL(5,2) | 分数 |
| rank | INT | 排名 |
| semester | VARCHAR(50) | 学期 |
| exam_date | DATE | 考试日期 |
| remark | VARCHAR(500) | 备注 |
| create_time | DATETIME | 创建时间 |
| update_time | DATETIME | 更新时间 |
| create_by | VARCHAR(50) | 创建人 |
| update_by | VARCHAR(50) | 更新人 |

### 考勤表 (attendance)

| 字段名 | 类型 | 说明 |
|--------|------|------|
| attendance_id | VARCHAR(20) | 考勤ID |
| student_id | VARCHAR(20) | 学生ID |
| class_id | VARCHAR(20) | 班级ID |
| attendance_date | DATE | 考勤日期 |
| status | VARCHAR(20) | 考勤状态：出勤、缺勤、迟到、请假 |
| period | VARCHAR(20) | 时段：上午、下午、全天 |
| remark | VARCHAR(500) | 备注 |
| create_time | DATETIME | 创建时间 |
| update_time | DATETIME | 更新时间 |
| create_by | VARCHAR(50) | 创建人 |
| update_by | VARCHAR(50) | 更新人 |

### 消息表 (messages)

| 字段名 | 类型 | 说明 |
|--------|------|------|
| message_id | VARCHAR(20) | 消息ID |
| receiver_id | VARCHAR(20) | 接收人ID |
| sender_id | VARCHAR(20) | 发送人ID |
| title | VARCHAR(200) | 标题 |
| content | VARCHAR(2000) | 内容 |
| message_type | VARCHAR(50) | 消息类型：通知、提醒、公告 |
| is_read | BOOLEAN | 是否已读 |
| read_time | DATETIME | 阅读时间 |
| create_time | DATETIME | 创建时间 |
| update_time | DATETIME | 更新时间 |

---

## 认证说明

所有需要认证的接口都需要在请求头中携带 JWT Token：

```
Authorization: Bearer <your-jwt-token>
```

Token 通过登录接口获取，有效期为 24 小时。

---

## 错误码说明

| 错误码 | 说明 |
|--------|------|
| 200 | 成功 |
| 400 | 请求参数错误 |
| 401 | 未认证或Token过期 |
| 403 | 无权限访问 |
| 404 | 资源不存在 |
| 409 | 资源冲突 |
| 500 | 服务器内部错误 |

---

## 分页参数说明

所有支持分页的接口都使用以下参数：

| 参数名 | 类型 | 说明 | 默认值 |
|--------|------|------|--------|
| page | Integer | 页码 | 1 |
| size | Integer | 每页数量 | 10 |

分页响应格式：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [],
    "total": 100,
    "size": 10,
    "current": 1,
    "pages": 10
  }
}
```
