package com.study.community.utils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * @ClassName community CookieUtil
 * @Author 陈必强
 * @Date 2020/12/15 19:53
 * @Description 从HttpServletRequest中获取cookie  (可能多次使用)
 **/
public class CookieUtil {

    //传入 HttpServletRequest 和 cookie 的 key name
    public static String getCookieValue(HttpServletRequest request, String name){
        if(request == null || name == null){
            throw new IllegalArgumentException("参数为空！");
        }
        Cookie[] cookies = request.getCookies();
        if(cookies != null){
            //如果cookies数组不为空，则遍历该cookie数组
            for (Cookie cookie : cookies){
                if(cookie.getName().equals(name)){
                    //若存在cookie的名称等于需要找的cookie名称，则返回该cookie对应的value
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
