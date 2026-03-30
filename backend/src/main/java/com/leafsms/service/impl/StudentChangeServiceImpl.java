package com.leafsms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leafsms.entity.Student;
import com.leafsms.entity.StudentChange;
import com.leafsms.mapper.StudentChangeMapper;
import com.leafsms.mapper.StudentMapper;
import com.leafsms.service.StudentChangeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class StudentChangeServiceImpl extends ServiceImpl<StudentChangeMapper, StudentChange> implements StudentChangeService {
    
    @Autowired
    private StudentChangeMapper studentChangeMapper;
    
    @Autowired
    private StudentMapper studentMapper;

    @Override
    public Page<StudentChange> pageChanges(int page, int size, String studentName, 
                                            String changeType, String status) {
        Page<StudentChange> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<StudentChange> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(studentName)) {
            wrapper.like(StudentChange::getStudentName, studentName);
        }
        if (StringUtils.hasText(changeType)) {
            wrapper.eq(StudentChange::getChangeType, changeType);
        }
        if (StringUtils.hasText(status)) {
            wrapper.eq(StudentChange::getStatus, status);
        }
        wrapper.orderByDesc(StudentChange::getCreateTime);
        
        return studentChangeMapper.selectPage(pageParam, wrapper);
    }

    @Override
    public List<StudentChange> listByStudentId(String studentId) {
        LambdaQueryWrapper<StudentChange> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StudentChange::getStudentId, studentId);
        wrapper.orderByDesc(StudentChange::getCreateTime);
        return studentChangeMapper.selectList(wrapper);
    }

    @Override
    @Transactional
    public boolean addChange(StudentChange change) {
        if (change.getStatus() == null) {
            change.setStatus("待审核");
        }
        
        Student student = studentMapper.selectById(change.getStudentId());
        if (student != null) {
            change.setStudentNo(student.getStudentNo());
            change.setStudentName(student.getName());
        }
        
        return studentChangeMapper.insert(change) > 0;
    }

    @Override
    @Transactional
    public boolean reviewChange(String changeId, String status, String comment, 
                                String reviewerId, String reviewerName) {
        StudentChange change = studentChangeMapper.selectById(changeId);
        if (change == null) {
            return false;
        }
        
        change.setStatus(status);
        change.setReviewComment(comment);
        change.setReviewerId(reviewerId);
        change.setReviewerName(reviewerName);
        change.setReviewTime(LocalDateTime.now());
        
        int result = studentChangeMapper.updateById(change);
        
        if (result > 0 && "已通过".equals(status)) {
            updateStudentStatus(change);
        }
        
        return result > 0;
    }
    
    private void updateStudentStatus(StudentChange change) {
        Student student = studentMapper.selectById(change.getStudentId());
        if (student == null) {
            return;
        }
        
        String changeType = change.getChangeType();
        switch (changeType) {
            case "转入":
                student.setStudentStatus("在读");
                student.setClassId(change.getTargetClass());
                break;
            case "转出":
                student.setStudentStatus("转出");
                break;
            case "休学":
                student.setStudentStatus("休学");
                break;
            case "复学":
                student.setStudentStatus("在读");
                student.setClassId(change.getTargetClass());
                break;
            case "毕业":
                student.setStudentStatus("已毕业");
                break;
        }
        
        studentMapper.updateById(student);
    }

    @Override
    public List<Map<String, Object>> getChangeTypeStatistics() {
        List<StudentChange> changes = studentChangeMapper.selectList(null);
        
        Map<String, Long> typeCount = new HashMap<>();
        for (StudentChange change : changes) {
            String type = change.getChangeType();
            typeCount.merge(type, 1L, Long::sum);
        }
        
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<String, Long> entry : typeCount.entrySet()) {
            Map<String, Object> item = new HashMap<>();
            item.put("type", entry.getKey());
            item.put("count", entry.getValue());
            result.add(item);
        }
        return result;
    }
}
