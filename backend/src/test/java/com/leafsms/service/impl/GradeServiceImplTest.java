package com.leafsms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.leafsms.entity.Grade;
import com.leafsms.mapper.GradeMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GradeServiceImplTest {

    @Spy
    private GradeServiceImpl gradeService;

    private GradeMapper gradeMapper;

    private Grade testGrade;
    private List<Grade> testGrades;

    @BeforeEach
    void setUp() {
        gradeMapper = mock(GradeMapper.class);
        ReflectionTestUtils.setField(gradeService, "baseMapper", gradeMapper);

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
    @DisplayName("按学生ID查询成绩 - 成功")
    void testFindByStudentId_Success() {
        when(gradeMapper.findByStudentId("test-student-id")).thenReturn(testGrades);

        List<Grade> result = gradeService.findByStudentId("test-student-id");

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("数学", result.get(0).getSubject());
        assertEquals("语文", result.get(1).getSubject());
        verify(gradeMapper).findByStudentId("test-student-id");
    }

    @Test
    @DisplayName("按学生ID查询成绩 - 空列表")
    void testFindByStudentId_EmptyList() {
        when(gradeMapper.findByStudentId("non-existent-student")).thenReturn(new ArrayList<>());

        List<Grade> result = gradeService.findByStudentId("non-existent-student");

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(gradeMapper).findByStudentId("non-existent-student");
    }

    @Test
    @DisplayName("分页查询成绩 - 无筛选条件")
    void testPageGrades_NoFilters() {
        Page<Grade> page = new Page<>(1, 10);
        page.setRecords(testGrades);
        page.setTotal(2);

        when(gradeMapper.selectPage(any(Page.class), any(QueryWrapper.class))).thenReturn(page);

        IPage<Grade> result = gradeService.pageGrades(new Page<>(1, 10), null, null, null, null);

        assertNotNull(result);
        assertEquals(2, result.getRecords().size());
        verify(gradeMapper).selectPage(any(Page.class), any(QueryWrapper.class));
    }

    @Test
    @DisplayName("分页查询成绩 - 带筛选条件")
    void testPageGrades_WithFilters() {
        Page<Grade> page = new Page<>(1, 10);
        page.setRecords(Collections.singletonList(testGrade));
        page.setTotal(1);

        when(gradeMapper.selectPage(any(Page.class), any(QueryWrapper.class))).thenReturn(page);

        IPage<Grade> result = gradeService.pageGrades(
                new Page<>(1, 10),
                "class-1",
                "数学",
                "期中考试",
                "2024-2025-1"
        );

        assertNotNull(result);
        assertEquals(1, result.getRecords().size());
        verify(gradeMapper).selectPage(any(Page.class), any(QueryWrapper.class));
    }

    @Test
    @DisplayName("分页查询成绩 - 空字符串筛选条件")
    void testPageGrades_EmptyStringFilters() {
        Page<Grade> page = new Page<>(1, 10);
        page.setRecords(testGrades);
        page.setTotal(2);

        when(gradeMapper.selectPage(any(Page.class), any(QueryWrapper.class))).thenReturn(page);

        IPage<Grade> result = gradeService.pageGrades(
                new Page<>(1, 10),
                "class-1",
                "",
                "",
                ""
        );

        assertNotNull(result);
        assertEquals(2, result.getRecords().size());
        verify(gradeMapper).selectPage(any(Page.class), any(QueryWrapper.class));
    }

    @Test
    @DisplayName("新增成绩 - 成功")
    void testAddGrade_Success() {
        Grade newGrade = new Grade();
        newGrade.setStudentId("student-1");
        newGrade.setClassId("class-1");
        newGrade.setSubject("英语");
        newGrade.setExamName("期末考试");
        newGrade.setScore(new BigDecimal("88.0"));
        newGrade.setSemester("2024-2025-1");

        when(gradeMapper.insert(any(Grade.class))).thenReturn(1);

        boolean result = gradeService.addGrade(newGrade);

        assertTrue(result);
        assertNotNull(newGrade.getCreateTime());
        assertNotNull(newGrade.getUpdateTime());
        verify(gradeMapper).insert(any(Grade.class));
    }

    @Test
    @DisplayName("新增成绩 - 失败")
    void testAddGrade_Failure() {
        Grade newGrade = new Grade();
        newGrade.setStudentId("student-1");
        newGrade.setClassId("class-1");
        newGrade.setSubject("英语");
        newGrade.setExamName("期末考试");
        newGrade.setScore(new BigDecimal("88.0"));
        newGrade.setSemester("2024-2025-1");

        when(gradeMapper.insert(any(Grade.class))).thenReturn(0);

        boolean result = gradeService.addGrade(newGrade);

        assertFalse(result);
        verify(gradeMapper).insert(any(Grade.class));
    }

    @Test
    @DisplayName("更新成绩 - 成功")
    void testUpdateGrade_Success() {
        Grade updateGrade = new Grade();
        updateGrade.setGradeId("test-grade-id");
        updateGrade.setStudentId("student-1");
        updateGrade.setClassId("class-1");
        updateGrade.setSubject("数学");
        updateGrade.setExamName("期中考试");
        updateGrade.setScore(new BigDecimal("95.0"));
        updateGrade.setSemester("2024-2025-1");

        when(gradeMapper.updateById(any(Grade.class))).thenReturn(1);

        boolean result = gradeService.updateGrade(updateGrade);

        assertTrue(result);
        assertNotNull(updateGrade.getUpdateTime());
        verify(gradeMapper).updateById(any(Grade.class));
    }

    @Test
    @DisplayName("更新成绩 - 失败")
    void testUpdateGrade_Failure() {
        Grade updateGrade = new Grade();
        updateGrade.setGradeId("non-existent-id");
        updateGrade.setStudentId("student-1");
        updateGrade.setClassId("class-1");
        updateGrade.setSubject("数学");
        updateGrade.setExamName("期中考试");
        updateGrade.setScore(new BigDecimal("95.0"));
        updateGrade.setSemester("2024-2025-1");

        when(gradeMapper.updateById(any(Grade.class))).thenReturn(0);

        boolean result = gradeService.updateGrade(updateGrade);

        assertFalse(result);
        verify(gradeMapper).updateById(any(Grade.class));
    }

    @Test
    @DisplayName("删除成绩 - 成功")
    void testDeleteGrade_Success() {
        when(gradeMapper.deleteById("test-grade-id")).thenReturn(1);

        boolean result = gradeService.deleteGrade("test-grade-id");

        assertTrue(result);
        verify(gradeMapper).deleteById("test-grade-id");
    }

    @Test
    @DisplayName("删除成绩 - 失败")
    void testDeleteGrade_Failure() {
        when(gradeMapper.deleteById("non-existent-id")).thenReturn(0);

        boolean result = gradeService.deleteGrade("non-existent-id");

        assertFalse(result);
        verify(gradeMapper).deleteById("non-existent-id");
    }

    @Test
    @DisplayName("分页查询带学生信息 - 有科目筛选")
    void testPageGradesWithStudent_WithSubject() {
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

        when(gradeMapper.findByClassIdAndSubject(any(Page.class), eq("class-1"), eq("数学")))
                .thenReturn(page);

        IPage<Map<String, Object>> result = gradeService.pageGradesWithStudent(
                new Page<>(1, 10),
                "class-1",
                "数学",
                null
        );

        assertNotNull(result);
        assertEquals(1, result.getRecords().size());
        assertEquals("张三", result.getRecords().get(0).get("student_name"));
        verify(gradeMapper).findByClassIdAndSubject(any(Page.class), eq("class-1"), eq("数学"));
    }

    @Test
    @DisplayName("分页查询带学生信息 - 无科目筛选")
    void testPageGradesWithStudent_WithoutSubject() {
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

        when(gradeMapper.findByClassIdWithStudent(any(Page.class), eq("class-1")))
                .thenReturn(page);

        IPage<Map<String, Object>> result = gradeService.pageGradesWithStudent(
                new Page<>(1, 10),
                "class-1",
                null,
                null
        );

        assertNotNull(result);
        assertEquals(1, result.getRecords().size());
        verify(gradeMapper).findByClassIdWithStudent(any(Page.class), eq("class-1"));
    }

    @Test
    @DisplayName("分页查询带学生信息 - 空字符串科目")
    void testPageGradesWithStudent_EmptySubject() {
        Page<Map<String, Object>> page = new Page<>(1, 10);
        page.setRecords(new ArrayList<>());
        page.setTotal(0);

        when(gradeMapper.findByClassIdWithStudent(any(Page.class), eq("class-1")))
                .thenReturn(page);

        IPage<Map<String, Object>> result = gradeService.pageGradesWithStudent(
                new Page<>(1, 10),
                "class-1",
                "",
                null
        );

        assertNotNull(result);
        assertTrue(result.getRecords().isEmpty());
        verify(gradeMapper).findByClassIdWithStudent(any(Page.class), eq("class-1"));
    }

    @Test
    @DisplayName("获取班级科目统计 - 成功")
    void testGetClassSubjectStats_Success() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("avg_score", 85.5);
        stats.put("max_score", 100.0);
        stats.put("min_score", 60.0);
        stats.put("count", 30L);

        when(gradeMapper.getClassSubjectStats("class-1", "数学", "期中考试"))
                .thenReturn(stats);

        Map<String, Object> result = gradeService.getClassSubjectStats("class-1", "数学", "期中考试");

        assertNotNull(result);
        assertEquals(85.5, result.get("avg_score"));
        assertEquals(100.0, result.get("max_score"));
        assertEquals(60.0, result.get("min_score"));
        verify(gradeMapper).getClassSubjectStats("class-1", "数学", "期中考试");
    }

    @Test
    @DisplayName("获取学生科目平均分 - 成功")
    void testGetStudentSubjectAvg_Success() {
        Map<String, Object> avgData = new HashMap<>();
        avgData.put("avg_score", 87.5);

        when(gradeMapper.getStudentSubjectAvg("student-1", "数学"))
                .thenReturn(avgData);

        Map<String, Object> result = gradeService.getStudentSubjectAvg("student-1", "数学");

        assertNotNull(result);
        assertEquals(87.5, result.get("avg_score"));
        verify(gradeMapper).getStudentSubjectAvg("student-1", "数学");
    }

    @Test
    @DisplayName("获取成绩统计 - 有班级筛选")
    void testGetGradeStatistics_WithClassId() {
        List<Grade> grades = new ArrayList<>();
        
        Grade g1 = new Grade();
        g1.setSubject("数学");
        g1.setScore(new BigDecimal("85.0"));
        grades.add(g1);

        Grade g2 = new Grade();
        g2.setSubject("数学");
        g2.setScore(new BigDecimal("90.0"));
        grades.add(g2);

        Grade g3 = new Grade();
        g3.setSubject("语文");
        g3.setScore(new BigDecimal("80.0"));
        grades.add(g3);

        when(gradeMapper.selectList(any(QueryWrapper.class))).thenReturn(grades);

        List<Map<String, Object>> result = gradeService.getGradeStatistics("class-1");

        assertNotNull(result);
        assertEquals(2, result.size());

        Map<String, Object> mathStat = result.stream()
                .filter(s -> "数学".equals(s.get("subject")))
                .findFirst()
                .orElse(null);
        assertNotNull(mathStat);
        assertEquals(87.5, mathStat.get("avg"));
        assertEquals(90.0, mathStat.get("max"));
        assertEquals(85.0, mathStat.get("min"));
        assertEquals(2, mathStat.get("count"));

        verify(gradeMapper).selectList(any(QueryWrapper.class));
    }

    @Test
    @DisplayName("获取成绩统计 - 无班级筛选")
    void testGetGradeStatistics_NoClassId() {
        List<Grade> grades = new ArrayList<>();

        Grade g1 = new Grade();
        g1.setSubject("数学");
        g1.setScore(new BigDecimal("100.0"));
        grades.add(g1);

        when(gradeMapper.selectList(any(QueryWrapper.class))).thenReturn(grades);

        List<Map<String, Object>> result = gradeService.getGradeStatistics(null);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("数学", result.get(0).get("subject"));
        assertEquals(100.0, result.get(0).get("avg"));
        assertEquals(100.0, result.get(0).get("max"));
        assertEquals(100.0, result.get(0).get("min"));
        assertEquals(1, result.get(0).get("count"));

        verify(gradeMapper).selectList(any(QueryWrapper.class));
    }

    @Test
    @DisplayName("获取成绩统计 - 空数据")
    void testGetGradeStatistics_EmptyData() {
        when(gradeMapper.selectList(any(QueryWrapper.class))).thenReturn(new ArrayList<>());

        List<Map<String, Object>> result = gradeService.getGradeStatistics("class-1");

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(gradeMapper).selectList(any(QueryWrapper.class));
    }

    @Test
    @DisplayName("获取成绩统计 - 多科目数据")
    void testGetGradeStatistics_MultipleSubjects() {
        List<Grade> grades = new ArrayList<>();

        Grade g1 = new Grade();
        g1.setSubject("数学");
        g1.setScore(new BigDecimal("80.0"));
        grades.add(g1);

        Grade g2 = new Grade();
        g2.setSubject("数学");
        g2.setScore(new BigDecimal("90.0"));
        grades.add(g2);

        Grade g3 = new Grade();
        g3.setSubject("语文");
        g3.setScore(new BigDecimal("70.0"));
        grades.add(g3);

        Grade g4 = new Grade();
        g4.setSubject("语文");
        g4.setScore(new BigDecimal("80.0"));
        grades.add(g4);

        Grade g5 = new Grade();
        g5.setSubject("英语");
        g5.setScore(new BigDecimal("85.0"));
        grades.add(g5);

        when(gradeMapper.selectList(any(QueryWrapper.class))).thenReturn(grades);

        List<Map<String, Object>> result = gradeService.getGradeStatistics(null);

        assertNotNull(result);
        assertEquals(3, result.size());

        for (Map<String, Object> stat : result) {
            String subject = (String) stat.get("subject");
            if ("数学".equals(subject)) {
                assertEquals(85.0, stat.get("avg"));
                assertEquals(2, stat.get("count"));
            } else if ("语文".equals(subject)) {
                assertEquals(75.0, stat.get("avg"));
                assertEquals(2, stat.get("count"));
            } else if ("英语".equals(subject)) {
                assertEquals(85.0, stat.get("avg"));
                assertEquals(1, stat.get("count"));
            }
        }

        verify(gradeMapper).selectList(any(QueryWrapper.class));
    }

    @Test
    @DisplayName("边界值测试 - 分数为0")
    void testAddGrade_ScoreZero() {
        Grade newGrade = new Grade();
        newGrade.setStudentId("student-1");
        newGrade.setClassId("class-1");
        newGrade.setSubject("数学");
        newGrade.setExamName("期中考试");
        newGrade.setScore(new BigDecimal("0"));
        newGrade.setSemester("2024-2025-1");

        when(gradeMapper.insert(any(Grade.class))).thenReturn(1);

        boolean result = gradeService.addGrade(newGrade);

        assertTrue(result);
        verify(gradeMapper).insert(any(Grade.class));
    }

    @Test
    @DisplayName("边界值测试 - 分数为100")
    void testAddGrade_ScoreHundred() {
        Grade newGrade = new Grade();
        newGrade.setStudentId("student-1");
        newGrade.setClassId("class-1");
        newGrade.setSubject("数学");
        newGrade.setExamName("期中考试");
        newGrade.setScore(new BigDecimal("100"));
        newGrade.setSemester("2024-2025-1");

        when(gradeMapper.insert(any(Grade.class))).thenReturn(1);

        boolean result = gradeService.addGrade(newGrade);

        assertTrue(result);
        verify(gradeMapper).insert(any(Grade.class));
    }

    @Test
    @DisplayName("边界值测试 - 小数分数")
    void testAddGrade_DecimalScore() {
        Grade newGrade = new Grade();
        newGrade.setStudentId("student-1");
        newGrade.setClassId("class-1");
        newGrade.setSubject("数学");
        newGrade.setExamName("期中考试");
        newGrade.setScore(new BigDecimal("85.55"));
        newGrade.setSemester("2024-2025-1");

        when(gradeMapper.insert(any(Grade.class))).thenReturn(1);

        boolean result = gradeService.addGrade(newGrade);

        assertTrue(result);
        verify(gradeMapper).insert(any(Grade.class));
    }

    @Test
    @DisplayName("时间戳自动设置测试")
    void testAddGrade_TimestampSet() {
        Grade newGrade = new Grade();
        newGrade.setStudentId("student-1");
        newGrade.setClassId("class-1");
        newGrade.setSubject("数学");
        newGrade.setExamName("期中考试");
        newGrade.setScore(new BigDecimal("85.0"));
        newGrade.setSemester("2024-2025-1");

        LocalDateTime beforeInsert = LocalDateTime.now();

        when(gradeMapper.insert(any(Grade.class))).thenReturn(1);

        gradeService.addGrade(newGrade);

        LocalDateTime afterInsert = LocalDateTime.now();

        assertNotNull(newGrade.getCreateTime());
        assertNotNull(newGrade.getUpdateTime());
        assertTrue(newGrade.getCreateTime().isAfter(beforeInsert.minusSeconds(1)));
        assertTrue(newGrade.getCreateTime().isBefore(afterInsert.plusSeconds(1)));
    }

    @Test
    @DisplayName("更新时间戳自动设置测试")
    void testUpdateGrade_TimestampSet() {
        Grade updateGrade = new Grade();
        updateGrade.setGradeId("test-grade-id");
        updateGrade.setStudentId("student-1");
        updateGrade.setClassId("class-1");
        updateGrade.setSubject("数学");
        updateGrade.setExamName("期中考试");
        updateGrade.setScore(new BigDecimal("95.0"));
        updateGrade.setSemester("2024-2025-1");

        LocalDateTime beforeUpdate = LocalDateTime.now();

        when(gradeMapper.updateById(any(Grade.class))).thenReturn(1);

        gradeService.updateGrade(updateGrade);

        LocalDateTime afterUpdate = LocalDateTime.now();

        assertNotNull(updateGrade.getUpdateTime());
        assertTrue(updateGrade.getUpdateTime().isAfter(beforeUpdate.minusSeconds(1)));
        assertTrue(updateGrade.getUpdateTime().isBefore(afterUpdate.plusSeconds(1)));
    }

    @Test
    @DisplayName("统计计算精度测试")
    void testGetGradeStatistics_Precision() {
        List<Grade> grades = new ArrayList<>();

        Grade g1 = new Grade();
        g1.setSubject("数学");
        g1.setScore(new BigDecimal("85.333"));
        grades.add(g1);

        Grade g2 = new Grade();
        g2.setSubject("数学");
        g2.setScore(new BigDecimal("90.666"));
        grades.add(g2);

        when(gradeMapper.selectList(any(QueryWrapper.class))).thenReturn(grades);

        List<Map<String, Object>> result = gradeService.getGradeStatistics(null);

        assertNotNull(result);
        assertEquals(1, result.size());

        Map<String, Object> mathStat = result.get(0);
        double avg = (Double) mathStat.get("avg");
        assertTrue(avg >= 87.99 && avg <= 88.01);
    }
}
