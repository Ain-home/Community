package com.study.community.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

/**
 * @ClassName community RedisConfig
 * @Author 陈必强
 * @Date 2020/12/29 19:51
 * @Description Redis 配置类
 **/
@Configuration
public class RedisConfig {

    //自动配置的Redis的RedisTemplate <Object, Object>  需要更改为 <String, Object>  更适应
    //方法名  即bean的id    定义参数  spring会自动注入该bean
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory){
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        //设置连接工厂
        //要访问数据，就要创建数据库连接，有了工厂以后，就具备访问数据库的能力
        template.setConnectionFactory(factory);

        //配置RedisTemplate （序列化的方式/数据转换的方式）
        //设置普通的key的序列化方式
        template.setKeySerializer(RedisSerializer.string());
        //设置普通的value的序列化方式(字符串，集合，列表等等)--通过建议序列化为JSON【格式化】
        template.setValueSerializer(RedisSerializer.json());

        //设置hash的key的序列化方式
        template.setHashKeySerializer(RedisSerializer.string());
        //设置hash的value的序列化方式
        template.setHashValueSerializer(RedisSerializer.json());

        //触发使之前设置的参数生效
        template.afterPropertiesSet();
        return template;
    }

}
