package com.nonelonely.component.email.util.parser;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import com.github.binarywang.java.emoji.EmojiConverter;
import com.nonelonely.common.utils.ToolUtil;

import com.nonelonely.component.email.enums.MailBoxTypeEnum;
import com.nonelonely.component.email.enums.MailEnum;
import com.nonelonely.component.email.enums.MailEnums;
import com.nonelonely.component.email.enums.MailTypeEnum;

import com.nonelonely.component.email.util.MailConstant;

import com.nonelonely.component.email.vo.MailAttachmentVo;
import com.nonelonely.component.fileUpload.FileUpload;
import com.nonelonely.modules.system.domain.Upload;
import com.nonelonely.modules.system.domain.mail.Mail;
import com.nonelonely.modules.system.repository.mail.MailRepository;
import com.nonelonely.modules.system.service.UploadService;
import com.nonelonely.modules.system.service.mail.IMailService;
import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.pop3.POP3Folder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;


import org.apache.commons.mail.util.MimeMessageParser;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.mail.*;
import javax.mail.Message.RecipientType;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;
import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 邮件解析类
 *
 * @author ben
 */
@Component
@Slf4j
public class MessageParser {
    @Autowired
    private MailRepository mailRepository;
    @Autowired
    private IMailService mailService;
    @Autowired
    UploadService uploadService;

    @Autowired
    private HtmlParse htmlParse;


    //邮件消息发送标题
    private String messageTitle = "你有一封来自发件人的新邮件";
    //邮件消息发送内容
    private String messageContent = "发件人给你发送了新邮件：邮件主题。请查收";
    // 正则
    private static Pattern pattern = Pattern.compile("<(.*?)>");

    /**
     * 解析邮件
     *
     * @param message 邮件对象
     */
    private void parse(Message message) {
        MimeMessage mimeMessage = (MimeMessage) message;
        try {
            long start = System.currentTimeMillis();
            log.info("{}：开始解析邮件时间{}", message.getSubject(), start);
            //得到邮件实体
            Mail mail = getMail(mimeMessage);
            if(null==mail){
                return;
            }
            mail.setCreateDate(new Date());
            //邮件写库
            EmojiConverter emojiConverter = EmojiConverter.getInstance();
            String content= emojiConverter.toAlias(mail.getMailContHtml());//将聊天内容进行转义
            mail.setMailContHtml(content);

            mail = mailRepository.save(mail);
//            if (saveResult) {
//                // 是否发送小叮当判断
//                if (sendCheck(message)) {
//                    //调用小叮当推送消息
//                    MessageTemp messageTemp = new MessageTemp();
//                    //标题
//                    messageTemp.setTitle(messageTitle.replace("发件人", mail.getFromUser()));
//                    //内容
//                    messageTemp.setContent(messageContent.replace("发件人", mail.getFromUser())
//                            .replace("邮件主题", mail.getTitle()));
//                    //接收人
//                    List<String> list = new ArrayList<>();
//                    list.add(mail.getUserId());
//                    messageTemp.setTo(list);
//                    messageService.sendMsg(messageTemp);
//                }
//                //保存日志存储
//                mailLogService.saveOrUpdateMailLog(mail, MailConstant.MAIL_STATUS_SUCCESS);
//
//                //保存成功删除缓存
//                delPop3MsgKey(message, mail);
//            }

            log.info("解析邮件" + mail.getTitle() + "完成");
            long end = System.currentTimeMillis();
            log.info("{}结束解析邮件时间{}时长{}", mimeMessage.getSubject(), end, end - start);
        } catch (Exception e) {
            log.error("解析邮件发生异常：", e);
        }
    }

    /**
     * 组装邮件实体
     *
     * @param mimeMessage
     * @return
     * @throws Exception
     */
    private Mail getMail(MimeMessage mimeMessage) throws Exception {
        Flags flags = mimeMessage.getFlags();
        //邮箱名
        String mailName = mimeMessage.getFolder().getStore().getURLName().getUsername();

        // 全局唯一Id
        String sysMessageId = mimeMessage.getMessageID();
        // 帐号邮件唯一索引
        String messageUID = getMessageUID(mimeMessage);
        // 部分邮箱 取不到messageId的， 默认为系统Id
        messageUID = messageUID == null ? sysMessageId : messageUID;

        // 检查邮件是否存在
        if(checkMailIsExist(messageUID , mailName)){
            //获取References
            String[] references = mimeMessage.getHeader("References");
            //回复邮件的第一封邮件的id
            String firstReferenceId = "";
            StringBuffer mailReference = new StringBuffer();
            if (null != references && references.length > 0) {
                for (String string : references) {
                    mailReference.append(string);
                }
                List<String> referencesList = getReferencesList(mailReference.toString());
                if (!referencesList.isEmpty()) {
                    firstReferenceId = referencesList.get(0);
                    log.info("邮件root_Mail_id =" + firstReferenceId);
                }
                log.info("邮件的references：" + mailReference);
            }
            MimeMessageParser parser = new MimeMessageParser(mimeMessage).parse();
            // 将html中的图片单独处理
            log.info("邮件解析之前的数据：{}, : {}", messageUID, parser.getHtmlContent());
            String htmlContent = htmlParse.parse(parser.getHtmlContent(), mailName, parser); // 获取Html内容
            log.info("邮件解析之后的数据：{}, : {}", messageUID, htmlContent);
            //获取收件人地址
            Address[] toAddress = mimeMessage.getRecipients(RecipientType.TO);
            String toAddressStr = getAddressStr(toAddress);
            //抄送人地址
            Address[] ccAddress = mimeMessage.getRecipients(RecipientType.CC);
            String ccAddressStr = getAddressStr(ccAddress);

            //组装邮件实体
            Mail mail = new Mail();
            mail.setSysMailId(sysMessageId);
            // 获取发件人地址
            mail.setFromUser(parser.getFrom());
            mail.setCcUser(ccAddressStr);
            mail.setToUser(toAddressStr);
            mail.setMailBox(mailName);
            mail.setMailSize(String.format("%.2f", mimeMessage.getSize() / 1024D)+"kb");
            // 获取纯文本邮件内容（注：有些邮件不支持html）
            mail.setMailCont(parser.getPlainContent());
            // 获取邮件主题 -- 解决乱码问题
            String title = MimeUtility.decodeText(mimeMessage.getHeader("subject")[0]);
            mail.setTitle(title);
            mail.setContType(htmlContent == null ? MailEnum.MAIL_CONTENT_TYPE_TEXT : MailEnum.MAIL_CONTENT_TYPE_HTML);
            mail.setMailType(MailEnum.MAIL_TYPE_RECEIVE);
            mail.setMessageId(messageUID);
            mail.setMailReferences(mailReference.toString());
            mail.setRootMailId(firstReferenceId);
            mail.setMailContHtml(htmlContent);
            // 设置紧急程度 1 紧急
            String[] urgent = mimeMessage.getHeader("X-Priority");
            if (urgent != null && urgent[0].equals(String.valueOf(MailEnums.MailUrgentEnum.URGENT.getCode()))) {
                mail.setUrgent(MailEnums.MailUrgentEnum.URGENT.getCode());
            } else {
                mail.setUrgent(MailEnums.MailUrgentEnum.NOURGENT.getCode());
            }

            // 判断邮件类型
            mail.setMailType(checkMailType(mimeMessage.getFolder().getFullName()));
           // mail.setFolderId((long) MailTypeEnum.MAIL_TYPE_INBOX.getCode());
            if (mimeMessage.getReceivedDate() == null) {
                log.info("获取邮件收取时间为空{}主题{}", mailName, mail.getTitle());
                //获取邮件收取时间为空，取当前时间
                mail.setReceiveTime(new Date());
            } else {
                mail.setReceiveTime(mimeMessage.getReceivedDate());
            }
            // 邮件状态
//            mail.setMailStatusFlag(mailFlagCheck(flags));
//            if (flags.contains(Flags.Flag.RECENT) || mail.getMailStatusFlag() == 999) {
//                mail.setIsRead(MailConstant.MAIL_UNREAD);
//            } else {
//                mail.setIsRead(MailConstant.MAIL_IS_READ);
//            }
            //邮件收取方式
            if (mimeMessage.getFolder() instanceof POP3Folder) {
                // pop3
                mail.setMailBoxType(MailBoxTypeEnum.MAIL_BOX_TYPE_POP3.getCode());
                // POP3收取的邮件都是未读
                mail.setIsRead(MailConstant.MAIL_UNREAD);
            } else if (mimeMessage.getFolder() instanceof IMAPFolder) {
                // imap
                mail.setMailBoxType(MailBoxTypeEnum.MAIL_BOX_TYPE_IMAP.getCode());
            }

            //邮件附件
            JSONArray jsonArray = new JSONArray();
            //邮件附件处理
            saveAttachment(mimeMessage, jsonArray);
            mail.setAttaIds(jsonArray.toJSONString());

            return mail;
        }else{
            return  null ;
        }
    }

    /**
     * 保存附件
     *
     * @param part 邮件中多个组合体中的其中一个组合体
     */
    private void saveAttachment(Part part, JSONArray jsonArray) throws MessagingException, IOException {
        log.info("邮件执行保存附件功能");
        if (part.isMimeType("multipart/*")) {
            //复杂体邮件
            Multipart multipart = (Multipart) part.getContent();
            //复杂体邮件包含多个邮件体
            int partCount = multipart.getCount();
            log.info("复杂体邮件包含多个邮件体{}", partCount);
            for (int i = 0; i < partCount; i++) {
                //获得复杂体邮件中其中一个邮件体
                BodyPart bodyPart = multipart.getBodyPart(i);
                //某一个邮件体也有可能是由多个邮件体组成的复杂体
                String disp = bodyPart.getDisposition();
                if (disp != null && (disp.equalsIgnoreCase(Part.ATTACHMENT) || disp.equalsIgnoreCase(Part.INLINE))) {
                    InputStream is = bodyPart.getInputStream();
                    String fileName = decodeText(bodyPart.getFileName());
                    Upload upload  = FileUpload.saveFile(is, fileName,"/mail/recmail",uploadService);
                    MailAttachmentVo vo = new MailAttachmentVo();
                    vo.setName(fileName);
                    vo.setSize(upload.getSize());
                    vo.setUrl(upload.getPath());
                    jsonArray.add(JSONObject.toJSONString(vo));
                } else if (bodyPart.isMimeType("multipart/*")) {
                    saveAttachment(bodyPart, jsonArray);
                } else {
                    String contentType = bodyPart.getContentType();
                    if (contentType.indexOf("name") != -1 || contentType.indexOf("application") != -1) {
                        FileUpload.saveFile(bodyPart.getInputStream(), decodeText(bodyPart.getFileName()),"/mail/recmail",uploadService);
                    }
                }
            }
        } else if (part.isMimeType("message/rfc822")) {
            saveAttachment((Part) part.getContent(), jsonArray);
        }
    }

    /**
     * 文本解码
     *
     * @param encodeText 解码MimeUtility.encodeText(String text)方法编码后的文本
     * @return 解码后的文本
     * @throws UnsupportedEncodingException
     */
    public static String decodeText(String encodeText) throws UnsupportedEncodingException {
        if (encodeText == null || "".equals(encodeText)) {
            return "";
        } else {
            return MimeUtility.decodeText(encodeText);
        }
    }

    /**
     * 邮件类型判断
     *
     * @param name
     * @return
     */
    private Integer checkMailType(String name) {
        // 收取信
        if (MailTypeEnum.MAIL_TYPE_INBOX.getMsg().equals(name)) {
            return MailTypeEnum.MAIL_TYPE_INBOX.getCode();
        } else if (MailTypeEnum.MAIL_TYPE_SEND_MESSAGES.getMsg().equals(name)) {
            // 已发送
            return MailTypeEnum.MAIL_TYPE_SEND_MESSAGES.getCode();
        } else if (MailTypeEnum.MAIL_TYPE_DRAFTS.getMsg().equals(name)) {
            // 草稿箱
            return MailTypeEnum.MAIL_TYPE_DRAFTS.getCode();
        } else if (MailTypeEnum.MAIL_TYPE_DELETED_MESSAGES.getMsg().equals(name)) {
            // 已删除
            return MailTypeEnum.MAIL_TYPE_DELETED_MESSAGES.getCode();
        } else if (MailTypeEnum.MAIL_TYPE_JUNK.getMsg().equals(name)) {
            // 垃圾箱
            return MailTypeEnum.MAIL_TYPE_JUNK.getCode();
        } else {
            // 未知
            return MailTypeEnum.MAIL_TYPE_X.getCode();
        }
    }

    /**
     * 描述：获取邮件关联id串
     *
     * @param references
     * @return
     */
    private static List<String> getReferencesList(String references) {
        List<String> referencesList = new ArrayList<>();
        Matcher m = pattern.matcher(references);
        while (m.find()) {
            referencesList.add(m.group(0));
        }
        return referencesList;
    }

    /**
     * 获取地址字符串
     *
     * @param address
     * @return
     */
    private String getAddressStr(Address[] address) {
        StringBuilder addressStr = new StringBuilder("");
        if (null != address) {
            for (Address addressPar : address) {
                String addstr = ToolUtil.getByLessThan(addressPar.toString());
                addressStr.append(addstr + ",");
            }
        }
        if (addressStr.length() < 1) {
            addressStr.append(",");
        }
        return addressStr.substring(0, addressStr.length() - 1);
    }

    @Async
    public void parse(Message... messages) {
        if (messages == null || messages.length == 0) {
            log.info("没有任何邮件");
        } else {
            for (Message m : messages) {
                if (null != m) {
                    parse(m);
                }
            }
        }
    }


    /**
     * 判断邮件状态
     *
     * @param flags
     * @return
     */
    private Integer mailFlagCheck(Flags flags) {

        // 已读
        if (flags.contains(Flags.Flag.SEEN)) {
            return 1;
        }
        // 草稿
        if (flags.contains(Flags.Flag.DRAFT)) {
            return 2;
        }
        // 垃圾箱
        if (flags.contains(Flags.Flag.FLAGGED)) {
            return 3;
        }
        // 已回复
        if (flags.contains(Flags.Flag.ANSWERED)) {
            return 4;
        }
        // 已删除
        if (flags.contains(Flags.Flag.DELETED)) {
            return 5;
        }
        // 新邮件
        if (flags.contains(Flags.Flag.RECENT)) {
            return 6;
        }
        // 自定义标记
        if (flags.contains(Flags.Flag.RECENT)) {
            return 7;
        }
        return 999;
    }

    /**
     * 获取邮件UID
     *
     * @param message
     * @return
     */
    private String getMessageUID(Message message) {
        String messageNumber = null;
        Folder folder = message.getFolder();
        try {
            if (folder instanceof POP3Folder) {
                POP3Folder inbox = (POP3Folder) folder;
                messageNumber = inbox.getUID(message);

            } else if (folder instanceof IMAPFolder) {
                IMAPFolder inbox = (IMAPFolder) folder;
                messageNumber = String.valueOf(inbox.getUID(message));
            }
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return messageNumber;
    }

    /**
     * 删除缓存中的 POP# redis
     *
     * @param message
     * @param mail
     */
    private void delPop3MsgKey(Message message, Mail mail) {
      /*  Folder folder = message.getFolder();
        if (folder instanceof POP3Folder) {
            stringRedisTemplate.delete(MailBoxCfg.USER_NEW_MAIL_POP3 + mail.getMessageId());
        } else {
            stringRedisTemplate.delete(MailBoxCfg.USER_NEW_MAIL_IMAP + mail.getMessageId());
        }*/
    }

    /**
     * redis 账号是否需要发送叮当消息 判断
     *
     * @param message
     * @return
     */
    private boolean sendCheck(Message message) {
//        MimeMessage mimeMessage = (MimeMessage) message;
//
//        // 全局唯一Id
//        String sysMessageId = null;
//        try {
//            sysMessageId = mimeMessage.getMessageID();
//        } catch (MessagingException e) {
//            return false;
//        }
//
//        // 获取最新id
//        String redisMessageId = stringRedisTemplate.opsForValue().get(MailBoxCfg.USER_NEW_MAIL_INIT + sysMessageId);
//
//        // 判断是否存在 ，如果存在 ，则说明为帐号一次拉取的邮件，不需要进行发送叮当消息
//        if (StringUtils.isEmpty(redisMessageId)) {
//            return true;
//        } else {
//            // stringRedisTemplate.delete(MailBoxCfg.USER_NEW_MAIL_INIT + sysMessageId);
//            return false;
//        }
        return false;
    }

    /**
     * 判断邮件是否存在
     * @param messageUID  邮件唯一Id
     * @param mailName 帐号
     * @return
     */
    private boolean checkMailIsExist(String messageUID , String mailName){
        // 获取数据库邮件
        if (StringUtils.isNotEmpty(messageUID) && StringUtils.isNotEmpty(mailName)) {

            int count = mailRepository.getMailByMessageId(messageUID,mailName);
            if (count > 0) {
                // 邮件已存在直接返回 ，比对模式下进行检查
                return false;
            }
        }
        return  true;
    }





}
