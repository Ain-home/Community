package com.study.community.dao;

import com.study.community.entity.LoginTicket;
import org.apache.ibatis.annotations.Mapper;

/**
 * @ClassName community LoginTicketMapper
 * @Author 陈必强
 * @Date 2020/12/14 20:51
 * @Description 登录凭证mapper(在User的业务层使用::登录)
 **/
@Mapper
public interface LoginTicketMapper {

    int insertLoginTicket(LoginTicket loginTicket);

    //通过ticket凭证查询登录在线用户
    LoginTicket selectByTicket(String ticket);

    //修改凭证的状态 0 - 登录有效 1 - 失效
    int updateStatus(String ticket, int status);

}
