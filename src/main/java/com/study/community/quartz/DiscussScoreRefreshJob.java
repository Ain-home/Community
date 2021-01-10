package com.study.community.quartz;

import com.study.community.entity.DiscussPost;
import com.study.community.service.DiscussPostService;
import com.study.community.service.ElasticsearchService;
import com.study.community.service.LikeService;
import com.study.community.utils.CommunityConstant;
import com.study.community.utils.RedisKeyUtil;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.yaml.snakeyaml.scanner.ScannerImpl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @ClassName community DiscussScoreRefreshJob
 * @Author 陈必强
 * @Date 2021/1/9 21:27
 * @Description 定时计算帖子score的Job
 * score算法 log(精华分+评论数x10+点赞数x2)+(发布时间-牛客纪元)
 **/
public class DiscussScoreRefreshJob implements Job, CommunityConstant {

    private static final Logger logger = LoggerFactory.getLogger(DiscussScoreRefreshJob.class);

    //获取需要计算的帖子的集合（id）
    @Autowired
    private RedisTemplate redisTemplate;

    //获取当前帖子的评论、点赞、加精状态
    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private LikeService likeService;

    //帖子状态score发生变化，更新到es服务器中
    @Autowired
    private ElasticsearchService elasticsearchService;

    //牛客纪元  时间常量
    private static final Date epoch;

    static {
        //初始化
        try {
            epoch = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2014-08-01 00:00:00");
        } catch (ParseException e) {
            throw new RuntimeException("初始化牛客纪元失败！"+e);
        }
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        //从redis中取要计算的帖子id集合
        String redisKey = RedisKeyUtil.GetDiscussScoreKey();
        //集合的每个id都需要计算，使用BoundSetOperations【多次计算】
        BoundSetOperations operations = redisTemplate.boundSetOps(redisKey);

        //若集合为空，则不需要计算score，取消任务
        if(operations.size() == 0){
            logger.info("任务取消，没有需要重新计算score的帖子！");
            return;
        }

        //在任务开始前后记录日志
        logger.info("【任务开始】:正在刷新帖子score，需要重新计算score的帖子有："+operations.size());

        while (operations.size() > 0){
            //刷新一个帖子的score(通过帖子id)
            //集合中弹出一个帖子id
            refresh((Integer) operations.pop());
        }

        logger.info("【任务结束】：帖子score重新计算完毕！");
    }

    //刷新帖子score 根据id
    private void refresh(int id){
        DiscussPost discussPost = discussPostService.findDiscussPostById(id);

        if(discussPost == null){
            logger.error("该帖子不存在：id = "+ id);
            return;
        }

        //计算score

        //是否加精
        boolean wonderful = discussPost.getStatus() == 1;
        //点赞数量
        int likeCount = (int) likeService.findEntityLikeCount(ENTITY_TYPE_DISCUSS,id);
        //评论数量
        int commentCount = discussPost.getCommentCount();

        //计算权重  加精=75
        double weight = (wonderful ? 75 :0) + commentCount*10 + likeCount*2;
        //score = 权重(不能小于1，小于1求对数后得出负数，业务设置score不为负数) + 发布时间距离天数(毫秒计算)
        double score = Math.log10(Math.max(weight,1))
                + (discussPost.getCreateTime().getTime() - epoch.getTime())/(1000*3600*24);

        //更新帖子的score
        discussPostService.updateScore(id,score);

        //同步es服务器中的帖子数据
        discussPost.setScore(score);
        elasticsearchService.saveDiscussPost(discussPost);
    }

}
