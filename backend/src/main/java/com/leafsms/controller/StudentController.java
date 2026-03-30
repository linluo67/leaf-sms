package com.leafsms.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.leafsms.common.Result;
import com.leafsms.entity.Student;
import com.leafsms.service.StudentService;
import com.leafsms.utils.LogUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/students")
public class StudentController {
    
    @Autowired
    private StudentService studentService;
    
    @Autowired
    private LogUtil logUtil;

    @GetMapping
    public Result<Page<Student>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String studentNo,
            @RequestParam(required = false) String grade,
            @RequestParam(required = false) String classId,
            @RequestParam(required = false) String status,
            HttpServletRequest request) {
        
        Page<Student> studentPage = studentService.pageStudents(page, size, name, studentNo, grade, classId, status);
        return Result.success(studentPage);
    }

    @GetMapping("/{studentId}")
    public Result<Student> getById(@PathVariable String studentId, HttpServletRequest request) {
        Student student = studentService.getById(studentId);
        if (student == null) {
            return Result.error("学生不存在");
        }
        return Result.success(student);
    }

    @PostMapping
    public Result<Student> add(@Valid @RequestBody Student student, HttpServletRequest request) {
        Student existing = studentService.getByStudentNo(student.getStudentNo());
        if (existing != null) {
            return Result.error("学籍号已存在");
        }
        
        boolean success = studentService.addStudent(student);
        if (success) {
            logUtil.log("新增学生", "POST", request.getRequestURI(), "学籍号: " + student.getStudentNo());
            return Result.success(student);
        }
        return Result.error("新增学生失败");
    }

    @PutMapping("/{studentId}")
    public Result<Student> update(@PathVariable String studentId, 
                                   @Valid @RequestBody Student student,
                                   HttpServletRequest request) {
        Student existing = studentService.getById(studentId);
        if (existing == null) {
            return Result.error("学生不存在");
        }
        
        student.setStudentId(studentId);
        boolean success = studentService.updateStudent(student);
        if (success) {
            logUtil.log("更新学生信息", "PUT", request.getRequestURI(), "ID: " + studentId);
            return Result.success(student);
        }
        return Result.error("更新学生失败");
    }

    @DeleteMapping("/{studentId}")
    public Result<Void> delete(@PathVariable String studentId, HttpServletRequest request) {
        boolean success = studentService.deleteStudent(studentId);
        if (success) {
            logUtil.log("删除学生", "DELETE", request.getRequestURI(), "ID: " + studentId);
            return Result.success(null);
        }
        return Result.error("删除学生失败");
    }

    @GetMapping("/statistics")
    public Result<Map<String, Object>> statistics(HttpServletRequest request) {
        Map<String, Object> stats = new HashMap<>();
        
        List<Map<String, Object>> gradeStats = studentService.getGradeStatistics();
        List<Map<String, Object>> genderStats = studentService.getGenderStatistics();
        
        long total = 0;
        for (Map<String, Object> item : gradeStats) {
            total += (Long) item.get("count");
        }
        
        stats.put("total", total);
        stats.put("byGrade", gradeStats);
        stats.put("byGender", genderStats);
        
        return Result.success(stats);
    }

    @PostMapping("/import")
    public Result<Map<String, Object>> importStudents(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        try {
            int successCount = studentService.importStudents(file);
            Map<String, Object> result = new HashMap<>();
            result.put("successCount", successCount);
            logUtil.log("导入学生", "POST", request.getRequestURI(), "成功导入: " + successCount + "条");
            return Result.success("导入成功", result);
        } catch (Exception e) {
            logUtil.log("导入学生", "POST", request.getRequestURI(), "导入失败: " + e.getMessage());
            return Result.error("导入失败: " + e.getMessage());
        }
    }

    @GetMapping("/export")
    public void exportStudents(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String grade,
            HttpServletResponse response,
            HttpServletRequest request) throws IOException {
        
        List<Student> students;
        if ("grade".equals(type) && grade != null) {
            students = studentService.list(new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Student>()
                .eq("grade", grade));
        } else {
            students = studentService.list();
        }
        
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("学生表");
        
        Row headerRow = sheet.createRow(0);
        String[] headers = {"学籍号", "姓名", "性别", "出生日期", "身份证号", "年级", "班级", "入学日期", "状态", "联系电话", "家庭住址"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(createHeaderStyle(workbook));
        }
        
        int rowNum = 1;
        for (Student student : students) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(student.getStudentNo());
            row.createCell(1).setCellValue(student.getName());
            row.createCell(2).setCellValue(student.getGender());
            row.createCell(3).setCellValue(student.getBirthDate() != null ? student.getBirthDate().toString() : "");
            row.createCell(4).setCellValue(student.getIdCard());
            row.createCell(5).setCellValue(student.getGrade());
            row.createCell(6).setCellValue(student.getClassName());
            row.createCell(7).setCellValue(student.getEnrollmentDate() != null ? student.getEnrollmentDate().toString() : "");
            row.createCell(8).setCellValue(student.getStatus());
            row.createCell(9).setCellValue(student.getPhone());
            row.createCell(10).setCellValue(student.getHouseholdAddress());
        }
        
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
        
        String fileName = URLEncoder.encode("学生表.xlsx", "UTF-8");
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
        
        workbook.write(response.getOutputStream());
        workbook.close();
        
        logUtil.log("导出学生", "GET", request.getRequestURI(), "导出成功");
    }
    
    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }
}
