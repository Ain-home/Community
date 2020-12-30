package com.study.community.service;

import com.study.community.entity.Message;

import java.util.List;

/**
 * @ClassName community MessageService
 * @Author 陈必强
 * @Date 2020/12/24 20:10
 * @Description 私信
 **/
public interface MessageService {

    List<Message> findConversations(int userId,int offset,int limit);

    int findConversationCount(int userId);

    List<Message> findLetters(String conversationId,int offset,int limit);

    int findLetterCount(String conversationId);

    int findLetterUnreadCount(int userId,String conversationId);

    int addMessage(Message message);

    int readMessages(List<Integer> ids);

}
