package com.leafsms.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.leafsms.common.Result;
import com.leafsms.entity.StudentChange;
import com.leafsms.service.StudentChangeService;
import com.leafsms.utils.LogUtil;
import com.leafsms.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/changes")
public class StudentChangeController {
    
    @Autowired
    private StudentChangeService studentChangeService;
    
    @Autowired
    private LogUtil logUtil;
    
    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping
    public Result<Page<StudentChange>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String studentName,
            @RequestParam(required = false) String changeType,
            @RequestParam(required = false) String status,
            HttpServletRequest request) {
        
        Page<StudentChange> changePage = studentChangeService.pageChanges(page, size, studentName, changeType, status);
        return Result.success(changePage);
    }

    @GetMapping("/{changeId}")
    public Result<StudentChange> getById(@PathVariable String changeId, HttpServletRequest request) {
        StudentChange change = studentChangeService.getById(changeId);
        if (change == null) {
            return Result.error("变动记录不存在");
        }
        return Result.success(change);
    }

    @PostMapping
    public Result<StudentChange> add(@Valid @RequestBody StudentChange change, HttpServletRequest request) {
        boolean success = studentChangeService.addChange(change);
        if (success) {
            logUtil.log("新增学籍变动", "POST", request.getRequestURI(), "类型: " + change.getChangeType());
            return Result.success(change);
        }
        return Result.error("新增学籍变动失败");
    }

    @PutMapping("/{changeId}/review")
    public Result<StudentChange> review(
            @PathVariable String changeId,
            @RequestBody Map<String, String> params,
            HttpServletRequest request) {
        
        String status = params.get("status");
        String comment = params.get("reviewComment");
        
        if (status == null || (!status.equals("已通过") && !status.equals("已驳回"))) {
            return Result.error("审核状态无效");
        }
        
        String token = request.getHeader("Authorization");
        String userId = jwtUtil.getUserIdFromToken(token);
        String username = jwtUtil.getUsernameFromToken(token);
        
        boolean success = studentChangeService.reviewChange(changeId, status, comment, userId, username);
        if (success) {
            logUtil.log("审核学籍变动", "PUT", request.getRequestURI(), "ID: " + changeId + ", 状态: " + status);
            return Result.success(studentChangeService.getById(changeId));
        }
        return Result.error("审核失败");
    }

    @GetMapping("/statistics")
    public Result<Map<String, Object>> statistics(HttpServletRequest request) {
        Map<String, Object> stats = new HashMap<>();
        
        List<Map<String, Object>> typeStats = studentChangeService.getChangeTypeStatistics();
        
        long total = 0;
        for (Map<String, Object> item : typeStats) {
            total += (Long) item.get("count");
        }
        
        stats.put("total", total);
        stats.put("byType", typeStats);
        
        return Result.success(stats);
    }

    @GetMapping("/trend")
    public Result<List<Map<String, Object>>> getTrend(
            @RequestParam String period,
            HttpServletRequest request) {
        
        LocalDate endDate = LocalDate.now();
        LocalDate startDate;
        
        switch (period) {
            case "week":
                startDate = endDate.minusWeeks(1);
                break;
            case "month":
                startDate = endDate.minusMonths(1);
                break;
            case "quarter":
                startDate = endDate.minusMonths(3);
                break;
            case "year":
                startDate = endDate.minusYears(1);
                break;
            default:
                startDate = endDate.minusMonths(1);
        }
        
        List<StudentChange> changes = studentChangeService.list(
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<StudentChange>()
                .ge("create_time", startDate)
                .le("create_time", endDate)
                .orderByAsc("create_time")
        );
        
        Map<String, Long> dailyCount = new HashMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        for (StudentChange change : changes) {
            String date = change.getCreateTime().format(formatter);
            dailyCount.merge(date, 1L, Long::sum);
        }
        
        List<Map<String, Object>> trend = new java.util.ArrayList<>();
        for (Map.Entry<String, Long> entry : dailyCount.entrySet()) {
            Map<String, Object> item = new HashMap<>();
            item.put("date", entry.getKey());
            item.put("count", entry.getValue());
            trend.add(item);
        }
        
        return Result.success(trend);
    }

    @GetMapping("/export")
    public void exportChanges(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String changeType,
            HttpServletResponse response,
            HttpServletRequest request) throws IOException {
        
        List<StudentChange> changes;
        if (changeType != null && !changeType.isEmpty()) {
            changes = studentChangeService.list(
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<StudentChange>()
                    .eq("change_type", changeType)
            );
        } else {
            changes = studentChangeService.list();
        }
        
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("学籍变动表");
        
        Row headerRow = sheet.createRow(0);
        String[] headers = {"变动ID", "学生姓名", "变动类型", "变动原因", "申请日期", "审核状态", "审核人", "审核时间", "备注"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(createHeaderStyle(workbook));
        }
        
        int rowNum = 1;
        for (StudentChange change : changes) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(change.getChangeId());
            row.createCell(1).setCellValue(change.getStudentName());
            row.createCell(2).setCellValue(change.getChangeType());
            row.createCell(3).setCellValue(change.getChangeReason());
            row.createCell(4).setCellValue(change.getChangeDate() != null ? change.getChangeDate().toString() : "");
            row.createCell(5).setCellValue(change.getStatus());
            row.createCell(6).setCellValue(change.getReviewerName());
            row.createCell(7).setCellValue(change.getReviewTime() != null ? change.getReviewTime().toString() : "");
            row.createCell(8).setCellValue(change.getReviewComment());
        }
        
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
        
        String fileName = URLEncoder.encode("学籍变动表.xlsx", "UTF-8");
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
        
        workbook.write(response.getOutputStream());
        workbook.close();
        
        logUtil.log("导出学籍变动", "GET", request.getRequestURI(), "导出成功");
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
