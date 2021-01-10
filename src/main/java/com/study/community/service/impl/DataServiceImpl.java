package com.study.community.service.impl;

import com.study.community.service.DataService;
import com.study.community.utils.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @ClassName community DataServiceImpl
 * @Author 陈必强
 * @Date 2021/1/9 14:54
 * @Description TODO
 **/
@Service
public class DataServiceImpl implements DataService {

    @Autowired
    private RedisTemplate redisTemplate;

    //日期格式化  yyyyMMdd
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");


    @Override
    public void recordUV(String ip) {
        String redisKey = RedisKeyUtil.GetUVKey(dateFormat.format(new Date()));
        redisTemplate.opsForHyperLogLog().add(redisKey,ip);
    }

    @Override
    public long calculateUV(Date start, Date end) {
        if(start == null || end == null){
            throw new IllegalArgumentException("日期参数不为空");
        }

        //整理该日期区间范围内的key
        List<String> keys = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        //设置开始时间
        calendar.setTime(start);
        while (!calendar.getTime().after(end)){
            //如果当前时间不晚于end,则执行循环
            //此时日期的key
            String key = RedisKeyUtil.GetUVKey(dateFormat.format(calendar.getTime()));
            keys.add(key);
            //每次循环加1天
            calendar.add(Calendar.DATE,1);
        }
        //合并
        //存储合并的key
        String redisKey =  RedisKeyUtil.GetUVKey(dateFormat.format(start),dateFormat.format(end));
        redisTemplate.opsForHyperLogLog().union(redisKey,keys.toArray());

        //返回合并后的统计结果
        return redisTemplate.opsForHyperLogLog().size(redisKey);
    }

    @Override
    public void recordDAU(int userId) {
        String redisKey = RedisKeyUtil.GetDAUKey(dateFormat.format(new Date()));
        //key  索引   布尔值
        redisTemplate.opsForValue().setBit(redisKey,userId,true);
    }

    @Override
    public long calculateDAU(Date start, Date end) {
        if(start == null || end == null){
            throw new IllegalArgumentException("日期参数不为空");
        }

        //整理该日期区间范围内的key
        List<byte[]> keys = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        //设置开始时间
        calendar.setTime(start);
        while (!calendar.getTime().after(end)){
            //如果当前时间不晚于end,则执行循环
            //此时日期的key
            String key = RedisKeyUtil.GetDAUKey(dateFormat.format(calendar.getTime()));
            keys.add(key.getBytes());
            //每次循环加1天
            calendar.add(Calendar.DATE,1);
        }
        //合并  进行OR运算
        return (long) redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection redisConnection) throws DataAccessException {
                //运算结果存储key
                String redisKey = RedisKeyUtil.GetDAUKey(dateFormat.format(start),dateFormat.format(end));
                //进行运算
                redisConnection.bitOp(RedisStringCommands.BitOperation.OR,redisKey.getBytes(),keys.toArray(new byte[0][0]));
                return redisConnection.bitCount(redisKey.getBytes());
            }
        });
    }

}
