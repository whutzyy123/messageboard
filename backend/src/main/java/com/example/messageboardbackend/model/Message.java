package com.example.messageboardbackend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;

/**
 * 留言实体类
 * 
 * @author Message Board Team
 * @version 1.0.0
 */
@Entity
@Table(name = "message", indexes = {
    // 用户ID索引：优化按用户查询留言的性能
    @Index(name = "idx_message_user_id", columnList = "user_id"),
    
    // 创建时间索引：优化按时间排序和范围查询的性能
    @Index(name = "idx_message_created_at", columnList = "created_at"),
    
    // 删除状态索引：优化软删除查询的性能
    @Index(name = "idx_message_deleted", columnList = "deleted"),
    
    // 复合索引：优化用户留言列表查询（用户ID + 删除状态 + 创建时间）
    @Index(name = "idx_message_user_deleted_created", 
           columnList = "user_id, deleted, created_at"),
    
    // 内容搜索索引：优化全文搜索性能（可选，如果使用MySQL全文索引）
    @Index(name = "idx_message_content", columnList = "content(100)")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE message SET deleted = true WHERE id = ?")
@Where(clause = "deleted = false")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_message_user"))
    private User user;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "deleted", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean deleted = false;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @Column(name = "like_count", nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer likeCount = 0;
    
    @Column(name = "is_hot", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean isHot = false;

    // 业务方法
    public boolean isDeleted() {
        return Boolean.TRUE.equals(this.deleted);
    }
    
    public void incrementLikeCount() {
        this.likeCount = (this.likeCount == null ? 0 : this.likeCount) + 1;
        this.updatedAt = LocalDateTime.now();
        // 检查是否成为热门留言
        if (this.likeCount >= 5) {
            this.isHot = true;
        }
    }
    
    public void decrementLikeCount() {
        this.likeCount = Math.max(0, (this.likeCount == null ? 0 : this.likeCount) - 1);
        this.updatedAt = LocalDateTime.now();
        // 如果点赞数少于5，取消热门状态
        if (this.likeCount < 5) {
            this.isHot = false;
        }
    }
    
    public boolean isHotMessage() {
        return Boolean.TRUE.equals(this.isHot);
    }

    public void softDelete() {
        this.deleted = true;
        this.updatedAt = LocalDateTime.now();
    }

    public void restore() {
        this.deleted = false;
        this.updatedAt = LocalDateTime.now();
    }
}
