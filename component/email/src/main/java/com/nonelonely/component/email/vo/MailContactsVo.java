package com.nonelonely.component.email.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 邮件-联系人
 *
 * @author yan
 * @Date 2020-09-18
 */
@Data
public class MailContactsVo implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;

    /**
     * 联系人姓名
     */
    private String name;
    /**
     * 联系人手机号
     */
    private String phone;

    /**
     * 联系人邮箱地址
     */
    private String mailBox;

    /**
     * 联系人邮箱备注
     */
    private String remarks;
}
