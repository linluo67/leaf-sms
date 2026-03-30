package com.leafsms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.leafsms.entity.Grade;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface GradeMapper extends BaseMapper<Grade> {
    
    @Select("SELECT * FROM grades WHERE student_id = #{studentId} ORDER BY exam_date DESC")
    List<Grade> findByStudentId(@Param("studentId") String studentId);
    
    @Select("SELECT * FROM grades WHERE class_id = #{classId} ORDER BY exam_date DESC")
    IPage<Grade> findByClassId(Page<Grade> page, @Param("classId") String classId);
    
    @Select("SELECT g.*, s.name as student_name, s.student_no FROM grades g " +
            "LEFT JOIN students s ON g.student_id = s.student_id " +
            "WHERE g.class_id = #{classId} AND g.subject = #{subject} ORDER BY g.exam_date DESC")
    IPage<Map<String, Object>> findByClassIdAndSubject(Page<Map<String, Object>> page, @Param("classId") String classId, @Param("subject") String subject);
    
    @Select("SELECT g.*, s.name as student_name, s.student_no FROM grades g " +
            "LEFT JOIN students s ON g.student_id = s.student_id " +
            "WHERE g.class_id = #{classId} ORDER BY g.exam_date DESC")
    IPage<Map<String, Object>> findByClassIdWithStudent(Page<Map<String, Object>> page, @Param("classId") String classId);
    
    @Select("SELECT AVG(score) as avg_score, MAX(score) as max_score, MIN(score) as min_score, COUNT(*) as count " +
            "FROM grades WHERE class_id = #{classId} AND subject = #{subject} AND exam_name = #{examName}")
    Map<String, Object> getClassSubjectStats(@Param("classId") String classId, @Param("subject") String subject, @Param("examName") String examName);
    
    @Select("SELECT AVG(score) as avg_score FROM grades WHERE student_id = #{studentId} AND subject = #{subject}")
    Map<String, Object> getStudentSubjectAvg(@Param("studentId") String studentId, @Param("subject") String subject);
}
