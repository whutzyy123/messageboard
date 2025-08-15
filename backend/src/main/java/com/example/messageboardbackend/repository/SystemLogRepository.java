package com.example.messageboardbackend.repository;

import com.example.messageboardbackend.model.SystemLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 系统日志数据访问接口
 * 
 * @author Message Board Team
 * @version 1.0.0
 */
@Repository
public interface SystemLogRepository extends JpaRepository<SystemLog, Long> {
    
    /**
     * 根据日志级别查找日志
     * 
     * @param level 日志级别
     * @param pageable 分页参数
     * @return 日志分页结果
     */
    Page<SystemLog> findByLevel(SystemLog.LogLevel level, Pageable pageable);
    
    /**
     * 根据创建时间范围查找日志
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param pageable 分页参数
     * @return 日志分页结果
     */
    @Query("SELECT l FROM SystemLog l WHERE l.createdAt BETWEEN :startTime AND :endTime")
    Page<SystemLog> findByCreatedAtBetween(@Param("startTime") LocalDateTime startTime, 
                                          @Param("endTime") LocalDateTime endTime, 
                                          Pageable pageable);
    
    /**
     * 根据日志级别和时间范围查找日志
     * 
     * @param level 日志级别
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param pageable 分页参数
     * @return 日志分页结果
     */
    @Query("SELECT l FROM SystemLog l WHERE l.level = :level AND l.createdAt BETWEEN :startTime AND :endTime")
    Page<SystemLog> findByLevelAndCreatedAtBetween(@Param("level") SystemLog.LogLevel level,
                                                   @Param("startTime") LocalDateTime startTime,
                                                   @Param("endTime") LocalDateTime endTime,
                                                   Pageable pageable);
    
    /**
     * 根据内容模糊查询日志
     * 
     * @param content 内容（模糊匹配）
     * @param pageable 分页参数
     * @return 日志分页结果
     */
    @Query("SELECT l FROM SystemLog l WHERE l.logContent LIKE %:content%")
    Page<SystemLog> findByLogContentContaining(@Param("content") String content, Pageable pageable);
    
    /**
     * 查找最新的日志
     * 
     * @param pageable 分页参数
     * @return 最新日志列表
     */
    @Query("SELECT l FROM SystemLog l ORDER BY l.createdAt DESC")
    List<SystemLog> findLatestLogs(Pageable pageable);
    
    /**
     * 根据日志级别统计数量
     * 
     * @param level 日志级别
     * @return 数量
     */
    long countByLevel(SystemLog.LogLevel level);
    
    /**
     * 删除指定时间之前的日志
     * 
     * @param beforeTime 指定时间
     * @return 删除的记录数
     */
    @Query("DELETE FROM SystemLog l WHERE l.createdAt < :beforeTime")
    int deleteByCreatedAtBefore(@Param("beforeTime") LocalDateTime beforeTime);
}
