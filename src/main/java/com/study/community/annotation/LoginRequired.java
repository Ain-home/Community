package com.study.community.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义注解：是否需要登录才能访问
 */
@Target(ElementType.METHOD)   //此自定义注解声明在方法上
@Retention(RetentionPolicy.RUNTIME)   //此自定义注解在程序运行时有效
public @interface LoginRequired {
}
