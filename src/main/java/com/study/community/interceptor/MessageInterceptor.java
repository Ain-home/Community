package com.study.community.interceptor;

import com.study.community.entity.User;
import com.study.community.service.MessageService;
import com.study.community.utils.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @ClassName community MessageInterceptor
 * @Author 陈必强
 * @Date 2021/1/5 19:28
 * @Description 每次查询总的未读消息数=会话数+系统通知数
 **/
@Component
public class MessageInterceptor implements HandlerInterceptor {

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private MessageService messageService;

    //在请求之后，调用模板之前计算出总的未读消息数
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        User user = hostHolder.getUser();
        if(user != null){
            int letterUnreadCount = messageService.findLetterUnreadCount(user.getId(),null);
            int noticeUnreadCount = messageService.findNoticeUnreadCount(user.getId(),null);
            int count = letterUnreadCount + noticeUnreadCount;
            if(modelAndView != null){
                modelAndView.addObject("allUnreadCount",count);
            }
        }
    }
}
