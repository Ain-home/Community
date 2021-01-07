package com.study.community.controller;

import com.google.code.kaptcha.Producer;
import com.study.community.entity.User;
import com.study.community.service.UserService;
import com.study.community.utils.CommunityConstant;
import com.study.community.utils.CommunityUtil;
import com.study.community.utils.RedisKeyUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.imageio.ImageIO;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName community LoginInController
 * @Author 陈必强
 * @Date 2020/12/9 22:15
 * @Description 登录
 **/
@Controller
public class LoginInController implements CommunityConstant {

    //记录错误日志
    private static final Logger logger = LoggerFactory.getLogger(LoginInController.class);

    @Autowired
    private UserService userService;

    //kaptcha（验证码图片生成器）
    @Autowired
    private Producer kaptchaProducer;

    //redis
    @Autowired
    private RedisTemplate redisTemplate;

    //注入项目路径常量
    @Value("${server.servlet.context-path}")
    private String contextPath;

    //转到注册页面
    @GetMapping("/register")
    public String RegisterPage(){
        return "site/register";
    }

    //转到登录页面（加载验证码图片时会再次向服务器发出请求，以获取验证码图片）
    @GetMapping("/login")
    public String LoginPage(){
        return "site/login";
    }

    //根据请求获取验证码图片(返回的图片，所以方法返回void，通过response返回图片,验证码的内容需要在登录时进行验证，所以验证码需要保存下来，安全起见，保存在session)
    @GetMapping("/kaptcha")
    public void GetKaptcha(HttpServletResponse response/*, HttpSession session*/){
        //生成验证码
        String text = kaptchaProducer.createText();
        //生成验证码图片
        BufferedImage image = kaptchaProducer.createImage(text);

        //将验证码存入session
        // session.setAttribute("kaptcha",text);
        //使用redis存储验证码（提高访问性能）
        //获取该验证码的归属 owner  临时凭证
        String kaptchaOwner = CommunityUtil.GenerateUUID();
        //将临时凭证发送给客户端（通过cookie）
        Cookie cookie = new Cookie("kaptchaOwner",kaptchaOwner);
        //设置cookie
        cookie.setMaxAge(60);   //生存时间  60秒
        cookie.setPath(contextPath);    //cookie有效路径：整个项目
        response.addCookie(cookie);
        //将验证码存入redis
        //获取key
        String kaptchaKey = RedisKeyUtil.GetKaptchaKey(kaptchaOwner);
        //将验证码以字符串的形式存入redis，并指定有效时间  60 秒
        redisTemplate.opsForValue().set(kaptchaKey,text,60, TimeUnit.SECONDS);

        //将图片输出给浏览器
        //声明返回的数据格式(png格式的图片)
        response.setContentType("image/png");
        try {
            //获取response的输出流
            OutputStream os =  response.getOutputStream();
            //输出图片
            ImageIO.write(image, "png", os);
        } catch (IOException e) {
            //若出现异常，输出到日志中
            logger.error("响应验证码失败"+e.getMessage());
        }
    }

    //注册处理(传入邮箱，账号，密码)
    @PostMapping("/register")
    public String register(Model model, User user) {
        Map<String, Object> map = userService.register(user);
        if(map == null || map.isEmpty()) {
            //注册成功（不返回其他错误信息）
            model.addAttribute("message","注册成功，我们已经向您的邮箱发送了一封激活邮件，请尽快激活！");
            //跳转到首页
            model.addAttribute("target","/index");
            return "site/operate-result";
        } else {
            model.addAttribute("usernameMsg",map.get("usernameMsg"));
            model.addAttribute("passwordMsg",map.get("passwordMsg"));
            model.addAttribute("emailMsg",map.get("emailMsg"));
            System.out.println(map.get("emailMsg"));
            return "site/register";
        }
    }

    //处理激活账户的请求
    //http://localhost:8080/community/activation/userId(用户id)/code(激活码)
    @GetMapping("/activation/{userId}/{code}")
    public String activation(Model model, @PathVariable("userId") int userId,
                             @PathVariable("code") String code) {
        int result = userService.activation(userId,code);
        if(result == AVTIVATION_SUCCESS) {
            //激活成功
            model.addAttribute("message","激活成功！您的账号已经可以正常使用了！");
            //跳转到登录页面
            model.addAttribute("target","/login");
        } else if(result == AVTIVATION_REPEAT) {
            //重复激活
            model.addAttribute("message","该账号已经激活过了！您可以尽情使用！");
            //跳转到登录页面
            model.addAttribute("target","/login");
        } else {
            //激活失败
            model.addAttribute("message","激活失败！请检查激活码是否正确！");
            //跳转到首页
            model.addAttribute("target","/index");
        }
        return "site/operate-result";
    }

    //登录处理(登录验证，生成凭证，验证码验证，返回cookie给浏览器保存)
    //登录失败处理
    //基本类型SpringMvc不会自动封装到model中，只有对象实体类才会自动封装到model
    //那么这些String等等的基本类型有两种方法可以获取，1.封装到model 2.登录失败返回request尚未结束，仍可以从request中获取它们[thymeleaf 中直接 (request.)param.username等等]
    @PostMapping("/login")
    public String login(String username, String password, String code, boolean remember,
                        Model model/*, HttpSession session*/, HttpServletResponse response,
                        @CookieValue("kaptchaOwner") String kaptchaOwner){
        //验证码验证
        //String kaptcha = (String)session.getAttribute("kaptcha");
        //重构：使用redis进行登录验证码验证
        String kaptcha = null;
        //需要在获取验证码时生成的cookie @CookieValue("kaptchaOwner") String kaptchaOwner
        //判断此时cookie中对应的owner是否仍有效（有效时间是60秒）
        if(StringUtils.isNotBlank(kaptchaOwner)){
            //owner有效
            //获取该owner对应的key，从而获取对应的value(验证码)
            String kaptchaKey = RedisKeyUtil.GetKaptchaKey(kaptchaOwner);
            kaptcha = (String) redisTemplate.opsForValue().get(kaptchaKey);
        }

        if(StringUtils.isBlank(kaptcha) || StringUtils.isBlank(code) || !kaptcha.equalsIgnoreCase(code)){
            //session中是否存在该验证码，传入的验证码是否Wie空，传入的验证码是否相等于session中的验证码(不区分大小写)
            //验证码错了
            model.addAttribute("codeMsg","验证码不正确！");
            return "site/login";
        }

        //检查账号密码，生成凭证，返回凭证信息
        //remember 逻辑：勾选了记住，则凭证有效时间长一些，否则短一些
        int expiredSeconds = remember ? REMEMBER_EXPIRED_SECONDS : DEFAULT_EXPIRED_SECONDS;
        Map<String, Object> map = userService.login(username,password,expiredSeconds);
        if(map.containsKey("ticket")){
            //只有生成凭证返回后才成功
            Cookie cookie = new Cookie("ticket", (String) map.get("ticket"));
            //cookie有效路径（包含在整个项目，则写入项目路径【用常量保存】）
            cookie.setPath(contextPath);
            //cookie有效时长
            cookie.setMaxAge(expiredSeconds);
            response.addCookie(cookie);
            return "redirect:/index";
        }else {
            //否则都是失败
            model.addAttribute("usernameMsg",map.get("usernameMsg"));
            model.addAttribute("passwordMsg",map.get("passwordMsg"));
            return "site/login";
        }
    }

    //退出登录，获取浏览器的cookie信息，修改凭证状态
    @GetMapping("/logout")
    public String logout(@CookieValue("ticket") String ticket){
        userService.logout(ticket);
        return "redirect:/login";  //Get方式的login请求
    }

}
