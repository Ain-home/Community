package com.study.community.service;

import com.study.community.entity.DiscussPost;
import org.springframework.data.domain.Page;

/**
 * @ClassName community ElasticsearchService
 * @Author 陈必强
 * @Date 2021/1/6 21:44
 * @Description elasticsearch 搜索业务层
 **/
public interface ElasticsearchService {

    //向es服务器提交新发布的帖子
    void saveDiscussPost(DiscussPost discussPost);

    //删除es服务器中的某id的帖子
    void deleteDiscussPost(int id);

    //搜索方法(分页查询，关键字，第几页，每页显示多少条数据)
    // current>=0
    Page<DiscussPost> searchDiscussPost(String keyWord,int current,int limit);

}
