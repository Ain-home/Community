package com.study.community;

import com.study.community.dao.DiscussPostMapper;
import com.study.community.dao.UserMapper;
import com.study.community.entity.DiscussPost;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * @ClassName community MapperTest
 * @Author 陈必强
 * @Date 2020/12/6 18:37
 * @Description 测试mapper
 **/
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MapperTest {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Test
    public void testSelectDiscussPost(){
        List<DiscussPost> discussPosts = discussPostMapper.selectDiscussPosts(0,0,10);
        for (DiscussPost discussPost:discussPosts){
            System.out.println(discussPost);
        }

        int count = discussPostMapper.selectDiscussPostRows(0);
        System.out.println(count);
    }

}
