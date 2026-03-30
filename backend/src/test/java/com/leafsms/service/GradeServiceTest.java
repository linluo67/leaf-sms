package com.leafsms.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.leafsms.entity.Grade;
import com.leafsms.mapper.GradeMapper;
import com.leafsms.service.impl.GradeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GradeServiceTest {

    @Mock
    private GradeMapper gradeMapper;

    @InjectMocks
    private GradeServiceImpl gradeService;

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

        gradeList = Arrays.asList(testGrade);
        
        // 使用ReflectionTestUtils注入baseMapper
        ReflectionTestUtils.setField(gradeService, "baseMapper", gradeMapper);
    }

    // 1. 查询学生成绩列表
    @Test
    void findByStudentId_ShouldReturnGrades() {
        when(gradeMapper.findByStudentId("stu-001")).thenReturn(gradeList);

        List<Grade> result = gradeService.findByStudentId("stu-001");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("test-grade-id", result.get(0).getGradeId());
        verify(gradeMapper, times(1)).findByStudentId("stu-001");
    }

    @Test
    void findByStudentId_WithNoGrades_ShouldReturnEmptyList() {
        when(gradeMapper.findByStudentId("stu-999")).thenReturn(Collections.emptyList());

        List<Grade> result = gradeService.findByStudentId("stu-999");

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    // 2. 分页查询成绩
    @Test
    void pageGrades_WithoutFilters_ShouldReturnAllGrades() {
        Page<Grade> page = new Page<>(1, 10);
        when(gradeMapper.selectPage(any(), any())).thenReturn(page);

        IPage<Grade> result = gradeService.pageGrades(page, null, null, null, null);

        assertNotNull(result);
        verify(gradeMapper, times(1)).selectPage(any(), any());
    }

    @Test
    void pageGrades_WithClassIdFilter_ShouldFilterByClass() {
        Page<Grade> page = new Page<>(1, 10);
        when(gradeMapper.selectPage(any(), any())).thenReturn(page);

        IPage<Grade> result = gradeService.pageGrades(page, "class-001", null, null, null);

        assertNotNull(result);
        verify(gradeMapper, times(1)).selectPage(any(), any());
    }

    @Test
    void pageGrades_WithAllFilters_ShouldApplyAllConditions() {
        Page<Grade> page = new Page<>(1, 10);
        when(gradeMapper.selectPage(any(), any())).thenReturn(page);

        IPage<Grade> result = gradeService.pageGrades(page, "class-001", "数学", "期中考试", "2023-2024-1");

        assertNotNull(result);
        verify(gradeMapper, times(1)).selectPage(any(), any());
    }

    // 3. 带学生信息的分页查询
    @Test
    void pageGradesWithStudent_WithSubject_ShouldCallSubjectQuery() {
        Page<Map<String, Object>> page = new Page<>();
        when(gradeMapper.findByClassIdAndSubject(any(), eq("class-001"), eq("数学"))).thenReturn(page);

        IPage<Map<String, Object>> result = gradeService.pageGradesWithStudent(page, "class-001", "数学", null);

        assertNotNull(result);
        verify(gradeMapper, times(1)).findByClassIdAndSubject(any(), eq("class-001"), eq("数学"));
        verify(gradeMapper, never()).findByClassIdWithStudent(any(), anyString());
    }

    @Test
    void pageGradesWithStudent_WithoutSubject_ShouldCallGeneralQuery() {
        Page<Map<String, Object>> page = new Page<>();
        when(gradeMapper.findByClassIdWithStudent(any(), eq("class-001"))).thenReturn(page);

        IPage<Map<String, Object>> result = gradeService.pageGradesWithStudent(page, "class-001", null, null);

        assertNotNull(result);
        verify(gradeMapper, times(1)).findByClassIdWithStudent(any(), eq("class-001"));
        verify(gradeMapper, never()).findByClassIdAndSubject(any(), anyString(), anyString());
    }

    // 4. 新增成绩
    @Test
    void addGrade_ShouldSetTimestampsAndReturnTrue() {
        when(gradeMapper.insert(any(Grade.class))).thenReturn(1);

        boolean result = gradeService.addGrade(testGrade);

        assertTrue(result);
        assertNotNull(testGrade.getCreateTime());
        assertNotNull(testGrade.getUpdateTime());
        verify(gradeMapper, times(1)).insert(testGrade);
    }

    @Test
    void addGrade_MapperFailure_ShouldReturnFalse() {
        when(gradeMapper.insert(any(Grade.class))).thenReturn(0);

        boolean result = gradeService.addGrade(testGrade);

        assertFalse(result);
    }

    // 5. 更新成绩
    @Test
    void updateGrade_ShouldSetUpdateTimeAndReturnTrue() {
        when(gradeMapper.updateById(any(Grade.class))).thenReturn(1);

        boolean result = gradeService.updateGrade(testGrade);

        assertTrue(result);
        assertNotNull(testGrade.getUpdateTime());
        verify(gradeMapper, times(1)).updateById(testGrade);
    }

    @Test
    void updateGrade_MapperFailure_ShouldReturnFalse() {
        when(gradeMapper.updateById(any(Grade.class))).thenReturn(0);

        boolean result = gradeService.updateGrade(testGrade);

        assertFalse(result);
    }

    // 6. 删除成绩
    @Test
    void deleteGrade_ShouldReturnTrueWhenDeleted() {
        when(gradeMapper.deleteById("test-grade-id")).thenReturn(1);

        boolean result = gradeService.deleteGrade("test-grade-id");

        assertTrue(result);
        verify(gradeMapper, times(1)).deleteById("test-grade-id");
    }

    @Test
    void deleteGrade_ShouldReturnFalseWhenNotDeleted() {
        when(gradeMapper.deleteById("non-existing-id")).thenReturn(0);

        boolean result = gradeService.deleteGrade("non-existing-id");

        assertFalse(result);
    }

    // 7. 获取班级科目统计
    @Test
    void getClassSubjectStats_ShouldReturnStats() {
        Map<String, Object> expectedStats = new HashMap<>();
        expectedStats.put("avg_score", 85.5);
        expectedStats.put("max_score", 100);
        expectedStats.put("min_score", 60);
        expectedStats.put("count", 50);

        when(gradeMapper.getClassSubjectStats("class-001", "数学", "期中考试")).thenReturn(expectedStats);

        Map<String, Object> result = gradeService.getClassSubjectStats("class-001", "数学", "期中考试");

        assertNotNull(result);
        assertEquals(85.5, result.get("avg_score"));
        assertEquals(100, result.get("max_score"));
        verify(gradeMapper, times(1)).getClassSubjectStats("class-001", "数学", "期中考试");
    }

    // 8. 获取学生科目平均分
    @Test
    void getStudentSubjectAvg_ShouldReturnAvg() {
        Map<String, Object> expectedAvg = new HashMap<>();
        expectedAvg.put("avg_score", 88.0);

        when(gradeMapper.getStudentSubjectAvg("stu-001", "数学")).thenReturn(expectedAvg);

        Map<String, Object> result = gradeService.getStudentSubjectAvg("stu-001", "数学");

        assertNotNull(result);
        assertEquals(88.0, result.get("avg_score"));
        verify(gradeMapper, times(1)).getStudentSubjectAvg("stu-001", "数学");
    }

    // 9. 获取成绩统计
    @Test
    void getGradeStatistics_WithClassId_ShouldReturnSubjectStats() {
        Grade grade1 = new Grade();
        grade1.setSubject("数学");
        grade1.setScore(new BigDecimal("90"));

        Grade grade2 = new Grade();
        grade2.setSubject("数学");
        grade2.setScore(new BigDecimal("80"));

        Grade grade3 = new Grade();
        grade3.setSubject("语文");
        grade3.setScore(new BigDecimal("85"));

        List<Grade> grades = Arrays.asList(grade1, grade2, grade3);
        when(gradeMapper.selectList(any())).thenReturn(grades);

        List<Map<String, Object>> result = gradeService.getGradeStatistics("class-001");

        assertNotNull(result);
        assertEquals(2, result.size());

        Map<String, Object> mathStats = result.stream()
                .filter(s -> "数学".equals(s.get("subject")))
                .findFirst()
                .orElse(null);

        assertNotNull(mathStats);
        assertEquals(85.0, mathStats.get("avg"));
        assertEquals(90.0, mathStats.get("max"));
        assertEquals(80.0, mathStats.get("min"));
        assertEquals(2, mathStats.get("count"));
    }

    @Test
    void getGradeStatistics_WithoutClassId_ShouldReturnAllSubjectStats() {
        Grade grade1 = new Grade();
        grade1.setSubject("英语");
        grade1.setScore(new BigDecimal("95"));

        List<Grade> grades = Collections.singletonList(grade1);
        when(gradeMapper.selectList(any())).thenReturn(grades);

        List<Map<String, Object>> result = gradeService.getGradeStatistics(null);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("英语", result.get(0).get("subject"));
    }

    @Test
    void getGradeStatistics_WithNoGrades_ShouldReturnEmptyList() {
        when(gradeMapper.selectList(any())).thenReturn(Collections.emptyList());

        List<Map<String, Object>> result = gradeService.getGradeStatistics("class-001");

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    // 10. 分数边界值测试
    @Test
    void addGrade_WithBoundaryScores_ShouldWorkCorrectly() {
        // 测试最低分
        Grade minGrade = new Grade();
        minGrade.setScore(new BigDecimal("0"));
        when(gradeMapper.insert(any(Grade.class))).thenReturn(1);

        boolean minResult = gradeService.addGrade(minGrade);
        assertTrue(minResult);

        // 测试最高分
        Grade maxGrade = new Grade();
        maxGrade.setScore(new BigDecimal("100"));

        boolean maxResult = gradeService.addGrade(maxGrade);
        assertTrue(maxResult);
    }

    // 11. getById 测试
    @Test
    void getById_ExistingId_ShouldReturnGrade() {
        when(gradeMapper.selectById("test-grade-id")).thenReturn(testGrade);

        Grade result = gradeService.getById("test-grade-id");

        assertNotNull(result);
        assertEquals("test-grade-id", result.getGradeId());
    }

    @Test
    void getById_NonExistingId_ShouldReturnNull() {
        when(gradeMapper.selectById("non-existing-id")).thenReturn(null);

        Grade result = gradeService.getById("non-existing-id");

        assertNull(result);
    }

    // 12. save 测试（继承自ServiceImpl的方法）
    @Test
    void save_ShouldDelegateToMapper() {
        when(gradeMapper.insert(any(Grade.class))).thenReturn(1);

        boolean result = gradeService.save(testGrade);

        assertTrue(result);
        verify(gradeMapper, times(1)).insert(testGrade);
    }

    // 13. 测试空参数处理
    @Test
    void pageGrades_WithEmptyStringFilters_ShouldIgnoreEmptyFilters() {
        Page<Grade> page = new Page<>(1, 10);
        when(gradeMapper.selectPage(any(), any())).thenReturn(page);

        IPage<Grade> result = gradeService.pageGrades(page, "", "", "", "");

        assertNotNull(result);
        verify(gradeMapper, times(1)).selectPage(any(), any());
    }

    // 14. 测试统计计算精度
    @Test
    void getGradeStatistics_ShouldCalculateAvgWithPrecision() {
        Grade grade1 = new Grade();
        grade1.setSubject("数学");
        grade1.setScore(new BigDecimal("90.5"));

        Grade grade2 = new Grade();
        grade2.setSubject("数学");
        grade2.setScore(new BigDecimal("91.5"));

        List<Grade> grades = Arrays.asList(grade1, grade2);
        when(gradeMapper.selectList(any())).thenReturn(grades);

        List<Map<String, Object>> result = gradeService.getGradeStatistics("class-001");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(91.0, result.get(0).get("avg"));
    }
}
