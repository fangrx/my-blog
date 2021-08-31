package com.nonelonely.component.email.enums;

import lombok.Getter;

/**
 * <p>
 * 邮件签名枚举类
 * </p>
 *
 * @author liuming
 * @since 2020-09-15
 */
public interface MailSignEnums {
    /**
     * 是否默认签名枚举类
     */
    @Getter
    enum MailSignDefaultTypeEnum {
        /**
         * 默认签名
         */
        DEFAULT(1, "是"),
        /**
         * 非默认签名
         */
        NONE(2, "否");

        /**
         * 编码
         */
        int code;

        /**
         * 描述
         */
        String desc;

        MailSignDefaultTypeEnum(int code, String desc) {
            this.code = code;
            this.desc = desc;
        }
    }
}
