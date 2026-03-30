package com.leafsms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leafsms.entity.Grade;
import com.leafsms.mapper.GradeMapper;
import com.leafsms.service.GradeService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GradeServiceImpl extends ServiceImpl<GradeMapper, Grade> implements GradeService {
    
    @Override
    public List<Grade> findByStudentId(String studentId) {
        return baseMapper.findByStudentId(studentId);
    }
    
    @Override
    public IPage<Grade> pageGrades(Page<Grade> page, String classId, String subject, String examName, String semester) {
        QueryWrapper<Grade> wrapper = new QueryWrapper<>();
        
        if (classId != null) {
            wrapper.eq("class_id", classId);
        }
        if (subject != null && !subject.isEmpty()) {
            wrapper.eq("subject", subject);
        }
        if (examName != null && !examName.isEmpty()) {
            wrapper.eq("exam_name", examName);
        }
        if (semester != null && !semester.isEmpty()) {
            wrapper.eq("semester", semester);
        }
        
        wrapper.orderByDesc("exam_date");
        return baseMapper.selectPage(page, wrapper);
    }
    
    @Override
    public IPage<Map<String, Object>> pageGradesWithStudent(Page<Map<String, Object>> page, String classId, String subject, String examName) {
        if (subject != null && !subject.isEmpty()) {
            return baseMapper.findByClassIdAndSubject(page, classId, subject);
        }
        return baseMapper.findByClassIdWithStudent(page, classId);
    }
    
    @Override
    public boolean addGrade(Grade grade) {
        grade.setCreateTime(LocalDateTime.now());
        grade.setUpdateTime(LocalDateTime.now());
        return baseMapper.insert(grade) > 0;
    }
    
    @Override
    public boolean updateGrade(Grade grade) {
        grade.setUpdateTime(LocalDateTime.now());
        return baseMapper.updateById(grade) > 0;
    }
    
    @Override
    public boolean deleteGrade(String gradeId) {
        return baseMapper.deleteById(gradeId) > 0;
    }
    
    @Override
    public Map<String, Object> getClassSubjectStats(String classId, String subject, String examName) {
        return baseMapper.getClassSubjectStats(classId, subject, examName);
    }
    
    @Override
    public Map<String, Object> getStudentSubjectAvg(String studentId, String subject) {
        return baseMapper.getStudentSubjectAvg(studentId, subject);
    }
    
    @Override
    public List<Map<String, Object>> getGradeStatistics(String classId) {
        QueryWrapper<Grade> wrapper = new QueryWrapper<>();
        if (classId != null) {
            wrapper.eq("class_id", classId);
        }
        
        List<Grade> grades = baseMapper.selectList(wrapper);
        
        Map<String, List<Grade>> subjectMap = new HashMap<>();
        for (Grade grade : grades) {
            subjectMap.computeIfAbsent(grade.getSubject(), k -> new java.util.ArrayList<>()).add(grade);
        }
        
        List<Map<String, Object>> stats = new java.util.ArrayList<>();
        for (Map.Entry<String, List<Grade>> entry : subjectMap.entrySet()) {
            Map<String, Object> stat = new HashMap<>();
            stat.put("subject", entry.getKey());
            
            List<Grade> subjectGrades = entry.getValue();
            double avg = subjectGrades.stream().mapToDouble(g -> g.getScore().doubleValue()).average().orElse(0);
            double max = subjectGrades.stream().mapToDouble(g -> g.getScore().doubleValue()).max().orElse(0);
            double min = subjectGrades.stream().mapToDouble(g -> g.getScore().doubleValue()).min().orElse(0);
            
            stat.put("avg", Math.round(avg * 100.0) / 100.0);
            stat.put("max", max);
            stat.put("min", min);
            stat.put("count", subjectGrades.size());
            
            stats.add(stat);
        }
        
        return stats;
    }
}
