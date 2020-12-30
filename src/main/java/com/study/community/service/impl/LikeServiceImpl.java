package com.study.community.service.impl;

import com.study.community.service.LikeService;
import com.study.community.utils.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * @ClassName community LikeServiceImpl
 * @Author 陈必强
 * @Date 2020/12/29 21:14
 * @Description TODO
 **/
@Service
public class LikeServiceImpl implements LikeService {

    //注入redisTemplate
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public void like(int userId, int entityType, int entityId) {
        //生成key
        String entityLikeKey = RedisKeyUtil.GetEntityLikeKey(entityType,entityId);
        //查看当前触发点赞的用户是否已经点过赞了（点过了即取消点赞） -- set 集合
        boolean isMember = redisTemplate.opsForSet().isMember(entityLikeKey, userId);
        if(isMember){
            //如果点过赞了（set集合中有该userId）,将该key的value值set集合中的对应userId删除
            redisTemplate.opsForSet().remove(entityLikeKey,userId);
        }else {
            //如果没点过赞，则将该userId加入到value值set中
            redisTemplate.opsForSet().add(entityLikeKey,userId);
        }
    }

    @Override
    public long findEntityLikeCount(int entityType, int entityId) {
        //即查看key对应的value值set集合中有多少userId
        //生成key
        String entityLikeKey = RedisKeyUtil.GetEntityLikeKey(entityType,entityId);
        return redisTemplate.opsForSet().size(entityLikeKey);
    }

    @Override
    public int findEntityLikeStatus(int userId, int entityType, int entityId) {
        //即查看key对应的value值set集合中是否包含该userId
        //生成key
        String entityLikeKey = RedisKeyUtil.GetEntityLikeKey(entityType,entityId);
        //返回1  表示点了赞   返回0 表示没点赞
        return redisTemplate.opsForSet().isMember(entityLikeKey,userId) ? 1 : 0;
    }


}
