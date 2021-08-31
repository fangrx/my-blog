package com.nonelonely.component.email.service.task;


import com.nonelonely.component.email.service.sender.SendMailService;
import com.nonelonely.component.email.util.MailCache;
import com.nonelonely.component.email.vo.MailReq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;



/**
 * 描述:
 * ${DESCRIPTION}
 *
 * @author nonelonely
 * @create 2021-01-27 23:02
 * 个人博客地址：https://www.nonelonely.com
 */
@Component
@EnableScheduling
@Slf4j
public class SendMailTask {
    @Autowired
    private SendMailService mailService;
    /**
     * 邮件队列读取线程类，从待解析的队列中读取邮件并解析
     */
    @Scheduled(cron = "*/1 * * * * ?")
    @Async
    public void sendMails() {
        try {
            int newMailCount = MailCache.getSendMailQueue().size();

            // 是否存在新邮件
            if (newMailCount > 0) {
                // 一次执行100封并发邮件
                for (int i = 0; i < (newMailCount < 100 ? newMailCount : 100); i++) {
                    MailReq message = MailCache.getSendMailQueue().poll();
                    if (message != null) {
                        mailService.sendMailAsync(message);
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

