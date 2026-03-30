package com.leafsms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.leafsms.entity.Message;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

@Mapper
public interface MessageMapper extends BaseMapper<Message> {
    
    @Select("SELECT m.*, s.username as sender_name, r.username as receiver_name " +
            "FROM messages m " +
            "LEFT JOIN users s ON m.sender_id = s.user_id " +
            "LEFT JOIN users r ON m.receiver_id = r.user_id " +
            "WHERE m.receiver_id = #{receiverId} ORDER BY m.create_time DESC")
    IPage<Map<String, Object>> findByReceiverId(Page<Map<String, Object>> page, @Param("receiverId") String receiverId);
    
    @Select("SELECT m.*, s.username as sender_name, r.username as receiver_name " +
            "FROM messages m " +
            "LEFT JOIN users s ON m.sender_id = s.user_id " +
            "LEFT JOIN users r ON m.receiver_id = r.user_id " +
            "WHERE m.receiver_id = #{receiverId} AND m.is_read = #{isRead} ORDER BY m.create_time DESC")
    IPage<Map<String, Object>> findByReceiverIdAndReadStatus(Page<Map<String, Object>> page, 
                                                              @Param("receiverId") String receiverId, 
                                                              @Param("isRead") Boolean isRead);
    
    @Select("SELECT COUNT(*) as count FROM messages WHERE receiver_id = #{receiverId} AND is_read = false")
    Map<String, Object> countUnreadByReceiverId(@Param("receiverId") String receiverId);
    
    @Update("UPDATE messages SET is_read = true, read_time = NOW() WHERE message_id = #{messageId}")
    int markAsRead(@Param("messageId") String messageId);
    
    @Update("UPDATE messages SET is_read = true, read_time = NOW() WHERE receiver_id = #{receiverId} AND is_read = false")
    int markAllAsRead(@Param("receiverId") String receiverId);
}
