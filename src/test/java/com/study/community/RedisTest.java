package com.study.community;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @ClassName community RedisTest
 * @Author 陈必强
 * @Date 2020/12/29 20:16
 * @Description redis测试类
 **/
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class RedisTest {

    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void testStrings(){
        String redisKey = "test:count";
        //存数据
        redisTemplate.opsForValue().set(redisKey,1);
        //取数据
        System.out.println(redisTemplate.opsForValue().get(redisKey));

        //增加
        System.out.println(redisTemplate.opsForValue().increment(redisKey));
        //减少
        System.out.println(redisTemplate.opsForValue().decrement(redisKey));
    }

    //绑定key   多次访问同一个key的操作时使用
    @Test
    public void testBoundOperations(){
        String redisKey = "test:count";
        //绑定key   此后 operation 等价于 redisTemplate.opsForValue()
        BoundValueOperations operations = redisTemplate.boundValueOps(redisKey);

        operations.increment();
        System.out.println(operations.get());
    }

    //编程式事务（redis声明式事务使用较少）
    @Test
    public void testTransactional(){
        Object obj = redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                //定义事务的key
                String redisKey = "test:tx";
                //启用事务
                redisOperations.multi();

                //事务内操作
                redisOperations.opsForValue().set(redisKey,10);
                //在事务未提交前执行的查询无法获得结果（无效）
                System.out.println(redisOperations.opsForValue().get(redisKey));
                redisOperations.opsForValue().decrement(redisKey);
                System.out.println(redisOperations.opsForValue().get(redisKey));

                return redisOperations.exec();   //提交事务
            }
        });
        System.out.println(obj);
    }

}
