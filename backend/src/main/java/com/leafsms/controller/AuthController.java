package com.leafsms.controller;

import com.leafsms.common.Result;
import com.leafsms.entity.User;
import com.leafsms.service.UserService;
import com.leafsms.utils.JwtUtil;
import com.leafsms.utils.LogUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private LogUtil logUtil;

    @PostMapping("/register")
    public Result<Map<String, Object>> register(@RequestBody Map<String, String> registerRequest, HttpServletRequest request) {
        String email = registerRequest.get("email");
        String password = registerRequest.get("password");

        if (email == null || email.trim().isEmpty()) {
            return Result.error("邮箱不能为空");
        }
        if (password == null || password.trim().isEmpty()) {
            return Result.error("密码不能为空");
        }

        User existingUser = userService.findByUsername(email);
        if (existingUser != null) {
            return Result.error("该邮箱已被注册");
        }

        User newUser = new User();
        newUser.setUsername(email);
        newUser.setEmail(email);
        newUser.setPassword(password);
        newUser.setRoleCode("ADMIN");
        newUser.setStatus(1);
        newUser.setCreateTime(LocalDateTime.now());

        boolean saved = userService.save(newUser);
        if (saved) {
            String token = jwtUtil.generateToken(newUser.getUserId(), newUser.getUsername());
            
            logUtil.log("用户注册成功", "POST", request.getRequestURI(), "邮箱: " + email);

            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("user", newUser);
            return Result.success("注册成功", response);
        } else {
            return Result.error("注册失败");
        }
    }

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> loginRequest, HttpServletRequest request) {
        String username = loginRequest.get("username");
        String email = loginRequest.get("email");
        String password = loginRequest.get("password");

        if ((username == null || username.trim().isEmpty()) && (email == null || email.trim().isEmpty())) {
            return Result.error("邮箱不能为空");
        }
        if (password == null || password.trim().isEmpty()) {
            return Result.error("密码不能为空");
        }

        String loginIdentifier = (email != null && !email.trim().isEmpty()) ? email : username;
        User user = userService.findByUsername(loginIdentifier);
        
        if (user != null) {
            if (user.getStatus() != null && user.getStatus() == 0) {
                logUtil.log("用户登录失败", "POST", request.getRequestURI(), "邮箱: " + loginIdentifier + " (账号已被禁用)");
                return Result.error("账号已被禁用，请联系管理员");
            }

            if (password.equals(user.getPassword())) {
                String token = jwtUtil.generateToken(user.getUserId(), user.getUsername());
                
                user.setLastLoginTime(LocalDateTime.now());
                userService.updateById(user);

                logUtil.log("用户登录成功", "POST", request.getRequestURI(), "邮箱: " + loginIdentifier + " 角色: " + user.getRoleCode());

                Map<String, Object> response = new HashMap<>();
                response.put("token", token);
                response.put("user", user);
                return Result.success("登录成功", response);
            }
        }

        logUtil.log("用户登录失败", "POST", request.getRequestURI(), "邮箱: " + loginIdentifier + " (密码错误或用户不存在)");
        return Result.error("邮箱或密码错误");
    }

    @GetMapping("/me")
    public Result<User> getCurrentUser(@RequestHeader("Authorization") String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return Result.error("未授权访问");
        }

        String token = authorization.substring(7);
        try {
            String userId = jwtUtil.getUserIdFromToken(token);
            User user = userService.getById(userId);

            if (user != null) {
                return Result.success(user);
            } else {
                return Result.error("用户不存在");
            }
        } catch (Exception e) {
            return Result.error("Token无效或已过期");
        }
    }

    @PutMapping("/me")
    public Result<Boolean> updateCurrentUser(@RequestHeader("Authorization") String authorization,
                                            @RequestBody User user,
                                            HttpServletRequest request) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return Result.error("未授权访问");
        }

        String token = authorization.substring(7);
        try {
            String userId = jwtUtil.getUserIdFromToken(token);
            user.setUserId(userId);
            user.setPassword(null);
            user.setRoleCode(null);
            boolean updated = userService.updateById(user);

            if (updated) {
                logUtil.log("更新用户信息", "PUT", request.getRequestURI(), "用户ID: " + userId);
                return Result.success("用户信息更新成功", true);
            } else {
                return Result.error("用户信息更新失败");
            }
        } catch (Exception e) {
            return Result.error("Token无效或已过期");
        }
    }

    @PutMapping("/password")
    public Result<Boolean> changePassword(@RequestHeader("Authorization") String authorization,
                                          @RequestBody Map<String, String> passwordData,
                                          HttpServletRequest request) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return Result.error("未授权访问");
        }

        String token = authorization.substring(7);
        try {
            String userId = jwtUtil.getUserIdFromToken(token);
            String oldPassword = passwordData.get("oldPassword");
            String newPassword = passwordData.get("newPassword");

            User user = userService.getById(userId);
            if (user == null) {
                return Result.error("用户不存在");
            }
            
            if (!user.getPassword().equals(oldPassword)) {
                return Result.error("原密码错误");
            }

            user.setPassword(newPassword);
            boolean updated = userService.updateById(user);

            if (updated) {
                logUtil.log("修改密码", "PUT", request.getRequestURI(), "用户ID: " + userId);
                return Result.success("密码修改成功", true);
            } else {
                return Result.error("密码修改失败");
            }
        } catch (Exception e) {
            return Result.error("Token无效或已过期");
        }
    }

    @PostMapping("/logout")
    public Result<Boolean> logout(HttpServletRequest request) {
        logUtil.log("用户登出", "POST", request.getRequestURI(), "登出成功");
        return Result.success("登出成功", true);
    }

    @PutMapping("/notification-settings")
    public Result<Boolean> updateNotificationSettings(@RequestHeader("Authorization") String authorization,
                                                        @RequestBody Map<String, Object> settings,
                                                        HttpServletRequest request) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return Result.error("未授权访问");
        }

        String token = authorization.substring(7);
        try {
            String userId = jwtUtil.getUserIdFromToken(token);
            User user = userService.getById(userId);
            
            if (user == null) {
                return Result.error("用户不存在");
            }

            logUtil.log("更新通知设置", "PUT", request.getRequestURI(), "用户ID: " + userId);
            return Result.success("通知设置更新成功", true);
        } catch (Exception e) {
            return Result.error("Token无效或已过期");
        }
    }
}
