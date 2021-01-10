package com.study.community.controller;

import com.study.community.service.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.kafka.annotation.PartitionOffset;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;

/**
 * @ClassName community DataController
 * @Author 陈必强
 * @Date 2021/1/9 15:34
 * @Description 数据统计
 **/
@Controller
public class DataController {

    @Autowired
    private DataService dataService;

    //访问网站数据统计页面
    //既可以处理GET请求，也能处理POST请求
    @RequestMapping(path = "/data",method = {RequestMethod.GET,RequestMethod.POST})
    public String toDataPage(){
        return "site/admin/data";
    }

    //统计UV请求
    //传入开始和结束日期，服务器传入的是日期的字符串，服务器不知道日期格式，需要指定日期格式才能转换为Date
    @PostMapping("/data/uv")
    public String GetUV(@DateTimeFormat(pattern = "yyyy-MM-dd") Date start,
                        @DateTimeFormat(pattern = "yyyy-MM-dd") Date end, Model model){
        long uv = dataService.calculateUV(start, end);
        //返回统计结果
        model.addAttribute("uvResult",uv);
        //统计完后，还是回到data.html页面
        //传回start,end显示
        model.addAttribute("uvStart",start);
        model.addAttribute("uvEnd",end);

        //转发【当前方法完成一部分请求，转发到的方法完成剩余请求】  此处和 return "site/admin/data"; 效果一样
        return "forward:/data";
    }

    //统计DAU请求
    @PostMapping("/data/dau")
    public String GetDAU(@DateTimeFormat(pattern = "yyyy-MM-dd") Date start,
                         @DateTimeFormat(pattern = "yyyy-MM-dd") Date end, Model model){
        long dau = dataService.calculateDAU(start, end);
        model.addAttribute("dauResult",dau);
        model.addAttribute("dauStart",start);
        model.addAttribute("dauEnd",end);
        return "forward:/data";
    }

}
