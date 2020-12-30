package com.study.community.controller.advice;

import com.study.community.utils.CommunityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @ClassName community ExceptionAdvice
 * @Author 陈必强
 * @Date 2020/12/28 21:23
 * @Description 全局异常处理
 **/
@ControllerAdvice(annotations = Controller.class)   //缩小扫描范围（只扫描带有Controller注解的bean）
public class ExceptionAdvice {

    //记录错误日志
    @Autowired
    private static final Logger logger = LoggerFactory.getLogger(ExceptionAdvice.class);

    @ExceptionHandler({Exception.class})   //处理所有异常（Exception是所有异常的父类）
    public void handlerException(Exception e, HttpServletRequest request, HttpServletResponse response) throws IOException {
        //此方法可以带很多参数，常用的就以上三个
        logger.error("服务器发生异常：" + e.getMessage());
        //将异常详细信息栈遍历出来
        for (StackTraceElement element : e.getStackTrace()){
            logger.error(element.toString());
        }

        //判断请求是普通请求（返回网页）还是异步请求
        String xRequestedWith = request.getHeader("x-requested-with");
        if("XMLHttpRequest".equals(xRequestedWith)){
            //如果返回值是XMLHttpRequest，则说明是异步请求（返回XML信息）
            response.setContentType("application/plain;charset=utf-8");   //需要人为地将返回的信息转化为JSON对象
            //也可以设置为 application/json 浏览器会自动地将返回信息转换为JSON对象

            //获取输出流输出相关字符串
            PrintWriter writer = response.getWriter();
            writer.write(CommunityUtil.GetJSON(1,"服务器异常！"));
        }else {
            //如果是普通请求,则通过重定向返回到错误页面显示
            response.sendRedirect(request.getContextPath() + "/error");
        }
    }

}
