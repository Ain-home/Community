package com.study.community.utils;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @ClassName community CommunityUtil
 * @Author 陈必强
 * @Date 2020/12/9 22:36
 * @Description 自定义配置的工具类
 **/
public class CommunityUtil {

    // 生成随机字符串--由数字和字母组成【不要横线】（随机激活码等等要用到）
    public static String GenerateUUID(){
        return UUID.randomUUID().toString().replaceAll("-","");
    }

    // MD5加密 (密码由 password + salt = key 再经过MD5加密)
    public static String md5(String key){
        if(StringUtils.isBlank(key)){
            //null，空串,空格都为true
            return null;
        }
        //Spring自带的加密工具通过md5加密成十六进制的字符串
        return DigestUtils.md5DigestAsHex(key.getBytes());
    }

    //处理JSON字符串（调用fastJson.jar的api）,返回JSON格式的字符串
    //JSON数据 key - value （可能返回编号，编号代表某种意思；可能返回提示信息；也可能返回一些业务数据）
    public static String GetJSON(int code, String msg, Map<String, Object> map){
        //把信息封装成JSON对象
        JSONObject json = new JSONObject();
        json.put("code",code);
        json.put("msg",msg);
        //Map需要将每一对键值对放入JSON对象中
        if(map != null){
            for (String key:map.keySet()){
                json.put(key,map.get(key));
            }
        }
        //把JSON对象转换为JSON格式的字符串
        return json.toJSONString();
    }

    //方法重载，便于调用
    public static String GetJSON(int code, String msg){
        return GetJSON(code, msg, null);
    }

    public static String GetJSON(int code){
        return GetJSON(code, null, null);
    }

    //JSON数据处理测试
    public static void main(String[] args) {
        Map<String,Object> map = new HashMap<>();
        map.put("name","wbh");
        map.put("age",21);
        System.out.println(GetJSON(0,"OK",map));
    }



}
