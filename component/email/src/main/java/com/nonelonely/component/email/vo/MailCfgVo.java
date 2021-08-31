package com.nonelonely.component.email.vo;


import lombok.Data;

import java.io.Serializable;


/**
 * 邮箱配置
 */
@Data
public class MailCfgVo implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * id
     */
    private Long id;
    /**
     * 账户名称
     */
    private String mailBoxName;
    /**
     * 邮箱地址
     */
    private String mailBoxCode;
}
