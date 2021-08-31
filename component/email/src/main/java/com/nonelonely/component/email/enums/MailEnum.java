package com.nonelonely.component.email.enums;

/**
 * 描述:邮件相关的枚举类
 *
 * @author yan
 */
public class MailEnum {

    //邮件-文本类型
    public static final int MAIL_CONTENT_TYPE_TEXT = 1;

    //邮件-HTML类型
    public static final int MAIL_CONTENT_TYPE_HTML = 2;

    //邮件类型-接收
    public static final int MAIL_TYPE_RECEIVE = 1;
    //邮件类型-回复
    public static final int MAIL_TYPE_REPLY = 2;
    //邮件类型-草稿
    public static final int MAIL_TYPE_DRAFT = 3;

    //邮件服务器协议类型-IMAP
    public static final int MAIL_TYPE_IMAP = 1;
    //邮件服务器协议类型-pop3
    public static final int MAIL_TYPE_POP3 = 2;

    // 状态正常
    public static final int MAIL_RECEIVER_STATUS_OK = 1;
    // 状态异常
    public static final int MAIL_RECEIVER_STATUS_ERR = 2;


}
