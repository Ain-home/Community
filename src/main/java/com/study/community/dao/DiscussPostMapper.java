package com.study.community.dao;

import com.study.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @ClassName community DiscussPostMapper
 * @Author 陈必强
 * @Date 2020/12/6 15:14
 * @Description 帖子mapper
 **/
@Mapper
public interface DiscussPostMapper {

    //根据用户id获取帖子（当id为0是获取所有用户的帖子，动态sql），offset分页起始行，limit该页帖子条数
    List<DiscussPost> selectDiscussPosts(int userId,int offset,int limit);

    //查找表中帖子的条数
    //@Param("userId")注解用于给参数取别名;如果方法只有一个参数，并且在动态sql <if> 中使用，则必须用别名
    int selectDiscussPostRows(@Param("userId") int userId);

    //增加帖子
    int insertDiscussPost(DiscussPost discussPost);

    //根据帖子id查询帖子的详情
    DiscussPost selectDiscussPostById(int id);

    //更新帖子的评论数(随着添加评论一起进行)
    int updateCommentCount(int id,int commentCount);


}
