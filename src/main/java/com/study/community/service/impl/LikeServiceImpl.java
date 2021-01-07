package com.study.community.service.impl;

import com.study.community.service.LikeService;
import com.study.community.utils.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
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
    public void like(int userId, int entityType, int entityId, int entityUserId) {
//        //生成key
//        String entityLikeKey = RedisKeyUtil.GetEntityLikeKey(entityType,entityId);
//        //查看当前触发点赞的用户是否已经点过赞了（点过了即取消点赞） -- set 集合
//        boolean isMember = redisTemplate.opsForSet().isMember(entityLikeKey, userId);
//        if(isMember){
//            //如果点过赞了（set集合中有该userId）,将该key的value值set集合中的对应userId删除
//            redisTemplate.opsForSet().remove(entityLikeKey,userId);
//        }else {
//            //如果没点过赞，则将该userId加入到value值set中
//            redisTemplate.opsForSet().add(entityLikeKey,userId);
//        }
        //重构后，需要进行点赞更新某实体（帖子或者评论的点赞数），也需要给某用户获赞数进行更新，要保证事务性
        //进行编程式事务
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                //生成key
                String entityLikeKey = RedisKeyUtil.GetEntityLikeKey(entityType,entityId);
                //根据实体的作者id构建key
                String userLikeKey = RedisKeyUtil.GetUserLikeKey(entityUserId);
                //查看当前触发点赞的用户是否已经点过赞了（点过了即取消点赞） -- set 集合
                //查询需要放在事务之外（在事务内查询是不会马上得出结果的）
                boolean isMember = redisOperations.opsForSet().isMember(entityLikeKey,userId);

                //开启事务
                redisOperations.multi();
                //在事务中执行两次更新操作
                if(isMember) {
                    //如果点过赞了（set集合中有该userId）,将该key的value值set集合中的对应userId删除
                    redisOperations.opsForSet().remove(entityLikeKey,userId);
                    //同时该实体的作者获赞数减一
                    redisOperations.opsForValue().decrement(userLikeKey);
                }else {
                    //如果没点过赞，则将该userId加入到value值set中
                    redisOperations.opsForSet().add(entityLikeKey,userId);
                    //同时该实体的作者获赞数加一
                    redisOperations.opsForValue().increment(userLikeKey);
                }

                return redisOperations.exec();
            }
        });
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

    @Override
    public int findUserLikeCount(int userId) {
        String userLikeKey = RedisKeyUtil.GetUserLikeKey(userId);
        //通过get获取到的value是Object类型的数据
        Integer count = (Integer) redisTemplate.opsForValue().get(userLikeKey);
        return count == null ? 0 : count.intValue();
    }


}
