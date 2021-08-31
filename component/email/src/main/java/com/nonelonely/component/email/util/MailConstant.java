package com.nonelonely.component.email.util;

/**
 * 邮件使用的常量
 *
 * @author ben
 * @date 2020-09-16
 */
public interface MailConstant {

    /**
     * 删除标志 0删除 1未删除
     */
    int MAIL_DEL_FLAG_DELETE = 0;
    /**
     * 删除标志 0删除 1未删除
     */
    int MAIL_DEL_FLAG_NOT_DELETE = 1;

    /**
     * 邮件是否已读1未读2已读
     */
    int MAIL_UNREAD = 1;
    /**
     * 邮件是否已读1未读2已读
     */
    int MAIL_IS_READ = 2;

    /**
     * 邮件接收状态：1已拉取2已加入消息队列3已保存4保存失败
     */
    int MAIL_STATUS_PULL = 1;
    /**
     * 邮件接收状态：1已拉取2已加入消息队列3已保存4保存失败
     */
    int MAIL_STATUS_SAVE_REDIS = 2;
    /**
     * 邮件接收状态：1已拉取2已加入消息队列3已保存4保存失败
     */
    int MAIL_STATUS_SUCCESS = 3;
    /**
     * 邮件接收状态：1已拉取2已加入消息队列3已保存4保存失败
     */
    int MAIL_STATUS_FAIL = 4;


    //  无变化
    int MAIL_NO_DIF = 0;
    //  帐号不一致
    int MAIL_DATA_DIF = 1;
    // 协议变动
    int MAIL_TYPE_DIF = 2;
    // 密码变动 / TODO：服务器地址变动发件收件 新增
    int MAIL_PASS_DIF = 3;
    // ssl 或者端口号
    int MAIL_SSL_DIF = 4;
    // 发件服务器变动
    int MAIL_STMP_SSL_DIF = 5;


    // imap线程名称
    String fatchImapMailThreadName = "FatchImapMailTask_";
    // pop3线程名称
    String fatchPop3MailThreadName = "FatchPop3MailTask_";
}
