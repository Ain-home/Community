package com.study.community.config;

import com.google.code.kaptcha.Producer;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * @ClassName community KaptchaConfig
 * @Author 陈必强
 * @Date 2020/12/13 0:02
 * @Description 谷歌验证码工具kaptcha配置类（根据文字创建图片）
 **/
@Configuration
public class KaptchaConfig {

    //将 Producer - kaptcha核心的类（接口），它有默认的实现类 DefaultKaptcha（接口实例化为Bean）
    @Bean
    public Producer kaptchaProducer(){
        DefaultKaptcha kaptcha = new DefaultKaptcha();
        //传入参数配置 properties 可以通过配置文件写入，也可以直接在配置类写入
        Properties properties = new Properties();
        properties.setProperty("kaptcha.image.width","100");
        properties.setProperty("kaptcha.image.height","40");
        properties.setProperty("kaptcha.textproducer.font.size","32");
        properties.setProperty("kaptcha.textproducer.font.color","0,0,0");  //颜色，基于三原色 红绿蓝  也可以写颜色的英文
        properties.setProperty("kaptcha.textproducer.char.string","01234567890ABCDEFGHIJKLMNOPQRSTUVWXYZ");   //随机字符串的内容
        properties.setProperty("kaptcha.textproducer.char.length","4");  //随机字符长度
        properties.setProperty("kaptcha.noise.impl","com.google.code.kaptcha.impl.NoNoise");  //干扰类(此处使用无干扰类)
        //传入配置
        Config config = new Config(properties);
        kaptcha.setConfig(config);
        return kaptcha;
    }

}
