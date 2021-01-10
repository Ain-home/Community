package com.study.community.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.File;

/**
 * @ClassName community WKConfig
 * @Author 陈必强
 * @Date 2021/1/10 0:06
 * @Description WK工具网页截图实现分享功能  配置类
 **/
@Configuration
public class WKConfig {

    //记录日志
    private static final Logger logger = LoggerFactory.getLogger(WKConfig.class);

    //注入配置的参数 application
    //截图文件存放路径
    @Value("${wk.image.storage}")
    private String wkImageStorage;

    //初始化方法（应用加载【@Configuration】时就执行，用于创建截图文件存放路径【文件夹】）
    @PostConstruct
    public void init(){
        //创建wk图片目录
        File file = new File(wkImageStorage);
        if(!file.exists()){
            //如果目录不存在,则创建它
            file.mkdir();
            logger.info("创建wk图片目录：" + wkImageStorage);
        }
    }

}
