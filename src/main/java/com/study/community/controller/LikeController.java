package com.study.community.controller;

import com.study.community.annotation.LoginRequired;
import com.study.community.entity.User;
import com.study.community.event.EventProducer;
import com.study.community.service.LikeService;
import com.study.community.utils.CommunityConstant;
import com.study.community.utils.CommunityUtil;
import com.study.community.utils.HostHolder;
import com.study.community.utils.RedisKeyUtil;
import com.study.community.vo.Event;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName community LikeController
 * @Author 陈必强
 * @Date 2020/12/29 21:27
 * @Description 点赞
 **/
@Controller
public class LikeController implements CommunityConstant {

    @Autowired
    private LikeService likeService;

    //当前用户才能进行点赞操作
    @Autowired
    private HostHolder hostHolder;

    //使用kafka发送系统通知,注入事件生产者
    @Autowired
    private EventProducer eventProducer;

    //点赞时，帖子score发生变化，将该帖子加到定时计算score的set中
    //set缓存在redis中
    @Autowired
    private RedisTemplate redisTemplate;

    //异步请求（点赞）  POST请求
    @PostMapping("/like")
    @ResponseBody
    @LoginRequired   //需要登录才能访问，不然返回登录页面
    public String like(int entityType,int entityId,int entityUserId, int discussPostId){
        User user = hostHolder.getUser();

        //点赞
        likeService.like(user.getId(),entityType,entityId,entityUserId);
        //统计点赞数量
        long likeCount = likeService.findEntityLikeCount(entityType, entityId);
        //点赞状态
        int likeStatus = likeService.findEntityLikeStatus(user.getId(),entityType,entityId);

        //封装返回信息
        Map<String,Object> map = new HashMap<>();
        map.put("likeCount",likeCount);
        map.put("likeStatus",likeStatus);

        //点赞后【取消点赞不触发】，触发点赞事件，生产者发送系统通知
        if(likeStatus == 1){
            //点赞时（排除取消点赞的情况）
            Event event = new Event()
                    .setTopic(TOPIC_LIKE)
                    .setUserId(hostHolder.getUser().getId())
                    .setEntityType(entityType)
                    .setEntityId(entityId)
                    .setEntityUserId(entityUserId)
                    //其他数据（点击查看时可以查看被点赞帖子的详情）
                    .setData("discussPostId",discussPostId);
            eventProducer.fireEvent(event);
        }

        //只有对帖子点赞才会影响帖子的score
        if(entityType == ENTITY_TYPE_DISCUSS){
            //添加到redis的set中
            String redisKey = RedisKeyUtil.GetDiscussScoreKey();
            redisTemplate.opsForSet().add(redisKey,discussPostId);
        }

        //返回JSON格式数据
        return CommunityUtil.GetJSON(0,null,map);
    }


}
