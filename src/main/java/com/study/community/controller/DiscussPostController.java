package com.study.community.controller;

import com.study.community.entity.Comment;
import com.study.community.entity.DiscussPost;
import com.study.community.entity.User;
import com.study.community.event.EventProducer;
import com.study.community.service.CommentService;
import com.study.community.service.DiscussPostService;
import com.study.community.service.LikeService;
import com.study.community.service.UserService;
import com.study.community.utils.CommunityConstant;
import com.study.community.utils.CommunityUtil;
import com.study.community.utils.HostHolder;
import com.study.community.vo.Event;
import com.study.community.vo.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * @ClassName community DiscussPostController
 * @Author 陈必强
 * @Date 2020/12/19 16:52
 * @Description 帖子
 **/
@Controller
@RequestMapping("/discuss")
public class DiscussPostController implements CommunityConstant {

    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private UserService userService;
    @Autowired
    private CommentService commentService;
    //获取点赞数量
    @Autowired
    private LikeService likeService;

    //处理发帖事件（kafka消息队列）
    @Autowired
    private EventProducer eventProducer;

    @Autowired
    private HostHolder hostHolder;

    //发布帖子
    @PostMapping("/add")   //传入的信息只有标题title和内容content，其他都是初始化参数
    @ResponseBody   //返回的是JSON格式的字符串,而不是指定的页面
    public String addDiscussPost(String title, String content){
        //判断用户是否登录（只有登录了才能发布帖子）
        User user = hostHolder.getUser();
        if(user == null){
            //如果用户未登录
            return CommunityUtil.GetJSON(403,"请先登录！");
        }
        //用户已经登录了
        //根据传入的参数，构造并初始化帖子对象
        DiscussPost discussPost = new DiscussPost();
        discussPost.setUserId(user.getId());
        discussPost.setTitle(title);
        discussPost.setContent(content);
        //type,status,commentCount,score默认为0
        discussPost.setCreateTime(new Date());
        discussPostService.addDiscussPost(discussPost);

        //发布帖子后，触发发帖事件（把新发布的帖子添加到es中）
        Event event =new Event()
                .setTopic(TOPIC_PUBLISH)
                .setUserId(user.getId())
                .setEntityType(ENTITY_TYPE_DISCUSS)
                .setEntityId(discussPost.getId());
        //EntityUserId用不上，可以省略
        eventProducer.fireEvent(event);

        //报错情况额外处理（全局错误处理）
        return CommunityUtil.GetJSON(0,"发布成功！");
    }

    //查看帖子详情（根据id
    @GetMapping("/detail/{discussPostId}")
    public String toDiscussPost(@PathVariable("discussPostId") int discussPostId, Model model, Page page){
        //获取帖子信息
        DiscussPost discussPost = discussPostService.findDiscussPostById(discussPostId);
        model.addAttribute("discussPost",discussPost);
        //获取帖子作者的信息
        User user = userService.findUserById(discussPost.getUserId());
        model.addAttribute("user",user);
        //获取帖子点赞相关信息（数量和点赞状态）
        long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_DISCUSS,discussPostId);
        model.addAttribute("likeCount",likeCount);
        //如果用户未登录，则点赞状态为未点赞即返回status 0
        int likeStatus = hostHolder.getUser() == null ? 0 : likeService.findEntityLikeStatus(hostHolder.getUser().getId(),ENTITY_TYPE_DISCUSS,discussPostId);
        model.addAttribute("likeStatus",likeStatus);

        //评论：给帖子的评论
        //回复：给评论的评论
        page.setLimit(5);
        page.setPath("/discuss/detail/"+discussPostId);
        page.setRows(discussPost.getCommentCount());
        //获取帖子的评论（分页显示）
        List<Comment> commentList = commentService.findCommentsByEntity(ENTITY_TYPE_DISCUSS,discussPost.getId(),page.getOffset(),page.getLimit());

        //用Map对需要展现的数据进行统一的封装（评论者的头像，username等等）
        //评论的VO列表
        List<Map<String, Object>> commentVoList = new ArrayList<>();
        if(commentList != null){
            for (Comment comment:commentList){
                Map<String,Object> commentVo = new HashMap<>();
                //一条评论
                commentVo.put("comment",comment);
                //评论的作者
                commentVo.put("user",userService.findUserById(comment.getUserId()));
                //评论的点赞数量
                likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_COMMENT,comment.getId());
                commentVo.put("likeCount",likeCount);
                //如果用户未登录，则点赞状态为未点赞即返回status 0
                likeStatus = hostHolder.getUser() == null ? 0 : likeService.findEntityLikeStatus(hostHolder.getUser().getId(),ENTITY_TYPE_COMMENT,comment.getId());
                commentVo.put("likeStatus",likeStatus);

                //查询该评论的回复列表(不分页，有多少，就显示多少条)
                List<Comment> replyList = commentService.findCommentsByEntity(ENTITY_TYPE_COMMENT,comment.getId(),0,Integer.MAX_VALUE);
                //回复的VO列表
                List<Map<String, Object>> replyVoList = new ArrayList<>();
                if(replyList != null){
                    for (Comment reply:replyList){
                        Map<String,Object> replyVo = new HashMap<>();
                        //一个回复
                        replyVo.put("reply",reply);
                        //回复的作者
                        replyVo.put("user",userService.findUserById(reply.getUserId()));
                        //回复的目标(哪个作者)
                        User target = reply.getTargetId() == 0 ? null : userService.findUserById(reply.getTargetId());
                        replyVo.put("target",target);
                        //回复的点赞数量
                        likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_COMMENT,reply.getId());
                        replyVo.put("likeCount",likeCount);
                        //如果用户未登录，则点赞状态为未点赞即返回status 0
                        likeStatus = hostHolder.getUser() == null ? 0 : likeService.findEntityLikeStatus(hostHolder.getUser().getId(),ENTITY_TYPE_COMMENT,reply.getId());
                        replyVo.put("likeStatus",likeStatus);

                        replyVoList.add(replyVo);
                    }
                }
                commentVo.put("replys",replyVoList);
                //回复的数量
                int replyCount = commentService.findCommentCount(ENTITY_TYPE_COMMENT,comment.getId());
                commentVo.put("replyCount",replyCount);

                commentVoList.add(commentVo);
            }
        }
        model.addAttribute("comments",commentVoList);

        return "site/discuss-detail";
    }

}
