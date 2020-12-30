package com.study.community.utils;

/**
 * @ClassName community RedisKeyUtil
 * @Author 陈必强
 * @Date 2020/12/29 21:00
 * @Description 生成redis的key的工具类,便于复用
 **/
public class RedisKeyUtil {

    //key的一部分 （:）
    private static final String SPLIT = ":";

    //实体类（帖子，评论）的前缀
    private static final String PREFIX_ENTITY_LIKE = "like:entity";

    //生成某个实体的赞数量的KEY
    //存入实体的相关信息（类型，id）
    //key->value   like:entity:entityType:entityId -> set(userId)  便于得知谁给点了赞，集合也容易统计点赞数等等
    public static String GetEntityLikeKey(int entityType, int entityId){
        //实体前缀拼接entityType和entityId生成实体的key
        return PREFIX_ENTITY_LIKE + SPLIT + entityType + SPLIT + entityId;
    }

}
