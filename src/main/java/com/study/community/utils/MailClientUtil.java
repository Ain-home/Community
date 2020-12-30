package com.study.community.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

/**
 * @ClassName community MailClientUtil
 * @Author 陈必强
 * @Date 2020/12/9 21:23
 * @Description 将发邮件的事委托给邮箱客户端去做（代替了邮箱客户端）
 **/
@Component
public class MailClientUtil {

    private static final Logger logger = LoggerFactory.getLogger(MailClientUtil.class);

    //核心组件 JavaMailSender  它是Spring自带的Bean  所以可以直接注入到容器
    @Autowired
    private JavaMailSender mailSender;

    //发送人  由配置文件中注入
    @Value("${spring.mail.username}")
    private String from;

    /**
     * 发送邮件
     * 每次发送邮件需要：收件人，邮件主题，邮件内容
     */
    public void sendMail(String to, String subject, String content){
        //构建 MimeMessage（邮件的主体） Spring 提供了 MimeMessageHelper 帮助类
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
        try {
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            //设置邮件内容支持HTML文件格式
            helper.setText(content,true);
        } catch (MessagingException e) {
            logger.error("发送邮件失败："+e.getMessage());
        }
        //调用 send 方法
        mailSender.send(helper.getMimeMessage());
    }

}
