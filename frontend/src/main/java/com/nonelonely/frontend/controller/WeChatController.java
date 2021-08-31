package com.nonelonely.frontend.controller;

import com.nonelonely.component.weChat.service.CoreService;
import com.nonelonely.component.weChat.util.SignUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Controller
@RequestMapping("/api")
public class WeChatController {
    /**
     * 将我们的工具类应用到我们的服务器验证过程中
     * @param signature
     * @param timestamp
     * @param nonce
     * @param echostr
     * @return
     */
    @GetMapping("/wechat")
    @ResponseBody
    public String renZheng(String signature,String timestamp,String nonce,String echostr)
    {
        if (SignUtil.checkSignature(signature, timestamp, nonce)) {
            return (echostr);
        }
        return null;
    }

    /**
     *  接收微信公众号发来的信息
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    @PostMapping("/wechat")
    public void doRandle(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException
    {
        // 消息的接收、处理、响应
        // 将请求、响应的编码均设置为UTF-8（防止中文乱码）
        response.setCharacterEncoding("UTF-8");

        // 调用核心业务类接收消息、处理消息
        String respXml = CoreService.processRequest(request);

        // 响应消息
        PrintWriter out = response.getWriter();
        out.print(respXml);
        out.close();



    }
}
