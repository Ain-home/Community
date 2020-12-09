package com.study.community.service;

import com.study.community.entity.DiscussPost;

import java.util.List;

/**
 * @ClassName community DiscussPostService
 * @Author 陈必强
 * @Date 2020/12/6 20:22
 * @Description TODO
 **/
public interface DiscussPostService {

    public List<DiscussPost> findDiscussPosts(int userId, int offset, int limit);

    public int findDiscussPostRows(int userId);

}
