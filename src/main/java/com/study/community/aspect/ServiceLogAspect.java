package com.study.community.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.SimpleFormatter;

/**
 * @ClassName community ServiceLogAspect
 * @Author 陈必强
 * @Date 2020/12/28 22:17
 * @Description 业务层统一记录日志--切面（AOP）
 **/
@Component
@Aspect
public class ServiceLogAspect {

    private static final Logger logger = LoggerFactory.getLogger(ServiceLogAspect.class);

    //定义切点
    @Pointcut("execution(* com.study.community.service.*.*(..))")
    public void pointcut(){}

    @Before("pointcut()")
    public void before(JoinPoint joinPoint){
        //用户（ip地址）,在XXX时间，访问了com.study.community.service.xxx方法
        //获取request
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        //获取ip
        String ip = request.getRemoteHost();
        //获取时间(当前时间)
        String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        //获取某个类，某个方法（通过连接点获取）  获取类+"."+方法名
        String target = joinPoint.getSignature().getDeclaringTypeName() + "." +joinPoint.getSignature().getName();

        //记录日志
        logger.info(String.format("用户[%s],在[%s],访问了[%s].",ip,now,target));
    }


}
