package com.nonelonely.component.email.service.sender;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.nonelonely.common.utils.ObjectUtils;
import com.nonelonely.common.utils.ResultVoUtil;
import com.nonelonely.common.vo.ResultVo;
import com.nonelonely.component.email.enums.MailEnums;
import com.nonelonely.component.email.service.listener.SendMailTransportListener;
import com.nonelonely.component.email.util.MailConstant;
import com.nonelonely.component.email.vo.MailAttachmentVo;
import com.nonelonely.component.email.vo.MailReq;
import com.nonelonely.modules.system.domain.mail.Mail;
import com.nonelonely.modules.system.domain.mail.MailBox;
import com.nonelonely.modules.system.domain.mail.MailSendLog;
import com.nonelonely.modules.system.repository.mail.MailBoxRepository;
import com.nonelonely.modules.system.repository.mail.MailRepository;
import com.nonelonely.modules.system.service.mail.IMailSendLogService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 描述:
 * ${DESCRIPTION}
 *
 * @author nonelonely
 * @create 2021-01-28 20:47
 * 个人博客地址：https://www.nonelonely.com
 */
@Slf4j
@Component
public class SendMailService {
    @Autowired
    private SendMailTransportListener listener;
    @Autowired
    private MailRepository mailRepository;
    @Autowired
    private MailBoxRepository mailBoxRepository;

    @Autowired
    private IMailSendLogService mailSendLogService;
    /**
     * 保存数据
     * @param mailReq 实体对象
     */

    public ResultVo sendMailAsync(MailReq mailReq) {

        // 2- 得到当前用户的外部邮件的配置信息
        MailBox mailCfg = mailBoxRepository.getMailBoxByMailBoxCode(mailReq.getMailBox());
        if (mailCfg == null ) {
            log.error("未找到用户配置{}", JSON.toJSONString(mailCfg));
            return ResultVoUtil.error("未找到用户配置");
        }
        // 4- 将邮件信息保存到数据库中
        Mail mail = saveToDb(mailCfg, mailReq);
        // 5- 如果不是保存到草稿，执行邮件发送
        if (mailReq.getSaveToDrag()== null || mailReq.getSaveToDrag() != 1) {
            if (mailCfg == null || mail == null) {
                log.error("邮件内容不存在{}", JSON.toJSONString(mailCfg), JSON.toJSONString(mail));
                return ResultVoUtil.error("邮件内容不存在");
            }
            // 3- 构建邮件发送对象，并发送邮件
            sendMailAsync(convertToSendingMail(mail), mailCfg);
        }
        return ResultVoUtil.success();
    }


    public ResultVo sendMail(MailReq mailReq) {

        // 2- 得到当前用户的外部邮件的配置信息
        MailBox mailCfg = mailBoxRepository.getMailBoxByMailBoxCode(mailReq.getMailBox());
        if (mailCfg == null ) {
            log.error("未找到用户配置{}", JSON.toJSONString(mailCfg));
            return ResultVoUtil.error("未找到用户配置");
        }
        // 4- 将邮件信息保存到数据库中
        Mail mail = saveToDb(mailCfg, mailReq);
        // 5- 如果不是保存到草稿，执行邮件发送
        if (mailReq.getSaveToDrag()== null || mailReq.getSaveToDrag() != 1) {
            if (mailCfg == null || mail == null) {
                log.error("邮件内容不存在{}", JSON.toJSONString(mailCfg), JSON.toJSONString(mail));
                return ResultVoUtil.error("邮件内容不存在");
            }
            try {
                // 3- 构建邮件发送对象，并发送邮件
                sendMail(convertToSendingMail(mail), mailCfg);
            }catch (Exception e){
                log.error(e.getMessage());
                e.printStackTrace();
                return ResultVoUtil.error("发送邮件失败");
            }
        }
        return ResultVoUtil.success();
    }
    /**
     * 保存邮件信息到数据库
     *
     * @param mailCfg 外部邮箱配置
     * @param mailReq 邮件对象
     * @return
     */
    private Mail saveToDb(MailBox mailCfg, MailReq mailReq) {
        // 1- 转换邮件对象
        Mail mail = convertToMail(mailCfg, mailReq);
        // 2- 特殊情况处理
        // 2.1- 如果是保存到草稿箱，将文件id修改为草稿箱对应id
        if (mailReq.getSaveToDrag()!=null && mailReq.getSaveToDrag() == 1) {
            //mail.setFolderId(Long.valueOf(MailEnums.MailFloderEnum.DRAFT.getCode()));
            mail.setMailType(MailEnums.MailTypeEnum.MAIL_TYPE_DRAFTS.getCode());
        }else{
            mail.setMailType(MailEnums.MailTypeEnum.MAIL_TYPE_SEND_MESSAGES.getCode());
        }
        // 2.2 校验邮件是否来自草稿箱，如果不是ID置为空（解决转发、回复带id问题）
        if (mailReq.getId() != null) {
            //先查询
            Mail mailTemp = mailRepository.getById(mailReq.getId());
            //以下条件ID置为空：查询结果为空，不是草稿箱邮件
            if (mailTemp == null || mailTemp.getMailType() != MailEnums.MailTypeEnum.MAIL_TYPE_DRAFTS.getCode()) {
                mailReq.setId(null);
            }

        }
        mail.setIsRead(MailConstant.MAIL_IS_READ);
        mailRepository.save(mail);
        return mail;
    }

    /**
     * 转换邮件实体类
     *
     * @param mailCfg 外部邮箱配置
     * @param mailReq 邮件对象
     * @return
     */
    private Mail convertToMail(MailBox mailCfg, MailReq mailReq) {
        // 1- 转换对象
        Mail mail = ObjectUtils.merge(new Mail(), mailReq);
        // 转换收件人json、抄送人json、密送人json
//        mail.setToUserJson(convertToString(mailReq.getToUserJson()));
//        mail.setCcUserJson(convertToString(mailReq.getCcUserJson()));
//        mail.setSsUserJson(convertToString(mailReq.getSsUserJson()));

        // 计算邮件文件和内容大小
        cacSize(mail);

        // 邮件发送人
        mail.setFromUser(mailCfg.getMailBoxCode());
        // 邮件类型
        mail.setMailType(MailEnums.MailTypeEnum.MAIL_TYPE_SEND_MESSAGES.getCode());
        // 所属文件夹
        // 默认保存到已发送，只有指定不保存到已发送的情况下才不对文件夹id赋值
//        if (MailEnums.MailSaveSentEnum.NOSAVE.getCode() != mailReq.getSaveToSent()) {
//            mail.setFolderId(Long.valueOf(MailEnums.MailFloderEnum.SEND.getCode()));
//        }
        // 邮件内容类型
        mail.setContType(MailEnums.MailContentTypeEnum.HTML.getCode());
        // 邮件状态标志
//        mail.setMailStatusFlag(MailEnums.MailTypeEnum.MAIL_TYPE_SEND_MESSAGES.getCode());
        // 邮件发送时间
        mail.setSendTime(new Date());
        //邮件收取方式1：IMAP  2：POP3
        mail.setMailBoxType(mailCfg.getMailBoxType());
        return mail;
    }

    /**
     * 计算邮件大小
     *
     * @param mail 邮件对象
     */
    private void cacSize(Mail mail) {
        try {
            double size = 0;
            if (StringUtils.isNotBlank(mail.getAttaIds())) {
                JSONArray array = JSON.parseArray(mail.getAttaIds());
                for (int i = 0; i < array.size(); i++) {
                    JSONObject item = array.getJSONObject(i);
                    size += getBytes(item.getString("size"));
                }
            }
            if (StringUtils.isNotBlank(mail.getMailContHtml())) {
                // 一个字节有两个字符
                size += mail.getMailContHtml().length() * 2;
            }
            mail.setMailSize(String.format("%.2f", size / 1024) + "KB");
        } catch (Exception e) {
            log.error("计算邮件大小出现问题");
            e.printStackTrace();
        }
    }

    /**
     * string转字节数
     *
     * @param size 字符串
     * @return 返回字节数
     */
    private double getBytes(String size) {
        if (StringUtils.isBlank(size)) {
            return 0;
        }
        size = size.toLowerCase();
        if (size.endsWith("kb")) {
            return Double.parseDouble(size.replace("kb", "")) * 1024;
        } else if (size.endsWith("mb")) {
            return Double.parseDouble(size.replace("mb", "")) * 1024 * 1024;
        } else if (size.endsWith("m")) {
            return Double.parseDouble(size.replace("m", "")) * 1024 * 1024;
        } else if (size.endsWith("b")) {
            return Double.parseDouble(size.replace("b", ""));
        }
        return 0;
    }

    /**
     * 转换邮件发送对象
     *
     * @param mail 邮件对象
     * @return
     */
    private SendingMail convertToSendingMail(Mail mail) {
        SendingMail sendingMail = new SendingMail();
        // 1- 设置基本信息
        sendingMail.setId(String.valueOf(mail.getId()));
        sendingMail.setToUsers(mail.getToUser());
        sendingMail.setCcUsers(mail.getCcUser());
        sendingMail.setBccUsers(mail.getSsUser());
        sendingMail.setSubject(mail.getTitle());
        sendingMail.setHtml(mail.getMailContHtml());
        // 2- 设置附件
        if (StringUtils.isNotBlank(mail.getAttaIds())) {
            String attaIds = mail.getAttaIds().replaceAll("\\\"", "\"");
            log.info("{}邮件附件信息为{}", mail.getTitle(), attaIds);
            List<JSONObject> jsonArray = JSON.parseArray(attaIds, JSONObject.class);
            if (jsonArray != null && jsonArray.size() > 0) {
                List<MailAttachmentVo> attachmentList = new ArrayList();
                jsonArray.forEach(item -> {
                    MailAttachmentVo mailAttachment = new MailAttachmentVo();
                    mailAttachment.setName(item.getString("name"));
                    mailAttachment.setUrl(item.getString("url"));
                    mailAttachment.setSize(item.getLong("size"));
                    attachmentList.add(mailAttachment);
                });
                sendingMail.setAttachmentList(attachmentList);
            }
        }
        // 3- 设置回执
        if (mail.getReadReceipt() != null && MailEnums.MailReceiptEnum.NEED.getCode() == mail.getReadReceipt()) {
            sendingMail.getMailHeader().put("Disposition-Notification-To", mail.getFromUser());
        }
        // 4- 设置紧急程度 1 紧急
        if (mail.getUrgent() != null && MailEnums.MailUrgentEnum.URGENT.getCode() == mail.getUrgent()) {
            sendingMail.getMailHeader().put("X-Priority", String.valueOf(mail.getUrgent()));
        }

        return sendingMail;
    }
    private void sendMailAsync(SendingMail mail, MailBox mailCfg) {
        // 1- 插入邮件发送日志
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("mail", mail);
        jsonObject.put("mailCfg", mailCfg);
        MailSendLog mailSendLog = mailSendLogService.saveMailLog( mail.getId(), mailCfg.getMailBoxCode(), jsonObject.toJSONString());
        try {
            // 构建对象，并执行邮件的异步发送
            new CommonMailSender(mailCfg, listener).sendMailAsync(mail);
        } catch (Exception e) {
            mailSendLogService.updateMailStatus(mailSendLog.getId(), 2, e.getMessage());
            log.error("发送邮件【" + mail.getSubject() + "】发送异常！", e);
        }

    }
    private void sendMail(SendingMail mail, MailBox mailCfg) throws  Exception{
        // 1- 插入邮件发送日志
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("mail", mail);
        jsonObject.put("mailCfg", mailCfg);
        MailSendLog mailSendLog = mailSendLogService.saveMailLog( mail.getId(), mailCfg.getMailBoxCode(), jsonObject.toJSONString());
        try {
            // 构建对象，并执行邮件的异步发送
            new CommonMailSender(mailCfg, listener).sendMail(mail);
        } catch (Exception e) {
            mailSendLogService.updateMailStatus(mailSendLog.getId(), 2, e.getMessage());
            log.error("发送邮件【" + mail.getSubject() + "】发送异常！", e);
            throw e;
        }



    }
}
