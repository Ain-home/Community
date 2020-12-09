package com.study.community.controller;

import com.study.community.entity.DiscussPost;
import com.study.community.service.DiscussPostService;
import com.study.community.service.UserService;
import com.study.community.vo.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import sun.text.normalizer.NormalizerBase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName community HomeController
 * @Author 陈必强
 * @Date 2020/12/6 20:37
 * @Description TODO
 **/
@Controller
public class HomeController {

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private UserService userService;

    // 等价于 @RequestMapping(path = "/",method = RequestMethod.GET)
    @GetMapping("/index")
    public String index(Model model,Page page){
        //在此方法调用前，SpringMVC会自动实例化方法参数中的model和page，并将page注入到model中
        //所以在thymeleaf中可以直接访问page对象中的数据域
        //分页信息Page
        page.setRows(discussPostService.findDiscussPostRows(0));
        page.setPath("/index");

        //获取所有帖子（但是用户信息只有一个用户id）
        List<DiscussPost> list = discussPostService.findDiscussPosts(0,page.getOffset(),page.getLimit());
        //将用户信息与帖子信息整合（可以新建一个VO来存储，也可以使用map）
        List<Map<String,Object>> discussPosts = new ArrayList<>();
        if(list != null){
            for (DiscussPost post:list){
                Map<String,Object> map = new HashMap<>();
                map.put("discussPost",post);
                map.put("user",userService.findUserById(post.getUserId()));
                discussPosts.add(map);
            }
        }

        model.addAttribute("discussPosts",discussPosts);
        return "index";
    }

}