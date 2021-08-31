package com.nonelonely.component.email.enums;

import lombok.Getter;

/**
 * <p>
 * smtp加密类型
 * </p>
 *
 * @author liuming
 * @since 2020-09-15
 */
@Getter
public enum MailSmtpTypeEnum {

    /**
     * ssl加密
     */
    SSL(1, "SSL"),

    /**
     * 不加密
     */
    NOSSL(2, "NOSSL"),

    /**
     * 不加密
     */
    TSL(3, "TSL"),

    /**
     * 不加密
     */
    STARTTLS(4, "STARTTLS"),;

    /**
     * 编码
     */
    int code;

    /**
     * 描述
     */
    String desc;

    MailSmtpTypeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
