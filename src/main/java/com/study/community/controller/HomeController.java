package com.study.community.controller;

import com.study.community.entity.DiscussPost;
import com.study.community.service.DiscussPostService;
import com.study.community.service.LikeService;
import com.study.community.service.UserService;
import com.study.community.utils.CommunityConstant;
import com.study.community.utils.CommunityUtil;
import com.study.community.vo.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName community HomeController
 * @Author 陈必强
 * @Date 2020/12/6 20:37
 * @Description 主页
 **/
@Controller
public class HomeController implements CommunityConstant {

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private UserService userService;

    //获取帖子点赞数
    @Autowired
    private LikeService likeService;

    //orderMode表示排序方式 等于0表示默认的按照最新来显示  等于1表示按照热度score来排序
    // 等价于 @RequestMapping(path = "/",method = RequestMethod.GET)
    @GetMapping("/index")
    public String index(Model model,Page page,
                        @RequestParam(name = "orderMode",defaultValue = "0") int orderMode){
        //在此方法调用前，SpringMVC会自动实例化方法参数中的model和page，并将page注入到model中
        //所以在thymeleaf中可以直接访问page对象中的数据域
        //分页信息Page
        page.setRows(discussPostService.findDiscussPostRows(0));
        page.setPath("/index?orderMode=" + orderMode);

        //获取所有帖子（但是用户信息只有一个用户id）
        List<DiscussPost> list = discussPostService.findDiscussPosts(0,page.getOffset(),page.getLimit(),orderMode);
        //将用户信息与帖子信息整合（可以新建一个VO来存储，也可以使用map）
        List<Map<String,Object>> discussPosts = new ArrayList<>();
        if(list != null){
            for (DiscussPost post:list){
                Map<String,Object> map = new HashMap<>();
                map.put("discussPost",post);
                map.put("user",userService.findUserById(post.getUserId()));
                //该帖子的点赞数
                long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_DISCUSS,post.getId());
                map.put("likeCount",likeCount);

                discussPosts.add(map);
            }
        }

        model.addAttribute("discussPosts",discussPosts);
        model.addAttribute("orderMode",orderMode);

        return "index";
    }

    //获取到错误页面
    @GetMapping("/error")
    public String GetErrorPage(){
        return "error/500";
    }

    //权限不够时显示的页面
    @GetMapping("/denied")
    public String GetDeniedPage(){
        return "/error/404";
    }


}
