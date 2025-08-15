package com.example.messageboardbackend.service;

import com.example.messageboardbackend.dto.MessageDto;
import com.example.messageboardbackend.model.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

/**
 * 留言服务接口
 * 
 * @author Message Board Team
 * @version 1.0.0
 */
public interface MessageService {
    
    /**
     * 获取留言列表（分页）
     * 
     * @param pageable 分页参数
     * @return 留言分页结果
     */
    Page<MessageDto> getMessages(Pageable pageable);
    
    /**
     * 根据ID获取留言
     * 
     * @param id 留言ID
     * @return 留言信息
     */
    MessageDto getMessageById(Long id);
    
    /**
     * 创建留言
     * 
     * @param messageDto 留言信息
     * @return 创建后的留言
     */
    MessageDto createMessage(MessageDto messageDto);
    
    /**
     * 创建留言（根据用户名）
     * 
     * @param messageDto 留言信息
     * @param username 用户名
     * @return 创建后的留言
     */
    MessageDto createMessage(MessageDto messageDto, String username);
    
    /**
     * 更新留言
     * 
     * @param messageDto 留言信息
     * @return 更新后的留言
     */
    MessageDto updateMessage(MessageDto messageDto);
    
    /**
     * 删除留言
     * 
     * @param id 留言ID
     */
    void deleteMessage(Long id);
    
    /**
     * 根据用户ID获取留言列表
     * 
     * @param userId 用户ID
     * @param pageable 分页参数
     * @return 留言分页结果
     */
    Page<MessageDto> getMessagesByUserId(Long userId, Pageable pageable);
    
    /**
     * 根据内容搜索留言
     * 
     * @param content 搜索内容
     * @param pageable 分页参数
     * @return 留言分页结果
     */
    Page<MessageDto> searchMessagesByContent(String content, Pageable pageable);
    
    /**
     * 获取热门留言列表（分页）
     * 
     * @param pageable 分页参数
     * @return 热门留言分页结果
     */
    Page<MessageDto> getHotMessages(Pageable pageable);
    
    /**
     * 获取所有热门留言
     * 
     * @return 热门留言列表
     */
    List<MessageDto> getAllHotMessages();
}
