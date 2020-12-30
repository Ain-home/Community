package com.study.community.service.impl;

import com.study.community.dao.LoginTicketMapper;
import com.study.community.dao.UserMapper;
import com.study.community.entity.LoginTicket;
import com.study.community.entity.User;
import com.study.community.service.UserService;
import com.study.community.utils.CommunityConstant;
import com.study.community.utils.CommunityUtil;
import com.study.community.utils.MailClientUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * @ClassName community UserServiceImpl
 * @Author 陈必强
 * @Date 2020/12/6 20:28
 * @Description TODO
 **/
@Service
public class UserServiceImpl implements UserService, CommunityConstant {

    @Autowired
    private UserMapper userMapper;
    //用户登录凭证
    @Autowired
    private LoginTicketMapper loginTicketMapper;

    //发送邮件，需要邮件客户端工具，thymeleaf模板引擎管理
    @Autowired
    private MailClientUtil mailClientUtil;
    @Autowired
    private TemplateEngine templateEngine;

    //发邮箱时要生成一个激活码（激活码要包含域名，项目名）
    @Value("${community.path.domain}")
    private String domain;
    @Value("${server.servlet.context-path}")
    private String contextPath;

    public User findUserById(int id){
        return userMapper.selectById(id);
    }

    public User findUserByName(String username){
        return userMapper.selectByName(username);
    }

    //返回注册结果
    @Override
    public Map<String, Object> register(User user) {
        Map<String, Object> map = new HashMap<>();

        // 空值处理
        if(user == null) {
            throw new IllegalArgumentException("参数不能为空！");
        }
        //业务上的异常（不报错，返回即可）
        if(StringUtils.isBlank(user.getUsername())) {
            map.put("usernameMsg","账号不能为空！");
            return map;
        }
        if(StringUtils.isBlank(user.getPassword())) {
            map.put("passwordMsg","密码不能为空！");
            return map;
        }
        if(StringUtils.isBlank(user.getEmail())) {
            map.put("emailMsg","邮箱不能为空！");
            return map;
        }

        //验证账号（判断该新用户账号，邮箱等是否已被注册）
        User u = userMapper.selectByName(user.getUsername());
        if(u != null) {
            map.put("usernameMsg","该账号已被占用！");
            return map;
        }
        u = userMapper.selectByEmail(user.getEmail());
        if(u != null) {
            map.put("emailMsg","该邮箱已注册！");
            return map;
        }

        //注册用户
        //生成随机字符串（取5位） 加在密码后
        user.setSalt(CommunityUtil.GenerateUUID().substring(0,5));
        //加密密码
        user.setPassword(CommunityUtil.md5(user.getPassword()+user.getSalt()));
        //传入的信息只有邮箱，账号，密码，其他信息还要进行额外初始化设置
        user.setType(0);
        user.setStatus(0);
        //生成随机字符串（激活码）
        user.setActivationCode(CommunityUtil.GenerateUUID());
        //生成随机头像（从牛客网上存储的1001个随机头像随机选取）
        //头像地址  http://images.nowcoder.com/head/0t.png  0数字可变 0-1001
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
        user.setCreateTime(new Date());
        System.out.println(user.toString());
        //存储新用户到数据库
        userMapper.insertUser(user);

        //使用thymeleaf html邮件模板发送激活邮件
        Context context = new Context();
        //传入参数 email
        context.setVariable("email",user.getEmail());
        //传入激活路径  http://localhost:8080/community/activation/userId(用户id)/code(激活码)
        //用户id在数据库后就有了
        StringBuilder url = new StringBuilder();
        url.append(domain);
        url.append(contextPath);
        url.append("/activation/");
        url.append(user.getId());
        url.append("/");
        url.append(user.getActivationCode());
        context.setVariable("url",url.toString());
        //生成动态网页HTML （字符串格式）
        String content = templateEngine.process("/mail/activation",context);
        //发送邮件
        mailClientUtil.sendMail(user.getEmail(),"激活邮件",content);

        return map;
    }

    //激活账户
    @Override
    public int activation(int userId, String code){
        User user = userMapper.selectById(userId);
        if(user.getStatus() == 1){
            //已经激活
            return AVTIVATION_REPEAT;
        } else if(user.getActivationCode().equals(code)){
            //激活成功
            //更新用户状态
            userMapper.updateStatus(userId,1);
            return AVTIVATION_SUCCESS;
        } else {
            return AVTIVATION_FAILURE;
        }
    }

    //返回登录结果
    @Override
    public Map<String, Object> login(String username, String password, int expiredSeconds) {
        Map<String, Object> map = new HashMap<>();

        // 空值处理
        if(StringUtils.isBlank(username)) {
            map.put("usernameMsg","账号不能为空！");
            return map;
        }
        if(StringUtils.isBlank(password)) {
            map.put("passwordMsg","密码不能为空！");
            return map;
        }

        //验证账号（输入的信息和数据库相比是否存在/正确）
        User user = userMapper.selectByName(username);
        if(user == null){
            map.put("usernameMsg","该账号不存在！");
            return map;
        }
        //账号是否激活
        if(user.getStatus() == 0){
            map.put("usernameMsg","该账号未激活！");
            return map;
        }
        //密码是否正确（加密后的字符串是否相等[同样的字符串加密后也一样]）
        password = CommunityUtil.md5(password + user.getSalt());
        if(!user.getPassword().equals(password)){
            map.put("passwordMsg","密码不正确！");
            return map;
        }

        //登录信息正确，生成登录凭证
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(user.getId());
        //生成随机字符串凭证
        loginTicket.setTicket(CommunityUtil.GenerateUUID());
        //设置为登录状态
        loginTicket.setStatus(0);
        //设置凭证有效时间(System.currentTimeMillis() 获取当前毫秒数)
        loginTicket.setExpired(new Date(System.currentTimeMillis() + expiredSeconds*1000));
        loginTicketMapper.insertLoginTicket(loginTicket);

        //将凭证信息返回给浏览器
        map.put("ticket",loginTicket.getTicket());

        return map;
    }

    //退出登录，修改凭证状态
    @Override
    public void logout(String ticket){
        //status1即该凭证失效
        loginTicketMapper.updateStatus(ticket,1);
    }

    //获取登录凭证
    @Override
    public LoginTicket findLoginTicketByTicket(String ticket) {
        return loginTicketMapper.selectByTicket(ticket);
    }

    //更新用户头像
    @Override
    public int updateUserHeader(int userId, String headerUrl){
        return userMapper.updateHeader(userId,headerUrl);
    }

}
