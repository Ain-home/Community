package com.study.community.event;

import com.alibaba.fastjson.JSONObject;
import com.study.community.entity.DiscussPost;
import com.study.community.entity.Message;
import com.study.community.service.DiscussPostService;
import com.study.community.service.ElasticsearchService;
import com.study.community.service.MessageService;
import com.study.community.utils.CommunityConstant;
import com.study.community.vo.Event;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.omg.CORBA.PUBLIC_MEMBER;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName community EventConsumer
 * @Author 陈必强
 * @Date 2021/1/4 18:32
 * @Description 事件消费者
 **/
@Component
public class EventConsumer implements CommunityConstant {

    //有可能会存在隐含的问题，记录日志
    private static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);

    //发消息需要向message表中插入数据  即依赖于MessageService
    @Autowired
    private MessageService messageService;
    @Autowired
    private DiscussPostService discussPostService;

    //维护es服务器中的数据
    @Autowired
    private ElasticsearchService elasticsearchService;

    //一个方法消费一个/多个主题
    @KafkaListener(topics = {TOPIC_COMMENT, TOPIC_LIKE, TOPIC_FOLLOW})
    public void handlerMessage(ConsumerRecord record){
        if(record == null || record.value() == null){
            //如果消息为空或者消息的内容为空
            //记录日志
            logger.error("消息的内容为空！");
            return;
        }

        //生产者发送的消息内容为JSON格式的字符串，需要将该JSON字符串重新解析为对象
        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
        if(event == null){
            //如果消息内容无法还原为Event对象
            logger.error("消息格式错误！");
            return;
        }

        //发送站内通知：构造一个Message，存入到message表中
        //注意:此消息和之前用户之间的message不一样【conversation_id的存值不一样】,此消息是后台【系统】发送给用户的消息【通知】
        //没有from_id。故而假设后台【系统】id永久为1,conversation_id为消息的topic，content为消息对象的JSON对象
        Message message = new Message();
        message.setFromId(SYSTEM_USER_ID);
        message.setToId(event.getEntityUserId());
        message.setConversationId(event.getTopic());
        //状态默认为0
        //message.setStatus(0);
        message.setCreateTime(new Date());
        //封装message的内容[event事件中的内容] content
        Map<String, Object> content = new HashMap<>();
        content.put("userId",event.getUserId());
        content.put("entityType",event.getEntityType());
        content.put("entityId",event.getEntityId());
        //event中的其他数据
        if(!event.getData().isEmpty()){
            //如果不为空
            for (Map.Entry<String,Object> entry:event.getData().entrySet()){
                content.put(entry.getKey(),entry.getValue());
            }
        }
        message.setContent(JSONObject.toJSONString(content));

        //将系统通知存入到数据库message表中
        messageService.addMessage(message);
    }

    // 消费发帖事件（在es中新增帖子或评论帖子后更新帖子数据）
    @KafkaListener(topics = {TOPIC_PUBLISH})
    public void handlePublishMessage(ConsumerRecord record){
        if(record == null || record.value() == null){
            //如果消息为空或者消息的内容为空
            //记录日志
            logger.error("消息的内容为空！");
            return;
        }

        //生产者发送的消息内容为JSON格式的字符串，需要将该JSON字符串重新解析为对象
        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
        if(event == null){
            //如果消息内容无法还原为Event对象
            logger.error("消息格式错误！");
            return;
        }

        //处理【消费】事件
        DiscussPost discussPost = discussPostService.findDiscussPostById(event.getEntityId());
        //向es中新增数据（更新则是覆盖旧数据）
        elasticsearchService.saveDiscussPost(discussPost);
    }

}
