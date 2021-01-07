package com.study.community.service;

/**
 * @ClassName community LikeService
 * @Author 陈必强
 * @Date 2020/12/29 21:11
 * @Description 点赞量业务（redis）
 **/
public interface LikeService {

    //点赞
    void like(int userId,int entityType,int entityId,int entityUserId);

    //查询某实体（帖子、评论）获赞数量
    long findEntityLikeCount(int entityType,int entityId);

    //查询某人对某实体的点赞状态(使用整型数据能够代表更多的：比如踩啊之类的状态)
    int findEntityLikeStatus(int userId,int entityType,int entityId);

    //查询某个用户获得的总赞数（帖子加评论）
    int findUserLikeCount(int userId);


}
