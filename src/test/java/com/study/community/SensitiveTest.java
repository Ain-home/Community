package com.study.community;

import com.study.community.utils.SensitiveFilter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @ClassName community SensitiveTest
 * @Author 陈必强
 * @Date 2020/12/19 15:36
 * @Description 敏感词算法测试
 **/
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class SensitiveTest {

    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Test
    public void textSensitive(){
        String str = "这里只能赌博，不能嫖娼，更不能吸毒！";
        str = sensitiveFilter.filter(str);
        System.out.println(str);

        String str1 = "这里只能-赌~博，不能-嫖_娼，更不能_吸-毒！";
        System.out.println(sensitiveFilter.filter(str1));
    }

}
