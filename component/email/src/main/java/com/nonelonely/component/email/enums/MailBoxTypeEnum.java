package com.nonelonely.component.email.enums;

import lombok.Getter;


/**
 * 邮件协议类型 枚举
 */
@Getter
public enum MailBoxTypeEnum {

    /**
     * imap
     */
    MAIL_BOX_TYPE_IMAP(1, "IMAP"),

    /**
     * pop3
     */
    MAIL_BOX_TYPE_POP3(2, "POP3"),;


    /**
     * 返回码
     */
    private final int code;

    /**
     * 返回消息
     */
    private final String msg;

    MailBoxTypeEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
