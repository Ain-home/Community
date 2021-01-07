package com.study.community;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class CommunityApplication {

    //解决redis和es之间启动Netty冲突
    @PostConstruct   //管理bean的生命周期，主要管理bean的初始化方法；它修饰的方法，会在构造器调用完后执行
    public void init(){
        // 解决Netty启动冲突
        // Netty4Utils setAvailableProcessors()方法的 启动开关
        System.setProperty("es.set.netty.runtime.available.processors", "false");
    }

    public static void main(String[] args) {
        SpringApplication.run(CommunityApplication.class, args);
    }

}
