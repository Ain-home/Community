package com.study.community.config;

import com.study.community.utils.CommunityConstant;
import com.study.community.utils.CommunityUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @ClassName community SecurityConfig
 * @Author 陈必强
 * @Date 2021/1/7 20:44
 * @Description Security配置类:要继承 WebSecurityConfigurerAdapter
 **/
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter implements CommunityConstant {

    //忽略对静态资源的访问
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/resources/**");
    }

    //绕过security的认证方法
//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        super.configure(auth);
//    }
    //虽然绕过了认证，但是没有了context，security无法对系统进行权限管理
    //在UserService进行处理

    //授权
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //分配权限
        http.authorizeRequests()
                //指定某些路径【请求】需要指定权限才能访问
                .antMatchers(
                    "/user/setting",
                        "/user/upload",
                        "/discuss/add",
                        "/comment/**",
                        "/letter/**",
                        "/notice/**",
                        "/like",
                        "/follow",
                        "/unfollow"
                )
                //哪些权限【任意一个就可以访问】可以访问上述路径
                .hasAnyAuthority(AUTHORITY_USER,AUTHORITY_ADMIN,AUTHORITY_MODERATOR)
                // 置顶 加精 需要 版主 权限   删除 需要 管理员 权限
                .antMatchers(
                    "/discuss/top",
                        "/discuss/wonderful"
                )
                .hasAnyAuthority(AUTHORITY_MODERATOR)
                .antMatchers(
                        "/discuss/delete",
                        "/data/**"
                )
                .hasAnyAuthority(AUTHORITY_ADMIN)
                //其他任何请求都允许直接访问
                .anyRequest().permitAll()
                //关闭【禁用】CSRF
                .and().csrf().disable();

        //权限不足时的处理
        //对于普通请求，直接返回一个HTML页面即可
        //对于异步请求，需要返回一个JSON字符串
        //区别处理
        http.exceptionHandling()
                //没有登录【凭证】时如何处理
                .authenticationEntryPoint(new AuthenticationEntryPoint() {
                    @Override
                    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {
                        //获取请求类型【通过请求头】
                        String xRequestedWith = request.getHeader("x-requested-with");
                        //判断请求类型
                        if("XMLHttpRequest".equals(xRequestedWith)){
                            //异步请求（期待返回XML数据，现已被JSON替代）
                            //设置response返回的类型：application/plain 表示返回的是普通的字符串[后续保证输出JSON字符串]
                            response.setContentType("application/plain;charset=utf-8");
                            //获取字符流，向前台输出内容
                            PrintWriter writer = response.getWriter();
                            //输出JSON字符串
                            writer.write(CommunityUtil.GetJSON(403,"你还没有登录！"));
                        }else{
                            //普通请求
                            //重定向返回登录页面
                            response.sendRedirect(request.getContextPath()+"/login");
                        }
                    }
                })
                //登录了，但是权限不足时
                .accessDeniedHandler(new AccessDeniedHandler() {
                    @Override
                    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException e) throws IOException, ServletException {
                        String xRequestedWith = request.getHeader("x-requested-with");
                        if("XMLHttpRequest".equals(xRequestedWith)){
                            response.setContentType("application/plain;charset=utf-8");
                            PrintWriter writer = response.getWriter();
                            writer.write(CommunityUtil.GetJSON(403,"你没有访问此功能的权限！"));
                        }else {
                            //返回权限不够时显示的页面
                            response.sendRedirect(request.getContextPath()+"/denied");
                        }
                    }
                });

        // Security 底层是 Filter ，它的拦截是在 Controller 之前的
        //退出时，security底层会默认拦截 /logout 请求,然后进行退出处理，我们自己的 /logout就不会再继续执行下去
        // 覆盖security的退出逻辑，才能执行我们的退出逻辑
        // 修改security默认拦截的退出路径
        http.logout().logoutUrl("/securityLogout");

    }

}
