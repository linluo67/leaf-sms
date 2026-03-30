package com.leafsms.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.leafsms.entity.Grade;

import java.util.List;
import java.util.Map;

public interface GradeService extends IService<Grade> {
    
    List<Grade> findByStudentId(String studentId);
    
    IPage<Grade> pageGrades(Page<Grade> page, String classId, String subject, String examName, String semester);
    
    IPage<Map<String, Object>> pageGradesWithStudent(Page<Map<String, Object>> page, String classId, String subject, String examName);
    
    boolean addGrade(Grade grade);
    
    boolean updateGrade(Grade grade);
    
    boolean deleteGrade(String gradeId);
    
    Map<String, Object> getClassSubjectStats(String classId, String subject, String examName);
    
    Map<String, Object> getStudentSubjectAvg(String studentId, String subject);
    
    List<Map<String, Object>> getGradeStatistics(String classId);
}
