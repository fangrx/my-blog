package com.nonelonely.component.webSocket.handler;



import com.nonelonely.component.webSSH.constant.ConstantPool;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;

import java.nio.charset.Charset;
import java.util.concurrent.CopyOnWriteArraySet;


/**
* @Description: WebSSH的WebSocket处理器
* @Author: NoCortY
* @Date: 2020/3/8
*/
@Component
public class LogWebSocketHandler implements WebSocketHandler{

    private Logger logger = LoggerFactory.getLogger(LogWebSocketHandler.class);

    private CopyOnWriteArraySet<WebSocketSession> webSocketSet = new CopyOnWriteArraySet<WebSocketSession>();


    public  LogWebSocketHandler(){

        final LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        final org.apache.logging.log4j.core.config.Configuration config = ctx.getConfiguration();
        final PatternLayout layout = PatternLayout.newBuilder()
                .withCharset(Charset.forName("UTF-8"))
                .withConfiguration(config)
                .withPattern("%d{HH:mm:ss.SSS} [%t] %-5level %logger{36} - %msg%n")
                .build();
        StatisticsAppender statisticsAppender= StatisticsAppender.createAppender("statisticsAppender",null,layout);
        statisticsAppender.start();
        config.addAppender(statisticsAppender);
        config.getLoggerConfig("com.nonelonely").addAppender(statisticsAppender, Level.INFO, null);
        config.getLoggerConfig("org.hibernate.SQL").addAppender(statisticsAppender, Level.DEBUG, null);

        ctx.updateLoggers(config);
    }

    //定义一个记录客户端的聊天昵称

    /**
     * @Description: 用户连接上WebSocket的回调
     * @Param: [webSocketSession]
     * @return: void
     * @Author: NoCortY
     * @Date: 2020/3/8
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession webSocketSession) throws Exception {
        logger.info("用户:{},连接WebSSH"+ webSocketSession.getAttributes().get(ConstantPool.USER_UUID_KEY));
        webSocketSet.add(webSocketSession);
    }

    /**
     * @Description: 收到消息的回调
     * @Param: [webSocketSession, webSocketMessage]
     * @return: void
     * @Author: NoCortY
     * @Date: 2020/3/8
     */
    @Override
    public void handleMessage(WebSocketSession webSocketSession, WebSocketMessage<?> webSocketMessage) throws Exception {

    }

    /**
     * @Description: 出现错误的回调
     * @Param: [webSocketSession, throwable]
     * @return: void
     * @Author: NoCortY
     * @Date: 2020/3/8
     */
    @Override
    public void handleTransportError(WebSocketSession webSocketSession, Throwable throwable) throws Exception {
        logger.error("LogWebSocketHandler数据传输错误");

    }

    /**
     * @Description: 连接关闭的回调
     * @Param: [webSocketSession, closeStatus]
     * @return: void
     * @Author: NoCortY
     * @Date: 2020/3/8
     */
    @Override
    public void afterConnectionClosed(WebSocketSession webSocketSession, CloseStatus closeStatus) throws Exception {
        logger.info("用户:{}断开webssh连接"+ String.valueOf(webSocketSession.getAttributes().get(ConstantPool.USER_UUID_KEY)));
        webSocketSet.remove(webSocketSession);

    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    public CopyOnWriteArraySet<WebSocketSession> getWebSocketSet(){

        return webSocketSet;
    }
}
