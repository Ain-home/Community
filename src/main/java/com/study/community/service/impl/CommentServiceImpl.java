package com.study.community.service.impl;

import com.study.community.dao.CommentMapper;
import com.study.community.entity.Comment;
import com.study.community.service.CommentService;
import com.study.community.service.DiscussPostService;
import com.study.community.utils.CommunityConstant;
import com.study.community.utils.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

/**
 * @ClassName community CommentServiceImpl
 * @Author 陈必强
 * @Date 2020/12/21 21:07
 * @Description TODO
 **/
@Service
public class CommentServiceImpl implements CommentService, CommunityConstant {

    @Autowired
    private CommentMapper commentMapper;

    //敏感词过滤
    @Autowired
    private SensitiveFilter sensitiveFilter;

    //增加评论时更新帖子表中的评论总数
    @Autowired
    private DiscussPostService discussPostService;

    @Override
    public Comment findCommentById(int id) {
        return commentMapper.selectCommentById(id);
    }

    @Override
    public List<Comment> findCommentsByEntity(int entityType, int entityId, int offset, int limit) {
        return commentMapper.selectCommentsByEntity(entityType,entityId,offset,limit);
    }

    @Override
    public int findCommentCount(int entityType, int entityId) {
        return commentMapper.selectCountByEntity(entityType,entityId);
    }

    //此方法包括增加评论和更新帖子评论数（两次数据库操作，要进行事务管理）
    //设置隔离级别和传播机制
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    @Override
    public int addComment(Comment comment) {
        if(comment == null){
            throw new IllegalArgumentException("参数不能为空！");
        }

        //转义和敏感词过滤
        comment.setContent(HtmlUtils.htmlEscape(comment.getContent()));
        comment.setContent(sensitiveFilter.filter(comment.getContent()));
        //添加评论
        int rows = commentMapper.insertComment(comment);

        //更新帖子评论数量
        if(comment.getEntityType() == ENTITY_TYPE_DISCUSS){
            //如果该评论是帖子的评论
            //获取评论表中某ID帖子的评论数，更新到帖子表中
            int count = commentMapper.selectCountByEntity(comment.getEntityType(),comment.getEntityId());
            discussPostService.updateCommentCount(comment.getEntityId(),count);
        }

        return rows;
    }

}
