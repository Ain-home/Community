package com.study.community.controller;

import com.study.community.annotation.LoginRequired;
import com.study.community.entity.User;
import com.study.community.service.LikeService;
import com.study.community.utils.CommunityUtil;
import com.study.community.utils.HostHolder;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
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
public class LikeController {

    @Autowired
    private LikeService likeService;

    //当前用户才能进行点赞操作
    @Autowired
    private HostHolder hostHolder;

    //异步请求（点赞）  POST请求
    @PostMapping("/like")
    @ResponseBody
    @LoginRequired   //需要登录才能访问，不然返回登录页面
    public String like(int entityType,int entityId){
        User user = hostHolder.getUser();

        //点赞
        likeService.like(user.getId(),entityType,entityId);
        //统计点赞数量
        long likeCount = likeService.findEntityLikeCount(entityType, entityId);
        //点赞状态
        int likeStatus = likeService.findEntityLikeStatus(user.getId(),entityType,entityId);

        //封装返回信息
        Map<String,Object> map = new HashMap<>();
        map.put("likeCount",likeCount);
        map.put("likeStatus",likeStatus);

        //返回JSON格式数据
        return CommunityUtil.GetJSON(0,null,map);
    }


}
