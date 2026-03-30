package com.leafsms.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.leafsms.entity.Student;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface StudentService extends IService<Student> {
    
    Page<Student> pageStudents(int page, int size, String name, String studentNo, 
                                String grade, String classId, String status);
    
    Student getByStudentNo(String studentNo);
    
    boolean addStudent(Student student);
    
    boolean updateStudent(Student student);
    
    boolean deleteStudent(String studentId);
    
    List<Map<String, Object>> getGradeStatistics();
    
    List<Map<String, Object>> getGenderStatistics();
    
    long countByClassId(String classId);
    
    Student getByParentId(String parentId);
    
    void updateClassStudentCount(String classId);
    
    int importStudents(MultipartFile file) throws Exception;
}
