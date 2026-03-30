package com.leafsms.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leafsms.common.GlobalExceptionHandler;
import com.leafsms.common.Result;
import com.leafsms.entity.Grade;
import com.leafsms.service.GradeService;
import com.leafsms.utils.LogUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GradeController.class)
@Import(GlobalExceptionHandler.class)
class GradeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @MockBean
    private GradeService gradeService;

    @MockBean
    private LogUtil logUtil;

    private Grade sampleGrade;
    private String gradeId;
    private String studentId;

    @BeforeEach
    void setUp() {
        gradeId = UUID.randomUUID().toString();
        studentId = UUID.randomUUID().toString();

        sampleGrade = new Grade();
        sampleGrade.setGradeId(gradeId);
        sampleGrade.setStudentId(studentId);
        sampleGrade.setClassId("class001");
        sampleGrade.setSubject("数学");
        sampleGrade.setExamName("期中考试");
        sampleGrade.setScore(new BigDecimal("85.5"));
        sampleGrade.setRank(5);
        sampleGrade.setSemester("2024-2025上学期");
        sampleGrade.setExamDate(LocalDateTime.now());
        sampleGrade.setRemark("表现良好");
        sampleGrade.setCreateTime(LocalDateTime.now());
        sampleGrade.setUpdateTime(LocalDateTime.now());
    }

    @Test
    @DisplayName("查询成绩列表 - 成功")
    void listGrades_Success() throws Exception {
        Page<Grade> page = new Page<>(1, 10);
        List<Grade> records = Collections.singletonList(sampleGrade);
        page.setRecords(records);
        page.setTotal(1);

        when(gradeService.pageGrades(any(Page.class), isNull(), isNull(), isNull(), isNull()))
                .thenReturn(page);

        mockMvc.perform(get("/api/v1/grades")
                        .param("page", "1")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data.records").isArray())
                .andExpect(jsonPath("$.data.records[0].gradeId").value(gradeId))
                .andExpect(jsonPath("$.data.records[0].subject").value("数学"));

        verify(gradeService).pageGrades(any(Page.class), isNull(), isNull(), isNull(), isNull());
    }

    @Test
    @DisplayName("查询成绩列表 - 带筛选条件")
    void listGrades_WithFilters() throws Exception {
        Page<Grade> page = new Page<>(1, 10);
        page.setRecords(Collections.singletonList(sampleGrade));
        page.setTotal(1);

        when(gradeService.pageGrades(any(Page.class), eq("class001"), eq("数学"), eq("期中考试"), eq("2024-2025上学期")))
                .thenReturn(page);

        mockMvc.perform(get("/api/v1/grades")
                        .param("page", "1")
                        .param("size", "10")
                        .param("classId", "class001")
                        .param("subject", "数学")
                        .param("examName", "期中考试")
                        .param("semester", "2024-2025上学期"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.records[0].classId").value("class001"));

        verify(gradeService).pageGrades(any(Page.class), eq("class001"), eq("数学"), eq("期中考试"), eq("2024-2025上学期"));
    }

    @Test
    @DisplayName("查询成绩列表(含学生信息) - 成功")
    void listWithStudent_Success() throws Exception {
        Page<Map<String, Object>> page = new Page<>(1, 10);
        Map<String, Object> record = new HashMap<>();
        record.put("grade_id", gradeId);
        record.put("student_no", "2024001");
        record.put("student_name", "张三");
        record.put("subject", "数学");
        page.setRecords(Collections.singletonList(record));
        page.setTotal(1);

        when(gradeService.pageGradesWithStudent(any(Page.class), isNull(), isNull(), isNull()))
                .thenReturn(page);

        mockMvc.perform(get("/api/v1/grades/with-student")
                        .param("page", "1")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.records[0].student_name").value("张三"));
    }

    @Test
    @DisplayName("根据学生ID查询成绩 - 成功")
    void getByStudentId_Success() throws Exception {
        when(gradeService.findByStudentId(studentId))
                .thenReturn(Collections.singletonList(sampleGrade));

        mockMvc.perform(get("/api/v1/grades/student/{studentId}", studentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].studentId").value(studentId));

        verify(gradeService).findByStudentId(studentId);
    }

    @Test
    @DisplayName("根据ID查询成绩 - 成功")
    void getById_Success() throws Exception {
        when(gradeService.getById(gradeId)).thenReturn(sampleGrade);

        mockMvc.perform(get("/api/v1/grades/{gradeId}", gradeId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.gradeId").value(gradeId))
                .andExpect(jsonPath("$.data.subject").value("数学"));

        verify(gradeService).getById(gradeId);
    }

    @Test
    @DisplayName("根据ID查询成绩 - 记录不存在")
    void getById_NotFound() throws Exception {
        when(gradeService.getById("non-existent-id")).thenReturn(null);

        mockMvc.perform(get("/api/v1/grades/{gradeId}", "non-existent-id"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("成绩记录不存在"));
    }

    @Test
    @DisplayName("新增成绩记录 - 成功")
    void addGrade_Success() throws Exception {
        when(gradeService.addGrade(any(Grade.class))).thenReturn(true);
        doNothing().when(logUtil).log(anyString(), anyString(), anyString(), anyString());

        Grade newGrade = new Grade();
        newGrade.setStudentId(studentId);
        newGrade.setClassId("class001");
        newGrade.setSubject("数学");
        newGrade.setExamName("期末考试");
        newGrade.setScore(new BigDecimal("92.0"));
        newGrade.setSemester("2024-2025上学期");

        mockMvc.perform(post("/api/v1/grades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newGrade)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("success"));

        verify(gradeService).addGrade(any(Grade.class));
        verify(logUtil).log(eq("新增成绩"), eq("POST"), anyString(), anyString());
    }

    @Test
    @DisplayName("更新成绩记录 - 成功")
    void updateGrade_Success() throws Exception {
        when(gradeService.getById(gradeId)).thenReturn(sampleGrade);
        when(gradeService.updateGrade(any(Grade.class))).thenReturn(true);
        doNothing().when(logUtil).log(anyString(), anyString(), anyString(), anyString());

        Grade updateGrade = new Grade();
        updateGrade.setStudentId(studentId);
        updateGrade.setClassId("class001");
        updateGrade.setSubject("数学");
        updateGrade.setExamName("补考");
        updateGrade.setScore(new BigDecimal("88.0"));
        updateGrade.setSemester("2024-2025上学期");

        mockMvc.perform(put("/api/v1/grades/{gradeId}", gradeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateGrade)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("success"));

        verify(gradeService).getById(gradeId);
        verify(gradeService).updateGrade(any(Grade.class));
    }

    @Test
    @DisplayName("更新成绩记录 - 记录不存在")
    void updateGrade_NotFound() throws Exception {
        when(gradeService.getById("non-existent-id")).thenReturn(null);

        Grade updateGrade = new Grade();
        updateGrade.setStudentId(studentId);
        updateGrade.setClassId("class001");
        updateGrade.setSubject("数学");
        updateGrade.setExamName("补考");
        updateGrade.setScore(new BigDecimal("88.0"));
        updateGrade.setSemester("2024-2025上学期");

        mockMvc.perform(put("/api/v1/grades/{gradeId}", "non-existent-id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateGrade)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("成绩记录不存在"));
    }

    @Test
    @DisplayName("更新成绩记录 - 更新失败")
    void updateGrade_Failure() throws Exception {
        when(gradeService.getById(gradeId)).thenReturn(sampleGrade);
        when(gradeService.updateGrade(any(Grade.class))).thenReturn(false);

        Grade updateGrade = new Grade();
        updateGrade.setStudentId(studentId);
        updateGrade.setClassId("class001");
        updateGrade.setSubject("数学");
        updateGrade.setExamName("补考");
        updateGrade.setScore(new BigDecimal("88.0"));
        updateGrade.setSemester("2024-2025上学期");

        mockMvc.perform(put("/api/v1/grades/{gradeId}", gradeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateGrade)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("更新成绩失败"));
    }

    @Test
    @DisplayName("删除成绩记录 - 成功")
    void deleteGrade_Success() throws Exception {
        when(gradeService.deleteGrade(gradeId)).thenReturn(true);
        doNothing().when(logUtil).log(anyString(), anyString(), anyString(), anyString());

        mockMvc.perform(delete("/api/v1/grades/{gradeId}", gradeId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("success"));

        verify(gradeService).deleteGrade(gradeId);
        verify(logUtil).log(eq("删除成绩"), eq("DELETE"), anyString(), anyString());
    }

    @Test
    @DisplayName("删除成绩记录 - 删除失败")
    void deleteGrade_Failure() throws Exception {
        when(gradeService.deleteGrade(gradeId)).thenReturn(false);

        mockMvc.perform(delete("/api/v1/grades/{gradeId}", gradeId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("删除成绩失败"));
    }

    @Test
    @DisplayName("查询成绩统计 - 成功")
    void statistics_Success() throws Exception {
        List<Map<String, Object>> stats = new ArrayList<>();
        Map<String, Object> stat = new HashMap<>();
        stat.put("subject", "数学");
        stat.put("avg", 85.5);
        stat.put("max", 100.0);
        stat.put("min", 60.0);
        stat.put("count", 30);
        stats.add(stat);

        when(gradeService.getGradeStatistics(isNull())).thenReturn(stats);

        mockMvc.perform(get("/api/v1/grades/statistics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data[0].subject").value("数学"))
                .andExpect(jsonPath("$.data[0].avg").value(85.5));
    }

    @Test
    @DisplayName("导出成绩 - 成功")
    void export_Success() throws Exception {
        Page<Map<String, Object>> page = new Page<>(1, 10000);
        Map<String, Object> record = new HashMap<>();
        record.put("student_no", "2024001");
        record.put("student_name", "张三");
        record.put("subject", "数学");
        record.put("exam_name", "期中考试");
        record.put("score", 85.5);
        record.put("rank", 5);
        record.put("semester", "2024-2025上学期");
        record.put("exam_date", LocalDateTime.now());
        record.put("remark", "表现良好");
        page.setRecords(Collections.singletonList(record));

        when(gradeService.pageGradesWithStudent(any(Page.class), isNull(), isNull(), isNull()))
                .thenReturn(page);
        doNothing().when(logUtil).log(anyString(), anyString(), anyString(), anyString());

        mockMvc.perform(get("/api/v1/grades/export"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .andExpect(header().string("Content-Disposition", org.hamcrest.Matchers.containsString(".xlsx")));
    }
}
