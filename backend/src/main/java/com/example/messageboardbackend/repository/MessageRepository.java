package com.example.messageboardbackend.repository;

import com.example.messageboardbackend.model.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 留言数据访问接口
 * 
 * @author Message Board Team
 * @version 1.0.0
 */
@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    
    /**
     * 查找所有未删除的留言（分页）
     * 
     * @param pageable 分页参数
     * @return 留言分页结果
     */
    Page<Message> findByDeletedFalse(Pageable pageable);
    
    /**
     * 根据用户ID查找留言（分页）
     * 
     * @param userId 用户ID
     * @param pageable 分页参数
     * @return 留言分页结果
     */
    Page<Message> findByUserIdAndDeletedFalse(Long userId, Pageable pageable);
    
    /**
     * 根据用户ID查找所有留言
     * 
     * @param userId 用户ID
     * @return 留言列表
     */
    List<Message> findByUserIdAndDeletedFalse(Long userId);
    
    /**
     * 根据创建时间范围查找留言
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param pageable 分页参数
     * @return 留言分页结果
     */
    @Query("SELECT m FROM Message m WHERE m.createdAt BETWEEN :startTime AND :endTime AND m.deleted = false")
    Page<Message> findByCreatedAtBetween(@Param("startTime") LocalDateTime startTime, 
                                        @Param("endTime") LocalDateTime endTime, 
                                        Pageable pageable);
    
    /**
     * 根据内容模糊查询留言
     * 
     * @param content 内容（模糊匹配）
     * @param pageable 分页参数
     * @return 留言分页结果
     */
    @Query("SELECT m FROM Message m WHERE m.content LIKE %:content% AND m.deleted = false")
    Page<Message> findByContentContaining(@Param("content") String content, Pageable pageable);
    
    /**
     * 统计用户的留言数量
     * 
     * @param userId 用户ID
     * @return 留言数量
     */
    long countByUserIdAndDeletedFalse(Long userId);
    
    /**
     * 查找最新的留言
     * 
     * @param limit 限制数量
     * @return 最新留言列表
     */
    @Query("SELECT m FROM Message m WHERE m.deleted = false ORDER BY m.createdAt DESC")
    List<Message> findLatestMessages(Pageable pageable);
    
    /**
     * 查找热门留言（点赞数大于等于5）
     * 
     * @param pageable 分页参数
     * @return 热门留言分页结果
     */
    Page<Message> findByIsHotTrueAndDeletedFalse(Pageable pageable);
    
    /**
     * 查找热门留言（按点赞数排序）
     * 
     * @param pageable 分页参数
     * @return 热门留言分页结果
     */
    @Query("SELECT m FROM Message m WHERE m.deleted = false AND m.likeCount >= 5 ORDER BY m.likeCount DESC, m.createdAt DESC")
    Page<Message> findHotMessagesOrderByLikeCount(Pageable pageable);
    
    /**
     * 查找所有热门留言
     * 
     * @return 热门留言列表
     */
    @Query("SELECT m FROM Message m WHERE m.deleted = false AND m.likeCount >= 5 ORDER BY m.likeCount DESC, m.createdAt DESC")
    List<Message> findAllHotMessages();
}
