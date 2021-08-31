package com.nonelonely.component.webSocket.config;



import com.nonelonely.component.webSocket.handler.LogWebSocketHandler;
import com.nonelonely.component.webSocket.handler.WebSSHWebSocketHandler;
import com.nonelonely.component.webSocket.interceptor.WebSocketInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
* @Description: websocket配置
* @Author: NoCortY
* @Date: 2020/3/8
*/
@Configuration
@EnableWebSocket
public class WebSSHWebSocketConfig implements WebSocketConfigurer{
    @Autowired
    WebSSHWebSocketHandler webSSHWebSocketHandler;
    @Autowired
    LogWebSocketHandler logWebSocketHandler;
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry webSocketHandlerRegistry) {
        //socket通道
        //指定处理器和路径
        webSocketHandlerRegistry.addHandler(webSSHWebSocketHandler, "/webSocket/webssh")
                 .addHandler(logWebSocketHandler, "/webSocket/log")
                .addInterceptors(new WebSocketInterceptor())
                .setAllowedOrigins("*");
    }
}
