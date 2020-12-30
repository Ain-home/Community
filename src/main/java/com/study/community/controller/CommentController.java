package com.study.community.controller;

import com.study.community.entity.Comment;
import com.study.community.service.CommentService;
import com.study.community.utils.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
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
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private HostHolder hostHolder;

    //增加评论（页面上提交评论的内容，隐含传入实体entity的类型和id[帖子还是评论],还有targetId回复用户的id，将这些信息封装到Comment实体中）
    @PostMapping("/add/{discussPostId}")
    public String addComment(@PathVariable("discussPostId") int discussPostId, Comment comment){
        comment.setUserId(hostHolder.getUser().getId());
        //默认评论状态
        comment.setStatus(0);
        comment.setCreateTime(new Date());

        commentService.addComment(comment);
        //重定向到帖子详情页面
        return "redirect:/discuss/detail/"+discussPostId;
    }

}
