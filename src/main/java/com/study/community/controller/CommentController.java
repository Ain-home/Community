package com.study.community.controller;

import com.study.community.entity.Comment;
import com.study.community.entity.DiscussPost;
import com.study.community.event.EventProducer;
import com.study.community.service.CommentService;
import com.study.community.service.DiscussPostService;
import com.study.community.utils.CommunityConstant;
import com.study.community.utils.HostHolder;
import com.study.community.utils.RedisKeyUtil;
import com.study.community.vo.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Date;

/**
 * @ClassName community CommentController
 * @Author 陈必强
 * @Date 2020/12/23 19:45
 * @Description 评论
 **/
@Controller
@RequestMapping("/comment")
public class CommentController implements CommunityConstant {

    @Autowired
    private CommentService commentService;
    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private HostHolder hostHolder;

    //使用kafka发送系统通知,注入事件生产者
    @Autowired
    private EventProducer eventProducer;

    //评论时，帖子score发生变化，将该帖子加到定时计算score的set中
    //set缓存在redis中
    @Autowired
    private RedisTemplate redisTemplate;

    //增加评论（页面上提交评论的内容，隐含传入实体entity的类型和id[帖子还是评论],还有targetId回复用户的id，将这些信息封装到Comment实体中）
    @PostMapping("/add/{discussPostId}")
    public String addComment(@PathVariable("discussPostId") int discussPostId, Comment comment){
        comment.setUserId(hostHolder.getUser().getId());
        //默认评论状态
        comment.setStatus(0);
        comment.setCreateTime(new Date());

        commentService.addComment(comment);

        //添加评论后，触发评论事件，生产者发送系统通知
        Event event = new Event()
                .setTopic(TOPIC_COMMENT)
                //登录用户是事件的触发者
                .setUserId(hostHolder.getUser().getId())
                .setEntityType(comment.getEntityType())
                .setEntityId(comment.getId())
                //其他数据（点击查看评论详情，需要给予帖子id查看帖子详情看评论）
                .setData("discussPostId",discussPostId);
        //评论的目标：可能是帖子，也可能是评论【查看】
        //所以entityUserId需要分别从帖子表或者评论表查询才能得知【区分帖子和评论，查找其作者】
        if(comment.getEntityType() == ENTITY_TYPE_DISCUSS){
            //帖子的评论
            DiscussPost target = discussPostService.findDiscussPostById(comment.getEntityId());
            //设置帖子的作者id
            event.setEntityUserId(target.getUserId());
        }else if(comment.getEntityType() == ENTITY_TYPE_COMMENT){
            //评论的回复评论
            Comment target = commentService.findCommentById(comment.getEntityId());
            //设置评论的作者id
            event.setEntityUserId(target.getUserId());
        }
        //发布系统通知【消息】
        eventProducer.fireEvent(event);
        //发送通知后，当前线程立刻调用后续的响应即可（后续消息的发布，通知作者则由消息队列kafka去处理）

        //【只有】评论帖子后，帖子实体的数据发生变化（评论数），需要更新到es服务器中
        // 触发事件,自动在消费中给es更新帖子数据
        if(comment.getEntityType() == ENTITY_TYPE_DISCUSS){
            event =new Event()
                    .setTopic(TOPIC_PUBLISH)
                    .setUserId(hostHolder.getUser().getId())
                    .setEntityType(ENTITY_TYPE_DISCUSS)
                    .setEntityId(discussPostId);
            //EntityUserId用不上，可以省略
            eventProducer.fireEvent(event);

            //只有评论帖子时，才会影响帖子score
            String redisKey = RedisKeyUtil.GetDiscussScoreKey();
            redisTemplate.opsForSet().add(redisKey,discussPostId);
        }

        //重定向到帖子详情页面
        return "redirect:/discuss/detail/"+discussPostId;
    }

}
