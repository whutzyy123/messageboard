package com.example.messageboardbackend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;

/**
 * 留言点赞实体
 * 
 * @author Message Board Team
 * @version 1.0.0
 */
@Entity
@Table(name = "message_like", indexes = {
    @Index(name = "idx_message_like_message_id", columnList = "message_id"),
    @Index(name = "idx_message_like_user_id", columnList = "user_id"),
    @Index(name = "idx_message_like_message_user", columnList = "message_id,user_id", unique = true)
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE message_like SET deleted = true WHERE id = ?")
@Where(clause = "deleted = false")
public class MessageLike {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "message_id", nullable = false)
    private Message message;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "deleted", nullable = false)
    private Boolean deleted = false;
    
    // 业务方法
    public boolean isDeleted() {
        return Boolean.TRUE.equals(deleted);
    }
    
    public void softDelete() {
        this.deleted = true;
    }
    
    public void restore() {
        this.deleted = false;
    }
}
