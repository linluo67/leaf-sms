package com.leafsms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.leafsms.entity.Attendance;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Mapper
public interface AttendanceMapper extends BaseMapper<Attendance> {
    
    @Select("SELECT * FROM attendance WHERE student_id = #{studentId} ORDER BY attendance_date DESC")
    List<Attendance> findByStudentId(@Param("studentId") String studentId);
    
    @Select("SELECT * FROM attendance WHERE class_id = #{classId} ORDER BY attendance_date DESC")
    IPage<Attendance> findByClassId(Page<Attendance> page, @Param("classId") String classId);
    
    @Select("SELECT * FROM attendance WHERE class_id = #{classId} AND attendance_date BETWEEN #{startDate} AND #{endDate} ORDER BY attendance_date DESC")
    IPage<Attendance> findByClassIdAndDateRange(Page<Attendance> page, @Param("classId") String classId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    @Select("SELECT a.*, s.name as student_name, s.student_no FROM attendance a " +
            "LEFT JOIN students s ON a.student_id = s.student_id " +
            "WHERE a.class_id = #{classId} ORDER BY a.attendance_date DESC")
    IPage<Map<String, Object>> findByClassIdWithStudent(Page<Map<String, Object>> page, @Param("classId") String classId);
    
    @Select("SELECT COUNT(*) as count FROM attendance WHERE student_id = #{studentId} AND status = #{status} " +
            "AND attendance_date BETWEEN #{startDate} AND #{endDate}")
    Map<String, Object> countByStudentIdAndStatus(@Param("studentId") String studentId, @Param("status") String status, 
                                                   @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    @Select("SELECT status, COUNT(*) as count FROM attendance WHERE class_id = #{classId} " +
            "AND attendance_date BETWEEN #{startDate} AND #{endDate} GROUP BY status")
    List<Map<String, Object>> getClassAttendanceStats(@Param("classId") String classId, 
                                                       @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
