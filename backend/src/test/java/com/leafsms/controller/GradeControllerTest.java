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
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

@WebMvcTest(GradeController.class)
@Import(GlobalExceptionHandler.class)
class GradeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private GradeService gradeService;

    @MockBean
    private LogUtil logUtil;

    private Grade testGrade;
    private List<Grade> testGrades;

    @BeforeEach
    void setUp() {
        testGrade = new Grade();
        testGrade.setGradeId("test-grade-id");
        testGrade.setStudentId("test-student-id");
        testGrade.setClassId("test-class-id");
        testGrade.setSubject("数学");
        testGrade.setExamName("期中考试");
        testGrade.setScore(new BigDecimal("85.5"));
        testGrade.setRank(10);
        testGrade.setSemester("2024-2025-1");
        testGrade.setExamDate(LocalDateTime.now());
        testGrade.setRemark("测试成绩");
        testGrade.setCreateTime(LocalDateTime.now());
        testGrade.setUpdateTime(LocalDateTime.now());

        testGrades = new ArrayList<>();
        testGrades.add(testGrade);

        Grade grade2 = new Grade();
        grade2.setGradeId("test-grade-id-2");
        grade2.setStudentId("test-student-id");
        grade2.setClassId("test-class-id");
        grade2.setSubject("语文");
        grade2.setExamName("期中考试");
        grade2.setScore(new BigDecimal("90.0"));
        grade2.setRank(5);
        grade2.setSemester("2024-2025-1");
        testGrades.add(grade2);
    }

    @Test
    @DisplayName("查询成绩列表 - 成功")
    void testListGrades_Success() throws Exception {
        Page<Grade> page = new Page<>(1, 10);
        page.setRecords(testGrades);
        page.setTotal(2);

        when(gradeService.pageGrades(any(Page.class), any(), any(), any(), any()))
                .thenReturn(page);

        MvcResult result = mockMvc.perform(get("/api/v1/grades")
                        .param("page", "1")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data.records").isArray())
                .andExpect(jsonPath("$.data.total").value(2))
                .andReturn();

        verify(gradeService).pageGrades(any(Page.class), isNull(), isNull(), isNull(), isNull());
    }

    @Test
    @DisplayName("查询成绩列表 - 带筛选条件")
    void testListGrades_WithFilters() throws Exception {
        Page<Grade> page = new Page<>(1, 10);
        page.setRecords(Collections.singletonList(testGrade));
        page.setTotal(1);

        when(gradeService.pageGrades(any(Page.class), eq("class-1"), eq("数学"), eq("期中考试"), eq("2024-2025-1")))
                .thenReturn(page);

        mockMvc.perform(get("/api/v1/grades")
                        .param("page", "1")
                        .param("size", "10")
                        .param("classId", "class-1")
                        .param("subject", "数学")
                        .param("examName", "期中考试")
                        .param("semester", "2024-2025-1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.total").value(1));

        verify(gradeService).pageGrades(any(Page.class), eq("class-1"), eq("数学"), eq("期中考试"), eq("2024-2025-1"));
    }

    @Test
    @DisplayName("查询成绩列表 - 分页参数默认值")
    void testListGrades_DefaultPagination() throws Exception {
        Page<Grade> page = new Page<>(1, 10);
        page.setRecords(new ArrayList<>());
        page.setTotal(0);

        when(gradeService.pageGrades(any(Page.class), any(), any(), any(), any()))
                .thenReturn(page);

        mockMvc.perform(get("/api/v1/grades")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(gradeService).pageGrades(any(Page.class), isNull(), isNull(), isNull(), isNull());
    }

    @Test
    @DisplayName("查询单个成绩 - 成功")
    void testGetById_Success() throws Exception {
        when(gradeService.getById("test-grade-id")).thenReturn(testGrade);

        mockMvc.perform(get("/api/v1/grades/test-grade-id")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.gradeId").value("test-grade-id"))
                .andExpect(jsonPath("$.data.subject").value("数学"))
                .andExpect(jsonPath("$.data.score").value(85.5));

        verify(gradeService).getById("test-grade-id");
    }

    @Test
    @DisplayName("查询单个成绩 - 不存在")
    void testGetById_NotFound() throws Exception {
        when(gradeService.getById("non-existent-id")).thenReturn(null);

        mockMvc.perform(get("/api/v1/grades/non-existent-id")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("成绩记录不存在"));

        verify(gradeService).getById("non-existent-id");
    }

    @Test
    @DisplayName("按学生ID查询成绩 - 成功")
    void testGetByStudentId_Success() throws Exception {
        when(gradeService.findByStudentId("test-student-id")).thenReturn(testGrades);

        mockMvc.perform(get("/api/v1/grades/student/test-student-id")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2));

        verify(gradeService).findByStudentId("test-student-id");
    }

    @Test
    @DisplayName("按学生ID查询成绩 - 空列表")
    void testGetByStudentId_EmptyList() throws Exception {
        when(gradeService.findByStudentId("non-existent-student")).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/api/v1/grades/student/non-existent-student")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(0));

        verify(gradeService).findByStudentId("non-existent-student");
    }

    @Test
    @DisplayName("新增成绩 - 成功")
    void testAddGrade_Success() throws Exception {
        Grade newGrade = new Grade();
        newGrade.setStudentId("student-1");
        newGrade.setClassId("class-1");
        newGrade.setSubject("英语");
        newGrade.setExamName("期末考试");
        newGrade.setScore(new BigDecimal("88.0"));
        newGrade.setSemester("2024-2025-1");

        when(gradeService.addGrade(any(Grade.class))).thenReturn(true);

        mockMvc.perform(post("/api/v1/grades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newGrade)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.studentId").value("student-1"));

        verify(gradeService).addGrade(any(Grade.class));
    }

    @Test
    @DisplayName("新增成绩 - 缺少必填字段studentId")
    void testAddGrade_MissingStudentId() throws Exception {
        Grade invalidGrade = new Grade();
        invalidGrade.setClassId("class-1");
        invalidGrade.setSubject("数学");
        invalidGrade.setExamName("期中考试");
        invalidGrade.setScore(new BigDecimal("85.0"));
        invalidGrade.setSemester("2024-2025-1");

        mockMvc.perform(post("/api/v1/grades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidGrade)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("学生ID不能为空"));

        verify(gradeService, never()).addGrade(any(Grade.class));
    }

    @Test
    @DisplayName("新增成绩 - 缺少必填字段subject")
    void testAddGrade_MissingSubject() throws Exception {
        Grade invalidGrade = new Grade();
        invalidGrade.setStudentId("student-1");
        invalidGrade.setClassId("class-1");
        invalidGrade.setExamName("期中考试");
        invalidGrade.setScore(new BigDecimal("85.0"));
        invalidGrade.setSemester("2024-2025-1");

        mockMvc.perform(post("/api/v1/grades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidGrade)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("科目不能为空"));

        verify(gradeService, never()).addGrade(any(Grade.class));
    }

    @Test
    @DisplayName("新增成绩 - 缺少必填字段score")
    void testAddGrade_MissingScore() throws Exception {
        Grade invalidGrade = new Grade();
        invalidGrade.setStudentId("student-1");
        invalidGrade.setClassId("class-1");
        invalidGrade.setSubject("数学");
        invalidGrade.setExamName("期中考试");
        invalidGrade.setSemester("2024-2025-1");

        mockMvc.perform(post("/api/v1/grades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidGrade)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("分数不能为空"));

        verify(gradeService, never()).addGrade(any(Grade.class));
    }

    @Test
    @DisplayName("新增成绩 - 分数低于最小值")
    void testAddGrade_ScoreBelowMin() throws Exception {
        Grade invalidGrade = new Grade();
        invalidGrade.setStudentId("student-1");
        invalidGrade.setClassId("class-1");
        invalidGrade.setSubject("数学");
        invalidGrade.setExamName("期中考试");
        invalidGrade.setScore(new BigDecimal("-1"));
        invalidGrade.setSemester("2024-2025-1");

        mockMvc.perform(post("/api/v1/grades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidGrade)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("分数不能小于0"));

        verify(gradeService, never()).addGrade(any(Grade.class));
    }

    @Test
    @DisplayName("新增成绩 - 分数高于最大值")
    void testAddGrade_ScoreAboveMax() throws Exception {
        Grade invalidGrade = new Grade();
        invalidGrade.setStudentId("student-1");
        invalidGrade.setClassId("class-1");
        invalidGrade.setSubject("数学");
        invalidGrade.setExamName("期中考试");
        invalidGrade.setScore(new BigDecimal("101"));
        invalidGrade.setSemester("2024-2025-1");

        mockMvc.perform(post("/api/v1/grades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidGrade)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("分数不能大于100"));

        verify(gradeService, never()).addGrade(any(Grade.class));
    }

    @Test
    @DisplayName("新增成绩 - 分数边界值0")
    void testAddGrade_ScoreBoundaryZero() throws Exception {
        Grade validGrade = new Grade();
        validGrade.setStudentId("student-1");
        validGrade.setClassId("class-1");
        validGrade.setSubject("数学");
        validGrade.setExamName("期中考试");
        validGrade.setScore(new BigDecimal("0"));
        validGrade.setSemester("2024-2025-1");

        when(gradeService.addGrade(any(Grade.class))).thenReturn(true);

        mockMvc.perform(post("/api/v1/grades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validGrade)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(gradeService).addGrade(any(Grade.class));
    }

    @Test
    @DisplayName("新增成绩 - 分数边界值100")
    void testAddGrade_ScoreBoundaryHundred() throws Exception {
        Grade validGrade = new Grade();
        validGrade.setStudentId("student-1");
        validGrade.setClassId("class-1");
        validGrade.setSubject("数学");
        validGrade.setExamName("期中考试");
        validGrade.setScore(new BigDecimal("100"));
        validGrade.setSemester("2024-2025-1");

        when(gradeService.addGrade(any(Grade.class))).thenReturn(true);

        mockMvc.perform(post("/api/v1/grades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validGrade)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(gradeService).addGrade(any(Grade.class));
    }

    @Test
    @DisplayName("新增成绩 - 科目长度超限")
    void testAddGrade_SubjectTooLong() throws Exception {
        Grade invalidGrade = new Grade();
        invalidGrade.setStudentId("student-1");
        invalidGrade.setClassId("class-1");
        invalidGrade.setSubject("一二三四五六七八九十一二三四五六七八九十一二三四五六七八九十一二三四五六七八九十一二三四五六七八九十1");
        invalidGrade.setExamName("期中考试");
        invalidGrade.setScore(new BigDecimal("85.0"));
        invalidGrade.setSemester("2024-2025-1");

        mockMvc.perform(post("/api/v1/grades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidGrade)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400));

        verify(gradeService, never()).addGrade(any(Grade.class));
    }

    @Test
    @DisplayName("新增成绩 - 服务层失败")
    void testAddGrade_ServiceFailure() throws Exception {
        Grade newGrade = new Grade();
        newGrade.setStudentId("student-1");
        newGrade.setClassId("class-1");
        newGrade.setSubject("英语");
        newGrade.setExamName("期末考试");
        newGrade.setScore(new BigDecimal("88.0"));
        newGrade.setSemester("2024-2025-1");

        when(gradeService.addGrade(any(Grade.class))).thenReturn(false);

        mockMvc.perform(post("/api/v1/grades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newGrade)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("新增成绩失败"));

        verify(gradeService).addGrade(any(Grade.class));
    }

    @Test
    @DisplayName("修改成绩 - 成功")
    void testUpdateGrade_Success() throws Exception {
        Grade updateGrade = new Grade();
        updateGrade.setStudentId("student-1");
        updateGrade.setClassId("class-1");
        updateGrade.setSubject("数学");
        updateGrade.setExamName("期中考试");
        updateGrade.setScore(new BigDecimal("95.0"));
        updateGrade.setSemester("2024-2025-1");

        when(gradeService.getById("test-grade-id")).thenReturn(testGrade);
        when(gradeService.updateGrade(any(Grade.class))).thenReturn(true);

        mockMvc.perform(put("/api/v1/grades/test-grade-id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateGrade)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.gradeId").value("test-grade-id"));

        verify(gradeService).getById("test-grade-id");
        verify(gradeService).updateGrade(any(Grade.class));
    }

    @Test
    @DisplayName("修改成绩 - 记录不存在")
    void testUpdateGrade_NotFound() throws Exception {
        Grade updateGrade = new Grade();
        updateGrade.setStudentId("student-1");
        updateGrade.setClassId("class-1");
        updateGrade.setSubject("数学");
        updateGrade.setExamName("期中考试");
        updateGrade.setScore(new BigDecimal("95.0"));
        updateGrade.setSemester("2024-2025-1");

        when(gradeService.getById("non-existent-id")).thenReturn(null);

        mockMvc.perform(put("/api/v1/grades/non-existent-id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateGrade)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("成绩记录不存在"));

        verify(gradeService).getById("non-existent-id");
        verify(gradeService, never()).updateGrade(any(Grade.class));
    }

    @Test
    @DisplayName("修改成绩 - 服务层失败")
    void testUpdateGrade_ServiceFailure() throws Exception {
        Grade updateGrade = new Grade();
        updateGrade.setStudentId("student-1");
        updateGrade.setClassId("class-1");
        updateGrade.setSubject("数学");
        updateGrade.setExamName("期中考试");
        updateGrade.setScore(new BigDecimal("95.0"));
        updateGrade.setSemester("2024-2025-1");

        when(gradeService.getById("test-grade-id")).thenReturn(testGrade);
        when(gradeService.updateGrade(any(Grade.class))).thenReturn(false);

        mockMvc.perform(put("/api/v1/grades/test-grade-id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateGrade)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("更新成绩失败"));

        verify(gradeService).getById("test-grade-id");
        verify(gradeService).updateGrade(any(Grade.class));
    }

    @Test
    @DisplayName("修改成绩 - 缺少必填字段")
    void testUpdateGrade_MissingRequiredField() throws Exception {
        Grade invalidGrade = new Grade();
        invalidGrade.setStudentId("student-1");
        invalidGrade.setClassId("class-1");
        invalidGrade.setScore(new BigDecimal("95.0"));
        invalidGrade.setSemester("2024-2025-1");

        mockMvc.perform(put("/api/v1/grades/test-grade-id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidGrade)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400));

        verify(gradeService, never()).updateGrade(any(Grade.class));
    }

    @Test
    @DisplayName("删除成绩 - 成功")
    void testDeleteGrade_Success() throws Exception {
        when(gradeService.deleteGrade("test-grade-id")).thenReturn(true);

        mockMvc.perform(delete("/api/v1/grades/test-grade-id")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isEmpty());

        verify(gradeService).deleteGrade("test-grade-id");
    }

    @Test
    @DisplayName("删除成绩 - 记录不存在")
    void testDeleteGrade_NotFound() throws Exception {
        when(gradeService.deleteGrade("non-existent-id")).thenReturn(false);

        mockMvc.perform(delete("/api/v1/grades/non-existent-id")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("删除成绩失败"));

        verify(gradeService).deleteGrade("non-existent-id");
    }

    @Test
    @DisplayName("查询成绩统计 - 成功")
    void testStatistics_Success() throws Exception {
        List<Map<String, Object>> stats = new ArrayList<>();
        Map<String, Object> mathStat = new HashMap<>();
        mathStat.put("subject", "数学");
        mathStat.put("avg", 85.5);
        mathStat.put("max", 100.0);
        mathStat.put("min", 60.0);
        mathStat.put("count", 30);
        stats.add(mathStat);

        when(gradeService.getGradeStatistics("class-1")).thenReturn(stats);

        mockMvc.perform(get("/api/v1/grades/statistics")
                        .param("classId", "class-1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].subject").value("数学"))
                .andExpect(jsonPath("$.data[0].avg").value(85.5));

        verify(gradeService).getGradeStatistics("class-1");
    }

    @Test
    @DisplayName("查询成绩统计 - 无classId参数")
    void testStatistics_NoClassId() throws Exception {
        List<Map<String, Object>> stats = new ArrayList<>();
        when(gradeService.getGradeStatistics(null)).thenReturn(stats);

        mockMvc.perform(get("/api/v1/grades/statistics")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray());

        verify(gradeService).getGradeStatistics(null);
    }

    @Test
    @DisplayName("查询带学生信息的成绩列表 - 成功")
    void testListWithStudent_Success() throws Exception {
        Page<Map<String, Object>> page = new Page<>(1, 10);
        List<Map<String, Object>> records = new ArrayList<>();
        Map<String, Object> record = new HashMap<>();
        record.put("grade_id", "test-grade-id");
        record.put("student_name", "张三");
        record.put("student_no", "2024001");
        record.put("subject", "数学");
        record.put("score", 85.5);
        records.add(record);
        page.setRecords(records);
        page.setTotal(1);

        when(gradeService.pageGradesWithStudent(any(Page.class), any(), any(), any()))
                .thenReturn(page);

        mockMvc.perform(get("/api/v1/grades/with-student")
                        .param("page", "1")
                        .param("size", "10")
                        .param("classId", "class-1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.records").isArray())
                .andExpect(jsonPath("$.data.records[0].student_name").value("张三"));

        verify(gradeService).pageGradesWithStudent(any(Page.class), eq("class-1"), isNull(), isNull());
    }

    @Test
    @DisplayName("返回结构验证 - 包含所有必要字段")
    void testResultStructure() throws Exception {
        when(gradeService.getById("test-grade-id")).thenReturn(testGrade);

        mockMvc.perform(get("/api/v1/grades/test-grade-id")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").exists())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.data.gradeId").value("test-grade-id"))
                .andExpect(jsonPath("$.data.studentId").value("test-student-id"))
                .andExpect(jsonPath("$.data.classId").value("test-class-id"))
                .andExpect(jsonPath("$.data.subject").value("数学"))
                .andExpect(jsonPath("$.data.examName").value("期中考试"))
                .andExpect(jsonPath("$.data.score").value(85.5))
                .andExpect(jsonPath("$.data.rank").value(10))
                .andExpect(jsonPath("$.data.semester").value("2024-2025-1"));
    }

    @Test
    @DisplayName("状态码验证 - 成功返回200")
    void testStatusCode_Success() throws Exception {
        Page<Grade> page = new Page<>(1, 10);
        page.setRecords(new ArrayList<>());
        page.setTotal(0);

        when(gradeService.pageGrades(any(Page.class), any(), any(), any(), any()))
                .thenReturn(page);

        mockMvc.perform(get("/api/v1/grades")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("状态码验证 - 业务错误返回400")
    void testStatusCode_BusinessError() throws Exception {
        when(gradeService.getById("non-existent-id")).thenReturn(null);

        mockMvc.perform(get("/api/v1/grades/non-existent-id")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400));
    }

    @Test
    @DisplayName("备注长度超限测试")
    void testAddGrade_RemarkTooLong() throws Exception {
        StringBuilder longRemark = new StringBuilder();
        for (int i = 0; i < 51; i++) {
            longRemark.append("这是一段很长的备注信息");
        }

        Grade invalidGrade = new Grade();
        invalidGrade.setStudentId("student-1");
        invalidGrade.setClassId("class-1");
        invalidGrade.setSubject("数学");
        invalidGrade.setExamName("期中考试");
        invalidGrade.setScore(new BigDecimal("85.0"));
        invalidGrade.setSemester("2024-2025-1");
        invalidGrade.setRemark(longRemark.toString());

        mockMvc.perform(post("/api/v1/grades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidGrade)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("备注长度不能超过500"));

        verify(gradeService, never()).addGrade(any(Grade.class));
    }

    @Test
    @DisplayName("考试名称长度超限测试")
    void testAddGrade_ExamNameTooLong() throws Exception {
        StringBuilder longName = new StringBuilder();
        for (int i = 0; i < 15; i++) {
            longName.append("这是一个很长的考试名称");
        }

        Grade invalidGrade = new Grade();
        invalidGrade.setStudentId("student-1");
        invalidGrade.setClassId("class-1");
        invalidGrade.setSubject("数学");
        invalidGrade.setExamName(longName.toString());
        invalidGrade.setScore(new BigDecimal("85.0"));
        invalidGrade.setSemester("2024-2025-1");

        mockMvc.perform(post("/api/v1/grades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidGrade)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("考试名称长度不能超过100"));

        verify(gradeService, never()).addGrade(any(Grade.class));
    }

    @Test
    @DisplayName("学期长度超限测试")
    void testAddGrade_SemesterTooLong() throws Exception {
        Grade invalidGrade = new Grade();
        invalidGrade.setStudentId("student-1");
        invalidGrade.setClassId("class-1");
        invalidGrade.setSubject("数学");
        invalidGrade.setExamName("期中考试");
        invalidGrade.setScore(new BigDecimal("85.0"));
        invalidGrade.setSemester("这是一个超过五十个字符长度的学期名称测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试");

        mockMvc.perform(post("/api/v1/grades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidGrade)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400));

        verify(gradeService, never()).addGrade(any(Grade.class));
    }

    @Test
    @DisplayName("小数分数测试")
    void testAddGrade_DecimalScore() throws Exception {
        Grade validGrade = new Grade();
        validGrade.setStudentId("student-1");
        validGrade.setClassId("class-1");
        validGrade.setSubject("数学");
        validGrade.setExamName("期中考试");
        validGrade.setScore(new BigDecimal("85.55"));
        validGrade.setSemester("2024-2025-1");

        when(gradeService.addGrade(any(Grade.class))).thenReturn(true);

        mockMvc.perform(post("/api/v1/grades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validGrade)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(gradeService).addGrade(any(Grade.class));
    }
}
