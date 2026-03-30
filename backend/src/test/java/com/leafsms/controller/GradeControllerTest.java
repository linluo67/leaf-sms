package com.leafsms.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.leafsms.entity.Grade;
import com.leafsms.service.GradeService;
import com.leafsms.utils.LogUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GradeController.class)
class GradeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GradeService gradeService;

    @MockBean
    private LogUtil logUtil;

    private Grade testGrade;
    private List<Grade> gradeList;

    @BeforeEach
    void setUp() {
        testGrade = new Grade();
        testGrade.setGradeId("test-grade-id");
        testGrade.setStudentId("stu-001");
        testGrade.setClassId("class-001");
        testGrade.setSubject("数学");
        testGrade.setExamName("期中考试");
        testGrade.setScore(new BigDecimal("95.5"));
        testGrade.setRank(1);
        testGrade.setSemester("2023-2024-1");
        testGrade.setExamDate(LocalDateTime.now());
        testGrade.setRemark("表现优秀");
        testGrade.setCreateTime(LocalDateTime.now());
        testGrade.setUpdateTime(LocalDateTime.now());

        gradeList = Arrays.asList(testGrade);
    }

    // 1. 查询成绩列表
    @Test
    void listGrades_ShouldReturnSuccess() throws Exception {
        Page<Grade> page = new Page<>(1, 10);
        page.setRecords(gradeList);
        page.setTotal(1);

        when(gradeService.pageGrades(any(), isNull(), isNull(), isNull(), isNull()))
                .thenReturn(page);

        mockMvc.perform(get("/api/v1/grades")
                        .param("page", "1")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data.records", hasSize(1)))
                .andExpect(jsonPath("$.data.total").value(1));
    }

    @Test
    void listGrades_WithFilters_ShouldReturnFilteredResults() throws Exception {
        Page<Grade> page = new Page<>(1, 10);
        page.setRecords(gradeList);
        page.setTotal(1);

        when(gradeService.pageGrades(any(), eq("class-001"), eq("数学"), eq("期中考试"), eq("2023-2024-1")))
                .thenReturn(page);

        mockMvc.perform(get("/api/v1/grades")
                        .param("page", "1")
                        .param("size", "10")
                        .param("classId", "class-001")
                        .param("subject", "数学")
                        .param("examName", "期中考试")
                        .param("semester", "2023-2024-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.records", hasSize(1)));
    }

    // 2. 查询单个学生成绩详情
    @Test
    void getByStudentId_ShouldReturnGrades() throws Exception {
        when(gradeService.findByStudentId("stu-001")).thenReturn(gradeList);

        mockMvc.perform(get("/api/v1/grades/student/stu-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data", hasSize(1)));
    }

    @Test
    void getByStudentId_WithNoGrades_ShouldReturnEmptyList() throws Exception {
        when(gradeService.findByStudentId("stu-999")).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/grades/student/stu-999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data", hasSize(0)));
    }

    // 3. 查询单个成绩记录
    @Test
    void getById_ExistingId_ShouldReturnGrade() throws Exception {
        when(gradeService.getById("test-grade-id")).thenReturn(testGrade);

        mockMvc.perform(get("/api/v1/grades/test-grade-id"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.gradeId").value("test-grade-id"))
                .andExpect(jsonPath("$.data.subject").value("数学"));
    }

    // 7. 查询不存在的成绩记录
    @Test
    void getById_NonExistingId_ShouldReturnError() throws Exception {
        when(gradeService.getById("non-existing-id")).thenReturn(null);

        mockMvc.perform(get("/api/v1/grades/non-existing-id"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("成绩记录不存在"));
    }

    // 4. 新增成绩记录 - 成功场景
    @Test
    void addGrade_ValidData_ShouldReturnSuccess() throws Exception {
        when(gradeService.addGrade(any())).thenReturn(true);

        String requestBody = """
                {
                    "studentId": "stu-001",
                    "classId": "class-001",
                    "subject": "数学",
                    "examName": "期中考试",
                    "score": 95.5,
                    "rank": 1,
                    "semester": "2023-2024-1",
                    "remark": "表现优秀"
                }
                """;

        mockMvc.perform(post("/api/v1/grades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("success"));
    }

    // 6. 参数缺失、必填字段校验失败
    @Test
    void addGrade_MissingStudentId_ShouldReturnValidationError() throws Exception {
        String requestBody = """
                {
                    "classId": "class-001",
                    "subject": "数学",
                    "examName": "期中考试",
                    "score": 95.5,
                    "semester": "2023-2024-1"
                }
                """;

        mockMvc.perform(post("/api/v1/grades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message", containsString("学生ID不能为空")));
    }

    @Test
    void addGrade_MissingSubject_ShouldReturnValidationError() throws Exception {
        String requestBody = """
                {
                    "studentId": "stu-001",
                    "classId": "class-001",
                    "examName": "期中考试",
                    "score": 95.5,
                    "semester": "2023-2024-1"
                }
                """;

        mockMvc.perform(post("/api/v1/grades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message", containsString("科目不能为空")));
    }

    // 8. 分数边界值场景 - 非法分数
    @Test
    void addGrade_ScoreBelowZero_ShouldReturnValidationError() throws Exception {
        String requestBody = """
                {
                    "studentId": "stu-001",
                    "classId": "class-001",
                    "subject": "数学",
                    "examName": "期中考试",
                    "score": -5,
                    "semester": "2023-2024-1"
                }
                """;

        mockMvc.perform(post("/api/v1/grades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message", containsString("分数不能小于0")));
    }

    @Test
    void addGrade_ScoreAbove100_ShouldReturnValidationError() throws Exception {
        String requestBody = """
                {
                    "studentId": "stu-001",
                    "classId": "class-001",
                    "subject": "数学",
                    "examName": "期中考试",
                    "score": 105,
                    "semester": "2023-2024-1"
                }
                """;

        mockMvc.perform(post("/api/v1/grades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message", containsString("分数不能大于100")));
    }

    // 8. 分数边界值场景 - 合法边界值
    @Test
    void addGrade_ScoreZero_ShouldSucceed() throws Exception {
        when(gradeService.addGrade(any())).thenReturn(true);

        String requestBody = """
                {
                    "studentId": "stu-001",
                    "classId": "class-001",
                    "subject": "数学",
                    "examName": "期中考试",
                    "score": 0,
                    "semester": "2023-2024-1"
                }
                """;

        mockMvc.perform(post("/api/v1/grades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void addGrade_Score100_ShouldSucceed() throws Exception {
        when(gradeService.addGrade(any())).thenReturn(true);

        String requestBody = """
                {
                    "studentId": "stu-001",
                    "classId": "class-001",
                    "subject": "数学",
                    "examName": "期中考试",
                    "score": 100,
                    "semester": "2023-2024-1"
                }
                """;

        mockMvc.perform(post("/api/v1/grades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    // 5. 修改成绩记录
    @Test
    void updateGrade_ExistingId_ShouldReturnSuccess() throws Exception {
        when(gradeService.getById("test-grade-id")).thenReturn(testGrade);
        when(gradeService.updateGrade(any())).thenReturn(true);

        String requestBody = """
                {
                    "studentId": "stu-001",
                    "classId": "class-001",
                    "subject": "数学",
                    "examName": "期中考试",
                    "score": 98.0,
                    "semester": "2023-2024-1"
                }
                """;

        mockMvc.perform(put("/api/v1/grades/test-grade-id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("success"));
    }

    @Test
    void updateGrade_NonExistingId_ShouldReturnError() throws Exception {
        when(gradeService.getById("non-existing-id")).thenReturn(null);

        String requestBody = """
                {
                    "studentId": "stu-001",
                    "classId": "class-001",
                    "subject": "数学",
                    "examName": "期中考试",
                    "score": 98.0,
                    "semester": "2023-2024-1"
                }
                """;

        mockMvc.perform(put("/api/v1/grades/non-existing-id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("成绩记录不存在"));
    }

    // 6. 参数格式错误 - JSON解析错误被全局异常处理器捕获返回400
    @Test
    void updateGrade_InvalidScoreFormat_ShouldReturnError() throws Exception {
        String requestBody = """
                {
                    "studentId": "stu-001",
                    "classId": "class-001",
                    "subject": "数学",
                    "examName": "期中考试",
                    "score": "invalid",
                    "semester": "2023-2024-1"
                }
                """;

        mockMvc.perform(put("/api/v1/grades/test-grade-id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400));
    }

    // 新增成绩失败场景
    @Test
    void addGrade_ServiceFailure_ShouldReturnError() throws Exception {
        when(gradeService.addGrade(any())).thenReturn(false);

        String requestBody = """
                {
                    "studentId": "stu-001",
                    "classId": "class-001",
                    "subject": "数学",
                    "examName": "期中考试",
                    "score": 95.5,
                    "semester": "2023-2024-1"
                }
                """;

        mockMvc.perform(post("/api/v1/grades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("新增成绩失败"));
    }

    // 更新成绩失败场景
    @Test
    void updateGrade_ServiceFailure_ShouldReturnError() throws Exception {
        when(gradeService.getById("test-grade-id")).thenReturn(testGrade);
        when(gradeService.updateGrade(any())).thenReturn(false);

        String requestBody = """
                {
                    "studentId": "stu-001",
                    "classId": "class-001",
                    "subject": "数学",
                    "examName": "期中考试",
                    "score": 98.0,
                    "semester": "2023-2024-1"
                }
                """;

        mockMvc.perform(put("/api/v1/grades/test-grade-id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("更新成绩失败"));
    }

    // 6. 删除成绩记录
    @Test
    void deleteGrade_ExistingId_ShouldReturnSuccess() throws Exception {
        when(gradeService.deleteGrade("test-grade-id")).thenReturn(true);

        mockMvc.perform(delete("/api/v1/grades/test-grade-id"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("success"));
    }

    @Test
    void deleteGrade_NonExistingId_ShouldReturnError() throws Exception {
        when(gradeService.deleteGrade("non-existing-id")).thenReturn(false);

        mockMvc.perform(delete("/api/v1/grades/non-existing-id"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("删除成绩失败"));
    }

    // 10. 返回结构验证
    @Test
    void listGrades_ShouldHaveCorrectResponseStructure() throws Exception {
        Page<Grade> page = new Page<>(1, 10);
        page.setRecords(gradeList);
        page.setTotal(1);

        when(gradeService.pageGrades(any(), isNull(), isNull(), isNull(), isNull()))
                .thenReturn(page);

        mockMvc.perform(get("/api/v1/grades"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").exists())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.timestamp").exists());
    }

    // 查询带学生信息的成绩列表
    @Test
    void listWithStudent_ShouldReturnSuccess() throws Exception {
        Page<Map<String, Object>> page = new Page<>();
        List<Map<String, Object>> records = new ArrayList<>();
        Map<String, Object> record = new HashMap<>();
        record.put("gradeId", "test-grade-id");
        record.put("studentName", "张三");
        record.put("subject", "数学");
        record.put("score", 95.5);
        records.add(record);
        page.setRecords(records);
        page.setTotal(1);

        when(gradeService.pageGradesWithStudent(any(), isNull(), isNull(), isNull()))
                .thenReturn(page);

        mockMvc.perform(get("/api/v1/grades/with-student"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.records", hasSize(1)));
    }

    // 成绩统计接口
    @Test
    void statistics_ShouldReturnSuccess() throws Exception {
        List<Map<String, Object>> stats = new ArrayList<>();
        Map<String, Object> stat = new HashMap<>();
        stat.put("subject", "数学");
        stat.put("avg", 85.5);
        stat.put("max", 100);
        stat.put("min", 60);
        stat.put("count", 50);
        stats.add(stat);

        when(gradeService.getGradeStatistics(isNull())).thenReturn(stats);

        mockMvc.perform(get("/api/v1/grades/statistics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data", hasSize(1)));
    }

    // 6. 参数字段过长校验
    @Test
    void addGrade_SubjectTooLong_ShouldReturnValidationError() throws Exception {
        String longSubject = "A".repeat(51);
        String requestBody = String.format("""
                {
                    "studentId": "stu-001",
                    "classId": "class-001",
                    "subject": "%s",
                    "examName": "期中考试",
                    "score": 95.5,
                    "semester": "2023-2024-1"
                }
                """, longSubject);

        mockMvc.perform(post("/api/v1/grades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message", containsString("科目长度不能超过50")));
    }
}
