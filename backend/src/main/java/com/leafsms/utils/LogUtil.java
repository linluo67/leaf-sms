package com.leafsms.utils;

import com.leafsms.service.OperationLogService;
import com.leafsms.service.UserService;
import com.leafsms.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class LogUtil {
    
    @Autowired
    private OperationLogService operationLogService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    public void log(String operationType, String requestMethod, String requestUrl, String description, HttpServletRequest request) {
        String ipAddress = getClientIpAddress(request);
        
        String userInfo = getCurrentUserInfo(request);
        
        String finalDescription = description;
        if (userInfo != null) {
            finalDescription = userInfo + " - " + description;
        }
        
        operationLogService.logOperation(operationType, finalDescription, ipAddress);
    }
    
    public void log(String operationType, String requestMethod, String requestUrl, String description) {
        operationLogService.logOperation(operationType, description, "127.0.0.1");
    }
    
    private String getCurrentUserInfo(HttpServletRequest request) {
        try {
            String authorization = request.getHeader("Authorization");
            if (authorization == null || !authorization.startsWith("Bearer ")) {
                return null;
            }
            
            String token = authorization.substring(7);
            String userId = jwtUtil.getUserIdFromToken(token);
            
            if (userId != null) {
                User user = userService.getById(userId);
                if (user != null) {
                    return user.getUsername();
                }
            }
        } catch (Exception e) {
        }
        
        return null;
    }
    
    private String getClientIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        
        return ip;
    }

}
