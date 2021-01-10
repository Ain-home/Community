package com.study.community.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @ClassName community ThreadPoolConfig
 * @Author 陈必强
 * @Date 2021/1/9 17:32
 * @Description Spring 线程池配置类
 **/
@Configuration
@EnableScheduling   //启用定时任务
@EnableAsync    //使 @Async 生效  @Async  使被标注方法在多线程环境下，被异步地调用
public class ThreadPoolConfig {
}
