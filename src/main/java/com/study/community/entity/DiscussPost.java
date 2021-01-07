package com.study.community.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;

/**
 * @ClassName community DiscussPost
 * @Author 陈必强
 * @Date 2020/12/6 14:34
 * @Description 帖子
 * 帖子为了能使用es进行搜索，需要存到es服务器中（额外配置此实体类）
 * 索引  type类型已经取消  分片  副本
 **/
@Document(indexName = "discusspost",shards = 6,replicas = 3)
public class DiscussPost {

    @Id   //存到es索引的id字段
    private int id;

    @Field(type = FieldType.Integer)
    private int userId;

    //主要搜索就搜索标题和内容
    // title = "互联网校招"  建立索引即把这句话提炼出关键词，用关键词关联这句话；存储时应该尽可能地拆分出更多的词条，增加搜索的范围；搜索时应该分析词义拆分关键词
    @Field(type = FieldType.Text,analyzer = "ik_max_word",searchAnalyzer = "ik_smart")  //类型，存储时的解析器，搜索时的解析器
    private String title;

    @Field(type = FieldType.Text,analyzer = "ik_max_word",searchAnalyzer = "ik_smart")
    private String content;

    //帖子类型 0-普通; 1-置顶
    @Field(type = FieldType.Integer)
    private int type;

    //0-正常; 1-精华; 2-拉黑
    @Field(type = FieldType.Integer)
    private int status;

    //日期需要指定格式
    @Field(type = FieldType.Date,format = DateFormat.basic_date)
    private Date createTime;

    //评论数（虽然冗余，但是便于提升性能）
    @Field(type = FieldType.Integer)
    private int commentCount;

    //帖子评分
    @Field(type = FieldType.Double)
    private double score;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return "DiscussPost{" +
                "id=" + id +
                ", userId=" + userId +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", type=" + type +
                ", status=" + status +
                ", createTime=" + createTime +
                ", commentCount=" + commentCount +
                ", score=" + score +
                '}';
    }
}
