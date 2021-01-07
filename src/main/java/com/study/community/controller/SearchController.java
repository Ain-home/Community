package com.study.community.controller;

import com.study.community.entity.DiscussPost;
import com.study.community.service.ElasticsearchService;
import com.study.community.service.LikeService;
import com.study.community.service.UserService;
import com.study.community.utils.CommunityConstant;
import com.study.community.vo.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName community SearchController
 * @Author 陈必强
 * @Date 2021/1/6 23:02
 * @Description 搜索功能
 **/
@Controller
public class SearchController implements CommunityConstant {

    //展示搜索结果帖子的作者
    @Autowired
    private UserService userService;

    //展示搜索结果帖子的点赞信息
    @Autowired
    private LikeService likeService;

    @Autowired
    private ElasticsearchService elasticsearchService;

    // search?keyword=xxx
    @GetMapping("/search")
    public String search(String keyword, Page page, Model model){
        //搜索帖子
        org.springframework.data.domain.Page<DiscussPost> searchResult = elasticsearchService.searchDiscussPost(keyword,page.getCurrent()-1,page.getLimit());

        //聚合返回数据
        List<Map<String,Object>> discussPosts = new ArrayList<>();
        if(searchResult != null){
            for (DiscussPost discussPost:searchResult){
                Map<String,Object> map = new HashMap<>();
                //帖子
                map.put("discussPost",discussPost);
                //作者
                map.put("user",userService.findUserById(discussPost.getUserId()));
                //点赞数量
                map.put("likeCount",likeService.findEntityLikeCount(ENTITY_TYPE_DISCUSS,discussPost.getId()));

                discussPosts.add(map);
            }
        }
        model.addAttribute("discussPosts",discussPosts);
        //在页面搜索框显示搜索关键字
        model.addAttribute("keyword",keyword);

        //分页信息
        page.setPath("/search?keyword="+keyword);
        page.setRows(searchResult == null ? 0 : (int) searchResult.getTotalElements());

        return "site/search";
    }

}
