package com.leafsms.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.leafsms.common.Result;
import com.leafsms.entity.Message;
import com.leafsms.service.MessageService;
import com.leafsms.utils.JwtUtil;
import com.leafsms.utils.LogUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/messages")
public class MessageController {
    
    @Autowired
    private MessageService messageService;
    
    @Autowired
    private LogUtil logUtil;
    
    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping
    public Result<IPage<Map<String, Object>>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Boolean isRead,
            @RequestHeader("Authorization") String authorization,
            HttpServletRequest request) {
        
        String userId = getUserIdFromToken(authorization);
        if (userId == null) {
            return Result.error("未授权访问");
        }
        
        Page<Map<String, Object>> pageParam = new Page<>(page, size);
        IPage<Map<String, Object>> messagePage = messageService.pageMessages(pageParam, userId, isRead);
        return Result.success(messagePage);
    }

    @GetMapping("/unread-count")
    public Result<Map<String, Object>> getUnreadCount(
            @RequestHeader("Authorization") String authorization,
            HttpServletRequest request) {
        
        String userId = getUserIdFromToken(authorization);
        if (userId == null) {
            return Result.error("未授权访问");
        }
        
        Map<String, Object> count = messageService.countUnread(userId);
        return Result.success(count);
    }

    @GetMapping("/{messageId}")
    public Result<Message> getById(@PathVariable String messageId, HttpServletRequest request) {
        Message message = messageService.getById(messageId);
        if (message == null) {
            return Result.error("消息不存在");
        }
        return Result.success(message);
    }

    @PostMapping
    public Result<Message> add(@Valid @RequestBody Message message, 
                               @RequestHeader("Authorization") String authorization,
                               HttpServletRequest request) {
        
        String userId = getUserIdFromToken(authorization);
        if (userId == null) {
            return Result.error("未授权访问");
        }
        
        message.setSenderId(userId);
        boolean success = messageService.addMessage(message);
        if (success) {
            logUtil.log("新增消息", "POST", request.getRequestURI(), "发送人ID: " + userId + ", 接收人ID: " + message.getReceiverId());
            return Result.success(message);
        }
        return Result.error("新增消息失败");
    }

    @PutMapping("/{messageId}/read")
    public Result<Boolean> markAsRead(@PathVariable String messageId, HttpServletRequest request) {
        boolean success = messageService.markAsRead(messageId);
        if (success) {
            logUtil.log("标记消息已读", "PUT", request.getRequestURI(), "消息ID: " + messageId);
            return Result.success("标记成功", true);
        }
        return Result.error("标记失败");
    }

    @PutMapping("/read-all")
    public Result<Boolean> markAllAsRead(@RequestHeader("Authorization") String authorization,
                                          HttpServletRequest request) {
        
        String userId = getUserIdFromToken(authorization);
        if (userId == null) {
            return Result.error("未授权访问");
        }
        
        boolean success = messageService.markAllAsRead(userId);
        if (success) {
            logUtil.log("全部标记已读", "PUT", request.getRequestURI(), "接收人ID: " + userId);
            return Result.success("标记成功", true);
        }
        return Result.error("标记失败");
    }

    @DeleteMapping("/{messageId}")
    public Result<Void> delete(@PathVariable String messageId, HttpServletRequest request) {
        boolean success = messageService.deleteMessage(messageId);
        if (success) {
            logUtil.log("删除消息", "DELETE", request.getRequestURI(), "消息ID: " + messageId);
            return Result.success(null);
        }
        return Result.error("删除消息失败");
    }
    
    private String getUserIdFromToken(String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return null;
        }
        String token = authorization.substring(7);
        try {
            return jwtUtil.getUserIdFromToken(token);
        } catch (Exception e) {
            return null;
        }
    }
}
