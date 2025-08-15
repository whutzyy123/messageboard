package com.example.messageboardbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * 留言数据传输对象
 * 
 * @author Message Board Team
 * @version 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageDto {
    
    private Long id;
    
    // userId 不再必填，由后端根据认证信息自动设置
    private Long userId;
    
    @NotBlank(message = "留言内容不能为空")
    @Size(max = 1000, message = "留言内容长度不能超过1000个字符")
    private String content;
    
    private String username; // 用户名，用于显示
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean deleted;
    private Integer likeCount = 0; // 点赞数量
    private Boolean isHot = false; // 是否为热门留言
    private Boolean isLiked = false; // 当前用户是否已点赞（前端使用）
}
