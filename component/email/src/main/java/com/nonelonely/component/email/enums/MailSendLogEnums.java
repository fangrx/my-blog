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
public interface MailSendLogEnums {
    /**
     * 邮件发送状态
     */
    @Getter
    enum MailSendStatusEnum {
        /**
         * 发送中
         */
        SENDING(0, "发送中"),
        /**
         * 发送成功
         */
        SUCCESS(1, "发送成功"),
        /**
         * 发送失败
         */
        FAIL(1, "发送失败");

        /**
         * 编码
         */
        int code;

        /**
         * 描述
         */
        String desc;

        MailSendStatusEnum(int code, String desc) {
            this.code = code;
            this.desc = desc;
        }
    }

}
