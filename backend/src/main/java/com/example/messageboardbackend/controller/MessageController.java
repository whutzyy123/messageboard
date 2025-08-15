package com.example.messageboardbackend.controller;

import com.example.messageboardbackend.dto.ApiResponse;
import com.example.messageboardbackend.dto.MessageDto;
import com.example.messageboardbackend.service.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * 留言控制器
 * 
 * @author Message Board Team
 * @version 1.0.0
 */
@RestController
@RequestMapping("/messages")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:8080"})
public class MessageController {

    private final MessageService messageService;

    /**
     * 获取留言列表
     * 
     * @param page 页码（从0开始）
     * @param size 每页大小
     * @return 留言列表
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<MessageDto>>> getMessages(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            log.debug("获取留言列表请求: page={}, size={}", page, size);
            
            // 检查认证状态
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
                log.debug("认证用户: {}", auth.getName());
            } else {
                log.debug("匿名用户访问");
            }
            
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
            Page<MessageDto> messages = messageService.getMessages(pageable);
            
            log.debug("成功获取留言列表: 共{}条", messages.getTotalElements());
            return ResponseEntity.ok(ApiResponse.success(messages));
        } catch (Exception e) {
            log.error("获取留言列表失败: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.error("获取留言列表失败: " + e.getMessage()));
        }
    }

    /**
     * 根据ID获取留言
     * 
     * @param id 留言ID
     * @return 留言详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MessageDto>> getMessageById(@PathVariable Long id) {
        try {
            MessageDto message = messageService.getMessageById(id);
            return ResponseEntity.ok(ApiResponse.success(message));
        } catch (Exception e) {
            log.error("获取留言详情失败: ID={}, 错误={}", id, e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error("获取留言详情失败: " + e.getMessage()));
        }
    }

    /**
     * 创建新留言
     * 
     * @param messageDto 留言信息
     * @return 创建结果
     */
    @PostMapping
    public ResponseEntity<ApiResponse<MessageDto>> createMessage(@RequestBody MessageDto messageDto) {
        try {
            log.debug("创建留言请求: content={}", messageDto.getContent());
            
            // 手动验证 content 字段
            if (messageDto.getContent() == null || messageDto.getContent().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(ApiResponse.error("留言内容不能为空"));
            }
            
            if (messageDto.getContent().length() > 1000) {
                return ResponseEntity.badRequest().body(ApiResponse.error("留言内容长度不能超过1000个字符"));
            }
            
            // 检查认证状态
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
                log.warn("未认证用户尝试创建留言");
                return ResponseEntity.status(401).body(ApiResponse.error("请先登录"));
            }
            
            // 从认证信息中获取用户信息
            String username = auth.getName();
            log.debug("认证用户创建留言: {}", username);
            
            MessageDto createdMessage = messageService.createMessage(messageDto, username);
            
            log.debug("留言创建成功: ID={}", createdMessage.getId());
            return ResponseEntity.ok(ApiResponse.success(createdMessage));
        } catch (Exception e) {
            log.error("创建留言失败: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.error("创建留言失败: " + e.getMessage()));
        }
    }

    /**
     * 更新留言
     * 
     * @param id 留言ID
     * @param messageDto 留言信息
     * @return 更新结果
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<MessageDto>> updateMessage(
            @PathVariable Long id,
            @Valid @RequestBody MessageDto messageDto) {
        try {
            messageDto.setId(id);
            MessageDto updatedMessage = messageService.updateMessage(messageDto);
            return ResponseEntity.ok(ApiResponse.success(updatedMessage));
        } catch (Exception e) {
            log.error("更新留言失败: ID={}, 错误={}", id, e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error("更新留言失败: " + e.getMessage()));
        }
    }

    /**
     * 删除留言
     * 
     * @param id 留言ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteMessage(@PathVariable Long id) {
        try {
            messageService.deleteMessage(id);
            return ResponseEntity.ok(ApiResponse.success("留言删除成功"));
        } catch (Exception e) {
            log.error("删除留言失败: ID={}, 错误={}", id, e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error("删除留言失败: " + e.getMessage()));
        }
    }

    /**
     * 根据用户ID获取留言列表
     * 
     * @param userId 用户ID
     * @param page 页码
     * @param size 每页大小
     * @return 留言列表
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<Page<MessageDto>>> getMessagesByUserId(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
            Page<MessageDto> messages = messageService.getMessagesByUserId(userId, pageable);
            return ResponseEntity.ok(ApiResponse.success(messages));
        } catch (Exception e) {
            log.error("获取用户留言列表失败: 用户ID={}, 错误={}", userId, e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error("获取用户留言列表失败: " + e.getMessage()));
        }
    }

    /**
     * 搜索留言
     * 
     * @param content 搜索内容
     * @param page 页码
     * @param size 每页大小
     * @return 搜索结果
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<MessageDto>>> searchMessages(
            @RequestParam String content,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
            Page<MessageDto> messages = messageService.searchMessagesByContent(content, pageable);
            return ResponseEntity.ok(ApiResponse.success(messages));
        } catch (Exception e) {
            log.error("搜索留言失败: 内容={}, 错误={}", content, e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error("搜索留言失败: " + e.getMessage()));
        }
    }
    
    /**
     * 获取热门留言列表
     * 
     * @param page 页码
     * @param size 每页大小
     * @return 热门留言列表
     */
    @GetMapping("/hot")
    public ResponseEntity<ApiResponse<Page<MessageDto>>> getHotMessages(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            log.debug("获取热门留言列表请求: page={}, size={}", page, size);
            
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "likeCount", "createdAt"));
            Page<MessageDto> hotMessages = messageService.getHotMessages(pageable);
            
            log.debug("成功获取热门留言列表: 共{}条", hotMessages.getTotalElements());
            return ResponseEntity.ok(ApiResponse.success(hotMessages));
        } catch (Exception e) {
            log.error("获取热门留言列表失败: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.error("获取热门留言列表失败: " + e.getMessage()));
        }
    }
    
    /**
     * 获取所有热门留言
     * 
     * @return 热门留言列表
     */
    @GetMapping("/hot/all")
    public ResponseEntity<ApiResponse<List<MessageDto>>> getAllHotMessages() {
        try {
            log.debug("获取所有热门留言请求");
            
            List<MessageDto> hotMessages = messageService.getAllHotMessages();
            
            log.debug("成功获取所有热门留言: 共{}条", hotMessages.size());
            return ResponseEntity.ok(ApiResponse.success(hotMessages));
        } catch (Exception e) {
            log.error("获取所有热门留言失败: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.error("获取所有热门留言失败: " + e.getMessage()));
        }
    }
}
