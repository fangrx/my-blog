package com.nonelonely.component.webSocket.handler;

import com.nonelonely.common.utils.SpringContextUtil;
import com.nonelonely.modules.system.domain.ErrorLog;
import com.nonelonely.modules.system.service.ErrorLogService;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.appender.RollingFileAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.springframework.context.ApplicationContext;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.Serializable;
import java.util.concurrent.CopyOnWriteArraySet;

import static com.nonelonely.common.utils.SpringContextUtil.getApplicationContext;

/**
 * 描述:
 * ${DESCRIPTION}
 *
 * @author nonelonely
 * @create 2021-02-01 19:31
 * 个人博客地址：https://www.nonelonely.com
 */
@Plugin(name = "Statistics", category = "Core", elementType = "appender", printObject = true)
public class StatisticsAppender extends AbstractAppender {

    protected StatisticsAppender(String name, Filter filter, Layout<? extends Serializable> layout) {
        super(name, filter, layout);

    }


    @Override
    public void append(LogEvent logEvent) {
          try {
               String msg = new String(getLayout().toByteArray(logEvent),"Utf-8");
                ApplicationContext applicationContext = SpringContextUtil.getApplicationContext();
                if (applicationContext!=null) {
                    if(logEvent.getLevel().intLevel()== Level.ERROR.intLevel()){
                        ErrorLogService errorLogService = (ErrorLogService) SpringContextUtil.getBean("errorLogServiceImpl");
                        if (errorLogService != null) {
                            ErrorLog errorLog = new ErrorLog();
                            errorLog.setContent(msg);
                            errorLog.setLevel(logEvent.getLevel().name());
                            errorLog.setThreadName(logEvent.getThreadName());
                            errorLogService.save(errorLog);
                        }

                    }
                    LogWebSocketHandler logWebSocketHandler = (LogWebSocketHandler) SpringContextUtil.getBean("logWebSocketHandler");
                    CopyOnWriteArraySet<WebSocketSession> list =logWebSocketHandler.getWebSocketSet();
                    for (WebSocketSession webSocketSession : list){

                        webSocketSession.sendMessage(new TextMessage(msg));

                    }
                 }
               //  System.out.println(msg);
          }catch (Exception e){
              e.printStackTrace();
          }

    }

    @PluginFactory
    public static StatisticsAppender createAppender(@PluginAttribute("name") String name,
                                                    @PluginElement("Filter") final Filter filter,
                                                    @PluginElement("Layout") Layout<? extends Serializable> layout) {
        if (name == null) {
            LOGGER.error("No name provided for MyCustomAppenderImpl");
            return null;
        }
        if (layout == null) {
            layout = PatternLayout.createDefaultLayout();
        }

        return new StatisticsAppender(name, filter, layout);
    }

}
