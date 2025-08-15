package com.example.messageboardbackend.service;

import com.example.messageboardbackend.dto.ApiResponse;

/**
 * 留言点赞服务接口
 * 
 * @author Message Board Team
 * @version 1.0.0
 */
public interface MessageLikeService {
    
    /**
     * 用户点赞留言
     * 
     * @param messageId 留言ID
     * @param username 用户名
     * @return 操作结果
     */
    ApiResponse<String> likeMessage(Long messageId, String username);
    
    /**
     * 用户取消点赞
     * 
     * @param messageId 留言ID
     * @param username 用户名
     * @return 操作结果
     */
    ApiResponse<String> unlikeMessage(Long messageId, String username);
    
    /**
     * 检查用户是否已点赞某条留言
     * 
     * @param messageId 留言ID
     * @param username 用户名
     * @return 是否已点赞
     */
    boolean isUserLikedMessage(Long messageId, String username);
    
    /**
     * 获取留言的点赞数量
     * 
     * @param messageId 留言ID
     * @return 点赞数量
     */
    long getMessageLikeCount(Long messageId);
}
