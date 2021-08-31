package com.nonelonely.component.email.util;


/**
 * 账号配置常量
 */
public class MailBoxCfg {

    // 邮箱认证相关 - 发送邮箱验证
    public static final String MAIL_DEBUG = "mail.debug";
    public static final String MAIL_SMTP_AUTH = "mail.smtp.auth";
    public static final String MAIL_SMTP_HOST = "mail.smtp.host";
    public static final String MAIL_SMTP_PORT = "mail.smtp.port";
    public static final String MAIL_SMTP_SOCKETFACTORY_PORT = "mail.smtp.socketFactory.port";


    // IMAP 配置相关
    public static final String MAIL_IMAP_HOST = "mail.imap.host";
    public static final String MAIL_IMAP_PORT = "mail.imap.port";
    public static final String MAIL_IMAP_SSL_ENABLE = "mail.imap.ssl.enable";

    // POP3 配置相关
    public static final String MAIL_POP3_HOST = "mail.pop3.host";
    public static final String MAIL_POP3_PORT = "mail.pop3.port";
    public static final String MAIL_POP3_SSL_ENABLE = "mail.pop3.ssl.enable";

    // 邮箱配置
    public static final String MAIL_FROM = "mail.from";
    public static final String MAIL_STORE_PROTOCOL = "mail.store.protocol";


    // 是否启动
    public static final String MAIL_CFG_TRUE = "true";
    public static final String MAIL_CFG_FALSE = "false";


    // 服务器类型
    public static final String MAIL_CFG_SMTP = "smtp";
    public static final String MAIL_CFG_IMAP = "imap";
    public static final String MAIL_CFG_IMAPS = "imaps";
    public static final String MAIL_CFG_POP3 = "pop3";
    public static final String MAIL_CFG_POP3S = "pop3s";


    // 网易地址前缀
    public static final String MAIL_CFG_163 = "163";
    public static final String MAIL_CFG_126 = "126";
    public static final String MAIL_CFG_YEAH = "yeah";

    // 初始化配置
    public static final String SPLIT_LONGPARAMETERS = "mail.mime.splitlongparameters";


    // 邮箱端口号配置
    public static final String IMAP_PORT_SSL = "993";
    public static final String IMAP_PORT = "143";
    public static final String POP3_PORT_SSL = "995";
    public static final String POP3_PORT = "110";
    public static final String SMTP_PORT_SSL = "465";
    public static final String SMTP_PORT = "25";

    // 邮箱Redis key
    // 邮箱账户与用户ID 关系
    public static final String USER_MAIL_CFG = "user_mail_cfg";
    // POP3新邮件唯一限制
    public static final String USER_NEW_MAIL_POP3 = "user_new_mail_pop3-";
    // IMAP新邮件唯一限制
    public static final String USER_NEW_MAIL_IMAP = "user_new_mail_imap-";
    // 账号初始化 邮件临时变量保存
    public static final String USER_NEW_MAIL_INIT = "user_new_mail_init-";


}
