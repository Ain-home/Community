package com.study.community.interceptor;

import com.study.community.annotation.LoginRequired;
import com.study.community.utils.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * @ClassName community LoginRequiredInterceptor
 * @Author 陈必强
 * @Date 2020/12/16 20:59
 * @Description 拦截需要登录才能访问的请求
 **/
@Component
public class LoginRequiredInterceptor implements HandlerInterceptor {

    //尝试获取当前用户，若没有（为空），则为未登录状态
    @Autowired
    private HostHolder hostHolder;

    //请求开始拦截所有请求，未登录而访问需要登录的请求return false，其他返回true
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //判断拦截的目标是否是方法（只拦截方法）  Object handler
        if(handler instanceof HandlerMethod){
            //HandlerMethod springMvc提供的类型，可以理解为保存方法信息的pojo
            //若handler是一个方法,先把它转义为HandlerMethod，(Object不好处理)
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            //获取拦截的method对象
            Method method = handlerMethod.getMethod();
            //尝试获取该方法的LoginRequired注解（有可能没有）
            LoginRequired loginRequired = method.getAnnotation(LoginRequired.class);
            if(loginRequired != null && hostHolder.getUser() == null){
                //若不为空，则说明该方法有LoginRequired注解，则说明该方法需要登录才能访问
                //且用户未登录，则不能访问，return false;拒绝后续的请求
                //强制此请求重定向至登录页面  应用路径 + 登录页面路径 (request中包含应用的路径  request.getContextPath() )
                response.sendRedirect(request.getContextPath() + "/login");
                return false;
            }
        }
        return true;
    }

}
