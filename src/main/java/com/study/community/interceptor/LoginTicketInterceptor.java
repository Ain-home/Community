package com.study.community.interceptor;

import com.study.community.entity.LoginTicket;
import com.study.community.entity.User;
import com.study.community.service.UserService;
import com.study.community.utils.CookieUtil;
import com.study.community.utils.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * @ClassName community LoginTicketInterceptor
 * @Author 陈必强
 * @Date 2020/12/15 19:46
 * @Description 在每次页面跳转（情求）时保持显示用户登录信息
 **/
@Component
public class LoginTicketInterceptor implements HandlerInterceptor {

    @Autowired
    private UserService userService;

    //多线程
    @Autowired
    private HostHolder hostHolder;

    //在请求开始就获取登录凭证（cookie）对应的用户信息，因为在网站各处都可能用到该用户
    //重写 preHandle （编写请求前的逻辑 -- 从cookie中获取用户信息）
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 从cookie中获取登录凭证
        String ticket = CookieUtil.getCookieValue(request,"ticket");

        if(ticket != null){
            //若凭证不为空，则查找该凭证
            LoginTicket loginTicket = userService.findLoginTicketByTicket(ticket);
            //检查凭证是否有效
            if(loginTicket != null && loginTicket.getStatus() == 0 && loginTicket.getExpired().after(new Date())){
                //若该存在该凭证且该凭证仍有效,该凭证失效时间在当前时间之后
                User user = userService.findUserById(loginTicket.getUserId());
                //在本次请求中持有用户信息（注意在多线程应用中隔离不同的用户，应用 ThreadLocal 存储类）
                //将数据存入到当前线程的ThreadLocal的map中，当请求没有处理完，这个线程就一直在；当请求结束，线程才被销毁
                hostHolder.setUser(user);
            }
        }

        return true;  //返回true后续才能继续执行
    }

    //在每次请求（controller）后模板引擎调用前将当前请求对应线程持有的user封装到modelAndView中
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        //获取当前线程持有的user
        User user = hostHolder.getUser();
        if(user != null && modelAndView != null){
            //当用户信息存在且 modelAndView 不为空
            modelAndView.addObject("loginUser",user);
        }
    }

    //在请求结束后，清除掉保存的用户信息
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        hostHolder.clear();
    }
}
