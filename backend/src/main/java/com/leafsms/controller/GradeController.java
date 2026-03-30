package com.leafsms.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.leafsms.common.Result;
import com.leafsms.entity.Grade;
import com.leafsms.service.GradeService;
import com.leafsms.utils.LogUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/grades")
public class GradeController {
    
    @Autowired
    private GradeService gradeService;
    
    @Autowired
    private LogUtil logUtil;

    @GetMapping
    public Result<IPage<Grade>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String classId,
            @RequestParam(required = false) String subject,
            @RequestParam(required = false) String examName,
            @RequestParam(required = false) String semester,
            HttpServletRequest request) {
        
        Page<Grade> pageParam = new Page<>(page, size);
        IPage<Grade> gradePage = gradeService.pageGrades(pageParam, classId, subject, examName, semester);
        return Result.success(gradePage);
    }

    @GetMapping("/with-student")
    public Result<IPage<Map<String, Object>>> listWithStudent(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String classId,
            @RequestParam(required = false) String subject,
            @RequestParam(required = false) String examName,
            HttpServletRequest request) {
        
        Page<Map<String, Object>> pageParam = new Page<>(page, size);
        IPage<Map<String, Object>> gradePage = gradeService.pageGradesWithStudent(pageParam, classId, subject, examName);
        return Result.success(gradePage);
    }

    @GetMapping("/student/{studentId}")
    public Result<List<Grade>> getByStudentId(@PathVariable String studentId, HttpServletRequest request) {
        List<Grade> grades = gradeService.findByStudentId(studentId);
        return Result.success(grades);
    }

    @GetMapping("/{gradeId}")
    public Result<Grade> getById(@PathVariable String gradeId, HttpServletRequest request) {
        Grade grade = gradeService.getById(gradeId);
        if (grade == null) {
            return Result.error("成绩记录不存在");
        }
        return Result.success(grade);
    }

    @PostMapping
    public Result<Grade> add(@Valid @RequestBody Grade grade, HttpServletRequest request) {
        boolean success = gradeService.addGrade(grade);
        if (success) {
            logUtil.log("新增成绩", "POST", request.getRequestURI(), "学生ID: " + grade.getStudentId());
            return Result.success(grade);
        }
        return Result.error("新增成绩失败");
    }

    @PutMapping("/{gradeId}")
    public Result<Grade> update(@PathVariable String gradeId,
                                   @Valid @RequestBody Grade grade,
                                   HttpServletRequest request) {
        Grade existing = gradeService.getById(gradeId);
        if (existing == null) {
            return Result.error("成绩记录不存在");
        }
        
        grade.setGradeId(gradeId);
        boolean success = gradeService.updateGrade(grade);
        if (success) {
            logUtil.log("更新成绩", "PUT", request.getRequestURI(), "ID: " + gradeId);
            return Result.success(grade);
        }
        return Result.error("更新成绩失败");
    }

    @DeleteMapping("/{gradeId}")
    public Result<Void> delete(@PathVariable String gradeId, HttpServletRequest request) {
        boolean success = gradeService.deleteGrade(gradeId);
        if (success) {
            logUtil.log("删除成绩", "DELETE", request.getRequestURI(), "ID: " + gradeId);
            return Result.success(null);
        }
        return Result.error("删除成绩失败");
    }

    @GetMapping("/statistics")
    public Result<List<Map<String, Object>>> statistics(
            @RequestParam(required = false) String classId,
            HttpServletRequest request) {
        List<Map<String, Object>> stats = gradeService.getGradeStatistics(classId);
        return Result.success(stats);
    }

    @GetMapping("/export")
    public void export(
            @RequestParam(required = false) String classId,
            @RequestParam(required = false) String subject,
            @RequestParam(required = false) String examName,
            HttpServletResponse response,
            HttpServletRequest request) throws IOException {
        
        Page<Map<String, Object>> pageParam = new Page<>(1, 10000);
        IPage<Map<String, Object>> gradePage = gradeService.pageGradesWithStudent(pageParam, classId, subject, examName);
        
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("成绩表");
        
        Row headerRow = sheet.createRow(0);
        String[] headers = {"学号", "姓名", "科目", "考试名称", "分数", "排名", "学期", "考试日期", "备注"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(createHeaderStyle(workbook));
        }
        
        int rowNum = 1;
        for (Map<String, Object> record : gradePage.getRecords()) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue((String) record.get("student_no"));
            row.createCell(1).setCellValue((String) record.get("student_name"));
            row.createCell(2).setCellValue((String) record.get("subject"));
            row.createCell(3).setCellValue((String) record.get("exam_name"));
            Object score = record.get("score");
            row.createCell(4).setCellValue(score != null ? score.toString() : "");
            Object rank = record.get("rank");
            row.createCell(5).setCellValue(rank != null ? rank.toString() : "");
            row.createCell(6).setCellValue((String) record.get("semester"));
            Object examDate = record.get("exam_date");
            row.createCell(7).setCellValue(examDate != null ? examDate.toString() : "");
            row.createCell(8).setCellValue((String) record.get("remark"));
        }
        
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
        
        String fileName = URLEncoder.encode("成绩表.xlsx", "UTF-8");
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
        
        workbook.write(response.getOutputStream());
        workbook.close();
        
        logUtil.log("导出成绩", "GET", request.getRequestURI(), "导出成功");
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
