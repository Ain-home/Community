package com.study.community.controller;

import com.alibaba.fastjson.JSONObject;
import com.study.community.entity.Message;
import com.study.community.entity.User;
import com.study.community.service.MessageService;
import com.study.community.service.UserService;
import com.study.community.utils.CommunityConstant;
import com.study.community.utils.CommunityUtil;
import com.study.community.utils.HostHolder;
import com.study.community.vo.Page;
import org.apache.ibatis.annotations.Mapper;
import org.omg.PortableInterceptor.INACTIVE;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.HtmlUtils;

import java.util.*;

/**
 * @ClassName community MessageController
 * @Author 陈必强
 * @Date 2020/12/24 20:20
 * @Description 私信
 **/
@Controller
public class MessageController implements CommunityConstant {

    @Autowired
    private MessageService messageService;
    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    //私信列表
    @GetMapping("/letter/list")
    public String GetLetterList(Model model, Page page){
        //获取当前用户
        User user = hostHolder.getUser();
        //设置分页信息
        page.setLimit(5);
        page.setPath("/letter/list");
        page.setRows(messageService.findConversationCount(user.getId()));
        //查询会话列表
        List<Message> conversationList = messageService.findConversations(user.getId(),page.getOffset(),page.getLimit());

        //封装前端需要展示的数据
        List<Map<String,Object>> conversations = new ArrayList<>();
        if(conversationList != null){
            for (Message message:conversationList){
                Map<String,Object> map = new HashMap<>();
                //一次会话
                map.put("conversation",message);
                //某会话的私信数
                map.put("letterCount",messageService.findLetterCount(message.getConversationId()));
                //某会话未读私信数
                map.put("unreadCount",messageService.findLetterUnreadCount(user.getId(),message.getConversationId()));
                //要前端私信页面要显示的私信对方信息
                int targetId = user.getId() == message.getFromId() ? message.getToId() : message.getFromId();
                map.put("target",userService.findUserById(targetId));

                conversations.add(map);
            }
        }

        model.addAttribute("conversations",conversations);
        //该登录用户总的未读私信数
        int letterUnreadCount = messageService.findLetterUnreadCount(user.getId(),null);
        model.addAttribute("letterUnreadCount",letterUnreadCount);
        //三类系统通知的未读总和
        int noticeUnreadCount = messageService.findNoticeUnreadCount(user.getId(),null);
        model.addAttribute("noticeUnreadCount",noticeUnreadCount);

        return "site/letter";
    }

    //会话详情
    @GetMapping("/letter/detail/{conversationId}")
    public String GetLetterDetail(@PathVariable("conversationId") String conversationId,Page page,Model model){
        //分页信息设置
        page.setLimit(5);
        page.setPath("/letter/detail/"+conversationId);
        page.setRows(messageService.findLetterCount(conversationId));

        //某段会话消息列表
        List<Message> letterList = messageService.findLetters(conversationId,page.getOffset(),page.getLimit());

        //封装前端要展示的数据
        List<Map<String,Object>> letters = new ArrayList<>();
        if(letterList != null){
            for (Message letter:letterList){
                Map<String,Object> map = new HashMap<>();
                map.put("letter",letter);
                map.put("fromUser",userService.findUserById(letter.getFromId()));
                letters.add(map);
            }
        }

        model.addAttribute("letters",letters);
        //与当前用户进行会话的用户
        model.addAttribute("target",GetLetterTarget(conversationId));

        //设置当前用户查看的消息状态为已读
        List<Integer> ids = GetLetterIds(letterList);
        if(!ids.isEmpty()){
            messageService.readMessages(ids);
        }

        return "site/letter-detail";
    }

    //获取当前用户所有的未读消息列表
    private List<Integer> GetLetterIds(List<Message> letterList){
        List<Integer> ids = new ArrayList<>();

        if(letterList != null){
            for (Message message : letterList){
                if(hostHolder.getUser().getId() == message.getToId() &&message.getStatus() == 0){
                    //如果消息接收者为当前用户且消息是未读的
                    ids.add(message.getId());
                }
            }
        }

        return ids;
    }

    //拆解会话ID获取此会话在当前展示中的target
    private User GetLetterTarget(String conversationId){
        String[] ids = conversationId.split("_");

        int id0 = Integer.parseInt(ids[0]);
        int id1 = Integer.parseInt(ids[1]);

        int targetId = hostHolder.getUser().getId() == id0 ? id1 : id0;
        return userService.findUserById(targetId);
    }

    //添加会话消息
    @PostMapping("/letter/send")
    @ResponseBody    //异步
    public String sendLetters(String toName,String content){
        User target = userService.findUserByName(toName);
        if(target == null){
            //如果该用户不存在
            return CommunityUtil.GetJSON(1,"目标用户不存在！");
        }

        Message message = new Message();
        message.setFromId(hostHolder.getUser().getId());
        message.setToId(target.getId());
        //拼接会话ID（会话双方ID加下划线组成，小的id前）
        if(message.getFromId() < message.getToId()){
            message.setConversationId(message.getFromId() + "_" + message.getToId());
        }else {
            message.setConversationId(message.getToId() + "_" + message.getFromId());
        }
        message.setContent(content);
        message.setStatus(0);  //可以不用设置，默认是0
        message.setCreateTime(new Date());

        messageService.addMessage(message);

        //如果一切正常，返回状态码 0
        return CommunityUtil.GetJSON(0);
    }

    //显示通知列表
    @GetMapping("/notice/list")
    public String GetNoticeList(Model model){
        User user = hostHolder.getUser();

        //查询评论类的通知
        Message message = messageService.findLatestNotice(user.getId(),TOPIC_COMMENT);
        System.out.println(message);
        if(message != null){
            //封装数据
            Map<String, Object> messageVo = new HashMap<>();
            messageVo.put("message",message);

            //将message的content从JSON字符串转义解析为map对象
            String content = HtmlUtils.htmlUnescape(message.getContent());
            Map<String, Object> data = JSONObject.parseObject(content,HashMap.class);

            //将content的内容存到messageVo中
            messageVo.put("user",userService.findUserById((Integer) data.get("userId")));
            messageVo.put("entityType",data.get("entityType"));
            messageVo.put("entityId",data.get("entityId"));
            messageVo.put("discussPostId",data.get("discussPostId"));

            //封装这一类通知的总数量
            int count = messageService.findNoticeCount(user.getId(),TOPIC_COMMENT);
            messageVo.put("count",count);

            //封装这一类通知未读的数量
            int unreadCount = messageService.findNoticeUnreadCount(user.getId(),TOPIC_COMMENT);
            messageVo.put("unreadCount",unreadCount);
            model.addAttribute("commentNotice",messageVo);
        }

        //查询点赞类的通知
        message = messageService.findLatestNotice(user.getId(),TOPIC_LIKE);
        if(message != null){
            //封装数据
            Map<String, Object> messageVo = new HashMap<>();
            messageVo.put("message",message);

            //将message的content从JSON字符串转义解析为map对象
            String content = HtmlUtils.htmlUnescape(message.getContent());
            Map<String, Object> data = JSONObject.parseObject(content,HashMap.class);

            //将content的内容存到messageVo中
            messageVo.put("user",userService.findUserById((Integer) data.get("userId")));
            messageVo.put("entityType",data.get("entityType"));
            messageVo.put("entityId",data.get("entityId"));
            messageVo.put("discussPostId",data.get("discussPostId"));

            //封装这一类通知的总数量
            int count = messageService.findNoticeCount(user.getId(),TOPIC_LIKE);
            messageVo.put("count",count);

            //封装这一类通知未读的数量
            int unreadCount = messageService.findNoticeUnreadCount(user.getId(),TOPIC_LIKE);
            messageVo.put("unreadCount",unreadCount);
            model.addAttribute("likeNotice",messageVo);
        }

        //查询关注类的通知
        message = messageService.findLatestNotice(user.getId(),TOPIC_FOLLOW);
        if(message != null){
            //封装数据
            Map<String, Object> messageVo = new HashMap<>();
            messageVo.put("message",message);

            //将message的content从JSON字符串转义解析为map对象
            String content = HtmlUtils.htmlUnescape(message.getContent());
            Map<String, Object> data = JSONObject.parseObject(content,HashMap.class);

            //将content的内容存到messageVo中
            messageVo.put("user",userService.findUserById((Integer) data.get("userId")));
            messageVo.put("entityType",data.get("entityType"));
            messageVo.put("entityId",data.get("entityId"));

            //封装这一类通知的总数量
            int count = messageService.findNoticeCount(user.getId(),TOPIC_FOLLOW);
            messageVo.put("count",count);

            //封装这一类通知未读的数量
            int unreadCount = messageService.findNoticeUnreadCount(user.getId(),TOPIC_FOLLOW);
            messageVo.put("unreadCount",unreadCount);
            model.addAttribute("followNotice",messageVo);
        }

        //三类系统通知的未读总和
        int noticeUnreadCount = messageService.findNoticeUnreadCount(user.getId(),null);
        model.addAttribute("noticeUnreadCount",noticeUnreadCount);

        //查询私信总的未读数量
        int letterUnreadCount = messageService.findLetterUnreadCount(user.getId(),null);
        model.addAttribute("letterUnreadCount",letterUnreadCount);

        return "site/notice";
    }

    //某一类主题的所有通知（分页显示）
    @GetMapping("/notice/detail/{topic}")
    public String GetNoticeDetail(@PathVariable("topic") String topic,Page page,Model model){
        User user = hostHolder.getUser();

        //分页初始化
        page.setLimit(5);
        page.setPath("/notice/detail/"+topic);
        page.setRows(messageService.findNoticeCount(user.getId(),topic));

        //分页查询某主题下的所有通知
        List<Message> noticeList = messageService.findNotices(user.getId(),topic,page.getOffset(),page.getLimit());

        //封装返回数据
        List<Map<String,Object>> noticeVoList = new ArrayList<>();
        if(noticeList != null){
            for (Message notice:noticeList){
                Map<String,Object> map = new HashMap<>();
                //通知
                map.put("notice",notice);
                //通知内容
                String content = HtmlUtils.htmlUnescape(notice.getContent());
                Map<String,Object> data = JSONObject.parseObject(content,HashMap.class);
                map.put("user",userService.findUserById((Integer) data.get("userId")));
                map.put("entityType",data.get("entityType"));
                map.put("entityId",data.get("entityId"));
                //关注没有discussPostId，但不影响
                map.put("discussPostId",data.get("discussPostId"));

                //通知的触发者（作者）
                map.put("fromUser",userService.findUserById(notice.getFromId()));

                noticeVoList.add(map);
            }
        }
        model.addAttribute("notices",noticeVoList);

        //设置已读
        List<Integer> ids = GetLetterIds(noticeList);
        if(!ids.isEmpty()){
            messageService.readMessages(ids);
        }

        return "site/notice-detail";
    }

}
