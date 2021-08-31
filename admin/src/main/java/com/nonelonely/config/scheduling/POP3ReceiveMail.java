package com.nonelonely.config.scheduling;


import com.nonelonely.component.email.service.task.GetMailTask;
import com.nonelonely.component.scheduledTask.config.ScheduledTaskJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;



/**
 * 使用POP3协议接收邮件
 */
@Component
@Slf4j
public class POP3ReceiveMail implements ScheduledTaskJob {
    @Autowired
    private GetMailTask getMailTask;

    @Override
    public void run()  {
        log.info("》》》》》》开始获取邮件");
        getMailTask.restart();
        log.info("》》》》》》结束获取邮件");
    }
}
