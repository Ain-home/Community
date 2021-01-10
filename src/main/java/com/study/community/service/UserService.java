package com.study.community.service;

import com.study.community.entity.LoginTicket;
import com.study.community.entity.User;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Map;

/**
 * @ClassName community UserService
 * @Author 陈必强
 * @Date 2020/12/6 20:26
 * @Description TODO
 **/
public interface UserService {

    User findUserById(int id);

    User findUserByName(String username);

    //注册用户，返回注册结果信息
    Map<String, Object> register(User user);

    //激活账户
    int activation(int userId, String code);

    //用户登录，生成凭证(需要传入凭证有效时长，以秒为单位)，返回登录信息
    Map<String, Object> login(String username, String password, int expiredSeconds);

    //退出登录，修改凭证状态
    void logout(String ticket);

    //根据ticket查询登录凭证
    LoginTicket findLoginTicketByTicket(String ticket);

    //更新用户头像
    int updateUserHeader(int userId, String headerUrl);

    //获取id用户权限
    public Collection<? extends GrantedAuthority> GetAuthorities(int userId);

}
