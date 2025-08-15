package com.example.messageboardbackend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Redis缓存服务
 * 
 * @author Message Board Team
 * @version 1.0.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RedisCacheService {

    private final RedisTemplate<String, Object> redisTemplate;
    
    private static final String MESSAGE_LIST_CACHE_KEY = "message:list";
    private static final long MESSAGE_LIST_CACHE_EXPIRE = 5; // 5分钟过期
    
    private static final String HOT_MESSAGE_CACHE_KEY = "message:hot";
    private static final long HOT_MESSAGE_CACHE_EXPIRE = 10; // 10分钟过期

    /**
     * 从缓存中获取留言列表
     * 
     * @return 留言列表，如果缓存不存在则返回null
     */
    @SuppressWarnings("unchecked")
    public List<Object> getMessageListFromCache() {
        try {
            Object cached = redisTemplate.opsForValue().get(MESSAGE_LIST_CACHE_KEY);
            if (cached != null) {
                log.debug("从Redis缓存获取留言列表成功");
                return (List<Object>) cached;
            }
        } catch (Exception e) {
            log.warn("从Redis缓存获取留言列表失败: {}", e.getMessage());
        }
        return null;
    }

    /**
     * 将留言列表存入缓存
     * 
     * @param messageList 留言列表
     */
    public void cacheMessageList(List<Object> messageList) {
        try {
            redisTemplate.opsForValue().set(MESSAGE_LIST_CACHE_KEY, messageList, MESSAGE_LIST_CACHE_EXPIRE, TimeUnit.MINUTES);
            log.debug("留言列表已缓存到Redis，过期时间: {}分钟", MESSAGE_LIST_CACHE_EXPIRE);
        } catch (Exception e) {
            log.warn("缓存留言列表到Redis失败: {}", e.getMessage());
        }
    }

    /**
     * 清除留言列表缓存
     */
    public void clearMessageListCache() {
        try {
            redisTemplate.delete(MESSAGE_LIST_CACHE_KEY);
            log.debug("留言列表缓存已清除");
        } catch (Exception e) {
            log.warn("清除留言列表缓存失败: {}", e.getMessage());
        }
    }

    /**
     * 检查缓存是否存在
     * 
     * @return 缓存是否存在
     */
    public boolean hasMessageListCache() {
        try {
            return Boolean.TRUE.equals(redisTemplate.hasKey(MESSAGE_LIST_CACHE_KEY));
        } catch (Exception e) {
            log.warn("检查留言列表缓存状态失败: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * 从缓存中获取热门留言列表
     * 
     * @return 热门留言列表，如果缓存不存在则返回null
     */
    @SuppressWarnings("unchecked")
    public List<Object> getHotMessageListFromCache() {
        try {
            Object cached = redisTemplate.opsForValue().get(HOT_MESSAGE_CACHE_KEY);
            if (cached != null) {
                log.debug("从Redis缓存获取热门留言列表成功");
                return (List<Object>) cached;
            }
        } catch (Exception e) {
            log.warn("从Redis缓存获取热门留言列表失败: {}", e.getMessage());
        }
        return null;
    }

    /**
     * 将热门留言列表存入缓存
     * 
     * @param hotMessageList 热门留言列表
     */
    public void cacheHotMessageList(List<Object> hotMessageList) {
        try {
            redisTemplate.opsForValue().set(HOT_MESSAGE_CACHE_KEY, hotMessageList, HOT_MESSAGE_CACHE_EXPIRE, TimeUnit.MINUTES);
            log.debug("热门留言列表已缓存到Redis，过期时间: {}分钟", HOT_MESSAGE_CACHE_EXPIRE);
        } catch (Exception e) {
            log.warn("缓存热门留言列表到Redis失败: {}", e.getMessage());
        }
    }

    /**
     * 清除热门留言缓存
     */
    public void clearHotMessageCache() {
        try {
            redisTemplate.delete(HOT_MESSAGE_CACHE_KEY);
            log.debug("热门留言缓存已清除");
        } catch (Exception e) {
            log.warn("清除热门留言缓存失败: {}", e.getMessage());
        }
    }

    /**
     * 检查热门留言缓存是否存在
     * 
     * @return 缓存是否存在
     */
    public boolean hasHotMessageCache() {
        try {
            return Boolean.TRUE.equals(redisTemplate.hasKey(HOT_MESSAGE_CACHE_KEY));
        } catch (Exception e) {
            log.warn("检查热门留言缓存状态失败: {}", e.getMessage());
            return false;
        }
    }
}
