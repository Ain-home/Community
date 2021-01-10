package com.study.community;

import com.study.community.entity.DiscussPost;
import com.study.community.service.DiscussPostService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

/**
 * @ClassName community CaffeineTest
 * @Author 陈必强
 * @Date 2021/1/10 22:54
 * @Description caffeine 本地缓存工具 测试类
 **/
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class CaffeineTest {

    @Autowired
    private DiscussPostService discussPostService;

    //增加数据，测试缓存【压力测试】
    @Test
    public void initDataForTest(){
        for (int i = 0; i < 300000; i++) {
            DiscussPost discussPost = new DiscussPost();
            discussPost.setUserId(111);
            discussPost.setTitle("互联网求职暖春计划");
            discussPost.setContent("今年的就业形势，确实不容乐观");
            discussPost.setCreateTime(new Date());
            discussPost.setScore(Math.random()*2000);
            discussPostService.addDiscussPost(discussPost);
        }
    }

}
