package com.leafsms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leafsms.entity.Clazz;
import com.leafsms.entity.InfoChangeLog;
import com.leafsms.entity.Student;
import com.leafsms.mapper.ClazzMapper;
import com.leafsms.mapper.InfoChangeLogMapper;
import com.leafsms.mapper.StudentMapper;
import com.leafsms.service.StudentService;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Service
public class StudentServiceImpl extends ServiceImpl<StudentMapper, Student> implements StudentService {
    
    @Autowired
    private StudentMapper studentMapper;
    
    @Autowired
    private ClazzMapper clazzMapper;
    
    @Autowired
    private InfoChangeLogMapper infoChangeLogMapper;

    @Override
    public Page<Student> pageStudents(int page, int size, String name, String studentNo, 
                                       String grade, String classId, String status) {
        Page<Student> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Student> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(name)) {
            wrapper.like(Student::getName, name);
        }
        if (StringUtils.hasText(studentNo)) {
            wrapper.like(Student::getStudentNo, studentNo);
        }
        if (StringUtils.hasText(grade)) {
            wrapper.eq(Student::getGrade, grade);
        }
        if (StringUtils.hasText(classId)) {
            wrapper.eq(Student::getClassId, classId);
        }
        if (StringUtils.hasText(status)) {
            wrapper.eq(Student::getStudentStatus, status);
        }
        wrapper.eq(Student::getStatus, 1);
        wrapper.orderByDesc(Student::getCreateTime);
        
        return studentMapper.selectPage(pageParam, wrapper);
    }

    @Override
    public Student getByStudentNo(String studentNo) {
        LambdaQueryWrapper<Student> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Student::getStudentNo, studentNo);
        return studentMapper.selectOne(wrapper);
    }

    @Override
    @Transactional
    public boolean addStudent(Student student) {
        student.setStatus(1);
        if (student.getStudentStatus() == null) {
            student.setStudentStatus("在读");
        }
        int result = studentMapper.insert(student);
        if (result > 0 && StringUtils.hasText(student.getClassId())) {
            updateClassStudentCount(student.getClassId());
        }
        return result > 0;
    }

    @Override
    @Transactional
    public boolean updateStudent(Student student) {
        Student oldStudent = studentMapper.selectById(student.getStudentId());
        if (oldStudent == null) {
            return false;
        }
        
        recordChanges(oldStudent, student);
        
        int result = studentMapper.updateById(student);
        
        if (result > 0) {
            if (!Objects.equals(oldStudent.getClassId(), student.getClassId())) {
                if (StringUtils.hasText(oldStudent.getClassId())) {
                    updateClassStudentCount(oldStudent.getClassId());
                }
                if (StringUtils.hasText(student.getClassId())) {
                    updateClassStudentCount(student.getClassId());
                }
            }
        }
        return result > 0;
    }

    @Override
    @Transactional
    public boolean deleteStudent(String studentId) {
        Student student = studentMapper.selectById(studentId);
        if (student == null) {
            return false;
        }
        
        student.setStatus(0);
        int result = studentMapper.updateById(student);
        
        if (result > 0 && StringUtils.hasText(student.getClassId())) {
            updateClassStudentCount(student.getClassId());
        }
        return result > 0;
    }

    @Override
    public List<Map<String, Object>> getGradeStatistics() {
        List<Student> students = studentMapper.selectList(
            new LambdaQueryWrapper<Student>()
                .eq(Student::getStatus, 1)
                .eq(Student::getStudentStatus, "在读")
        );
        
        Map<String, Long> gradeCount = new HashMap<>();
        for (Student student : students) {
            String grade = student.getGrade();
            gradeCount.merge(grade, 1L, Long::sum);
        }
        
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<String, Long> entry : gradeCount.entrySet()) {
            Map<String, Object> item = new HashMap<>();
            item.put("grade", entry.getKey());
            item.put("count", entry.getValue());
            result.add(item);
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> getGenderStatistics() {
        List<Student> students = studentMapper.selectList(
            new LambdaQueryWrapper<Student>()
                .eq(Student::getStatus, 1)
                .eq(Student::getStudentStatus, "在读")
        );
        
        Map<String, Long> genderCount = new HashMap<>();
        for (Student student : students) {
            String gender = student.getGender();
            genderCount.merge(gender, 1L, Long::sum);
        }
        
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<String, Long> entry : genderCount.entrySet()) {
            Map<String, Object> item = new HashMap<>();
            item.put("gender", "M".equals(entry.getKey()) ? "男" : "女");
            item.put("count", entry.getValue());
            result.add(item);
        }
        return result;
    }

    @Override
    public long countByClassId(String classId) {
        return studentMapper.selectCount(
            new LambdaQueryWrapper<Student>()
                .eq(Student::getClassId, classId)
                .eq(Student::getStatus, 1)
                .eq(Student::getStudentStatus, "在读")
        );
    }

    @Override
    public Student getByParentId(String parentId) {
        return studentMapper.selectOne(
            new LambdaQueryWrapper<Student>()
                .eq(Student::getParentId, parentId)
                .eq(Student::getStatus, 1)
                .last("LIMIT 1")
        );
    }

    @Override
    public void updateClassStudentCount(String classId) {
        long count = countByClassId(classId);
        Clazz clazz = clazzMapper.selectById(classId);
        if (clazz != null) {
            clazz.setStudentCount((int) count);
            clazzMapper.updateById(clazz);
        }
    }
    
    private void recordChanges(Student oldStudent, Student newStudent) {
        Map<String, String[]> fieldLabels = new HashMap<>();
        fieldLabels.put("name", new String[]{"姓名", oldStudent.getName(), newStudent.getName()});
        fieldLabels.put("gender", new String[]{"性别", oldStudent.getGender(), newStudent.getGender()});
        fieldLabels.put("phone", new String[]{"联系电话", oldStudent.getPhone(), newStudent.getPhone()});
        fieldLabels.put("guardianName", new String[]{"监护人姓名", oldStudent.getGuardianName(), newStudent.getGuardianName()});
        fieldLabels.put("guardianPhone", new String[]{"监护人电话", oldStudent.getGuardianPhone(), newStudent.getGuardianPhone()});
        fieldLabels.put("currentAddress", new String[]{"现住址", oldStudent.getCurrentAddress(), newStudent.getCurrentAddress()});
        
        for (Map.Entry<String, String[]> entry : fieldLabels.entrySet()) {
            String fieldName = entry.getKey();
            String[] values = entry.getValue();
            String fieldLabel = values[0];
            String oldValue = values[1];
            String newValue = values[2];
            
            if (!Objects.equals(oldValue, newValue)) {
                InfoChangeLog log = new InfoChangeLog();
                log.setStudentId(oldStudent.getStudentId());
                log.setStudentNo(oldStudent.getStudentNo());
                log.setFieldName(fieldName);
                log.setFieldLabel(fieldLabel);
                log.setOldValue(oldValue);
                log.setNewValue(newValue);
                infoChangeLogMapper.insert(log);
            }
        }
    }

    @Override
    @Transactional
    public int importStudents(MultipartFile file) throws Exception {
        Workbook workbook = WorkbookFactory.create(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);
        
        int successCount = 0;
        int startRow = 1;
        
        for (int i = startRow; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;
            
            try {
                Student student = new Student();
                student.setStudentNo(getCellValue(row, 0));
                student.setName(getCellValue(row, 1));
                student.setGender(getCellValue(row, 2));
                student.setIdCard(getCellValue(row, 3));
                student.setGrade(getCellValue(row, 4));
                student.setClassName(getCellValue(row, 5));
                student.setPhone(getCellValue(row, 6));
                student.setGuardianName(getCellValue(row, 7));
                student.setGuardianPhone(getCellValue(row, 8));
                student.setCurrentAddress(getCellValue(row, 9));
                student.setStudentStatus("在读");
                student.setStatus(1);
                
                Student existing = getByStudentNo(student.getStudentNo());
                if (existing == null) {
                    addStudent(student);
                    successCount++;
                }
            } catch (Exception e) {
                continue;
            }
        }
        
        workbook.close();
        return successCount;
    }
    
    private String getCellValue(Row row, int column) {
        Cell cell = row.getCell(column);
        if (cell == null) {
            return "";
        }
        
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                }
                return String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }
}
