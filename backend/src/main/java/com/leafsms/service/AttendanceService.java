package com.leafsms.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.leafsms.entity.Attendance;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface AttendanceService extends IService<Attendance> {
    
    List<Attendance> findByStudentId(String studentId);
    
    IPage<Attendance> pageAttendance(Page<Attendance> page, String classId, LocalDate startDate, LocalDate endDate, String status);
    
    IPage<Map<String, Object>> pageAttendanceWithStudent(Page<Map<String, Object>> page, String classId, LocalDate startDate, LocalDate endDate, String status);
    
    boolean addAttendance(Attendance attendance);
    
    boolean updateAttendance(Attendance attendance);
    
    boolean deleteAttendance(String attendanceId);
    
    Map<String, Object> countByStudentIdAndStatus(String studentId, String status, LocalDate startDate, LocalDate endDate);
    
    List<Map<String, Object>> getClassAttendanceStats(String classId, LocalDate startDate, LocalDate endDate);
    
    Map<String, Object> getAttendanceStatistics(String classId, LocalDate startDate, LocalDate endDate);
}
