package com.leafsms.entity;

import com.baomidou.mybatisplus.annotation.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@TableName("classes")
public class Clazz {
    
    @TableId(value = "class_id", type = IdType.ASSIGN_UUID)
    private String classId;
    
    @TableField("class_name")
    @NotBlank(message = "班级名称不能为空")
    @Size(max = 50, message = "班级名称长度不能超过50个字符")
    private String className;
    
    @TableField("grade")
    @NotBlank(message = "年级不能为空")
    private String grade;
    
    @TableField("headteacher_id")
    private String headteacherId;
    
    @TableField("headteacher_name")
    private String headteacherName;
    
    @TableField("classroom")
    private String classroom;
    
    @TableField("student_count")
    private Integer studentCount;
    
    @TableField("status")
    private Integer status;
    
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableField(exist = false)
    private User headteacher;

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getHeadteacherId() {
        return headteacherId;
    }

    public void setHeadteacherId(String headteacherId) {
        this.headteacherId = headteacherId;
    }

    public String getHeadteacherName() {
        return headteacherName;
    }

    public void setHeadteacherName(String headteacherName) {
        this.headteacherName = headteacherName;
    }

    public String getClassroom() {
        return classroom;
    }

    public void setClassroom(String classroom) {
        this.classroom = classroom;
    }

    public Integer getStudentCount() {
        return studentCount;
    }

    public void setStudentCount(Integer studentCount) {
        this.studentCount = studentCount;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    public User getHeadteacher() {
        return headteacher;
    }

    public void setHeadteacher(User headteacher) {
        this.headteacher = headteacher;
    }
}
