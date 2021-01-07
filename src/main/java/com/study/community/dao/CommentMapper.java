package com.study.community.dao;

import com.study.community.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @ClassName community CommentMapper
 * @Author 陈必强
 * @Date 2020/12/21 20:55
 * @Description 评论mapper
 **/
@Mapper
public interface CommentMapper {

    //根据id查询评论
    Comment selectCommentById(int id);

    //根据实体来分页查询评论（帖子的评论，课程的评论）--[status = 0]
    List<Comment> selectCommentsByEntity(int entityType,int entityId,int offset,int limit);

    //查询数据的条目数
    int selectCountByEntity(int entityType,int entityId);

    int insertComment(Comment comment);

}
