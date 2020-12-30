package com.study.community.dao;

import com.study.community.entity.Message;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @ClassName community MessageMapper
 * @Author 陈必强
 * @Date 2020/12/24 19:25
 * @Description TODO
 **/
@Mapper
public interface MessageMapper {

    //查询当前用户的会话列表(分页),针对每个会话值返回一条最新的私信消息
    List<Message> selectConversations(int userId,int offset,int limit);

    //查询当前用户的会话数量
    int selectConversationCount(int userId);

    //查询某个会话所包含的消息列表(分页展示)
    List<Message> selectLetters(String conversationId,int offset,int limit);

    //查询某个会话所包含的消息数量
    int selectLetterCount(String conversationId);

    //查询未读的消息数量(某个人[to_id]，或某个会话)--动态sql
    int selectLetterUnreadCount(int userId,String conversationId);

    //新增消息
    int insertMessage(Message message);

    //更改消息状态（设置为已读）
    int updateStatus(List<Integer> ids,int status);

}
