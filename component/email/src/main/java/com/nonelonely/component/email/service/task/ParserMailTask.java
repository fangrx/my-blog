package com.nonelonely.component.email.service.task;



import com.nonelonely.component.email.util.MailCache;
import com.nonelonely.component.email.util.parser.MessageParser;
import lombok.extern.slf4j.Slf4j;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.mail.Message;

/**
 * 邮件解析任务 <br>
 * 每间隔十秒处理一次新邮件，上一次处理完到下一次处理完中间间隔十秒  (修改成了1秒)
 *
 * @author nonelonely
 */
@Component
@EnableScheduling
@Slf4j
public class ParserMailTask {

    @Resource
    private MessageParser messageParser;

    /**
     * 邮件队列读取线程类，从待解析的队列中读取邮件并解析
     */
    @Scheduled(cron = "*/1 * * * * ?")
    @Async
    public void parseNewMails() {
        try {
            int newMailCount = MailCache.getNewMailQueue().size();
            if (newMailCount>0) {
                log.info("新邮件数量：------> :" + newMailCount);
            }
            // 是否存在新邮件
            if (newMailCount > 0) {
                // 一次执行100封并发邮件
                for (int i = 0; i < (newMailCount < 100 ? newMailCount : 100); i++) {
                    Message message = MailCache.getNewMailQueue().poll();
                    if (message != null) {
                        log.info("开始解析新邮件<" + message.getSubject() + ">");
                        messageParser.parse(message);
                    }
                }
            } else {
               // log.info("系统暂没有新邮件。。");
            }
        } catch (Exception e) {
            log.error("解析邮件发生异常", e);
        }
    }


}
