package com.study.community.controller;

import com.study.community.annotation.LoginRequired;
import com.study.community.entity.User;
import com.study.community.service.UserService;
import com.study.community.utils.CommunityUtil;
import com.study.community.utils.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @ClassName community UserController
 * @Author 陈必强
 * @Date 2020/12/16 18:51
 * @Description 用户
 **/
@Controller
@RequestMapping("/user")
public class UserController {

    //日志记录
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    //获取域名
    @Value("${community.path.domain}")
    private String domain;
    //获取项目的访问路径
    @Value("${server.servlet.context-path}")
    private String contextPath;
    //获取上传文件的保存路径
    @Value("${community.path.upload}")
    private String uploadPath;

    @Autowired
    private UserService userService;

    //从 HostHolder 中获取当前登录的用户（即为要修改信息的用户）
    @Autowired
    private HostHolder hostHolder;

    //转到用户信息设置页面，只有用户登录后才能访问
    @LoginRequired
    @GetMapping("/setting")
    public String SettingPage(){
        return "site/setting";
    }

    @LoginRequired  //只有登录后才能更换头像
    @PostMapping("/upload")
    public String uploadHeader(MultipartFile headerImage, Model model){
        if(headerImage == null){
            //如果上传的图片为空
            model.addAttribute("error","您还没有选择图片！");
            return "site/setting";
        }

        //声明随机的保存图片名称（后缀不变）
        //获取上传的原始文件名
        String filename = headerImage.getOriginalFilename();
        //截取文件后缀(截取文件全名称中最后一个.后面的字符串)
        String suffix = filename.substring(filename.lastIndexOf("."));
        if(StringUtils.isBlank(suffix)){
            //如果后缀为空，或者上传的文件不存在后缀
            model.addAttribute("error","文件的格式不正确！");
            return "site/setting";
        }
        //生成随机的文件名 + 文件后缀
        filename = CommunityUtil.GenerateUUID() + suffix;

        //确定文件的存放路径(存到xx文件夹叫xx名)
        File dest = new File(uploadPath + "/" + filename);
        //将文件写入到指定的文件路径中（指定的文件）
        try {
            //存储文件
            headerImage.transferTo(dest);
        } catch (IOException e) {
            logger.error("上传文件失败：" + e.getMessage());
            throw new RuntimeException("上传文件失败！服务器异常！");
        }

        //更新当前用户的头像图片路径(web访问路径)
        // web访问路径：例如 http://localhost:8080/community/user/header/xxx.png
        //获取当前用户
        User user = hostHolder.getUser();
        //拼接web访问路径
        String headerUrl = domain + contextPath + "/user/header/" + filename;
        userService.updateUserHeader(user.getId(),headerUrl);

        //返回首页
        return "redirect:/index";
    }

    //通过web访问路径获取头像图片（文件）  响应的图片是二进制的数据，所以需要通过流手动将图片输出
    @GetMapping("/header/{filename}")
    public void GetHeader(@PathVariable("filename") String filename, HttpServletResponse response){
        //找到服务器存放文件的路径(带上本地路径的filename)
        filename = uploadPath + "/" +filename;

        //声明向浏览器输出的文件的格式(需要解析文件的后缀)
        String suffix = filename.substring(filename.lastIndexOf("."));
        //声明响应 图片格式
        response.setContentType("image/" + suffix);
        //通过字节输出流（二进制数据）输出图片
        try (
            //Java 7的语法，自动资源管理块；编译时，会自动将此块中声明的资源写入到finally中关闭它
            //要输出文件，要先能读取文件，则需要创建文件的输入流
            FileInputStream fileInputStream = new FileInputStream(filename);
            //os由response管理，即由springMvc管理，springMvc会自行关闭它
            //但是fileInputStream是自己创建的输入流，需要自己手动关闭它
        ) {
            //获取response的输出流
            OutputStream os = response.getOutputStream();

            //建立缓冲区
            byte[] buffer = new byte[1024];
            //设置游标
            int cursor = 0;
            //每次读取buffer的数据，将读取的数据给游标   等于 -1 表示未读取，不等于则读取到数据
            while ((cursor = fileInputStream.read(buffer)) != -1){
                //输出
                os.write(buffer,0,cursor);
            }
        } catch (IOException e) {
            logger.error("读取头像图片失败：" + e.getMessage());
        }
    }

}
