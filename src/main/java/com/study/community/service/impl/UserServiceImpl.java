package com.study.community.service.impl;

import com.study.community.dao.UserMapper;
import com.study.community.entity.User;
import com.study.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @ClassName community UserServiceImpl
 * @Author 陈必强
 * @Date 2020/12/6 20:28
 * @Description TODO
 **/
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    public User findUserById(int id){
        return userMapper.selectById(id);
    }
}
