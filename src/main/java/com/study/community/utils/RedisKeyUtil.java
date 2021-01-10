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

    //重构key加入user(便于统计一个用户收到多少赞)
    private static final String PREFIX_USER_LIKE = "like:user";

    //关注功能
    // 抽象被关注对象（可以是用户、帖子、问题等等）为实体类
    //从我的角度，它就是我关注的对象
    private static final String PREFIX_FOLLOWEE = "followee";
    // 抽象关注对象（可以是用户、帖子、问题等等）
    //从它的角度，我就是它的粉丝
    private static final String PREFIX_FOLLOWER = "follower";

    //使用Redis存储验证码
    //构造key的前缀
    private static final String PREFIX_KAPTCHA = "kaptcha";

    //使用redis存储登录凭证（替代MySQL表login_ticket）
    //key前缀
    private static final String PREFIX_TICKET = "ticket";

    //使用Redis缓存用户信息
    //key前缀
    private static final String PREFIX_USER = "user";

    //使用Redis统计网站的数据
    // UV  key前缀
    private static final String PREFIX_UV = "uv";
    // DAU  key前缀
    private static final String PREFIX_DAU = "dau";

    //使用redis缓存在定时区间内帖子score有变动的帖子
    // key 前缀
    private static final String PREFIX_DISCUSSPOST = "discuss";


    //生成某个实体的赞数量的KEY
    //存入实体的相关信息（类型，id）
    //key->value   like:entity:entityType:entityId -> set(userId)  便于得知谁给点了赞，集合也容易统计点赞数等等
    public static String GetEntityLikeKey(int entityType, int entityId){
        //实体前缀拼接entityType和entityId生成实体的key
        return PREFIX_ENTITY_LIKE + SPLIT + entityType + SPLIT + entityId;
    }

    //生成某个用户收到的赞数量  对应的key
    //  like:user:userId -> int
    public static String GetUserLikeKey(int userId){
        return PREFIX_USER_LIKE + SPLIT + userId;
    }

    //某个用户关注的实体（帖子，用户等等）  对应的key
    // followee:userId:entityType -> zset(entityId,now)   now是关注的时间,作为分数score
    public static String GetFolloweeKey(int userId, int entityType){
        return PREFIX_FOLLOWEE + SPLIT + userId + SPLIT + entityType;
    }

    //某个实体拥有的粉丝
    // follower:entityType:entityId -> zset(userId,now)   now是关注的时间,作为分数score
    public static String GetFollowerKey(int entityType,int entityId){
        return PREFIX_FOLLOWER +SPLIT + entityType + SPLIT + entityId;
    }

    //拼登录验证码存储在Redis中的key
    // owner   每个用户登录验证码与用户相关，但登录时不存在用户信息，故而使用owner代替(在获取验证码时和用户登录时对比进行验证码验证)
    public static String GetKaptchaKey(String owner){
        return PREFIX_KAPTCHA + SPLIT + owner;
    }

    //返回登录凭证的key
    // 使用字符串保存登录的用户信息  登录凭证 ->  用户信息
    public static String GetTicketKey(String ticket){
        return PREFIX_TICKET + SPLIT + ticket;
    }

    //返回用户信息的key
    // 使用字符串缓存用户信息  用户id  -> 用户信息
    public static String GetUserKey(int userId){
        return PREFIX_USER + SPLIT + userId;
    }

    //返回单日[若要一周或者其他，则可以使用HyperLogLog的合并功能] UV  key
    //date  日期的字符串，包括年月日
    public static String GetUVKey(String date){
        return PREFIX_UV + SPLIT + date;
    }

    //区间UV合并 key
    public static String GetUVKey(String startDate,String endDate){
        return PREFIX_UV + SPLIT + startDate + SPLIT + endDate;
    }

    //返回单日 DAU key
    //传入日期  date  年月日
    public static String GetDAUKey(String date){
        return PREFIX_DAU + SPLIT + date;
    }

    //区间DAU合并 key
    public static String GetDAUKey(String startDate,String endDate){
        return PREFIX_DAU + SPLIT + startDate + SPLIT + endDate;
    }

    //返回定时区间内 score 发生变化的帖子  key
    //不需要传入参数
    public static String GetDiscussScoreKey(){
        return PREFIX_DISCUSSPOST + SPLIT + "score";
    }


}
