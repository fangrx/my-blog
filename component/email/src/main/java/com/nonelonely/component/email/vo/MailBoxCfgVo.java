package com.nonelonely.component.email.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;


/**
 * 邮箱账户相关
 */
@Data
public class MailBoxCfgVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;  //  'id',
    private String userId;   //  '用户Id',
    private String userName;  //  '用户名称',
    private String userImg;   //  '用户头像',
    private String phone; //  '用户手机号',
    private String orgId;   //  '单位Id',
    private String orgName; //  '单位名称',
    private String mailBoxName;//  '账户名称',
    private String mailBoxCode;  //  '账户=登录CODE',
    private String mailBoxPwd;   //  '密码',
    private Integer mailBoxType;  //  '类型',
    private String mailImapUrl; //  'imap-url',
    private String mailImapPort;  //  'imap-port',
    private String mailSmtpPort; //  'smtp-port',
    private String mailSmtpUrl;  //  'smtp-url',
    private String mailPop3Url;   //  'pop3-url',
    private String mailPop3Port;   //  'pop3-port',
    private String mailUrl;   //  '邮箱url',
    private String mailPort;   //  '邮箱port',
    private Integer mailIsDefault;  //  '是否默认 1：是 2：否',
    private Integer mailIsSsl;  //  '是否启动ssl  1：是 2：否',
    private Integer mailSmtpIsSsl;  //  '发件是否启动ssl  1：是 2：否',
    private Integer mailIsReceipt; //  '是否回执 1：是 2: 否',
    private Integer mailIsDel; //  '是否删除服务器邮件 1：是 2: 否',
    private Date createDate; //  '创建时间',
    private Date updateDate; //  '修改时间',
    private String createBy;//  '创建者',
    private String updateBy;  //  '更新者',
    private String remarks;   //  '备注信息',
    private Integer delFlag;   //  '删除标志0删除1未删除',
    private Integer mailEnableSign;

}
