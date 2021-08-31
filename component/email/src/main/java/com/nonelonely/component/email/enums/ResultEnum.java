package com.nonelonely.component.email.enums;

import lombok.Getter;

/**
 * @author caojingchen
 * @since 2020/4/20 16:06
 **/
@Getter
public enum ResultEnum {

    /**
     * 成功
     */
    SUCCESS(0, "成功"),
    /**
     * 失败
     */
    FAIL(1, "失败"),
    /**
     * 业务处理失败，一般作为无法拦截的意外异常的返回
     */
    BUSINESS_ERROR(999, "业务处理失败"),
    /**
     * 参数缺失，当请求参数不满足必填校验时，使用
     */
    PARAM_MISSING(900, "参数缺失"),
    /**
     * 当目标数据已经存在时报错
     */
    RECORD_EXISTS(901, "当前记录已经存在"),
    /**
     * 数据的状态字段不符合当前场景要求
     */
    STATUS_ERROR(902, "数据状态错误"),
    /**
     * 当操作的目标数据不存在时
     */
    RECORD_NOT_EXISTS(903, "当前记录不存在"),
    /**
     * 权限验证失败
     */
    AUTH_ERROR(904, "认证失败"),

    SENSITIVE_ILLEGAL(905, "敏感词校验不合法"),


    MAILBOX_IS_EXISTS(1000, "邮箱账号已存在或者该已经注册过账号"),

    MAILBOX_INSERT_ERROR(1001, "邮箱账号入库错误"),

    MAILBOX_UPDATE_ERROR(1002, "帐号更新失败或者Id不存在"),
    MAILBOX_UPDATE_IS_EXISTS(1003, "账号已存在 ，无需更新"),
    MAILBOX_AUTH_ERROR(1004, "邮箱认证错误"),
    MAILBOX_CHECK_ERROR(1005, "邮箱验证错误"),
    MAILBOX_POP3_CHECK_ERROR(1006, "POP3服务器或者账号信息错误，请检查 :"),
    MAILBOX_AUTH_CHECK_ERROR(1007, "授权错误:"),
    MAILBOX_OTHER_CHECK_ERROR(1008, "其他错误:"),
    MAILBOX_INIT_ERROR(1009, "初始化错误:"),
    MAILBOX_DATA_ERROR(1010, "帐号配置错误，请检查输入项:"),
    MAILBOX_PORT_ERROR(1011, "帐号配置端口号有误，请检查输入项目"),
    MAILBOX_POP3_PARAM_MISSING(1011, "POP3帐号配置缺失，请检查输入项目"),
    MAILBOX_IMAP_PARAM_MISSING(1012, "IMAP帐号配置缺失，请检查输入项目"),
    MAILBOX_SSL_PARAM_MISSING(1013, "帐号配置缺失，请检查SSL输入项目"),
    MAILBOX_PORT_IMAP_SSL_ERROR(1012, "帐号配置端口号有误，请检查SSL模式下的IMAP端口号配置"),
    MAILBOX_PORT_POP3_SSL_ERROR(1013, "帐号配置端口号有误，请检查SSL模式下的POP3端口号配置"),
    MAILBOX_PORT_IMAP_ERROR(1014, "帐号配置端口号有误，请检查非SSL模式下的IMAP端口号配置"),
    MAILBOX_PORT_POP3_ERROR(1015, "帐号配置端口号有误，请检查非SSL模式下的POP3端口号配置"),
    MAILBOX_PORT_SMTP_ERROR(1016, "帐号配置端口号有误，请检查非SSL模式下的SMTP端口号配置"),
    MAILBOX_PORT_SMTP_SSL_ERROR(1017, "帐号配置端口号有误，请检查SSL模式下的SMTP端口号配置"),
    MAILBOX_BOX_EXISTS(1018, "该邮箱帐号已存在 , 请核实"),
    MAILBOX_CONNECT_ERR(1019, "无法连接到服务器"),
    MAILBOX_CONNECT_OK(1020, "配置成功，如邮件过多，系统将花费一定时间，将服务器邮件通过后台程序同步到系统中。"),;

    /**
     * 返回码
     */
    private final int code;

    /**
     * 返回消息
     */
    private final String msg;

    ResultEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
