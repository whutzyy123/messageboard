package com.example.messageboardbackend.repository;

import com.example.messageboardbackend.model.MessageLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 留言点赞数据访问层
 * 
 * @author Message Board Team
 * @version 1.0.0
 */
@Repository
public interface MessageLikeRepository extends JpaRepository<MessageLike, Long> {
    
    /**
     * 根据留言ID和用户ID查找点赞记录
     * 
     * @param messageId 留言ID
     * @param userId 用户ID
     * @return 点赞记录
     */
    Optional<MessageLike> findByMessageIdAndUserIdAndDeletedFalse(Long messageId, Long userId);
    
    /**
     * 检查用户是否已点赞某条留言
     * 
     * @param messageId 留言ID
     * @param userId 用户ID
     * @return 是否已点赞
     */
    boolean existsByMessageIdAndUserIdAndDeletedFalse(Long messageId, Long userId);
    
    /**
     * 根据留言ID统计点赞数量
     * 
     * @param messageId 留言ID
     * @return 点赞数量
     */
    @Query("SELECT COUNT(ml) FROM MessageLike ml WHERE ml.message.id = :messageId AND ml.deleted = false")
    long countByMessageId(@Param("messageId") Long messageId);
    
    /**
     * 根据留言ID获取所有点赞记录
     * 
     * @param messageId 留言ID
     * @return 点赞记录列表
     */
    @Query("SELECT ml FROM MessageLike ml WHERE ml.message.id = :messageId AND ml.deleted = false")
    List<MessageLike> findByMessageId(@Param("messageId") Long messageId);
    
    /**
     * 根据用户ID获取用户的所有点赞记录
     * 
     * @param userId 用户ID
     * @return 点赞记录列表
     */
    @Query("SELECT ml FROM MessageLike ml WHERE ml.user.id = :userId AND ml.deleted = false")
    List<MessageLike> findByUserId(@Param("userId") Long userId);
}
