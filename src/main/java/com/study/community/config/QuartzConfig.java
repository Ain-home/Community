package com.study.community.config;

import com.study.community.quartz.DiscussScoreRefreshJob;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;

/**
 * @ClassName community QuartzConfig
 * @Author 陈必强
 * @Date 2021/1/9 20:06
 * @Description Spring Quartz的配置类
 * 首次配置Quartz Job的信息,首次读取后即将信息存储到数据库中
 **/
@Configuration
public class QuartzConfig {

    //FactoryBean可以简化Bean的实例化过程
    /**
     * 1.Spring通过FactoryBean封装bean的实例化过程
     * 2.将FactoryBean装配到容器中
     * 3.将FactoryBean注入给其他的bean
     * 4.该bean得到的是FactoryBean所管理的对象实例
     */

    //配置JobDetail(JobDetailFactoryBean)
    //刷新帖子score的任务Job
    @Bean
    public JobDetailFactoryBean refreshScoreJobDetail(){
        JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();
        //设置Job的类型
        factoryBean.setJobClass(DiscussScoreRefreshJob.class);
        //设置Job的name
        factoryBean.setName("DiscussScoreRefreshJob");
        //设置Job的分组
        factoryBean.setGroup("communityJobGroup");
        //设置Job是否是连续性的
        factoryBean.setDurability(true);
        //设置Job是否是可恢复的
        factoryBean.setRequestsRecovery(true);

        return factoryBean;
    }

    //配置Trigger(SimpleTriggerFactoryBean能够实现较为简单的定时需求，CronTriggerFactoryBean能够实现较为复杂的定时需求)
    //参数 该参数会被容器自动注入beanId为参数名称的JobDetail
    @Bean
    public SimpleTriggerFactoryBean refreshScoreTrigger(JobDetail refreshScoreJobDetail){
        SimpleTriggerFactoryBean factoryBean = new SimpleTriggerFactoryBean();
        //设置Trigger的JobDetail
        factoryBean.setJobDetail(refreshScoreJobDetail);
        //设置Trigger的名字
        factoryBean.setName("DiscussScoreRefreshTrigger");
        //设置Trigger的分组
        factoryBean.setGroup("communityTriggerGroup");
        //设置Trigger的刷新时间间隔（定时5分钟刷新一次）
        factoryBean.setRepeatInterval(1000*60*5);
        //设置Trigger的
        factoryBean.setJobDataMap(new JobDataMap());

        return factoryBean;
    }

}
