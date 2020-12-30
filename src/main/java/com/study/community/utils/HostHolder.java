package com.study.community.utils;

import com.study.community.entity.User;
import org.springframework.stereotype.Component;

/**
 * @ClassName community HostHolder
 * @Author 陈必强
 * @Date 2020/12/15 20:10
 * @Description 用做容器，用于持有用户信息，代替session对象(在多线程的环境下，隔离不同用户)
 **/
@Component
public class HostHolder {

    //ThreadLocal 能够隔离不同线程（每次存取数据时，都获取当前线程[对应的数据存储map]，然后进行数据的存取）
    private ThreadLocal<User> threadLocal = new ThreadLocal<>();

    //将user传入到当前线程
    public void setUser(User user){
        threadLocal.set(user);
    }

    //获取当前线程的user
    public User getUser(){
        return threadLocal.get();
    }

    //清理当前线程中的user
    public void clear(){
        threadLocal.remove();
    }

}
