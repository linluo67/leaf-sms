package com.leafsms.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.leafsms.common.Result;
import com.leafsms.entity.User;
import com.leafsms.service.UserService;
import com.leafsms.utils.LogUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private LogUtil logUtil;

    @GetMapping
    public Result<Page<User>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String roleCode,
            @RequestParam(required = false) Integer status,
            HttpServletRequest request) {
        
        Page<User> userPage = userService.pageUsers(page, size, keyword, roleCode, status);
        return Result.success(userPage);
    }

    @GetMapping("/{userId}")
    public Result<User> getById(@PathVariable String userId, HttpServletRequest request) {
        User user = userService.getById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }
        return Result.success(user);
    }

    @PostMapping
    public Result<User> add(@Valid @RequestBody User user, HttpServletRequest request) {
        User existing = userService.findByUsername(user.getUsername());
        if (existing != null) {
            return Result.error("用户名已存在");
        }
        
        if (user.getStatus() == null) {
            user.setStatus(1);
        }
        
        boolean success = userService.save(user);
        if (success) {
            logUtil.log("新增用户", "POST", request.getRequestURI(), "用户名: " + user.getUsername());
            return Result.success(user);
        }
        return Result.error("新增用户失败");
    }

    @PutMapping("/{userId}")
    public Result<User> update(@PathVariable String userId, 
                                @Valid @RequestBody User user,
                                HttpServletRequest request) {
        User existing = userService.getById(userId);
        if (existing == null) {
            return Result.error("用户不存在");
        }
        
        user.setUserId(userId);
        user.setPassword(null);
        boolean success = userService.updateById(user);
        if (success) {
            logUtil.log("更新用户", "PUT", request.getRequestURI(), "ID: " + userId);
            return Result.success(user);
        }
        return Result.error("更新用户失败");
    }

    @DeleteMapping("/{userId}")
    public Result<Void> delete(@PathVariable String userId, HttpServletRequest request) {
        boolean success = userService.removeById(userId);
        if (success) {
            logUtil.log("删除用户", "DELETE", request.getRequestURI(), "ID: " + userId);
            return Result.success(null);
        }
        return Result.error("删除用户失败");
    }

    @PutMapping("/{userId}/reset-password")
    public Result<Void> resetPassword(@PathVariable String userId, 
                                       @RequestBody String newPassword,
                                       HttpServletRequest request) {
        User user = userService.getById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        user.setPassword(newPassword);
        boolean success = userService.updateById(user);
        if (success) {
            logUtil.log("重置用户密码", "PUT", request.getRequestURI(), "ID: " + userId);
            return Result.success(null);
        }
        return Result.error("重置密码失败");
    }

    @PutMapping("/{userId}/status")
    public Result<Void> updateStatus(@PathVariable String userId, 
                                   @RequestBody Map<String, Integer> params,
                                   HttpServletRequest request) {
        User user = userService.getById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        Integer status = params.get("status");
        if (status == null) {
            return Result.error("状态参数不能为空");
        }
        
        user.setStatus(status);
        boolean success = userService.updateById(user);
        if (success) {
            logUtil.log("更新用户状态", "PUT", request.getRequestURI(), "ID: " + userId + ", 状态: " + status);
            return Result.success(null);
        }
        return Result.error("更新状态失败");
    }

    @PostMapping("/send-reset-code")
    public Result<Void> sendResetCode(@RequestBody Map<String, String> params, 
                                    HttpServletRequest request) {
        String email = params.get("email");
        if (email == null || email.isEmpty()) {
            return Result.error("邮箱不能为空");
        }
        
        User user = userService.lambdaQuery()
            .eq(User::getEmail, email)
            .one();
        
        if (user == null) {
            return Result.error("该邮箱未注册");
        }
        
        logUtil.log("发送密码重置验证码", "POST", request.getRequestURI(), "邮箱: " + email);
        return Result.success("验证码已发送", null);
    }
}
