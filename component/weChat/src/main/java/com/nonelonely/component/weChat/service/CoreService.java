package com.nonelonely.component.weChat.service;


import com.alibaba.fastjson.JSON;
import com.nonelonely.common.utils.ToolUtil;
import com.nonelonely.component.weChat.model.resp.Article;
import com.nonelonely.component.weChat.model.resp.NewsMessage;
import com.nonelonely.component.weChat.model.resp.TextMessage;
import com.nonelonely.component.weChat.util.MessageUtil;


import javax.servlet.http.HttpServletRequest;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * 类名: CoreService </br>
 * 描述: 核心服务类 </br>
 * 开发人员：小海豚博客 </br>
 * 创建时间：  2015-9-30 </br>
 * 发布版本：V1.0  </br>
 */
public class CoreService {
    /**
     * 处理微信发来的请求
     * @param request
     * @return xml
     */
    public static String processRequest(HttpServletRequest request) {
        // xml格式的消息数据
        String respXml = null;
        // 默认返回的文本消息内容
        String respContent = "未知的消息类型！";
        try {
            // 调用parseXml方法解析请求消息
            Map<String, String> requestMap = MessageUtil.parseXml(request);
            // 发送方帐号
            String fromUserName = requestMap.get("FromUserName");
            // 开发者微信号
            String toUserName = requestMap.get("ToUserName");
            // 消息类型
            String msgType = requestMap.get("MsgType");


            System.out.println("-------------------------------------了"+fromUserName+":"+toUserName);


            // 文本消息
            if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_TEXT)) {
                String msg=requestMap.get("Content"); //获取内容
                try {
                    msg = URLEncoder.encode(msg,"utf-8");
                }catch (Exception e)
                {

                }
                String url_="http://i.itpk.cn/api.php?api_key=a9c2dd927fc7a1f417c7a3ed1ff44ce0&api_secret=0jyde3x288mv&question="+msg;
                respContent="我看不懂什么意思呢";

                try {
                    respContent = ToolUtil.getHTTP(url_);
                    respContent.replace("","小码")  ;
                    com.alibaba.fastjson.JSONObject jsonobject = JSON.parseObject(respContent);
                    respContent= jsonobject.getString("content").toString().replaceAll("\\{br}","\n");
                }catch (Exception e){

                    e.printStackTrace();
                }

                TextMessage textMessage=new TextMessage();
                textMessage.setToUserName(fromUserName);
                textMessage.setFromUserName(toUserName);
                textMessage.setCreateTime(new Date().getTime());
                textMessage.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_TEXT);
                textMessage.setContent(respContent);//原来信息返回
                respXml = MessageUtil.messageToXml(textMessage);
                    return respXml;
            }

            // 图片消息
            else if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_IMAGE)) {
                //对图文消息
                NewsMessage newmsg=new NewsMessage();
                newmsg.setToUserName(fromUserName);
                newmsg.setFromUserName(toUserName);
                newmsg.setCreateTime(new Date().getTime());
                newmsg.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_NEWS);

                if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_IMAGE)) { // 图片消息
                    System.out.println("==============这是图片消息！");
                    Article article=new Article();
                    article.setDescription("这是图文消息 1"); //图文消息的描述
                    article.setPicUrl("https://s1.ax1x.com/2020/06/20/Nl8oYd.png"); //图文消息图片地址
                    article.setTitle("图文消息 1");  //图文消息标题
                    article.setUrl("http://www.cuiyongzhi.com");  //图文 url 链接
                    List<Article> list=new ArrayList<Article>();
                    list.add(article);     //这里发送的是单图文，如果需要发送多图文则在这里 list 中加入多个 Article 即可！
                    newmsg.setArticleCount(list.size());
                    newmsg.setArticles(list);
                    return MessageUtil.messageToXml(newmsg);
                }
            }
            // 语音消息
            else if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_VOICE)) {
                respContent = "您发送的是语音消息！";
            }
            // 视频消息
            else if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_VIDEO)) {
                respContent = "您发送的是视频消息！";
            }
            // 视频消息
            else if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_SHORTVIDEO)) {
                respContent = "您发送的是小视频消息！";
            }
            // 地理位置消息
            else if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_LOCATION)) {
                respContent = "您发送的是地理位置消息！";
            }
            // 链接消息
            else if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_LINK)) {
                respContent = "您发送的是链接消息！";
            }
            // 事件推送
            else if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_EVENT)) {
                // 事件类型
                String eventType = requestMap.get("Event");
                // 关注
                if (eventType.equals(MessageUtil.EVENT_TYPE_SUBSCRIBE)) {
                    respContent = "谢谢您的关注！";
                }
                // 取消关注
                else if (eventType.equals(MessageUtil.EVENT_TYPE_UNSUBSCRIBE)) {
                    // TODO 取消订阅后用户不会再收到公众账号发送的消息，因此不需要回复
                }
                // 扫描带参数二维码
                else if (eventType.equals(MessageUtil.EVENT_TYPE_SCAN)) {
                    // TODO 处理扫描带参数二维码事件
                }
                // 上报地理位置
                else if (eventType.equals(MessageUtil.EVENT_TYPE_LOCATION)) {
                    // TODO 处理上报地理位置事件
                }
                // 自定义菜单
                else if (eventType.equals(MessageUtil.EVENT_TYPE_CLICK)) {
                    // TODO 处理菜单点击事件
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return respXml;
    }
}
