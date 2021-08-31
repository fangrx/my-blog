package com.nonelonely.component.email.service.listener;

import com.alibaba.fastjson.JSONObject;

import com.nonelonely.component.email.enums.MailSendLogEnums;
import com.nonelonely.component.email.util.MailMessage;
import com.nonelonely.modules.system.domain.mail.Mail;
import com.nonelonely.modules.system.service.mail.IMailSendLogService;
import com.nonelonely.modules.system.service.mail.IMailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;


import org.springframework.stereotype.Component;

import javax.mail.event.TransportEvent;
import javax.mail.event.TransportListener;


/**
 * 描述:邮件发送的监听器
 *
 * @author 杨建全
 * @date 2017年5月26日 下午4:24:42
 */
@Slf4j
@Component
public class SendMailTransportListener implements TransportListener {



    @Autowired
    IMailService mailService;
    @Autowired
    IMailSendLogService mailSendLogService;

    /**
     * 邮件发送成功监听
     *
     * @param e
     */
    @Override
    public void messageDelivered(TransportEvent e) {
        writeMailInfo(e, MailSendLogEnums.MailSendStatusEnum.SUCCESS.getCode());
    }

    /**
     * 邮件发送失败监听
     *
     * @param e
     */
    @Override
    public void messageNotDelivered(TransportEvent e) {
        writeMailInfo(e, MailSendLogEnums.MailSendStatusEnum.FAIL.getCode());
    }

    /**
     * 邮件发送部分成功监听
     *
     * @param e
     */
    @Override
    public void messagePartiallyDelivered(TransportEvent e) {
        writeMailInfo(e, MailSendLogEnums.MailSendStatusEnum.SUCCESS.getCode());
    }

    /**
     * 回写邮件信息
     *
     * @param e
     * @param status 发送状态
     */
    private void writeMailInfo(TransportEvent e, int status) {
        try {
            // 1- 得到消息内容
            MailMessage message = (MailMessage) e.getMessage();
            // 2- 更新发送日志
            updateMailSendStatus(e, status, message.getId());
            // 3- 更新邮件信息
            updateMailInfo(Long.valueOf(message.getId()), message.getMessageID());
        } catch (Exception e1) {
            log.error("", e1);
        }
    }

    /**
     * 更新邮件发送状态
     *
     * @param e      事件
     * @param mailId 邮件id啦
     */
    private void updateMailSendStatus(TransportEvent e, int status, String mailId) {
        // 收集发送结果
        JSONObject json = new JSONObject();
        json.put("validSent", e.getValidSentAddresses());
        json.put("validUnSent", e.getValidUnsentAddresses());
        // 更新邮件发送状态
        mailSendLogService.updateMailStatusByMailId(mailId, status, json.toJSONString());
    }

    /**
     * 更新邮件信息
     *
     * @param mailId    邮件id
     * @param sysMailId 系统该邮件id
     */
    private void updateMailInfo(Long mailId, String sysMailId) {

        Mail mail = mailService.getById(mailId);
        mail.setId(mailId);
        mail.setSysMailId(sysMailId);
        mailService.save(mail);
    }
}
