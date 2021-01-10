package com.study.community;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.*;
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

    //两种redis高级数据类型测试
    //HyperLogLog 测试
    //统计20万个重复数据的独立总数
    @Test
    public void testHyperLogLog(){
        String redisKey = "test:hll:01";

        for (int i = 1;i <= 100000;i++){
            redisTemplate.opsForHyperLogLog().add(redisKey,i);
        }

        for (int i = 1; i <= 100000 ; i++) {
            int r = (int) (Math.random() * 100000 + 1);
            redisTemplate.opsForHyperLogLog().add(redisKey,i);
        }

        //计算独立总数
        long size = redisTemplate.opsForHyperLogLog().size(redisKey);
        System.out.println(size);
    }

    //将多组（3组）数据合并，再统计合并后的重复数据的独立总数
    @Test
    public void testHyperLogLogUnion(){
        //构造三组数据
        String redisKey2 = "test:hll:02";
        for (int i = 1; i <= 10000; i++) {
            redisTemplate.opsForHyperLogLog().add(redisKey2,i);
        }

        String redisKey3 = "test:hll:03";
        for (int i = 5001; i <= 15000; i++) {
            redisTemplate.opsForHyperLogLog().add(redisKey3,i);
        }

        String redisKey4 = "test:hll:04";
        for (int i = 10001; i <= 20000; i++) {
            redisTemplate.opsForHyperLogLog().add(redisKey4,i);
        }

        //三组数据总3万条数据，独立总数为20000
        //合并：将数据合并后，重新存回redis
        String unionKey = "test:hll:union";
        //第一个参数为要保存独立总数的key，后面的参数都是要合并的key
        redisTemplate.opsForHyperLogLog().union(unionKey,redisKey2,redisKey3,redisKey4);

        long size = redisTemplate.opsForHyperLogLog().size(unionKey);
        System.out.println("独立总数是："+size);
    }

    //Bitmap  测试
    //统计一组数据的布尔值
    @Test
    public void testBitmap(){
        String redisKey = "test:bm:01";

        //记录
        // 参数：key,索引,布尔值（逻辑）   连续的值，默认就是false
        // 统计的true的个数
        redisTemplate.opsForValue().setBit(redisKey,1,true);
        redisTemplate.opsForValue().setBit(redisKey,4,true);
        redisTemplate.opsForValue().setBit(redisKey,7,true);

        // 查询
        // 根据key和索引查询其布尔值（0/1）
        System.out.println(redisTemplate.opsForValue().getBit(redisKey,0));
        System.out.println(redisTemplate.opsForValue().getBit(redisKey,1));
        System.out.println(redisTemplate.opsForValue().getBit(redisKey,2));

        //统计  该方法不在opsForValue里面，需要获取redis底层的连接[RedisConnection]才能访问该方法
        Object obj = redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection redisConnection) throws DataAccessException {
                //统计 true 的总数  参数为key的byte数组
                return redisConnection.bitCount(redisKey.getBytes());
            }
        });
        System.out.println(obj);
    }

    //多组数据进行布尔运算 与或非
    //统计三组数据的布尔值，并对这3组数据做or运算
    @Test
    public void testBitmapOperation(){
        //每组数据 0-6
        String redisKey2 = "test:bm:02";
        redisTemplate.opsForValue().setBit(redisKey2,0,true);
        redisTemplate.opsForValue().setBit(redisKey2,1,true);
        redisTemplate.opsForValue().setBit(redisKey2,2,true);

        String redisKey3 = "test:bm:03";
        redisTemplate.opsForValue().setBit(redisKey3,2,true);
        redisTemplate.opsForValue().setBit(redisKey3,3,true);
        redisTemplate.opsForValue().setBit(redisKey3,4,true);

        String redisKey4 = "test:bm:04";
        redisTemplate.opsForValue().setBit(redisKey4,4,true);
        redisTemplate.opsForValue().setBit(redisKey4,5,true);
        redisTemplate.opsForValue().setBit(redisKey4,6,true);

        //运算结果会存到新的key中
        String operationKey = "test:bm:or";
        //运算的方法也需要用到底层连接
        Object obj = redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection redisConnection) throws DataAccessException {
                //参数：运算符，保存key，后面是哪些需要进行计算的keys
                redisConnection.bitOp(RedisStringCommands.BitOperation.OR,
                        operationKey.getBytes(),redisKey2.getBytes(),redisKey3.getBytes(),redisKey4.getBytes());
                //返回  统计的结果
                return redisConnection.bitCount(operationKey.getBytes());
            }
        });
        System.out.println("统计结果："+obj);

        //输出0-6每位的运算结果
        System.out.println(redisTemplate.opsForValue().getBit(operationKey,0));
        System.out.println(redisTemplate.opsForValue().getBit(operationKey,1));
        System.out.println(redisTemplate.opsForValue().getBit(operationKey,2));
        System.out.println(redisTemplate.opsForValue().getBit(operationKey,3));
        System.out.println(redisTemplate.opsForValue().getBit(operationKey,4));
        System.out.println(redisTemplate.opsForValue().getBit(operationKey,5));
        System.out.println(redisTemplate.opsForValue().getBit(operationKey,6));
    }


}
