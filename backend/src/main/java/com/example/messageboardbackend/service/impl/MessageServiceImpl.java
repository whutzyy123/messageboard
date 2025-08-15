package com.example.messageboardbackend.service.impl;

import com.example.messageboardbackend.dto.MessageDto;
import com.example.messageboardbackend.model.Message;
import com.example.messageboardbackend.model.User;
import com.example.messageboardbackend.repository.MessageRepository;
import com.example.messageboardbackend.repository.UserRepository;
import com.example.messageboardbackend.service.MessageService;
import com.example.messageboardbackend.service.RedisCacheService;
import com.example.messageboardbackend.service.KafkaProducerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/**
 * 留言服务实现类
 * 
 * @author Message Board Team
 * @version 1.0.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class MessageServiceImpl implements MessageService {
    
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final RedisCacheService redisCacheService;
    private final KafkaProducerService kafkaProducerService;
    
    @Override
    public Page<MessageDto> getMessages(Pageable pageable) {
        // 首先尝试从Redis缓存获取数据
        if (pageable.getPageNumber() == 0 && pageable.getPageSize() <= 50) {
            List<Object> cachedMessages = redisCacheService.getMessageListFromCache();
            if (cachedMessages != null) {
                log.info("从Redis缓存获取留言列表，缓存命中");
                // 这里简化处理，实际项目中需要更复杂的缓存逻辑
                // 由于分页查询的复杂性，这里主要缓存第一页数据
                Page<Message> messages = messageRepository.findByDeletedFalse(pageable);
                return messages.map(this::convertToDto);
            }
        }
        
        // 缓存未命中，从数据库查询
        Page<Message> messages = messageRepository.findByDeletedFalse(pageable);
        Page<MessageDto> result = messages.map(this::convertToDto);
        
        // 如果是第一页且数据量不大，则缓存到Redis
        if (pageable.getPageNumber() == 0 && pageable.getPageSize() <= 50) {
            try {
                redisCacheService.cacheMessageList((List<Object>) (List<?>) result.getContent());
                log.debug("留言列表已缓存到Redis");
            } catch (Exception e) {
                log.warn("缓存留言列表失败: {}", e.getMessage());
            }
        }
        
        return result;
    }
    
    @Override
    public MessageDto getMessageById(Long id) {
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("留言不存在"));
        
        if (message.getDeleted()) {
            throw new RuntimeException("留言已删除");
        }
        
        return convertToDto(message);
    }
    
    @Override
    public MessageDto createMessage(MessageDto messageDto) {
        // 验证用户是否存在
        User user = userRepository.findById(messageDto.getUserId())
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        // 创建留言
        Message message = new Message();
        message.setUser(user);
        message.setContent(messageDto.getContent());
        message.setDeleted(false);
        
        Message savedMessage = messageRepository.save(message);
        log.info("留言创建成功: ID={}, 用户={}", savedMessage.getId(), user.getUsername());
        
        // 发送Kafka日志消息
        try {
            kafkaProducerService.sendUserMessageLog(user.getUsername());
            log.debug("用户发布留言日志已发送到Kafka");
        } catch (Exception e) {
            log.warn("发送Kafka日志失败: {}", e.getMessage());
        }
        
        // 清除Redis缓存，因为数据已更新
        try {
            redisCacheService.clearMessageListCache();
            log.debug("留言列表缓存已清除");
        } catch (Exception e) {
            log.warn("清除缓存失败: {}", e.getMessage());
        }
        
        return convertToDto(savedMessage);
    }
    
    @Override
    public MessageDto createMessage(MessageDto messageDto, String username) {
        // 根据用户名查找用户
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户不存在: " + username));
        
        // 创建留言
        Message message = new Message();
        message.setUser(user);
        message.setContent(messageDto.getContent());
        message.setDeleted(false);
        
        Message savedMessage = messageRepository.save(message);
        log.info("留言创建成功: ID={}, 用户={}", savedMessage.getId(), user.getUsername());
        
        // 发送Kafka日志消息
        try {
            kafkaProducerService.sendUserMessageLog(user.getUsername());
            log.debug("用户发布留言日志已发送到Kafka");
        } catch (Exception e) {
            log.warn("发送Kafka日志失败: {}", e.getMessage());
        }
        
        // 清除Redis缓存，因为数据已更新
        try {
            redisCacheService.clearMessageListCache();
            log.debug("留言列表缓存已清除");
        } catch (Exception e) {
            log.warn("清除缓存失败: {}", e.getMessage());
        }
        
        return convertToDto(savedMessage);
    }
    
    @Override
    public MessageDto updateMessage(MessageDto messageDto) {
        Message message = messageRepository.findById(messageDto.getId())
                .orElseThrow(() -> new RuntimeException("留言不存在"));
        
        if (message.getDeleted()) {
            throw new RuntimeException("留言已删除");
        }
        
        // 更新留言内容
        message.setContent(messageDto.getContent());
        
        Message updatedMessage = messageRepository.save(message);
        log.info("留言更新成功: ID={}", updatedMessage.getId());
        
        // 清除Redis缓存，因为数据已更新
        try {
            redisCacheService.clearMessageListCache();
            log.debug("留言列表缓存已清除");
        } catch (Exception e) {
            log.warn("清除缓存失败: {}", e.getMessage());
        }
        
        return convertToDto(updatedMessage);
    }
    
    @Override
    public void deleteMessage(Long id) {
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("留言不存在"));
        
        // 软删除
        message.setDeleted(true);
        messageRepository.save(message);
        
        log.info("留言删除成功: ID={}", id);
        
        // 清除Redis缓存，因为数据已更新
        try {
            redisCacheService.clearMessageListCache();
            log.debug("留言列表缓存已清除");
        } catch (Exception e) {
            log.warn("清除缓存失败: {}", e.getMessage());
        }
    }
    
    @Override
    public Page<MessageDto> getMessagesByUserId(Long userId, Pageable pageable) {
        Page<Message> messages = messageRepository.findByUserIdAndDeletedFalse(userId, pageable);
        return messages.map(this::convertToDto);
    }
    
    @Override
    public Page<MessageDto> searchMessagesByContent(String content, Pageable pageable) {
        Page<Message> messages = messageRepository.findByContentContaining(content, pageable);
        return messages.map(this::convertToDto);
    }
    
    /**
     * 将Message实体转换为MessageDto
     * 
     * @param message Message实体
     * @return MessageDto
     */
    private MessageDto convertToDto(Message message) {
        MessageDto dto = new MessageDto();
        dto.setId(message.getId());
        dto.setUserId(message.getUser().getId());
        dto.setContent(message.getContent());
        dto.setUsername(message.getUser().getUsername());
        dto.setCreatedAt(message.getCreatedAt());
        dto.setUpdatedAt(message.getUpdatedAt());
        dto.setDeleted(message.getDeleted());
        dto.setLikeCount(message.getLikeCount());
        dto.setIsHot(message.getIsHot());
        return dto;
    }
    
    @Override
    public Page<MessageDto> getHotMessages(Pageable pageable) {
        // 首先尝试从Redis缓存获取数据
        if (pageable.getPageNumber() == 0 && pageable.getPageSize() <= 50) {
            List<Object> cachedHotMessages = redisCacheService.getHotMessageListFromCache();
            if (cachedHotMessages != null) {
                log.info("从Redis缓存获取热门留言列表，缓存命中");
                // 由于分页查询的复杂性，这里主要缓存第一页数据
                Page<Message> hotMessages = messageRepository.findHotMessagesOrderByLikeCount(pageable);
                return hotMessages.map(this::convertToDto);
            }
        }
        
        // 缓存未命中，从数据库查询
        Page<Message> hotMessages = messageRepository.findHotMessagesOrderByLikeCount(pageable);
        Page<MessageDto> result = hotMessages.map(this::convertToDto);
        
        // 如果是第一页且数据量不大，则缓存到Redis
        if (pageable.getPageNumber() == 0 && pageable.getPageSize() <= 50) {
            try {
                redisCacheService.cacheHotMessageList((List<Object>) (List<?>) result.getContent());
                log.debug("热门留言列表已缓存到Redis");
            } catch (Exception e) {
                log.warn("缓存热门留言列表失败: {}", e.getMessage());
            }
        }
        
        return result;
    }
    
    @Override
    public List<MessageDto> getAllHotMessages() {
        // 首先尝试从Redis缓存获取数据
        List<Object> cachedHotMessages = redisCacheService.getHotMessageListFromCache();
        if (cachedHotMessages != null) {
            log.info("从Redis缓存获取所有热门留言，缓存命中");
            return (List<MessageDto>) (List<?>) cachedHotMessages;
        }
        
        // 缓存未命中，从数据库查询
        List<Message> hotMessages = messageRepository.findAllHotMessages();
        List<MessageDto> result = hotMessages.stream().map(this::convertToDto).toList();
        
        // 缓存到Redis
        try {
            redisCacheService.cacheHotMessageList((List<Object>) (List<?>) result);
            log.debug("所有热门留言已缓存到Redis");
        } catch (Exception e) {
            log.warn("缓存所有热门留言失败: {}", e.getMessage());
        }
        
        return result;
    }
}
