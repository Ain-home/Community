package com.study.community.service;

import java.util.Date;

/**
 * @ClassName community DataService
 * @Author 陈必强
 * @Date 2021/1/9 14:54
 * @Description 网站数据统计
 **/
public interface DataService {

    //将访问网站的IP计入UV
    void recordUV(String ip);

    //统计指定日期范围内的UV
    long calculateUV(Date start,Date end);

    //将活跃用户计入DAU
    void recordDAU(int userId);

    //统计指定日期范围内的DAU
    long calculateDAU(Date start,Date end);

}
