package com.leafsms.controller;

import com.leafsms.common.Result;
import com.leafsms.entity.Student;
import com.leafsms.entity.User;
import com.leafsms.service.*;
import com.leafsms.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private UserService userService;

    @Autowired
    private StudentService studentService;

    @Autowired
    private ClazzService clazzService;

    @Autowired
    private StudentChangeService studentChangeService;

    @Autowired
    private GradeService gradeService;

    @Autowired
    private AttendanceService attendanceService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/admin/stats")
    public Result<Map<String, Object>> adminStats() {
        Map<String, Object> stats = new HashMap<>();
        
        long totalStudents = studentService.count();
        long totalClasses = clazzService.count();
        long totalUsers = userService.count();
        long totalChanges = studentChangeService.count();
        
        stats.put("totalStudents", totalStudents);
        stats.put("totalClasses", totalClasses);
        stats.put("totalUsers", totalUsers);
        stats.put("totalChanges", totalChanges);
        
        return Result.success(stats);
    }

    @GetMapping("/teacher/stats")
    public Result<Map<String, Object>> teacherStats(
            @RequestHeader("Authorization") String authorization) {
        String userId = getUserIdFromToken(authorization);
        if (userId == null) {
            return Result.error("未授权访问");
        }
        
        Map<String, Object> stats = new HashMap<>();
        
        long totalStudents = studentService.count();
        long totalClasses = clazzService.count();
        long unreadMessages = messageService.countUnread(userId) != null ? 
            (Long) messageService.countUnread(userId).getOrDefault("count", 0L) : 0L;
        
        stats.put("totalStudents", totalStudents);
        stats.put("totalClasses", totalClasses);
        stats.put("unreadMessages", unreadMessages);
        stats.put("pendingGrades", 0);
        stats.put("noticeList", new java.util.ArrayList<>());
        
        return Result.success(stats);
    }

    @GetMapping("/academic/stats")
    public Result<Map<String, Object>> academicStats(
            @RequestHeader("Authorization") String authorization) {
        String userId = getUserIdFromToken(authorization);
        if (userId == null) {
            return Result.error("未授权访问");
        }
        
        Map<String, Object> stats = new HashMap<>();
        
        long totalStudents = studentService.count();
        long totalClasses = clazzService.count();
        long pendingChanges = studentChangeService.count(new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<com.leafsms.entity.StudentChange>()
            .eq("status", "待审核"));
        
        stats.put("totalStudents", totalStudents);
        stats.put("totalClasses", totalClasses);
        stats.put("pendingChanges", pendingChanges);
        
        return Result.success(stats);
    }

    @GetMapping("/headteacher/stats")
    public Result<Map<String, Object>> headTeacherStats(
            @RequestHeader("Authorization") String authorization) {
        String userId = getUserIdFromToken(authorization);
        if (userId == null) {
            return Result.error("未授权访问");
        }
        
        Map<String, Object> stats = new HashMap<>();
        
        long myClassStudents = studentService.count(new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<com.leafsms.entity.Student>()
            .eq("class_id", userId));
        
        LocalDate today = LocalDate.now();
        LocalDate weekAgo = today.minusDays(7);
        
        Map<String, Object> attendanceStats = attendanceService.getAttendanceStatistics(userId, weekAgo, today);
        
        long unreadMessages = messageService.countUnread(userId) != null ? 
            (Long) messageService.countUnread(userId).getOrDefault("count", 0L) : 0L;
        long pendingChanges = studentChangeService.count(new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<com.leafsms.entity.StudentChange>()
            .eq("status", "待审核"));
        
        stats.put("myClassStudents", myClassStudents);
        stats.put("todayAttendance", attendanceStats != null ? attendanceStats.getOrDefault("present", 0L) : 0L);
        stats.put("pendingChanges", pendingChanges);
        stats.put("unreadMessages", unreadMessages);
        stats.put("todoList", new java.util.ArrayList<>());
        
        return Result.success(stats);
    }

    @GetMapping("/parent/stats")
    public Result<Map<String, Object>> parentStats(
            @RequestHeader("Authorization") String authorization) {
        String userId = getUserIdFromToken(authorization);
        if (userId == null) {
            return Result.error("未授权访问");
        }
        
        Map<String, Object> stats = new HashMap<>();
        
        Student child = studentService.getByParentId(userId);
        
        if (child != null) {
            Map<String, Object> childInfo = new HashMap<>();
            childInfo.put("name", child.getName());
            childInfo.put("studentNo", child.getStudentNo());
            childInfo.put("className", child.getClassName());
            childInfo.put("status", child.getStudentStatus());
            stats.put("childInfo", childInfo);
            
            var grades = gradeService.findByStudentId(child.getStudentId());
            var attendances = attendanceService.findByStudentId(child.getStudentId());
            
            stats.put("recentGrades", grades.size());
            stats.put("recentAttendance", attendances.size());
            
            long presentCount = attendances.stream()
                .filter(a -> "出勤".equals(a.getStatus()))
                .count();
            long attendanceRate = attendances.isEmpty() ? 0 : 
                (presentCount * 100 / attendances.size());
            stats.put("attendanceRate", attendanceRate + "%");
        } else {
            stats.put("childInfo", null);
            stats.put("recentGrades", 0);
            stats.put("recentAttendance", 0);
            stats.put("attendanceRate", "0%");
        }
        
        Map<String, Object> unreadMessages = messageService.countUnread(userId);
        stats.put("unreadMessages", unreadMessages != null ? unreadMessages.get("count") : 0);
        stats.put("messageList", new java.util.ArrayList<>());
        
        return Result.success(stats);
    }

    private String getUserIdFromToken(String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return null;
        }
        String token = authorization.substring(7);
        try {
            return jwtUtil.getUserIdFromToken(token);
        } catch (Exception e) {
            return null;
        }
    }
}
