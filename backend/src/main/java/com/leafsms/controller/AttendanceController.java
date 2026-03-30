package com.leafsms.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.leafsms.common.Result;
import com.leafsms.entity.Attendance;
import com.leafsms.service.AttendanceService;
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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/attendance")
public class AttendanceController {

    @Autowired
    private AttendanceService attendanceService;

    @Autowired
    private LogUtil logUtil;

    @GetMapping
    public Result<IPage<Attendance>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String classId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String status,
            HttpServletRequest request) {

        Page<Attendance> pageParam = new Page<>(page, size);
        LocalDate start = startDate != null ? LocalDate.parse(startDate) : null;
        LocalDate end = endDate != null ? LocalDate.parse(endDate) : null;

        IPage<Attendance> attendancePage = attendanceService.pageAttendance(pageParam, classId, start, end, status);
        return Result.success(attendancePage);
    }

    @GetMapping("/with-student")
    public Result<IPage<Map<String, Object>>> listWithStudent(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String classId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String status,
            HttpServletRequest request) {

        Page<Map<String, Object>> pageParam = new Page<>(page, size);
        LocalDate start = startDate != null ? LocalDate.parse(startDate) : null;
        LocalDate end = endDate != null ? LocalDate.parse(endDate) : null;

        IPage<Map<String, Object>> attendancePage = attendanceService.pageAttendanceWithStudent(pageParam, classId, start, end, status);
        return Result.success(attendancePage);
    }

    @GetMapping("/student/{studentId}")
    public Result<List<Attendance>> getByStudentId(@PathVariable String studentId, HttpServletRequest request) {
        List<Attendance> attendances = attendanceService.findByStudentId(studentId);
        return Result.success(attendances);
    }

    @GetMapping("/{attendanceId}")
    public Result<Attendance> getById(@PathVariable String attendanceId, HttpServletRequest request) {
        Attendance attendance = attendanceService.getById(attendanceId);
        if (attendance == null) {
            return Result.error("考勤记录不存在");
        }
        return Result.success(attendance);
    }

    @PostMapping
    public Result<Attendance> add(@Valid @RequestBody Attendance attendance, HttpServletRequest request) {
        boolean success = attendanceService.addAttendance(attendance);
        if (success) {
            logUtil.log("新增考勤", "POST", request.getRequestURI(), "学生ID: " + attendance.getStudentId());
            return Result.success(attendance);
        }
        return Result.error("新增考勤失败");
    }

    @PutMapping("/{attendanceId}")
    public Result<Attendance> update(@PathVariable String attendanceId,
                                     @Valid @RequestBody Attendance attendance,
                                     HttpServletRequest request) {
        Attendance existing = attendanceService.getById(attendanceId);
        if (existing == null) {
            return Result.error("考勤记录不存在");
        }

        attendance.setAttendanceId(attendanceId);
        boolean success = attendanceService.updateAttendance(attendance);
        if (success) {
            logUtil.log("更新考勤", "PUT", request.getRequestURI(), "ID: " + attendanceId);
            return Result.success(attendance);
        }
        return Result.error("更新考勤失败");
    }

    @DeleteMapping("/{attendanceId}")
    public Result<Void> delete(@PathVariable String attendanceId, HttpServletRequest request) {
        boolean success = attendanceService.deleteAttendance(attendanceId);
        if (success) {
            logUtil.log("删除考勤", "DELETE", request.getRequestURI(), "ID: " + attendanceId);
            return Result.success(null);
        }
        return Result.error("删除考勤失败");
    }

    @GetMapping("/statistics")
    public Result<Map<String, Object>> statistics(
            @RequestParam String classId,
            @RequestParam String startDate,
            @RequestParam String endDate,
            HttpServletRequest request) {

        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);

        Map<String, Object> stats = attendanceService.getAttendanceStatistics(classId, start, end);
        return Result.success(stats);
    }

    @GetMapping("/export")
    public void export(
            @RequestParam(required = false) String classId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String status,
            HttpServletResponse response,
            HttpServletRequest request) throws IOException {

        Page<Map<String, Object>> pageParam = new Page<>(1, 10000);
        LocalDate start = startDate != null ? LocalDate.parse(startDate) : null;
        LocalDate end = endDate != null ? LocalDate.parse(endDate) : null;

        IPage<Map<String, Object>> attendancePage = attendanceService.pageAttendanceWithStudent(pageParam, classId, start, end, status);

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("考勤表");

        Row headerRow = sheet.createRow(0);
        String[] headers = {"学号", "姓名", "考勤日期", "考勤状态", "备注"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(createHeaderStyle(workbook));
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        int rowNum = 1;
        for (Map<String, Object> record : attendancePage.getRecords()) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue((String) record.get("student_no"));
            row.createCell(1).setCellValue((String) record.get("student_name"));
            Object attendanceDate = record.get("attendance_date");
            row.createCell(2).setCellValue(attendanceDate != null ? attendanceDate.toString() : "");
            row.createCell(3).setCellValue((String) record.get("status"));
            row.createCell(4).setCellValue((String) record.get("remark"));
        }

        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        String fileName = URLEncoder.encode("考勤表.xlsx", "UTF-8");
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName);

        workbook.write(response.getOutputStream());
        workbook.close();

        logUtil.log("导出考勤", "GET", request.getRequestURI(), "导出成功");
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
