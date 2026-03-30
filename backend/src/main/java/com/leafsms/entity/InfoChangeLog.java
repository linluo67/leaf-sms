package com.leafsms.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.time.LocalDateTime;

@TableName("info_change_logs")
public class InfoChangeLog {
    
    @TableId(value = "log_id", type = IdType.ASSIGN_UUID)
    private String logId;
    
    @TableField("student_id")
    private String studentId;
    
    @TableField("student_no")
    private String studentNo;
    
    @TableField("field_name")
    private String fieldName;
    
    @TableField("field_label")
    private String fieldLabel;
    
    @TableField("old_value")
    private String oldValue;
    
    @TableField("new_value")
    private String newValue;
    
    @TableField(value = "change_time", fill = FieldFill.INSERT)
    private LocalDateTime changeTime;
    
    @TableField("change_by")
    private String changeBy;
    
    @TableField("change_by_name")
    private String changeByName;

    public String getLogId() {
        return logId;
    }

    public void setLogId(String logId) {
        this.logId = logId;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getStudentNo() {
        return studentNo;
    }

    public void setStudentNo(String studentNo) {
        this.studentNo = studentNo;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldLabel() {
        return fieldLabel;
    }

    public void setFieldLabel(String fieldLabel) {
        this.fieldLabel = fieldLabel;
    }

    public String getOldValue() {
        return oldValue;
    }

    public void setOldValue(String oldValue) {
        this.oldValue = oldValue;
    }

    public String getNewValue() {
        return newValue;
    }

    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }

    public LocalDateTime getChangeTime() {
        return changeTime;
    }

    public void setChangeTime(LocalDateTime changeTime) {
        this.changeTime = changeTime;
    }

    public String getChangeBy() {
        return changeBy;
    }

    public void setChangeBy(String changeBy) {
        this.changeBy = changeBy;
    }

    public String getChangeByName() {
        return changeByName;
    }

    public void setChangeByName(String changeByName) {
        this.changeByName = changeByName;
    }
}
