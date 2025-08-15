package com.example.messageboardbackend.controller;

import com.example.messageboardbackend.dto.ApiResponse;
import com.example.messageboardbackend.service.MessageLikeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * 留言点赞控制器
 * 
 * @author Message Board Team
 * @version 1.0.0
 */
@RestController
@RequestMapping("/messages")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:8080"})
public class MessageLikeController {

    private final MessageLikeService messageLikeService;

    /**
     * 用户点赞留言
     * 
     * @param messageId 留言ID
     * @return 操作结果
     */
    @PostMapping("/{messageId}/like")
    public ResponseEntity<ApiResponse<String>> likeMessage(@PathVariable Long messageId) {
        try {
            log.debug("用户点赞留言请求: messageId={}", messageId);
            
            // 检查认证状态
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
                log.warn("未认证用户尝试点赞留言");
                return ResponseEntity.status(401).body(ApiResponse.error("请先登录"));
            }
            
            String username = auth.getName();
            log.debug("认证用户点赞留言: username={}, messageId={}", username, messageId);
            
            ApiResponse<String> result = messageLikeService.likeMessage(messageId, username);
            
            if (Boolean.TRUE.equals(result.getSuccess())) {
                log.debug("用户 {} 点赞留言 {} 成功", username, messageId);
            } else {
                log.warn("用户 {} 点赞留言 {} 失败: {}", username, messageId, result.getMessage());
            }
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("点赞留言失败: messageId={}, 错误: {}", messageId, e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.error("点赞失败: " + e.getMessage()));
        }
    }

    /**
     * 用户取消点赞
     * 
     * @param messageId 留言ID
     * @return 操作结果
     */
    @DeleteMapping("/{messageId}/like")
    public ResponseEntity<ApiResponse<String>> unlikeMessage(@PathVariable Long messageId) {
        try {
            log.debug("用户取消点赞请求: messageId={}", messageId);
            
            // 检查认证状态
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
                log.warn("未认证用户尝试取消点赞");
                return ResponseEntity.status(401).body(ApiResponse.error("请先登录"));
            }
            
            String username = auth.getName();
            log.debug("认证用户取消点赞: username={}, messageId={}", username, messageId);
            
            ApiResponse<String> result = messageLikeService.unlikeMessage(messageId, username);
            
            if (Boolean.TRUE.equals(result.getSuccess())) {
                log.debug("用户 {} 取消点赞留言 {} 成功", username, messageId);
            } else {
                log.warn("用户 {} 取消点赞留言 {} 失败: {}", username, messageId, result.getMessage());
            }
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("取消点赞失败: messageId={}, 错误: {}", messageId, e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.error("取消点赞失败: " + e.getMessage()));
        }
    }

    /**
     * 检查用户是否已点赞某条留言
     * 
     * @param messageId 留言ID
     * @return 是否已点赞
     */
    @GetMapping("/{messageId}/like/status")
    public ResponseEntity<ApiResponse<Boolean>> getLikeStatus(@PathVariable Long messageId) {
        try {
            log.debug("获取点赞状态请求: messageId={}", messageId);
            
            // 检查认证状态
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
                log.debug("匿名用户查询点赞状态");
                return ResponseEntity.ok(ApiResponse.success(false));
            }
            
            String username = auth.getName();
            log.debug("认证用户查询点赞状态: username={}, messageId={}", username, messageId);
            
            boolean isLiked = messageLikeService.isUserLikedMessage(messageId, username);
            
            log.debug("用户 {} 对留言 {} 的点赞状态: {}", username, messageId, isLiked);
            return ResponseEntity.ok(ApiResponse.success(isLiked));
        } catch (Exception e) {
            log.error("获取点赞状态失败: messageId={}, 错误: {}", messageId, e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.error("获取点赞状态失败: " + e.getMessage()));
        }
    }

    /**
     * 获取留言的点赞数量
     * 
     * @param messageId 留言ID
     * @return 点赞数量
     */
    @GetMapping("/{messageId}/like/count")
    public ResponseEntity<ApiResponse<Long>> getLikeCount(@PathVariable Long messageId) {
        try {
            log.debug("获取点赞数量请求: messageId={}", messageId);
            
            long likeCount = messageLikeService.getMessageLikeCount(messageId);
            
            log.debug("留言 {} 的点赞数量: {}", messageId, likeCount);
            return ResponseEntity.ok(ApiResponse.success(likeCount));
        } catch (Exception e) {
            log.error("获取点赞数量失败: messageId={}, 错误: {}", messageId, e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.error("获取点赞数量失败: " + e.getMessage()));
        }
    }
}
