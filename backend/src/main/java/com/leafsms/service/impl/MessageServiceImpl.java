package com.leafsms.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leafsms.entity.Message;
import com.leafsms.mapper.MessageMapper;
import com.leafsms.service.MessageService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements MessageService {
    
    @Override
    public IPage<Map<String, Object>> pageMessages(Page<Map<String, Object>> page, String receiverId, Boolean isRead) {
        if (isRead != null) {
            return baseMapper.findByReceiverIdAndReadStatus(page, receiverId, isRead);
        }
        return baseMapper.findByReceiverId(page, receiverId);
    }
    
    @Override
    public boolean addMessage(Message message) {
        message.setCreateTime(LocalDateTime.now());
        message.setUpdateTime(LocalDateTime.now());
        message.setIsRead(false);
        return baseMapper.insert(message) > 0;
    }
    
    @Override
    public boolean markAsRead(String messageId) {
        Message message = baseMapper.selectById(messageId);
        if (message != null) {
            message.setIsRead(true);
            message.setReadTime(LocalDateTime.now());
            message.setUpdateTime(LocalDateTime.now());
            return baseMapper.updateById(message) > 0;
        }
        return false;
    }
    
    @Override
    public boolean markAllAsRead(String receiverId) {
        return baseMapper.markAllAsRead(receiverId) > 0;
    }
    
    @Override
    public boolean deleteMessage(String messageId) {
        return baseMapper.deleteById(messageId) > 0;
    }
    
    @Override
    public Map<String, Object> countUnread(String receiverId) {
        return baseMapper.countUnreadByReceiverId(receiverId);
    }
}
