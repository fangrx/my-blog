package com.nonelonely.component.email.enums;

import lombok.Getter;

/**
 * <p>
 * 邮件枚举类
 * </p>
 *
 * @author liuming
 * @since 2020-09-15
 */
public interface MailEnums {
    /**
     * 回执类型枚举
     */
    @Getter
    enum MailReceiptEnum {
        /**
         * 需要回执
         */
        NEED(1, "需要"),
        /**
         * 不需要回执
         */
        NONEED(2, "不需要");

        /**
         * 编码
         */
        int code;

        /**
         * 描述
         */
        String desc;

        MailReceiptEnum(int code, String desc) {
            this.code = code;
            this.desc = desc;
        }
    }

    /**
     * 紧急程度枚举
     */
    @Getter
    enum MailUrgentEnum {
        /**
         * 紧急
         */
        URGENT(1, "紧急"),
        /**
         * 不紧急
         */
        NOURGENT(2, "不紧急");

        /**
         * 编码
         */
        int code;

        /**
         * 描述
         */
        String desc;

        MailUrgentEnum(int code, String desc) {
            this.code = code;
            this.desc = desc;
        }
    }

    /**
     * 是否保存到已发送枚举
     */
    @Getter
    enum MailSaveSentEnum {
        /**
         * 紧急
         */
        SAVE(1, "保存到已发送"),
        /**
         * 不紧急
         */
        NOSAVE(2, "不保存到已发送");

        /**
         * 编码
         */
        int code;

        /**
         * 描述
         */
        String desc;

        MailSaveSentEnum(int code, String desc) {
            this.code = code;
            this.desc = desc;
        }
    }

    /**
     * 邮件内容类型
     */
    @Getter
    enum MailContentTypeEnum {
        /**
         * html类型
         */
        HTML(1, "html类型"),
        /**
         * 文本类型
         */
        TXT(2, "文本");

        /**
         * 编码
         */
        int code;

        /**
         * 描述
         */
        String desc;

        MailContentTypeEnum(int code, String desc) {
            this.code = code;
            this.desc = desc;
        }
    }

    /**
     * 邮件文件夹枚举
     */
    @Getter
    enum MailFloderEnum {
        /**
         * 收件箱
         */
        INBOX(1, "收件箱"),
        /**
         * 草稿箱
         */
        DRAFT(2, "草稿箱"),
        /**
         * 已发送
         */
        SEND(3, "已发送"),
        /**
         * 已删除
         */
        DELETE(4, "已删除"),;

        /**
         * 编码
         */
        int code;

        /**
         * 描述
         */
        String desc;

        MailFloderEnum(int code, String desc) {
            this.code = code;
            this.desc = desc;
        }
    }

    /**
     * 邮件类型 枚举
     */
    @Getter
    enum MailTypeEnum {

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

    /**
     * 邮件认证枚举
     */
    @Getter
    enum MailAuthEnum {
        /**
         * 名称
         */
        NAME("name", "myname"),
        /**
         * 版本
         */
        VERSION("version", "1.0.0"),
        /**
         * 客户端
         */
        VENDOR("vendor", "myclient"),
        /**
         * 地址
         */
        SUPPORT_EMAIL("support-email", "testmail@test.com"),

        // SSL 验证
        SSL_FACTORY("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory"),

        // SSL 回调
        SSL_FALLBACK("mail.smtp.socketFactory.fallback", "false"),;

        /**
         * 编码
         */
        String code;

        /**
         * 描述
         */
        String desc;

        MailAuthEnum(String code, String desc) {
            this.code = code;
            this.desc = desc;
        }
    }

    /**
     * 邮件配置类枚举
     */
    @Getter
    enum MailConfigEnum {
        /**
         * 名称
         */
        MAIL_IMAP_FETCHSIZE("mail.imap.fetchsize", 4 * 1024 * 1024),
        MAIL_POP3_FETCHSIZE("mail.pop3.fetchsize", 4 * 1024 * 1024),;

        /**
         * 编码
         */
        int code;

        /**
         * 描述
         */
        String desc;

        MailConfigEnum(String desc, int code) {
            this.code = code;
            this.desc = desc;
        }
    }
}
