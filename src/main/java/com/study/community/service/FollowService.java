package com.study.community.service;

import java.util.List;
import java.util.Map;

/**
 * @ClassName community FollowService
 * @Author 陈必强
 * @Date 2020/12/30 20:54
 * @Description 关注功能业务
 **/
public interface FollowService {

    //某个用户关注了某个实体（用户，帖子等等）
    void follow(int userId,int entityType,int entityId);

    //某个用户取消了对某个实体的关注
    void unfollow(int userId,int entityType,int entityId);

    //查询某用户关注的某一实体数量
    long findFolloweeCount(int userId, int entityType);

    //查询某一个实体的粉丝数量
    long findFollowerCount(int entityType, int entityId);

    //查询当前用户是否已关注某实体
    boolean hasFollowed(int userId, int entityType, int entityId);

    //查询某个用户关注的用户（列表）分页  --  集合存储用户信息，关注的时间
    List<Map<String,Object>> findFollowees(int userId, int offset, int limit);

    //查询关注某个用户的用户（粉丝列表）分页
    List<Map<String,Object>> findFollowers(int userId, int offset, int limit);

}
