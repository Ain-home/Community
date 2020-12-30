package com.study.community.entity;

import java.util.Date;

/**
 * @ClassName community LoginTicket
 * @Author 陈必强
 * @Date 2020/12/14 20:49
 * @Description 用户登录凭证（保存用户登录信息到数据库）
 **/
public class LoginTicket {

    private int id;
    //对应用户
    private int userId;
    //登录凭证（字符串）
    private String ticket;
    //状态  0 - 登录有效 1 - 失效
    private int status;
    //凭证过期时间
    private Date expired;

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

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Date getExpired() {
        return expired;
    }

    public void setExpired(Date expired) {
        this.expired = expired;
    }

    @Override
    public String toString() {
        return "LoginTicket{" +
                "id=" + id +
                ", userId=" + userId +
                ", ticket='" + ticket + '\'' +
                ", status=" + status +
                ", expired=" + expired +
                '}';
    }
}
