package com.leafsms.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.leafsms.entity.Grade;
import com.leafsms.mapper.GradeMapper;
import com.leafsms.service.impl.GradeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GradeServiceTest {

    @Mock
    private GradeMapper gradeMapper;

    private GradeServiceImpl gradeService;

    private Grade sampleGrade;
    private String gradeId;
    private String studentId;

    @BeforeEach
    void setUp() throws Exception {
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

        // 手动创建service并通过反射设置baseMapper
        gradeService = new GradeServiceImpl();
        Field baseMapperField = GradeServiceImpl.class.getSuperclass().getDeclaredField("baseMapper");
        baseMapperField.setAccessible(true);
        baseMapperField.set(gradeService, gradeMapper);
    }

    @Test
    @DisplayName("根据学生ID查询成绩列表 - 成功")
    void findByStudentId_Success() {
        List<Grade> expectedGrades = Arrays.asList(sampleGrade);
        when(gradeMapper.findByStudentId(studentId)).thenReturn(expectedGrades);

        List<Grade> result = gradeService.findByStudentId(studentId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(studentId, result.get(0).getStudentId());
        verify(gradeMapper).findByStudentId(studentId);
    }

    @Test
    @DisplayName("根据学生ID查询成绩列表 - 空结果")
    void findByStudentId_EmptyResult() {
        when(gradeMapper.findByStudentId("non-existent-id")).thenReturn(Collections.emptyList());

        List<Grade> result = gradeService.findByStudentId("non-existent-id");

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("分页查询成绩 - 无条件")
    void pageGrades_NoConditions() {
        Page<Grade> pageParam = new Page<>(1, 10);
        List<Grade> records = Collections.singletonList(sampleGrade);
        pageParam.setRecords(records);
        pageParam.setTotal(1);

        when(gradeMapper.selectPage(any(Page.class), any(QueryWrapper.class))).thenReturn(pageParam);

        IPage<Grade> result = gradeService.pageGrades(new Page<>(1, 10), null, null, null, null);

        assertNotNull(result);
        assertEquals(1, result.getTotal());
        assertEquals(1, result.getRecords().size());
    }

    @Test
    @DisplayName("分页查询成绩 - 带所有条件")
    void pageGrades_WithAllConditions() {
        Page<Grade> pageParam = new Page<>(1, 10);
        List<Grade> records = Collections.singletonList(sampleGrade);
        pageParam.setRecords(records);
        pageParam.setTotal(1);

        when(gradeMapper.selectPage(any(Page.class), any(QueryWrapper.class))).thenReturn(pageParam);

        IPage<Grade> result = gradeService.pageGrades(
                new Page<>(1, 10),
                "class001",
                "数学",
                "期中考试",
                "2024-2025上学期"
        );

        assertNotNull(result);
        assertEquals(1, result.getTotal());
        verify(gradeMapper).selectPage(any(Page.class), any(QueryWrapper.class));
    }

    @Test
    @DisplayName("分页查询成绩(含学生信息) - 按科目查询")
    void pageGradesWithStudent_BySubject() {
        Page<Map<String, Object>> pageParam = new Page<>(1, 10);
        Map<String, Object> record = new HashMap<>();
        record.put("grade_id", gradeId);
        record.put("student_name", "张三");
        pageParam.setRecords(Collections.singletonList(record));
        pageParam.setTotal(1);

        when(gradeMapper.findByClassIdAndSubject(any(Page.class), eq("class001"), eq("数学")))
                .thenReturn(pageParam);

        IPage<Map<String, Object>> result = gradeService.pageGradesWithStudent(
                new Page<>(1, 10),
                "class001",
                "数学",
                null
        );

        assertNotNull(result);
        assertEquals(1, result.getTotal());
        verify(gradeMapper).findByClassIdAndSubject(any(Page.class), eq("class001"), eq("数学"));
    }

    @Test
    @DisplayName("分页查询成绩(含学生信息) - 不按科目查询")
    void pageGradesWithStudent_WithoutSubject() {
        Page<Map<String, Object>> pageParam = new Page<>(1, 10);
        Map<String, Object> record = new HashMap<>();
        record.put("grade_id", gradeId);
        record.put("student_name", "张三");
        pageParam.setRecords(Collections.singletonList(record));
        pageParam.setTotal(1);

        when(gradeMapper.findByClassIdWithStudent(any(Page.class), eq("class001")))
                .thenReturn(pageParam);

        IPage<Map<String, Object>> result = gradeService.pageGradesWithStudent(
                new Page<>(1, 10),
                "class001",
                null,
                null
        );

        assertNotNull(result);
        assertEquals(1, result.getTotal());
        verify(gradeMapper).findByClassIdWithStudent(any(Page.class), eq("class001"));
    }

    @Test
    @DisplayName("新增成绩记录 - 成功")
    void addGrade_Success() {
        when(gradeMapper.insert(any(Grade.class))).thenReturn(1);

        Grade newGrade = new Grade();
        newGrade.setStudentId(studentId);
        newGrade.setClassId("class001");
        newGrade.setSubject("数学");
        newGrade.setExamName("期末考试");
        newGrade.setScore(new BigDecimal("92.0"));
        newGrade.setSemester("2024-2025上学期");

        boolean result = gradeService.addGrade(newGrade);

        assertTrue(result);
        assertNotNull(newGrade.getCreateTime());
        assertNotNull(newGrade.getUpdateTime());
        verify(gradeMapper).insert(any(Grade.class));
    }

    @Test
    @DisplayName("新增成绩记录 - 失败")
    void addGrade_Failure() {
        when(gradeMapper.insert(any(Grade.class))).thenReturn(0);

        Grade newGrade = new Grade();
        newGrade.setStudentId(studentId);
        newGrade.setClassId("class001");
        newGrade.setSubject("数学");
        newGrade.setExamName("期末考试");
        newGrade.setScore(new BigDecimal("92.0"));
        newGrade.setSemester("2024-2025上学期");

        boolean result = gradeService.addGrade(newGrade);

        assertFalse(result);
    }

    @Test
    @DisplayName("更新成绩记录 - 成功")
    void updateGrade_Success() {
        when(gradeMapper.updateById(any(Grade.class))).thenReturn(1);

        Grade updateGrade = new Grade();
        updateGrade.setGradeId(gradeId);
        updateGrade.setScore(new BigDecimal("90.0"));

        boolean result = gradeService.updateGrade(updateGrade);

        assertTrue(result);
        assertNotNull(updateGrade.getUpdateTime());
        verify(gradeMapper).updateById(any(Grade.class));
    }

    @Test
    @DisplayName("更新成绩记录 - 失败")
    void updateGrade_Failure() {
        when(gradeMapper.updateById(any(Grade.class))).thenReturn(0);

        Grade updateGrade = new Grade();
        updateGrade.setGradeId(gradeId);
        updateGrade.setScore(new BigDecimal("90.0"));

        boolean result = gradeService.updateGrade(updateGrade);

        assertFalse(result);
    }

    @Test
    @DisplayName("删除成绩记录 - 成功")
    void deleteGrade_Success() {
        when(gradeMapper.deleteById(gradeId)).thenReturn(1);

        boolean result = gradeService.deleteGrade(gradeId);

        assertTrue(result);
        verify(gradeMapper).deleteById(gradeId);
    }

    @Test
    @DisplayName("删除成绩记录 - 失败")
    void deleteGrade_Failure() {
        when(gradeMapper.deleteById(gradeId)).thenReturn(0);

        boolean result = gradeService.deleteGrade(gradeId);

        assertFalse(result);
    }

    @Test
    @DisplayName("根据ID查询成绩 - 成功")
    void getById_Success() {
        when(gradeMapper.selectById(gradeId)).thenReturn(sampleGrade);

        Grade result = gradeService.getById(gradeId);

        assertNotNull(result);
        assertEquals(gradeId, result.getGradeId());
    }

    @Test
    @DisplayName("根据ID查询成绩 - 不存在")
    void getById_NotFound() {
        when(gradeMapper.selectById("non-existent-id")).thenReturn(null);

        Grade result = gradeService.getById("non-existent-id");

        assertNull(result);
    }

    @Test
    @DisplayName("获取班级科目统计 - 成功")
    void getClassSubjectStats_Success() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("avg_score", 85.5);
        stats.put("max_score", 100.0);
        stats.put("min_score", 60.0);
        stats.put("count", 30);

        when(gradeMapper.getClassSubjectStats("class001", "数学", "期中考试"))
                .thenReturn(stats);

        Map<String, Object> result = gradeService.getClassSubjectStats("class001", "数学", "期中考试");

        assertNotNull(result);
        assertEquals(85.5, result.get("avg_score"));
    }

    @Test
    @DisplayName("获取学生科目平均分 - 成功")
    void getStudentSubjectAvg_Success() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("avg_score", 88.0);

        when(gradeMapper.getStudentSubjectAvg(studentId, "数学"))
                .thenReturn(stats);

        Map<String, Object> result = gradeService.getStudentSubjectAvg(studentId, "数学");

        assertNotNull(result);
        assertEquals(88.0, result.get("avg_score"));
    }

    @Test
    @DisplayName("获取成绩统计 - 按班级")
    void getGradeStatistics_ByClass() {
        Grade grade1 = new Grade();
        grade1.setSubject("数学");
        grade1.setScore(new BigDecimal("80"));

        Grade grade2 = new Grade();
        grade2.setSubject("数学");
        grade2.setScore(new BigDecimal("90"));

        Grade grade3 = new Grade();
        grade3.setSubject("语文");
        grade3.setScore(new BigDecimal("85"));

        List<Grade> grades = Arrays.asList(grade1, grade2, grade3);

        when(gradeMapper.selectList(any(QueryWrapper.class))).thenReturn(grades);

        List<Map<String, Object>> result = gradeService.getGradeStatistics("class001");

        assertNotNull(result);
        assertEquals(2, result.size());

        Map<String, Object> mathStat = result.stream()
                .filter(s -> "数学".equals(s.get("subject")))
                .findFirst()
                .orElse(null);
        assertNotNull(mathStat);
        assertEquals(85.0, mathStat.get("avg"));
        assertEquals(90.0, mathStat.get("max"));
        assertEquals(80.0, mathStat.get("min"));
        assertEquals(2, mathStat.get("count"));
    }

    @Test
    @DisplayName("获取成绩统计 - 无班级筛选")
    void getGradeStatistics_NoClass() {
        Grade grade1 = new Grade();
        grade1.setSubject("数学");
        grade1.setScore(new BigDecimal("80"));

        List<Grade> grades = Collections.singletonList(grade1);

        when(gradeMapper.selectList(any(QueryWrapper.class))).thenReturn(grades);

        List<Map<String, Object>> result = gradeService.getGradeStatistics(null);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("获取成绩统计 - 空数据")
    void getGradeStatistics_EmptyData() {
        when(gradeMapper.selectList(any(QueryWrapper.class))).thenReturn(Collections.emptyList());

        List<Map<String, Object>> result = gradeService.getGradeStatistics("class001");

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("分数边界值 - 最低分0分")
    void scoreBoundary_Min() {
        when(gradeMapper.insert(any(Grade.class))).thenReturn(1);

        Grade grade = new Grade();
        grade.setStudentId(studentId);
        grade.setClassId("class001");
        grade.setSubject("数学");
        grade.setExamName("测试");
        grade.setScore(BigDecimal.ZERO);
        grade.setSemester("2024-2025上学期");

        boolean result = gradeService.addGrade(grade);

        assertTrue(result);
        assertEquals(BigDecimal.ZERO, grade.getScore());
    }

    @Test
    @DisplayName("分数边界值 - 最高分100分")
    void scoreBoundary_Max() {
        when(gradeMapper.insert(any(Grade.class))).thenReturn(1);

        Grade grade = new Grade();
        grade.setStudentId(studentId);
        grade.setClassId("class001");
        grade.setSubject("数学");
        grade.setExamName("测试");
        grade.setScore(new BigDecimal("100"));
        grade.setSemester("2024-2025上学期");

        boolean result = gradeService.addGrade(grade);

        assertTrue(result);
        assertEquals(new BigDecimal("100"), grade.getScore());
    }

    @Test
    @DisplayName("分数边界值 - 小数分数")
    void scoreBoundary_Decimal() {
        when(gradeMapper.insert(any(Grade.class))).thenReturn(1);

        Grade grade = new Grade();
        grade.setStudentId(studentId);
        grade.setClassId("class001");
        grade.setSubject("数学");
        grade.setExamName("测试");
        grade.setScore(new BigDecimal("85.5"));
        grade.setSemester("2024-2025上学期");

        boolean result = gradeService.addGrade(grade);

        assertTrue(result);
        assertEquals(new BigDecimal("85.5"), grade.getScore());
    }
}
