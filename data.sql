DROP DATABASE IF EXISTS leaf_sms;
CREATE DATABASE leaf_sms DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE leaf_sms;

SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS users;
CREATE TABLE users (
    user_id VARCHAR(36) PRIMARY KEY COMMENT '用户ID',
    username VARCHAR(50) NOT NULL COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码',
    real_name VARCHAR(50) COMMENT '真实姓名',
    gender CHAR(1) COMMENT '性别：M-男，F-女',
    phone VARCHAR(20) COMMENT '联系电话',
    email VARCHAR(50) NOT NULL COMMENT '邮箱',
    role_code VARCHAR(20) NOT NULL COMMENT '角色编码：ADMIN-系统管理员，ACADEMIC-教务管理员，HEADTEACHER-班主任，TEACHER-任课教师，PARENT-家长',
    status INT DEFAULT 1 COMMENT '状态：1-启用，0-禁用',
    last_login_time DATETIME COMMENT '最后登录时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_email (email)
) COMMENT '用户表';

INSERT INTO users (user_id, username, password, real_name, gender, phone, email, role_code) VALUES
('USER001', 'admin_system', '123456', '陈明华', 'M', '13912345678', 'chenmh@qq.com', 'ADMIN'),
('USER002', 'academic_admin', '123456', '刘晓梅', 'F', '13823456789', 'liuxm@qq.com', 'ACADEMIC'),
('USER003', 'teacher_li', '123456', '李秀英', 'F', '13734567890', 'lixy@qq.com', 'HEADTEACHER'),
('USER004', 'teacher_wang', '123456', '王建国', 'M', '13645678901', 'wangjg@qq.com', 'TEACHER'),
('USER005', 'parent_zhang', '123456', '张伟', 'M', '13556789012', 'zhangwei@qq.com', 'PARENT');

DROP TABLE IF EXISTS classes;
CREATE TABLE classes (
    class_id VARCHAR(36) PRIMARY KEY COMMENT '班级ID',
    class_name VARCHAR(50) NOT NULL COMMENT '班级名称',
    grade VARCHAR(20) NOT NULL COMMENT '年级',
    headteacher_id VARCHAR(36) COMMENT '班主任ID',
    headteacher_name VARCHAR(50) COMMENT '班主任姓名',
    classroom VARCHAR(50) COMMENT '教室位置',
    student_count INT DEFAULT 0 COMMENT '学生人数',
    status INT DEFAULT 1 COMMENT '状态：1-有效，0-停用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    KEY idx_grade (grade),
    KEY idx_headteacher (headteacher_id)
) COMMENT '班级信息表';

INSERT INTO classes (class_id, class_name, grade, headteacher_id, headteacher_name, classroom, student_count) VALUES
('CLS001', '高一(1)班', '高一', 'USER003', '李老师', '教学楼A-101', 0);

DROP TABLE IF EXISTS students;
CREATE TABLE students (
    student_id VARCHAR(36) PRIMARY KEY COMMENT '学生ID',
    student_no VARCHAR(30) NOT NULL COMMENT '学籍号',
    name VARCHAR(50) NOT NULL COMMENT '姓名',
    gender CHAR(1) NOT NULL COMMENT '性别：M-男，F-女',
    birth_date DATE COMMENT '出生日期',
    nation VARCHAR(20) DEFAULT '汉族' COMMENT '民族',
    id_card VARCHAR(18) COMMENT '身份证号',
    household_address VARCHAR(200) COMMENT '户口所在地',
    phone VARCHAR(20) COMMENT '联系电话',
    grade VARCHAR(20) COMMENT '年级',
    class_id VARCHAR(36) COMMENT '班级ID',
    class_name VARCHAR(50) COMMENT '班级名称',
    enrollment_date DATE COMMENT '入学日期',
    student_status VARCHAR(20) DEFAULT '在读' COMMENT '学籍状态：在读/休学/转出/毕业',
    guardian_name VARCHAR(50) COMMENT '监护人姓名',
    guardian_phone VARCHAR(20) COMMENT '监护人电话',
    guardian_relation VARCHAR(20) COMMENT '监护关系',
    parent_id VARCHAR(36) COMMENT '关联家长用户ID',
    status INT DEFAULT 1 COMMENT '记录状态：1-有效，0-删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by VARCHAR(36) COMMENT '创建人',
    update_by VARCHAR(36) COMMENT '更新人',
    UNIQUE KEY uk_student_no (student_no),
    KEY idx_name (name),
    KEY idx_class (class_id),
    KEY idx_grade (grade),
    KEY idx_status (student_status)
) COMMENT '学生信息表';

INSERT INTO students (student_id, student_no, name, gender, birth_date, nation, grade, class_id, class_name, enrollment_date, student_status, guardian_name, guardian_phone, guardian_relation, parent_id) VALUES
('STU001', '202401001', '陈浩然', 'M', '2008-05-12', '汉族', '初一', 'CLS001', '初一(1)班', '2024-09-01', '在读', '陈伟', '13912345678', '父亲', 'USER005');

UPDATE classes c SET student_count = (SELECT COUNT(*) FROM students s WHERE s.class_id = c.class_id AND s.status = 1 AND s.student_status = '在读');

DROP TABLE IF EXISTS student_changes;
CREATE TABLE student_changes (
    change_id VARCHAR(36) PRIMARY KEY COMMENT '变动ID',
    student_id VARCHAR(36) NOT NULL COMMENT '学生ID',
    student_no VARCHAR(30) COMMENT '学籍号',
    student_name VARCHAR(50) COMMENT '学生姓名',
    change_type VARCHAR(20) NOT NULL COMMENT '变动类型：转入/转出/休学/复学/毕业',
    change_reason TEXT COMMENT '变动原因',
    change_date DATE COMMENT '变动日期',
    original_school VARCHAR(100) COMMENT '原学校（转入时）',
    target_school VARCHAR(100) COMMENT '目标学校（转出时）',
    original_class VARCHAR(50) COMMENT '原班级',
    target_class VARCHAR(50) COMMENT '目标班级（复学时）',
    expected_return_date DATE COMMENT '预计复学日期（休学时）',
    attachments VARCHAR(500) COMMENT '附件材料',
    status VARCHAR(20) DEFAULT '待审核' COMMENT '状态：待审核/已通过/已驳回',
    reviewer_id VARCHAR(36) COMMENT '审核人ID',
    reviewer_name VARCHAR(50) COMMENT '审核人姓名',
    review_time DATETIME COMMENT '审核时间',
    review_comment TEXT COMMENT '审核意见',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by VARCHAR(36) COMMENT '创建人',
    KEY idx_student (student_id),
    KEY idx_type (change_type),
    KEY idx_date (change_date),
    KEY idx_status (status)
) COMMENT '学籍变动表';

DROP TABLE IF EXISTS info_change_logs;
CREATE TABLE info_change_logs (
    log_id VARCHAR(36) PRIMARY KEY COMMENT '日志ID',
    student_id VARCHAR(36) NOT NULL COMMENT '学生ID',
    student_no VARCHAR(30) COMMENT '学籍号',
    field_name VARCHAR(50) NOT NULL COMMENT '变更字段',
    field_label VARCHAR(50) COMMENT '字段名称',
    old_value VARCHAR(500) COMMENT '原值',
    new_value VARCHAR(500) COMMENT '新值',
    change_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '变更时间',
    change_by VARCHAR(36) COMMENT '变更人ID',
    change_by_name VARCHAR(50) COMMENT '变更人姓名',
    KEY idx_student (student_id),
    KEY idx_time (change_time)
) COMMENT '信息变更记录表';

DROP TABLE IF EXISTS operation_logs;
CREATE TABLE operation_logs (
    log_id VARCHAR(36) PRIMARY KEY COMMENT '日志ID',
    user_id VARCHAR(36) COMMENT '操作用户ID',
    username VARCHAR(50) COMMENT '操作用户名',
    operation_type VARCHAR(50) COMMENT '操作类型',
    operation_desc VARCHAR(200) COMMENT '操作描述',
    ip_address VARCHAR(50) COMMENT 'IP地址',
    operation_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    KEY idx_user (user_id),
    KEY idx_time (operation_time)
) COMMENT '操作日志表';

DROP TABLE IF EXISTS grades;
CREATE TABLE grades (
    grade_id VARCHAR(36) PRIMARY KEY COMMENT '成绩ID',
    student_id VARCHAR(36) NOT NULL COMMENT '学生ID',
    class_id VARCHAR(36) NOT NULL COMMENT '班级ID',
    subject VARCHAR(50) NOT NULL COMMENT '科目',
    exam_name VARCHAR(100) NOT NULL COMMENT '考试名称',
    score DECIMAL(5,2) NOT NULL COMMENT '分数',
    `rank` INT COMMENT '排名',
    semester VARCHAR(50) NOT NULL COMMENT '学期',
    exam_date DATE COMMENT '考试日期',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by VARCHAR(36) COMMENT '创建人',
    update_by VARCHAR(36) COMMENT '更新人',
    KEY idx_student (student_id),
    KEY idx_class (class_id),
    KEY idx_exam (exam_name, semester)
) COMMENT '成绩表';

DROP TABLE IF EXISTS attendance;
CREATE TABLE attendance (
    attendance_id VARCHAR(36) PRIMARY KEY COMMENT '考勤ID',
    student_id VARCHAR(36) NOT NULL COMMENT '学生ID',
    class_id VARCHAR(36) NOT NULL COMMENT '班级ID',
    attendance_date DATE NOT NULL COMMENT '考勤日期',
    status VARCHAR(20) NOT NULL COMMENT '考勤状态：出勤、缺勤、迟到、请假',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by VARCHAR(36) COMMENT '创建人',
    update_by VARCHAR(36) COMMENT '更新人',
    KEY idx_student (student_id),
    KEY idx_class (class_id),
    KEY idx_date (attendance_date),
    KEY idx_status (status)
) COMMENT '考勤表';

DROP TABLE IF EXISTS messages;
CREATE TABLE messages (
    message_id VARCHAR(36) PRIMARY KEY COMMENT '消息ID',
    receiver_id VARCHAR(36) NOT NULL COMMENT '接收人ID',
    sender_id VARCHAR(36) NOT NULL COMMENT '发送人ID',
    title VARCHAR(200) NOT NULL COMMENT '标题',
    content VARCHAR(2000) NOT NULL COMMENT '内容',
    is_read BOOLEAN DEFAULT FALSE COMMENT '是否已读',
    read_time DATETIME COMMENT '阅读时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    KEY idx_receiver (receiver_id),
    KEY idx_sender (sender_id),
    KEY idx_read (is_read),
    KEY idx_create_time (create_time)
) COMMENT '消息表';

SET FOREIGN_KEY_CHECKS = 1;

SELECT '数据库初始化完成！' AS message;
