package com.study.community.controller;

import com.study.community.annotation.LoginRequired;
import com.study.community.entity.User;
import com.study.community.event.EventProducer;
import com.study.community.service.FollowService;
import com.study.community.service.UserService;
import com.study.community.utils.CommunityConstant;
import com.study.community.utils.CommunityUtil;
import com.study.community.utils.HostHolder;
import com.study.community.vo.Event;
import com.study.community.vo.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

/**
 * @ClassName community FollowController
 * @Author 陈必强
 * @Date 2020/12/30 21:06
 * @Description 关注功能视图层
 **/
@Controller
public class FollowController implements CommunityConstant {

    @Autowired
    private FollowService followService;
    @Autowired
    private UserService userService;

    //获取当前用户
    @Autowired
    private HostHolder hostHolder;

    //使用kafka发送系统通知,注入事件生产者
    @Autowired
    private EventProducer eventProducer;

    //关注和取消关注都是异步请求，刷新局部页面
    //关注
    @PostMapping("/follow")
    @ResponseBody
    @LoginRequired   //需要登录才能访问
    public String follow(int entityType, int entityId){
        User user = hostHolder.getUser();

        //关注
        followService.follow(user.getId(),entityType,entityId);

        //关注后，触发关注事件，生产者发送系统通知
        Event event = new Event()
                .setTopic(TOPIC_FOLLOW)
                .setUserId(hostHolder.getUser().getId())
                .setEntityType(entityType)
                .setEntityId(entityId)
                //关注的对象必然是用户，所以entityId即可entityUserId
                .setEntityUserId(entityId);
        eventProducer.fireEvent(event);

        //返回JSON格式数据
        return CommunityUtil.GetJSON(0,"已关注！");
    }
    //取消关注
    @PostMapping("/unfollow")
    @ResponseBody
    @LoginRequired   //需要登录才能访问
    public String unfollow(int entityType, int entityId){
        User user = hostHolder.getUser();

        //关注
        followService.unfollow(user.getId(),entityType,entityId);

        //返回JSON格式数据
        return CommunityUtil.GetJSON(0,"已取消关注！");
    }

    //查询某个用户关注的人（用户）
    @GetMapping("/followees/{userId}")
    public String GetFollowees(@PathVariable("userId") int userId, Page page,Model model){
        User user = userService.findUserById(userId);
        if(user == null){
            throw new RuntimeException("该用户不存在！");
        }

        //返回某个用户
        model.addAttribute("user",user);

        //初始化分页数据
        page.setLimit(5);
        page.setPath("/followees/" + userId);
        page.setRows((int) followService.findFolloweeCount(userId,ENTITY_TYPE_USER));

        //获取关注的用户
        List<Map<String, Object>> followeeList = followService.findFollowees(userId,page.getOffset(),page.getLimit());
        if(followeeList != null){
            //如果有关注的用户，查看对该用户的关注状态（有取消了关注但该用户仍然显示在关注用户列表中，显示未关注状态）
            for (Map<String, Object> map : followeeList){
                User u = (User) map.get("user");
                //封装关注状态到map中
                map.put("hasFollowed",hasFollowed(u.getId()));
            }
        }
        //返回关注用户以及关注时间，关注的状态等信息(都在List<Map<String, Object>> 中)
        model.addAttribute("followees",followeeList);

        return "site/followee";
    }

    //查询某个用户的粉丝（用户）
    @GetMapping("/followers/{userId}")
    public String GetFollowers(@PathVariable("userId") int userId, Page page,Model model){
        User user = userService.findUserById(userId);
        if(user == null){
            throw new RuntimeException("该用户不存在！");
        }

        //返回某个用户
        model.addAttribute("user",user);

        //初始化分页数据
        page.setLimit(5);
        page.setPath("/followers/" + userId);
        page.setRows((int) followService.findFollowerCount(ENTITY_TYPE_USER,userId));

        //获取关注的用户
        List<Map<String, Object>> followerList = followService.findFollowers(userId,page.getOffset(),page.getLimit());
        if(followerList != null){
            //如果有关注的用户，查看对该用户的关注状态（有取消了关注但该用户仍然显示在关注用户列表中，显示未关注状态）
            for (Map<String, Object> map : followerList){
                User u = (User) map.get("user");
                //封装关注状态到map中
                map.put("hasFollowed",hasFollowed(u.getId()));
            }
        }
        //返回关注用户以及关注时间，关注的状态等信息(都在List<Map<String, Object>> 中)
        model.addAttribute("followers",followerList);

        return "site/follower";
    }

    //获取当前用户对某个用户的关注状态
    private boolean hasFollowed(int userId){
        if(hostHolder.getUser() == null){
            //如果当前未登录
            return false;
        }
        return followService.hasFollowed(hostHolder.getUser().getId(),ENTITY_TYPE_USER,userId);
    }


}
