package com.study.community.service.impl;

import com.study.community.dao.DiscussPostMapper;
import com.study.community.entity.DiscussPost;
import com.study.community.service.DiscussPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @ClassName community DiscussPostServiceImpl
 * @Author 陈必强
 * @Date 2020/12/6 20:29
 * @Description TODO
 **/
@Service
public class DiscussPostServiceImpl implements DiscussPostService {

    @Autowired
    private DiscussPostMapper discussPostMapper;

    public List<DiscussPost> findDiscussPosts(int userId, int offset, int limit){
        return discussPostMapper.selectDiscussPosts(userId,offset,limit);
    }

    public int findDiscussPostRows(int userId){
        return discussPostMapper.selectDiscussPostRows(userId);
    }

}
