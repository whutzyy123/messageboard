package com.messageboard.repository;

import com.messageboard.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 留言数据访问接口
 */
@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    
    /**
     * 分页查询所有留言（按创建时间倒序）
     */
    Page<Message> findAllByOrderByCreatedAtDesc(Pageable pageable);
    
    /**
     * 查询热门留言
     */
    List<Message> findByIsHotTrueOrderByLikesCountDesc();
    
    /**
     * 根据用户ID查询留言
     */
    List<Message> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    /**
     * 根据内容关键词搜索留言
     */
    @Query("SELECT m FROM Message m WHERE m.content LIKE %:keyword% ORDER BY m.createdAt DESC")
    List<Message> findByContentContaining(@Param("keyword") String keyword);
    
    /**
     * 统计用户留言数量
     */
    long countByUserId(Long userId);
    
    /**
     * 查询点赞数大于指定值的留言
     */
    List<Message> findByLikesCountGreaterThanOrderByLikesCountDesc(Integer minLikes);
}
