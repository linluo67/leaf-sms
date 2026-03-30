package com.leafsms.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.leafsms.entity.Message;

import java.util.Map;

public interface MessageService extends IService<Message> {
    
    IPage<Map<String, Object>> pageMessages(Page<Map<String, Object>> page, String receiverId, Boolean isRead);
    
    boolean addMessage(Message message);
    
    boolean markAsRead(String messageId);
    
    boolean markAllAsRead(String receiverId);
    
    boolean deleteMessage(String messageId);
    
    Map<String, Object> countUnread(String receiverId);
}
