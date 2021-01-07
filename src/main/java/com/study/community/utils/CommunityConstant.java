package com.study.community.utils;

import org.springframework.beans.factory.annotation.Value;

/**
 * @ClassName community CommunityConstant
 * @Author 陈必强
 * @Date 2020/12/10 22:19
 * @Description 常量库
 **/
public interface CommunityConstant {

    /**
     * 系统用户id
     */
    int SYSTEM_USER_ID = 1;

    //账户激活情况
    /**
     * 激活成功
     */
    int AVTIVATION_SUCCESS = 0;
    /**
     * 重复激活
     */
    int AVTIVATION_REPEAT = 1;
    /**
     * 激活失败
     */
    int AVTIVATION_FAILURE = 2;

    /**
     * 默认的登录凭证有效时长(秒)  默认12小时
     */
    int DEFAULT_EXPIRED_SECONDS = 3600*12;

    /**
     * 记住我，勾选后，凭证有效时长（秒）  记7天
     */
    int REMEMBER_EXPIRED_SECONDS = 3600*24*7;

    /**
     * 帖子所属的类别（帖子的评论，评论的评论（回复），课程的评论等等）
     * 实体类型：帖子
     */
    int ENTITY_TYPE_DISCUSS = 1;

    /**
     * 实体类型：评论
     */
    int ENTITY_TYPE_COMMENT = 2;

    /**
     * 实体类型：用户
     */
    int ENTITY_TYPE_USER = 3;

    /**
     * 事件的topic:评论
     */
    String TOPIC_COMMENT = "comment";

    /**
     * 事件的topic:点赞
     */
    String TOPIC_LIKE = "like";

    /**
     * 事件的topic:关注
     */
    String TOPIC_FOLLOW = "follow";

    /**
     * 事件的topic：发帖
     */
    String TOPIC_PUBLISH = "publish";

}
