package com.study.community.utils;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName community SensitiveFilter
 * @Author 陈必强
 * @Date 2020/12/16 22:04
 * @Description 敏感词过滤器 -- 交由容器管理
 **/
@Component
public class SensitiveFilter {

    //记录日志
    private static final Logger logger = LoggerFactory.getLogger(SensitiveFilter.class);

    //定义常量（用以替换敏感词）
    private static final String REPLACEMENT = "***";

    // 初始化前缀树
    // 处理根节点(空节点，什么都没有，只有一个引用)
    private TrieNode root = new TrieNode();
    // 根据敏感词集构造敏感树
    // 初始化方法
    @PostConstruct   //当容器实例化这个bean（服务启动时），在调用bean的构造器后，这个注解声明的方法就会被自动调用【前缀树构造完毕】
    public void init(){
        //读取敏感词集
        //从类路径下通过类加载器加载，读取指定路径（名称）的文件   类路径 target/classes/
        try(
            //自动资源管理块（自动关闭）
            //字节流（字节流读文字不方便，转换为字符流）
            InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
            //再将字符流转换为缓冲流（效率高）
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        ){
            //读取每一行（每一个）敏感词
            String keyWord;
            while ((keyWord = reader.readLine()) != null){
                //读取的敏感词不为空时
                //将敏感词添加到前缀树中
                this.addKeyWord(keyWord);
            }
        } catch (IOException e){
            logger.error("加载敏感词集失败：" + e.getMessage());
        }
    }

    //将一个敏感词添加到前缀树中
    private void addKeyWord(String keyWord) {
        //临时节点（相当于一个指针，沿根节点不断地创建它的下级节点）
        TrieNode tempNode = root;
        for (int i = 0; i < keyWord.length(); i++){
            //遍历敏感词，每次获取一个字符，挂在前缀树的一支上
            char c = keyWord.charAt(i);
            //查看是否存在字符值为c的子节点（若存在，则不用新建；不存在，则需要新建）
            TrieNode subNode = tempNode.getSubNode(c);
            if(subNode == null){
                //如果不存在
                //初始化该字符子节点
                subNode = new TrieNode();
                //添加到临时节点（父节点）的子节点中
                tempNode.addSubNode(c,subNode);
            }
            //使临时节点指向子节点（刚刚遍历的字符值对应的子节点【新建或者已存在】）
            tempNode = subNode;

            //若keyWord结束时，设置结束标识
            if(i == keyWord.length()-1){
                tempNode.setKeyWordEnd(true);
            }
        }
    }

    //过滤敏感词的方法：传入待过滤文本，返回过滤后的文本
    public String filter(String text){
        // 若传入的文本为空
        if(StringUtils.isBlank(text)){
            return null;
        }

        // 执行过滤
        // 准备三个指针
        // 指针1指向前缀树，遍历当前关键词是否是敏感词
        TrieNode triePointer = root;
        // 指针2,3指向文本
        int begin = 0;
        int pos = 0;
        // 保存结果(替换敏感词后的文本结果)
        StringBuilder sb = new StringBuilder();
        //开始检测是begin==pos
        while(pos < text.length()){
            //当指针3未到文本末尾时，继续循环(需要注意在循环外将最后这段begin到pos不为敏感词的字符串写入到sb)
            //以pos的位置开始检测是否为敏感词
            char c = text.charAt(pos);
            //规避特殊符号的影响（对特殊符号的处理）
            if(isSymbol(c)){
                //若此时指针1处于前缀树的根节点，则将此符号写入结果，并让指针2继续向下走，指针1不动
                if(triePointer == root){
                    sb.append(c);
                    begin++;
                }
                //无论特殊符号在检测字符串段开头或中间，指针3都向下走
                pos++;
                //继续遍历下一个字符
                continue;
            }
            //若不是特殊字符，则检查当前指针1的下级节点是否包含这个字符
            triePointer = triePointer.getSubNode(c);
            if(triePointer == null){
                //如果前缀树的下级节点为空,则说明以begin开头的字符不是敏感词
                sb.append(text.charAt(begin));
                //进入下一个字符（text）位置(pos和begin向下走)
                pos = ++begin;
                //此时指针1重新指向根节点
                triePointer = root;
            } else if(triePointer.isKeyWordEnd()){
                //如果前缀树的下级节点是敏感词的结尾字符
                //发现了敏感词，以begin开头，pos结尾
                //此时将替代的*号代替这段敏感词
                sb.append(REPLACEMENT);
                //begin和pos来到过去敏感词的下一个位置，继续开始下一个敏感词的检查
                begin = ++pos;
                //前缀树的根节点指向根
                triePointer = root;
            } else {
                //前缀树指针下一个节点不为空，且不是敏感词结尾,则继续对这个疑似敏感词进行检查 pos向下走
                pos++;
            }
        }

        //将最后一段不为敏感词，但之前判断疑似敏感词的写入结果(从begin到最后)
        sb.append(text.substring(begin));

        return sb.toString();
    }

    //判断是否为特殊符号，是则返回true
    private boolean isSymbol(Character c){
        //org.apache.commons.lang3.CharUtils;
        //isAsciiAlphanumeric 判断字符是否为普通字符，如果是，则返回true；如果是特殊符号，则返回false
        //(c < 0x2E80 || c > 0x9FFF)   0x2E80~0x9FFF是东亚的文字范围
        return !CharUtils.isAsciiAlphanumeric(c) && (c < 0x2E80 || c > 0x9FFF);
    }

    //定义前缀树结构（节点）  --  私有内部类
    private class TrieNode {

        //敏感词结束标识（是否是一个敏感词）
        private boolean isKeyWordEnd = false;

        //子节点 （子节点及其对应的字符，key是下级字符，value是下级节点）【非二叉树结构】
        private Map<Character, TrieNode> subNodes = new HashMap<>();

        public boolean isKeyWordEnd() {
            return isKeyWordEnd;
        }

        public void setKeyWordEnd(boolean keyWordEnd) {
            isKeyWordEnd = keyWordEnd;
        }

        //添加子节点
        public void addSubNode(Character c, TrieNode node){
            subNodes.put(c,node);
        }

        //获取子节点(通过key获取)
        public TrieNode getSubNode(Character c){
            return subNodes.get(c);
        }
    }


}
