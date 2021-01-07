package com.study.community;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @ClassName community KafkaTest
 * @Author 陈必强
 * @Date 2021/1/3 22:43
 * @Description kafka测试类
 **/
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class KafkaTest {

    @Autowired
    private KafkaProducer kafkaProducer;

    @Test
    public void testKafka(){
        kafkaProducer.sendMessage("test","你好！");
        kafkaProducer.sendMessage("test","在吗？");

        //不希望程序马上结束（因为消费者需要一定时间才能收到消息【略有延迟】）
        //主线程阻塞
        try {
            Thread.sleep(1000*20);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}

//kafka的生产者(主动发消息)
@Component  //spring容器管理bean
class KafkaProducer{

    //注入工具类
    @Autowired
    private KafkaTemplate kafkaTemplate;

    //发送消息(消息主题，消息内容)
    public void sendMessage(String topic,String content){
        kafkaTemplate.send(topic,content);
    }

}

//kafka的消费者（被动收消息【略有延迟】）
@Component
class KafkaConsumer{

    //监听主题名为"test"的主题
    @KafkaListener(topics = {"test"})
    public void handlerMessage(ConsumerRecord record){
        System.out.println(record.value());
    }

}
