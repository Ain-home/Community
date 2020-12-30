package com.study.community;

import com.study.community.dao.DiscussPostMapper;
import com.study.community.dao.LoginTicketMapper;
import com.study.community.dao.MessageMapper;
import com.study.community.dao.UserMapper;
import com.study.community.entity.DiscussPost;
import com.study.community.entity.LoginTicket;
import com.study.community.entity.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
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

    @Autowired
    private LoginTicketMapper loginTicketMapper;

    @Autowired
    private MessageMapper messageMapper;

    @Test
    public void testInsertLoginTicket(){
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(157);
        loginTicket.setTicket("abc");
        loginTicket.setStatus(0);
        //过期时间为当期时间往后推1000毫秒*60*10   10分钟
        System.out.println(new Date());
        System.out.println(new Date(System.currentTimeMillis()+1000*60*10));
        loginTicket.setExpired(new Date(System.currentTimeMillis()+1000*60*10));
        loginTicketMapper.insertLoginTicket(loginTicket);
    }

    @Test
    public void testSelectLoginTicket(){
        LoginTicket ticket = loginTicketMapper.selectByTicket("abc");
        System.out.println(ticket);
        loginTicketMapper.updateStatus("abc",1);
        System.out.println(loginTicketMapper.selectByTicket("abc"));
    }

    @Test
    public void testSelectDiscussPost(){
        List<DiscussPost> discussPosts = discussPostMapper.selectDiscussPosts(0,0,10);
        for (DiscussPost discussPost:discussPosts){
            System.out.println(discussPost);
        }

        int count = discussPostMapper.selectDiscussPostRows(0);
        System.out.println(count);
    }

    @Test
    public void testInsertUser(){
        User user = new User();
        user.setUsername("test");
        user.setPassword("123");
        user.setCreateTime(new Date());
        user.setHeaderUrl("http://images.nowcoder.com/head/13t.png");
        user.setActivationCode("42432");
        user.setStatus(0);
        user.setType(0);
        user.setSalt("981");
        user.setEmail("test@qq.com");
        userMapper.insertUser(user);
    }

}
