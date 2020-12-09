package com.study.community;

import com.study.community.dao.UserMapper;
import com.study.community.entity.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeansException;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class CommunityApplicationTests implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Test
    public void testIoc(){
        System.out.println(applicationContext);
    }

    @Test
    public void testSelectUser(){
        UserMapper userMapper = applicationContext.getBean(UserMapper.class);
        User user = userMapper.selectById(1);
        System.out.println(user);
    }
}
