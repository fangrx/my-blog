package com.nonelonely.component.email.enums;

import lombok.Getter;


/**
 * 邮件类型 枚举
 */
@Getter
public enum MailTypeEnum {

    /**
     * 收件箱
     */
    MAIL_TYPE_INBOX(1, "INBOX"),

    /**
     * 已发送
     */
    MAIL_TYPE_SEND_MESSAGES(2, "Send Messages"),

    /**
     * 草稿箱
     */
    MAIL_TYPE_DRAFTS(3, "Drafts"),

    /**
     * 已删除
     */
    MAIL_TYPE_DELETED_MESSAGES(4, "Deleted Messages"),

    /**
     * 垃圾箱
     */
    MAIL_TYPE_JUNK(5, "Junk"),

    /**
     * 未知
     */
    MAIL_TYPE_X(9, "X");


    /**
     * 返回码
     */
    private final int code;

    /**
     * 返回消息
     */
    private final String msg;

    MailTypeEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
