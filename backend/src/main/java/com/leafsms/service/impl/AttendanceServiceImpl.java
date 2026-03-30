package com.leafsms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leafsms.entity.Attendance;
import com.leafsms.mapper.AttendanceMapper;
import com.leafsms.service.AttendanceService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AttendanceServiceImpl extends ServiceImpl<AttendanceMapper, Attendance> implements AttendanceService {
    
    @Override
    public List<Attendance> findByStudentId(String studentId) {
        return baseMapper.findByStudentId(studentId);
    }
    
    @Override
    public IPage<Attendance> pageAttendance(Page<Attendance> page, String classId, LocalDate startDate, LocalDate endDate, String status) {
        if (startDate != null && endDate != null) {
            return baseMapper.findByClassIdAndDateRange(page, classId, startDate, endDate);
        }
        
        QueryWrapper<Attendance> wrapper = new QueryWrapper<>();
        
        if (classId != null) {
            wrapper.eq("class_id", classId);
        }
        if (status != null && !status.isEmpty()) {
            wrapper.eq("status", status);
        }
        if (startDate != null) {
            wrapper.ge("attendance_date", startDate);
        }
        if (endDate != null) {
            wrapper.le("attendance_date", endDate);
        }
        
        wrapper.orderByDesc("attendance_date");
        return baseMapper.selectPage(page, wrapper);
    }
    
    @Override
    public IPage<Map<String, Object>> pageAttendanceWithStudent(Page<Map<String, Object>> page, String classId, LocalDate startDate, LocalDate endDate, String status) {
        return baseMapper.findByClassIdWithStudent(page, classId);
    }
    
    @Override
    public boolean addAttendance(Attendance attendance) {
        attendance.setCreateTime(LocalDateTime.now());
        attendance.setUpdateTime(LocalDateTime.now());
        return baseMapper.insert(attendance) > 0;
    }
    
    @Override
    public boolean updateAttendance(Attendance attendance) {
        attendance.setUpdateTime(LocalDateTime.now());
        return baseMapper.updateById(attendance) > 0;
    }
    
    @Override
    public boolean deleteAttendance(String attendanceId) {
        return baseMapper.deleteById(attendanceId) > 0;
    }
    
    @Override
    public Map<String, Object> countByStudentIdAndStatus(String studentId, String status, LocalDate startDate, LocalDate endDate) {
        return baseMapper.countByStudentIdAndStatus(studentId, status, startDate, endDate);
    }
    
    @Override
    public List<Map<String, Object>> getClassAttendanceStats(String classId, LocalDate startDate, LocalDate endDate) {
        return baseMapper.getClassAttendanceStats(classId, startDate, endDate);
    }
    
    @Override
    public Map<String, Object> getAttendanceStatistics(String classId, LocalDate startDate, LocalDate endDate) {
        List<Map<String, Object>> stats = baseMapper.getClassAttendanceStats(classId, startDate, endDate);
        
        Map<String, Object> result = new HashMap<>();
        result.put("total", 0);
        result.put("present", 0);
        result.put("absent", 0);
        result.put("late", 0);
        result.put("leave", 0);
        
        for (Map<String, Object> stat : stats) {
            String status = (String) stat.get("status");
            Long count = ((Number) stat.get("count")).longValue();
            
            result.put("total", (Long) result.get("total") + count);
            
            switch (status) {
                case "出勤":
                    result.put("present", count);
                    break;
                case "缺勤":
                    result.put("absent", count);
                    break;
                case "迟到":
                    result.put("late", count);
                    break;
                case "请假":
                    result.put("leave", count);
                    break;
            }
        }
        
        return result;
    }
}
