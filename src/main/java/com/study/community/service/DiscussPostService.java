package com.study.community.service;

import com.study.community.entity.DiscussPost;

import java.util.List;

/**
 * @ClassName community DiscussPostService
 * @Author 陈必强
 * @Date 2020/12/6 20:22
 * @Description TODO
 **/
public interface DiscussPostService {

    List<DiscussPost> findDiscussPosts(int userId, int offset, int limit,int orderMode);

    int findDiscussPostRows(int userId);

    int addDiscussPost(DiscussPost discussPost);

    DiscussPost findDiscussPostById(int id);

    //增加评论时更新帖子的评论数
    int updateCommentCount(int id,int commentCount);

    //修改帖子类型 type  置顶
    int updateType(int id,int type);

    //修改帖子状态  status  加精 删除
    int updateStatus(int id,int status);

    //更新帖子的score
    int updateScore(int id,double score);

}
