package com.study.community.vo;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName community Event
 * @Author 陈必强
 * @Date 2021/1/4 18:17
 * @Description 系统通知功能：封装事件对象
 * Kafka 应用
 **/
public class Event {

    //主题
    private String topic;

    //事件触发者
    private int userId;

    //事件类型（点赞，评论，关注等等）
    private int entityType;
    private int entityId;
    //事件作用对象
    private int entityUserId;

    //其他数据(封装在map中)
    private Map<String,Object> data = new HashMap<>();

    public String getTopic() {
        return topic;
    }

    //设置为有返回值，便于灵活初始化Event
    public Event setTopic(String topic) {
        this.topic = topic;
        return this;
    }

    public int getUserId() {
        return userId;
    }

    public Event setUserId(int userId) {
        this.userId = userId;
        return this;
    }

    public int getEntityType() {
        return entityType;
    }

    public Event setEntityType(int entityType) {
        this.entityType = entityType;
        return this;
    }

    public int getEntityId() {
        return entityId;
    }

    public Event setEntityId(int entityId) {
        this.entityId = entityId;
        return this;
    }

    public int getEntityUserId() {
        return entityUserId;
    }

    public Event setEntityUserId(int entityUserId) {
        this.entityUserId = entityUserId;
        return this;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public Event setData(String key, Object value) {
        this.data.put(key,value);
        return this;
    }

    @Override
    public String toString() {
        return "Event{" +
                "topic='" + topic + '\'' +
                ", userId=" + userId +
                ", entityType=" + entityType +
                ", entityId=" + entityId +
                ", entityUserId=" + entityUserId +
                ", data=" + data +
                '}';
    }

}
