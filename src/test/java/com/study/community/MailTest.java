package com.study.community;

import com.study.community.utils.MailClientUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

/**
 * @ClassName community MailTest
 * @Author 陈必强
 * @Date 2020/12/9 21:38
 * @Description 发送邮件的测试
 **/
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MailTest {

    @Autowired
    private MailClientUtil mailClientUtil;

    //测试需要使用thymeleaf的处理  TemplateEngine 由Spring容器直接管理
    @Autowired
    private TemplateEngine templateEngine;

    @Test
    public void testTextMail(){
        mailClientUtil.sendMail("chenbq11@yonyou.com","Text邮件","Welcome!");
    }

    @Test
    public void testHtmlMail(){
        //thymeleaf的context
        Context context = new Context();
        //传入页面需要的参数
        context.setVariable("username","SUN");

        //生成动态网页HTML（也就是字符串）
        String content =  templateEngine.process("/mail/mailmodel",context);
        System.out.println(content);

        //发邮件
        mailClientUtil.sendMail("chenbq11@yonyou.com","Html邮件", content);
    }

}
