package com.study.community.service.impl;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.study.community.dao.DiscussPostMapper;
import com.study.community.entity.DiscussPost;
import com.study.community.service.DiscussPostService;
import com.study.community.utils.SensitiveFilter;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName community DiscussPostServiceImpl
 * @Author 陈必强
 * @Date 2020/12/6 20:29
 * @Description TODO
 **/
@Service
public class DiscussPostServiceImpl implements DiscussPostService {

    //记录日志
    private static final Logger logger = LoggerFactory.getLogger(DiscussPostServiceImpl.class);

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    //注入caffeine缓存设置参数
    @Value("${caffeine.discuss.max-size}")
    private int maxSize;
    @Value("${caffeine.discuss.expire-seconds}")
    private int expireSeconds;

    //caffeine的核心接口【组件】： Cache；它有两个常用的子接口：LoadingCache[多个线程同步缓存，排队从DB取],AsyncLoadingCache[异步缓存，并发从DB取缓存]
    //热门帖子列表的缓存
    private LoadingCache<String,List<DiscussPost>> discussListCache;
    //帖子总行数的缓存
    private LoadingCache<Integer,Integer> discussRowsCache;
    //缓存只需要初始化一次，在服务启动时初始化一次即可
    @PostConstruct
    public void init(){
        //通过 newBuilder构建Cache
        //初始化帖子列表缓存
        discussListCache = Caffeine.newBuilder()
                //缓存最大数据量
                .maximumSize(maxSize)
                //写入缓存后的多久后过期
                .expireAfterWrite(expireSeconds, TimeUnit.SECONDS)
                //build方法使上述参数生效，需要传入CacheLoader接口参数【匿名实现】
                //CacheLoader 作用：若缓存中没有要查询的数据，该如何从DB中获取数据
                .build(new CacheLoader<String, List<DiscussPost>>() {
                    @Nullable
                    @Override
                    public List<DiscussPost> load(@NonNull String key) throws Exception {
                        //对key的处理
                        if(key == null || key.length() == 0){
                            //如果key为空
                            throw new IllegalArgumentException("参数错误");
                        }
                        //通过key获取需要的参数  offset limit
                        String[] params = key.split(":");
                        if(params == null || params.length != 2){
                            //key是之前设计的，有一定的要求
                            throw new IllegalArgumentException("参数错误");
                        }
                        int offset = Integer.valueOf(params[0]);
                        int limit = Integer.valueOf(params[1]);

                        //一级缓存后，可以设计二级缓存
                        //二级缓存 ： redis 【省略】

                        logger.debug("load discuss list from DB to Caffeine");
                        return discussPostMapper.selectDiscussPosts(0,offset,limit,1);
                    }
                });
        //初始化帖子总数缓存
        discussRowsCache = Caffeine.newBuilder()
                //参数这里可以设置为不一样的
                .maximumSize(maxSize)
                .expireAfterWrite(expireSeconds, TimeUnit.SECONDS)
                .build(new CacheLoader<Integer, Integer>() {
                    @Nullable
                    @Override
                    public Integer load(@NonNull Integer key) throws Exception {
                        //key不需要处理，直接使用即可

                        //一级缓存后，可以设计二级缓存
                        //二级缓存 ： redis 【省略】

                        logger.debug("load discuss rows from DB to Caffeine");
                        return discussPostMapper.selectDiscussPostRows(key);
                    }
                });
    }

    public List<DiscussPost> findDiscussPosts(int userId, int offset, int limit,int orderMode){
        if(userId == 0 && orderMode == 1){
            //只缓存首页最热门的帖子列表,且缓存一页数据（与offset，limit相关，所以可以使用它们俩拼接为key）
            //如果是取热门帖子，直接从缓存中取结果
            return discussListCache.get(offset + ":" + limit);
        }
        //访问数据库时记录日志
        logger.debug("load discuss list from DB!");
        //从数据库中取数据
        return discussPostMapper.selectDiscussPosts(userId,offset,limit,orderMode);
    }

    public int findDiscussPostRows(int userId){
        if(userId == 0){
            //只有在热门帖子列表时才通过缓存读取数据，此时userId==0
            //LoadingCache必须要为key-value的形式，所以使用userId为key[没啥用，但数据结构固定要]
            return discussRowsCache.get(userId);
        }
        //如果用户查看自己帖子时，直接从DB中取数据，不通过缓存
        logger.debug("load discuss rows from DB!");
        return discussPostMapper.selectDiscussPostRows(userId);
    }

    @Override
    public int addDiscussPost(DiscussPost discussPost) {
        if(discussPost == null){
            throw new IllegalArgumentException("参数不能为空！");
        }
        //转义HTML标签（转义HTML标签  <div>123</div> --> &lt;div&gt;123&lt;/div&gt;），没有标签则不处理
        //SpringMvc提供的HtmlUtils
        discussPost.setTitle(HtmlUtils.htmlEscape(discussPost.getTitle()));
        discussPost.setContent(HtmlUtils.htmlEscape(discussPost.getContent()));
        //敏感词过滤 title 和 content
        discussPost.setTitle(sensitiveFilter.filter(discussPost.getTitle()));
        discussPost.setContent(sensitiveFilter.filter(discussPost.getContent()));

        return discussPostMapper.insertDiscussPost(discussPost);
    }

    @Override
    public DiscussPost findDiscussPostById(int id) {
        return discussPostMapper.selectDiscussPostById(id);
    }

    @Override
    public int updateCommentCount(int id, int commentCount) {
        return discussPostMapper.updateCommentCount(id,commentCount);
    }

    @Override
    public int updateType(int id, int type) {
        return discussPostMapper.updateType(id, type);
    }

    @Override
    public int updateStatus(int id, int status) {
        return discussPostMapper.updateStatus(id, status);
    }

    @Override
    public int updateScore(int id, double score) {
        return discussPostMapper.updateScore(id, score);
    }

}
