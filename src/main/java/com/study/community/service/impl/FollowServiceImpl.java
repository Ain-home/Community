package com.study.community.service.impl;

import com.study.community.entity.User;
import com.study.community.service.FollowService;
import com.study.community.service.UserService;
import com.study.community.utils.CommunityConstant;
import com.study.community.utils.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @ClassName community FollowServiceImpl
 * @Author 陈必强
 * @Date 2020/12/30 20:55
 * @Description 关注功能业务实现
 **/
@Service
public class FollowServiceImpl implements FollowService, CommunityConstant {

    @Autowired
    private RedisTemplate redisTemplate;

    //查询关注用户和用户粉丝需要使用
    @Autowired
    private UserService userService;

    //关注（两次redis存储操作，需要进行事务管理）
    @Override
    public void follow(int userId, int entityType, int entityId) {
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                //构造key
                String followeeKey = RedisKeyUtil.GetFolloweeKey(userId,entityType);
                String followerKey = RedisKeyUtil.GetFollowerKey(entityType,entityId);

                //开启事务
                redisOperations.multi();
                //两次存储
                //followeeKey的value是有序集合，元素为entityId,score为当前关注时间（这里将它转化为毫秒数）
                redisOperations.opsForZSet().add(followeeKey,entityId,System.currentTimeMillis());
                //followerKey的value是有序集合，元素为userId,score为当前关注时间
                redisOperations.opsForZSet().add(followerKey,userId,System.currentTimeMillis());

                return redisOperations.exec();
            }
        });
    }

    //取关
    @Override
    public void unfollow(int userId, int entityType, int entityId) {
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                //构造key
                String followeeKey = RedisKeyUtil.GetFolloweeKey(userId,entityType);
                String followerKey = RedisKeyUtil.GetFollowerKey(entityType,entityId);

                //开启事务
                redisOperations.multi();
                //两次存储
                //followeeKey的value是有序集合，元素为entityId,score为当前关注时间（这里将它转化为毫秒数）
                redisOperations.opsForZSet().remove(followeeKey,entityId);
                //followerKey的value是有序集合，元素为userId,score为当前关注时间
                redisOperations.opsForZSet().remove(followerKey,userId);

                return redisOperations.exec();
            }
        });
    }

    @Override
    public long findFolloweeCount(int userId, int entityType) {
        //构造key
        String followeeKey = RedisKeyUtil.GetFolloweeKey(userId,entityType);
        //返回某key对应的有序集合的元素个数
        return redisTemplate.opsForZSet().zCard(followeeKey);
    }

    @Override
    public long findFollowerCount(int entityType, int entityId) {
        //构造key
        String followerKey = RedisKeyUtil.GetFollowerKey(entityType, entityId);
        return redisTemplate.opsForZSet().zCard(followerKey);
    }

    @Override
    public boolean hasFollowed(int userId, int entityType, int entityId) {
        String followeeKey = RedisKeyUtil.GetFolloweeKey(userId, entityType);
        //查询key对应的score是否为空，不为空，则说明存在，则已关注，返回true
        return redisTemplate.opsForZSet().score(followeeKey,entityId) != null;
    }

    @Override
    public List<Map<String, Object>> findFollowees(int userId, int offset, int limit) {
        //关注的实体是用户
        //构造key
        String followeeKey = RedisKeyUtil.GetFolloweeKey(userId,ENTITY_TYPE_USER);
        //redis查询 (range默认是根据score这里是时间从小到大排序)，我们需要获取最新时间的，则需要倒序
        //根据关注时间从最新到最早获取关注用户的ID集合
        Set<Integer> targetIds = redisTemplate.opsForZSet().reverseRange(followeeKey,offset,offset+limit-1);

        if(targetIds == null){
            //如果关注用户id集合为空
            return null;
        }

        List<Map<String, Object>> list = new ArrayList<>();
        for (Integer targetId : targetIds){
            Map<String, Object> map = new HashMap<>();
            //封装用户信息
            User user = userService.findUserById(targetId);
            map.put("user",user);
            //封装关注时间
            Double score = redisTemplate.opsForZSet().score(followeeKey,targetId);
            //还原成时间，并封装
            map.put("followTime",new Date(score.longValue()));

            list.add(map);
        }

        return list;
    }

    @Override
    public List<Map<String, Object>> findFollowers(int userId, int offset, int limit) {
        //构造key(粉丝也是用户实体类型)
        String followerKey = RedisKeyUtil.GetFollowerKey(ENTITY_TYPE_USER,userId);
        Set<Integer> targetIds = redisTemplate.opsForZSet().reverseRange(followerKey,offset,offset+limit-1);

        if(targetIds == null){
            return null;
        }

        List<Map<String, Object>> list = new ArrayList<>();
        for (Integer targetId : targetIds){
            Map<String, Object> map = new HashMap<>();
            //封装用户信息
            User user = userService.findUserById(targetId);
            map.put("user",user);
            //封装关注时间
            Double score = redisTemplate.opsForZSet().score(followerKey,targetId);
            //还原成时间，并封装
            map.put("followTime",new Date(score.longValue()));

            list.add(map);
        }

        return list;
    }


}
