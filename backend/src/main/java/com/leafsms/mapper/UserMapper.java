package com.leafsms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.leafsms.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    @Select("SELECT u.*, d.department_name " +
            "FROM users u " +
            "LEFT JOIN departments d ON u.department_id = d.department_id " +
            "WHERE u.user_id = #{userId}")
    User findWithDepartmentName(@Param("userId") String userId);

    @Select("<script>" +
            "SELECT u.*, d.department_name " +
            "FROM users u " +
            "LEFT JOIN departments d ON u.department_id = d.department_id " +
            "<where>" +
            "<if test='keyword != null and keyword != \"\"'>" +
            "(u.username LIKE CONCAT('%', #{keyword}, '%') " +
            "OR u.email LIKE CONCAT('%', #{keyword}, '%') " +
            "OR u.student_no LIKE CONCAT('%', #{keyword}, '%') " +
            "OR u.teacher_no LIKE CONCAT('%', #{keyword}, '%'))" +
            "</if>" +
            "<if test='status != null'>" +
            "AND u.status = #{status}" +
            "</if>" +
            "<if test='userType != null'>" +
            "AND u.user_type = #{userType}" +
            "</if>" +
            "</where>" +
            "ORDER BY u.create_time DESC" +
            "</script>")
    Page<User> pageWithDepartmentName(Page<User> page, 
                                        @Param("keyword") String keyword, 
                                        @Param("status") Integer status, 
                                        @Param("userType") Integer userType);
}
