package com.leafsms.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.leafsms.common.Result;
import com.leafsms.entity.Clazz;
import com.leafsms.service.ClazzService;
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
import java.util.List;

@RestController
@RequestMapping("/api/v1/classes")
public class ClazzController {
    
    @Autowired
    private ClazzService clazzService;
    
    @Autowired
    private LogUtil logUtil;

    @GetMapping
    public Result<Page<Clazz>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String grade,
            @RequestParam(required = false) String className,
            HttpServletRequest request) {
        
        Page<Clazz> clazzPage = clazzService.pageClazzs(page, size, grade, className);
        return Result.success(clazzPage);
    }

    @GetMapping("/all")
    public Result<List<Clazz>> listAll(HttpServletRequest request) {
        List<Clazz> clazzList = clazzService.list();
        return Result.success(clazzList);
    }

    @GetMapping("/{classId}")
    public Result<Clazz> getById(@PathVariable String classId, HttpServletRequest request) {
        Clazz clazz = clazzService.getById(classId);
        if (clazz == null) {
            return Result.error("班级不存在");
        }
        return Result.success(clazz);
    }

    @PostMapping
    public Result<Clazz> add(@Valid @RequestBody Clazz clazz, HttpServletRequest request) {
        boolean success = clazzService.addClazz(clazz);
        if (success) {
            logUtil.log("新增班级", "POST", request.getRequestURI(), "班级: " + clazz.getClassName());
            return Result.success(clazz);
        }
        return Result.error("新增班级失败");
    }

    @PutMapping("/{classId}")
    public Result<Clazz> update(@PathVariable String classId, 
                                 @Valid @RequestBody Clazz clazz,
                                 HttpServletRequest request) {
        Clazz existing = clazzService.getById(classId);
        if (existing == null) {
            return Result.error("班级不存在");
        }
        
        clazz.setClassId(classId);
        boolean success = clazzService.updateClazz(clazz);
        if (success) {
            logUtil.log("更新班级", "PUT", request.getRequestURI(), "ID: " + classId);
            return Result.success(clazz);
        }
        return Result.error("更新班级失败");
    }

    @DeleteMapping("/{classId}")
    public Result<Void> delete(@PathVariable String classId, HttpServletRequest request) {
        boolean success = clazzService.deleteClazz(classId);
        if (success) {
            logUtil.log("删除班级", "DELETE", request.getRequestURI(), "ID: " + classId);
            return Result.success(null);
        }
        return Result.error("删除班级失败");
    }

    @GetMapping("/export")
    public void exportClasses(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String grade,
            HttpServletResponse response,
            HttpServletRequest request) throws IOException {
        
        List<Clazz> classes;
        if ("grade".equals(type) && grade != null) {
            classes = clazzService.listByGrade(grade);
        } else {
            classes = clazzService.list();
        }
        
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("班级表");
        
        Row headerRow = sheet.createRow(0);
        String[] headers = {"班级ID", "班级名称", "年级", "班主任", "学生人数", "创建时间"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(createHeaderStyle(workbook));
        }
        
        int rowNum = 1;
        for (Clazz clazz : classes) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(clazz.getClassId());
            row.createCell(1).setCellValue(clazz.getClassName());
            row.createCell(2).setCellValue(clazz.getGrade());
            row.createCell(3).setCellValue(clazz.getHeadteacherName());
            row.createCell(4).setCellValue(clazz.getStudentCount());
            row.createCell(5).setCellValue(clazz.getCreateTime() != null ? clazz.getCreateTime().toString() : "");
        }
        
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
        
        String fileName = URLEncoder.encode("班级表.xlsx", "UTF-8");
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
        
        workbook.write(response.getOutputStream());
        workbook.close();
        
        logUtil.log("导出班级", "GET", request.getRequestURI(), "导出成功");
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
