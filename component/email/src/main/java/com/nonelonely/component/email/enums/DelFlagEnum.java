package com.nonelonely.component.email.enums;

import lombok.Getter;

/**
 * <p>
 * 标记删除枚举类
 * </p>
 *
 * @author liuming
 * @since 2020-09-15
 */
@Getter
public enum DelFlagEnum {
    /**
     * 删除
     */
    DELETE(0, "是"),
    /**
     * 未删除
     */
    NONE(1, "否");

    /**
     * 编码
     */
    int code;

    /**
     * 描述
     */
    String desc;

    DelFlagEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
