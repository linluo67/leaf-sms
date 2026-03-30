package com.leafsms.entity;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class GradeValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private Grade createValidGrade() {
        Grade grade = new Grade();
        grade.setStudentId("student001");
        grade.setClassId("class001");
        grade.setSubject("数学");
        grade.setExamName("期中考试");
        grade.setScore(new BigDecimal("85.5"));
        grade.setSemester("2024-2025上学期");
        return grade;
    }

    @Test
    @DisplayName("有效成绩记录 - 全部字段合法")
    void validGrade_AllFieldsValid() {
        Grade grade = createValidGrade();

        Set<ConstraintViolation<Grade>> violations = validator.validate(grade);

        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("学生ID - 不能为空")
    void studentId_NotNull() {
        Grade grade = createValidGrade();
        grade.setStudentId(null);

        Set<ConstraintViolation<Grade>> violations = validator.validate(grade);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("studentId")));
    }

    @Test
    @DisplayName("班级ID - 不能为空")
    void classId_NotNull() {
        Grade grade = createValidGrade();
        grade.setClassId(null);

        Set<ConstraintViolation<Grade>> violations = validator.validate(grade);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("classId")));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("科目 - 不能为空或空字符串")
    void subject_NotBlank(String subject) {
        Grade grade = createValidGrade();
        grade.setSubject(subject);

        Set<ConstraintViolation<Grade>> violations = validator.validate(grade);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("subject")));
    }

    @Test
    @DisplayName("科目 - 长度超过50字符")
    void subject_MaxLength() {
        Grade grade = createValidGrade();
        // 创建51个字符的字符串
        grade.setSubject("数学数学数学数学数学数学数学数学数学数学数学数学数学数学数学数学数学数学数学数学数学数学数学数学数学数学数");

        Set<ConstraintViolation<Grade>> violations = validator.validate(grade);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v ->
                v.getPropertyPath().toString().equals("subject") &&
                        v.getMessage().contains("长度不能超过50")
        ));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("考试名称 - 不能为空或空字符串")
    void examName_NotBlank(String examName) {
        Grade grade = createValidGrade();
        grade.setExamName(examName);

        Set<ConstraintViolation<Grade>> violations = validator.validate(grade);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("examName")));
    }

    @Test
    @DisplayName("考试名称 - 长度超过100字符")
    void examName_MaxLength() {
        Grade grade = createValidGrade();
        grade.setExamName("考试".repeat(60));

        Set<ConstraintViolation<Grade>> violations = validator.validate(grade);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v ->
                v.getPropertyPath().toString().equals("examName") &&
                        v.getMessage().contains("长度不能超过100")
        ));
    }

    @Test
    @DisplayName("分数 - 不能为空")
    void score_NotNull() {
        Grade grade = createValidGrade();
        grade.setScore(null);

        Set<ConstraintViolation<Grade>> violations = validator.validate(grade);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("score")));
    }

    @Test
    @DisplayName("分数 - 小于0")
    void score_MinValue() {
        Grade grade = createValidGrade();
        grade.setScore(new BigDecimal("-0.1"));

        Set<ConstraintViolation<Grade>> violations = validator.validate(grade);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v ->
                v.getPropertyPath().toString().equals("score") &&
                        v.getMessage().contains("不能小于0")
        ));
    }

    @Test
    @DisplayName("分数 - 大于100")
    void score_MaxValue() {
        Grade grade = createValidGrade();
        grade.setScore(new BigDecimal("100.1"));

        Set<ConstraintViolation<Grade>> violations = validator.validate(grade);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v ->
                v.getPropertyPath().toString().equals("score") &&
                        v.getMessage().contains("不能大于100")
        ));
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "50", "100", "85.5", "99.99"})
    @DisplayName("分数 - 有效边界值")
    void score_ValidBoundaries(String scoreStr) {
        Grade grade = createValidGrade();
        grade.setScore(new BigDecimal(scoreStr));

        Set<ConstraintViolation<Grade>> violations = validator.validate(grade);

        assertTrue(violations.isEmpty(), "分数 " + scoreStr + " 应该是有效的");
    }

    @ParameterizedTest
    @ValueSource(strings = {"-1", "-0.01", "100.01", "101", "150"})
    @DisplayName("分数 - 无效边界值")
    void score_InvalidBoundaries(String scoreStr) {
        Grade grade = createValidGrade();
        grade.setScore(new BigDecimal(scoreStr));

        Set<ConstraintViolation<Grade>> violations = validator.validate(grade);

        assertFalse(violations.isEmpty(), "分数 " + scoreStr + " 应该是无效的");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("学期 - 不能为空或空字符串")
    void semester_NotBlank(String semester) {
        Grade grade = createValidGrade();
        grade.setSemester(semester);

        Set<ConstraintViolation<Grade>> violations = validator.validate(grade);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("semester")));
    }

    @Test
    @DisplayName("学期 - 长度超过50字符")
    void semester_MaxLength() {
        Grade grade = createValidGrade();
        grade.setSemester("2024-2025上学期".repeat(5));

        Set<ConstraintViolation<Grade>> violations = validator.validate(grade);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v ->
                v.getPropertyPath().toString().equals("semester") &&
                        v.getMessage().contains("长度不能超过50")
        ));
    }

    @Test
    @DisplayName("备注 - 长度超过500字符")
    void remark_MaxLength() {
        Grade grade = createValidGrade();
        grade.setRemark("备注".repeat(300));

        Set<ConstraintViolation<Grade>> violations = validator.validate(grade);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v ->
                v.getPropertyPath().toString().equals("remark") &&
                        v.getMessage().contains("长度不能超过500")
        ));
    }

    @Test
    @DisplayName("备注 - 为空是允许的")
    void remark_NullAllowed() {
        Grade grade = createValidGrade();
        grade.setRemark(null);

        Set<ConstraintViolation<Grade>> violations = validator.validate(grade);

        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("备注 - 空字符串是允许的")
    void remark_EmptyAllowed() {
        Grade grade = createValidGrade();
        grade.setRemark("");

        Set<ConstraintViolation<Grade>> violations = validator.validate(grade);

        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("多字段同时验证失败")
    void multipleValidationFailures() {
        Grade grade = new Grade();
        grade.setStudentId(null);
        grade.setClassId(null);
        grade.setSubject("");
        grade.setExamName(null);
        grade.setScore(new BigDecimal("150"));
        grade.setSemester("");

        Set<ConstraintViolation<Grade>> violations = validator.validate(grade);

        assertFalse(violations.isEmpty());
        assertTrue(violations.size() >= 6);
    }

    @Test
    @DisplayName("排名 - 可为null")
    void rank_NullAllowed() {
        Grade grade = createValidGrade();
        grade.setRank(null);

        Set<ConstraintViolation<Grade>> violations = validator.validate(grade);

        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("排名 - 可设置有效值")
    void rank_ValidValue() {
        Grade grade = createValidGrade();
        grade.setRank(1);

        Set<ConstraintViolation<Grade>> violations = validator.validate(grade);

        assertTrue(violations.isEmpty());
    }

    @ParameterizedTest
    @CsvSource({
            "student001, class001, 数学, 期中考试, 85.5, 2024-2025上学期",
            "student002, class002, 语文, 期末考试, 92.0, 2024-2025下学期",
            "student003, class001, 英语, 月考, 78.5, 2024-2025上学期",
            "student004, class003, 物理, 期中考试, 100, 2024-2025上学期",
            "student005, class001, 化学, 期末考试, 0, 2024-2025下学期"
    })
    @DisplayName("多种有效成绩记录组合")
    void validGradeCombinations(String studentId, String classId, String subject,
                                 String examName, String score, String semester) {
        Grade grade = new Grade();
        grade.setStudentId(studentId);
        grade.setClassId(classId);
        grade.setSubject(subject);
        grade.setExamName(examName);
        grade.setScore(new BigDecimal(score));
        grade.setSemester(semester);

        Set<ConstraintViolation<Grade>> violations = validator.validate(grade);

        assertTrue(violations.isEmpty());
    }
}
