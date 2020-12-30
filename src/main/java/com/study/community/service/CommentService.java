package com.study.community.service;

import com.study.community.entity.Comment;

import java.util.List;

/**
 * @ClassName community CommentService
 * @Author 陈必强
 * @Date 2020/12/21 21:07
 * @Description TODO
 **/
public interface CommentService {

    //查询某一页评论
    List<Comment> findCommentsByEntity(int entityType,int entityId,int offset,int limit);

    //查询评论条数
    int findCommentCount(int entityType,int entityId);

    //增加评论
    int addComment(Comment comment);

}
