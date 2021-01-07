package com.study.community.service;

import com.study.community.entity.Message;

import java.util.List;

/**
 * @ClassName community MessageService
 * @Author 陈必强
 * @Date 2020/12/24 20:10
 * @Description 私信和系统通知
 **/
public interface MessageService {

    List<Message> findConversations(int userId,int offset,int limit);

    int findConversationCount(int userId);

    List<Message> findLetters(String conversationId,int offset,int limit);

    int findLetterCount(String conversationId);

    int findLetterUnreadCount(int userId,String conversationId);

    int addMessage(Message message);

    int readMessages(List<Integer> ids);

    //获取某一主题下最新的通知
    Message findLatestNotice(int userId,String topic);

    //获取某一主题通知的数量
    int findNoticeCount(int userId,String topic);

    //获取某一主题未读通知的数量
    int findNoticeUnreadCount(int userId,String topic);

    //获取某一主题的所有通知（分页显示）
    List<Message> findNotices(int userId,String topic,int offset,int limit);

}
