package com.study.community;

import java.io.IOException;

/**
 * @ClassName community WKTest
 * @Author 陈必强
 * @Date 2021/1/9 23:55
 * @Description wk截网页图工具功能 Java编码实现 测试
 **/
public class WKTest {

    public static void main(String[] args) {
        //命令
        String cmd = "D:/study/wkhtmltopdf/wkhtmltox-0.12.6-1.msvc2015-win64/bin/wkhtmltoimage --quality 75 https://www.nowcoder.com d:/study/wkhtmltopdf/wk-images/3.png";
        //执行命令
        try {
            //将操作命令交给操作系统，剩下的由操作系统进行（提交命令和生成图片异步进行）
            Runtime.getRuntime().exec(cmd);
            System.out.println("ok!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
