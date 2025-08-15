package com.example.messageboardbackend.service.impl;

import com.example.messageboardbackend.dto.ApiResponse;
import com.example.messageboardbackend.model.Message;
import com.example.messageboardbackend.model.MessageLike;
import com.example.messageboardbackend.model.User;
import com.example.messageboardbackend.repository.MessageLikeRepository;
import com.example.messageboardbackend.repository.MessageRepository;
import com.example.messageboardbackend.repository.UserRepository;
import com.example.messageboardbackend.service.MessageLikeService;
import com.example.messageboardbackend.service.RedisCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 留言点赞服务实现类
 * 
 * @author Message Board Team
 * @version 1.0.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class MessageLikeServiceImpl implements MessageLikeService {
    
    private final MessageLikeRepository messageLikeRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final RedisCacheService redisCacheService;
    
    @Override
    public ApiResponse<String> likeMessage(Long messageId, String username) {
        try {
            // 查找用户
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("用户不存在: " + username));
            
            // 查找留言
            Message message = messageRepository.findById(messageId)
                    .orElseThrow(() -> new RuntimeException("留言不存在: " + messageId));
            
            if (message.getDeleted()) {
                return ApiResponse.error("留言已删除");
            }
            
            // 检查是否已经点赞
            if (messageLikeRepository.existsByMessageIdAndUserIdAndDeletedFalse(messageId, user.getId())) {
                return ApiResponse.error("您已经点赞过这条留言");
            }
            
            // 创建点赞记录
            MessageLike messageLike = new MessageLike();
            messageLike.setMessage(message);
            messageLike.setUser(user);
            messageLikeRepository.save(messageLike);
            
            // 更新留言点赞数
            message.incrementLikeCount();
            messageRepository.save(message);
            
            // 清除相关缓存
            redisCacheService.clearMessageListCache();
            redisCacheService.clearHotMessageCache();
            
            log.info("用户 {} 点赞留言 {} 成功，当前点赞数: {}", username, messageId, message.getLikeCount());
            
            return ApiResponse.success("点赞成功");
        } catch (Exception e) {
            log.error("点赞留言失败: messageId={}, username={}, 错误: {}", messageId, username, e.getMessage());
            return ApiResponse.error("点赞失败: " + e.getMessage());
        }
    }
    
    @Override
    public ApiResponse<String> unlikeMessage(Long messageId, String username) {
        try {
            // 查找用户
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("用户不存在: " + username));
            
            // 查找点赞记录
            MessageLike messageLike = messageLikeRepository.findByMessageIdAndUserIdAndDeletedFalse(messageId, user.getId())
                    .orElseThrow(() -> new RuntimeException("您还没有点赞过这条留言"));
            
            // 软删除点赞记录
            messageLike.softDelete();
            messageLikeRepository.save(messageLike);
            
            // 更新留言点赞数
            Message message = messageLike.getMessage();
            message.decrementLikeCount();
            messageRepository.save(message);
            
            // 清除相关缓存
            redisCacheService.clearMessageListCache();
            redisCacheService.clearHotMessageCache();
            
            log.info("用户 {} 取消点赞留言 {} 成功，当前点赞数: {}", username, messageId, message.getLikeCount());
            
            return ApiResponse.success("取消点赞成功");
        } catch (Exception e) {
            log.error("取消点赞失败: messageId={}, username={}, 错误: {}", messageId, username, e.getMessage());
            return ApiResponse.error("取消点赞失败: " + e.getMessage());
        }
    }
    
    @Override
    public boolean isUserLikedMessage(Long messageId, String username) {
        try {
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("用户不存在: " + username));
            
            return messageLikeRepository.existsByMessageIdAndUserIdAndDeletedFalse(messageId, user.getId());
        } catch (Exception e) {
            log.error("检查用户点赞状态失败: messageId={}, username={}, 错误: {}", messageId, username, e.getMessage());
            return false;
        }
    }
    
    @Override
    public long getMessageLikeCount(Long messageId) {
        try {
            return messageLikeRepository.countByMessageId(messageId);
        } catch (Exception e) {
            log.error("获取留言点赞数失败: messageId={}, 错误: {}", messageId, e.getMessage());
            return 0;
        }
    }
}
