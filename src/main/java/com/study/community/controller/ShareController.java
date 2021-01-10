package com.study.community.controller;

import com.study.community.event.EventProducer;
import com.study.community.utils.CommunityConstant;
import com.study.community.utils.CommunityUtil;
import com.study.community.vo.Event;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName community ShareController
 * @Author 陈必强
 * @Date 2021/1/10 0:14
 * @Description 分享功能  网页长图分享链接
 **/
@Controller
public class ShareController implements CommunityConstant {

    private static final Logger logger = LoggerFactory.getLogger(ShareController.class);

    //分享事件  异步执行  kafka
    @Autowired
    private EventProducer eventProducer;

    //应用域名
    @Value("${community.path.domain}")
    private String domain;

    //项目访问路径（用户访问图片链接需要）
    @Value("${server.servlet.context-path}")
    private String contextPath;

    //生成图片存放目录
    @Value("${wk.image.storage}")
    private String wkImageStorage;

    //生成图片(以异步的方式生成【图片生成时间较长】，一般习惯于用事件驱动，分享事件，controller将事件交给kafka，后续由kafka去执行)
    @GetMapping("/share")
    @ResponseBody   //异步，返回JSON信息
    //传入要生成图片的html路径
    public String share(String htmlUrl){
        // 随机生成文件名
        String fileName = CommunityUtil.GenerateUUID();

        //分享事件  kafka
        Event event = new Event()
                .setTopic(TOPIC_SHARE)
                // map信息数据  传入图片的路径，生成的文件名，文件的后缀
                .setData("htmlUrl",htmlUrl)
                .setData("fileName",fileName)
                .setData("suffix",".png");
        eventProducer.fireEvent(event);

        //返回图片访问路径
        Map<String,Object> map = new HashMap<>();
        map.put("shareUrl",domain+contextPath+"/share/image/"+fileName);

        //JSON数据
        return CommunityUtil.GetJSON(0,null,map);
    }

    //通过图片链接获取图片
    //通过response输出图片
    @GetMapping("/share/image/{fileName}")
    public void GetShareImage(@PathVariable("fileName") String fileName,
                              HttpServletResponse response){
        if(StringUtils.isBlank(fileName)){
            throw new IllegalArgumentException("文件名不能为空！");
        }

        //输出文件格式
        response.setContentType("image/png");
        //通过链接获取服务器上的图片
        File file = new File(wkImageStorage + "/" + fileName + ".png");
        //输出
        try {
            OutputStream outputStream = response.getOutputStream();
            //读取要输出的图片文件（边读边输出）
            FileInputStream fileInputStream = new FileInputStream(file);
            //缓冲区
            byte[] buffer = new byte[1024];
            //游标
            int cursor = 0;
            while ((cursor = fileInputStream.read(buffer)) != -1){
                //每次读取的数据放在buffer，不等于-1表示读到了数据
                //读到了数据则将其输出
                outputStream.write(buffer,0,cursor);
            }
        } catch (IOException e) {
            logger.error("获取长图失败：" + e.getMessage());
        }
    }

}
