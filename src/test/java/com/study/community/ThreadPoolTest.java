package com.study.community;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.quartz.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.concurrent.*;

/**
 * @ClassName community ThreadPoolTest
 * @Author 陈必强
 * @Date 2021/1/9 16:18
 * @Description 任务调度与执行  JDK Spring Quartz 三类线程池的使用
 **/
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class ThreadPoolTest {

    //输出日志，带线程id
    private static final Logger logger = LoggerFactory.getLogger(ThreadPoolTest.class);

    //JDK线程池
    //常用的 普通线程池 ExecutorService
    //Executors  线程池实例化工厂，newFixedThreadPool(5); 表示该线程池初始化后包含5个线程
    private ExecutorService executorService = Executors.newFixedThreadPool(5);
    //可执行定时任务的线程池  scheduledExecutorService
    private ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(5);

    //注意：main方法和Test线程执行不一样，在main方法中，子线程不挂的话，主线程会阻塞子线程不结束；
    // Test启动的本就是子线程，它创建的子线程和Test方法创建的线程是并发的，因此Test线程结束后，不会再继续处理它创建的子线程
    // 解决：使用sleep方法使Test线程休眠一段时间直至它创建的子线程运行结束
    //为了处理sleep方法的异常，重新封装
    private void sleep(long m){
        try {
            Thread.sleep(m);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //1.JDK ExecutorService 测试
    @Test
    public void testExecutorService(){
        //分配任务，线程池就会分配一个线程去执行这个任务
        //任务就是线程体
        Runnable task = new Runnable() {
            @Override
            public void run() {
                logger.debug("Hello ExecutorService");
            }
        };

        //执行10次task，每执行一次，线程池就调用一个线程去执行它
        for (int i = 0; i < 10; i++) {
            executorService.submit(task);
        }

        //使Test线程休眠，等待上面的10个任务执行完
        sleep(10000);
    }

    //2.JDK ScheduledExecutorService 测试
    @Test
    public void testScheduledExecutorService(){
        //同样，提供线程体task，然后设置定时
        Runnable task = new Runnable() {
            @Override
            public void run() {
                logger.debug("Hello ScheduledExecutorService");
            }
        };

        // scheduleWithFixedDelay()  延迟一段时间后执行【只执行一次】
        // scheduleAtFixedRate()  以固定的频率去执行任务【执行多次】  参数：任务，延迟时间，执行周期，前面两个时间的单位（MILLISECONDS ms）
        scheduledExecutorService.scheduleAtFixedRate(task,10000,1000, TimeUnit.MILLISECONDS);

        sleep(30000);
    }


    //Spring 线程池，启用了Spring即注入了，但需要在application文件中设置线程池
    //注入 ThreadPoolTaskExecutor   比JDK普通线程池更灵活（各种其他配置
    @Autowired
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;
    //注入 ThreadPoolTaskScheduler(还需要配置类)
    @Autowired
    private ThreadPoolTaskScheduler threadPoolTaskScheduler;

    //1.Spring ThreadPoolTaskExecutor 测试
    @Test
    public void testThreadPoolTaskExecutor(){
        Runnable task = new Runnable() {
            @Override
            public void run() {
                logger.debug("Hello ThreadPoolTaskExecutor");
            }
        };

        for (int i = 0; i < 10; i++) {
            threadPoolTaskExecutor.submit(task);
        }

        sleep(10000);
    }

    //2.Spring ThreadPoolTaskScheduler 测试
    @Test
    public void testThreadPoolTaskScheduler(){
        Runnable task = new Runnable() {
            @Override
            public void run() {
                logger.debug("Hello ThreadPoolTaskScheduler");
            }
        };

        // 设置开始进行的时间 当期时间延迟10000ms
        Date startTime = new Date(System.currentTimeMillis()+10000);
        //参数默认以ms为单位
        threadPoolTaskScheduler.scheduleAtFixedRate(task,startTime,1000);

        sleep(30000);
    }
    //Spring 线程池在使用时还有简便的调用方式
    // 在任意一个bean下的方法，注明一个注解【标注@Async表示普通线程池环境，标注一个@Scheduled表示在ThreadPoolTaskScheduler环境下】，即可使该方法在Spring线程池环境下运行（该方法成为task）


    //Quartz
    //核心组件
    // Scheduler 核心调度工具【所有的任务都由它调度】；
    // Job 定义任务的接口；
    // JobDetail 配置Job信息；
    // Trigger 触发器，配置Job运行详情；
    // 配置好后，重新启动，Quartz就会读取配置信息，然后写到数据库表中，以后就读取表中的信息来执行任务
    // Quartz有默认的配置，若重新不配置，则使用默认的配置；若重新配置，则需要在application配置文件中编写配置信息
    // 默认的配置是从内存中读取配置信息；重新配置后从数据库中读取配置信息
    // 删除Job由Scheduler通过Job的名字和该Job所在的group使用deleteJob()执行




}
