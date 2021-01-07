package com.study.community.event;

import com.alibaba.fastjson.JSONObject;
import com.study.community.vo.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * @ClassName community EventProducer
 * @Author 陈必强
 * @Date 2021/1/4 18:28
 * @Description Kafka消息队列  事件生产者
 **/
@Component    //交给容器管理
public class EventProducer {

    @Autowired
    private KafkaTemplate kafkaTemplate;

    // 处理/触发事件（发送消息）
    public void fireEvent(Event event){
        //将事件发布到指定的主题【发送一个消息到事件的topic，且消息的内容是一个JSON的字符串】
        kafkaTemplate.send(event.getTopic(), JSONObject.toJSONString(event));
    }

}
