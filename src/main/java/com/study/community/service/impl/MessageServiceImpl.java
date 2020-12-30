package com.study.community.service.impl;

import com.study.community.dao.MessageMapper;
import com.study.community.entity.Message;
import com.study.community.service.MessageService;
import com.study.community.utils.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

/**
 * @ClassName community MessageServiceImpl
 * @Author 陈必强
 * @Date 2020/12/24 20:15
 * @Description 私信
 **/
@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageMapper messageMapper;

    //过滤敏感词
    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Override
    public List<Message> findConversations(int userId, int offset, int limit) {
        return messageMapper.selectConversations(userId, offset, limit);
    }

    @Override
    public int findConversationCount(int userId) {
        return messageMapper.selectConversationCount(userId);
    }

    @Override
    public List<Message> findLetters(String conversationId, int offset, int limit) {
        return messageMapper.selectLetters(conversationId, offset, limit);
    }

    @Override
    public int findLetterCount(String conversationId) {
        return messageMapper.selectLetterCount(conversationId);
    }

    @Override
    public int findLetterUnreadCount(int userId, String conversationId) {
        return messageMapper.selectLetterUnreadCount(userId, conversationId);
    }

    @Override
    public int addMessage(Message message) {
        //过滤html标签
        message.setContent(HtmlUtils.htmlEscape(message.getContent()));
        //过滤敏感词
        message.setContent(sensitiveFilter.filter(message.getContent()));
        return messageMapper.insertMessage(message);
    }

    @Override
    public int readMessages(List<Integer> ids) {
        //status=1表示已读
        return messageMapper.updateStatus(ids,1);
    }

}
