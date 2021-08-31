package com.nonelonely.component.email.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;


@Data
public class MailVo implements Serializable {


    /**
     * ID列表
     */
    private List<Long> idList;
    /**
     * 开始时间戳
     */
    private Long startTimestamp;
    /**
     * 结束时间戳
     */
    private Long endTimestamp;
    /**
     * 分页：页数
     */
    private Integer pageNum;
    /**
     * 分页：每页条数
     */
    private Integer pageSize;


    private Long id;
    /**
     * 用户Id
     */
    private String userId;
    /**
     * 服务端邮件Id
     */
    private String sysMailId;
    /**
     * 发送人
     */
    private String fromUser;
    /**
     * 接收人
     */
    private String toUser;
    /**
     * 抄送人
     */
    private String ccUser;
    /**
     * 密送人
     */
    private String ssUser;
    /**
     * 接收人json格式
     */
    private List toUserJson;
    /**
     * 抄送人json格式
     */
    private List ccUserJson;
    /**
     * 密送人json格式
     */
    private List ssUserJson;
    /**
     * 当前邮箱账号名
     */
    private String mailBox;
    /**
     * 邮件收取方式1：IMAP  2：POP3
     */
    private Integer mailBoxType;
    /**
     * 邮件标题
     */
    private String title;
    /**
     * 内容类型1文本类型2HTML类型
     */
    private Integer contType;
    /**
     * HTML内容
     */
    private String mailContHtml;
    /**
     * 文本内容
     */
    private String mailCont;
    /**
     * 邮件大小
     */
    private String mailSize;
    /**
     * 附件ID串
     */
    private String attaIds;
    /**
     * 邮件messageId
     */
    private String messageId;
    /**
     * 邮件的关联关系串
     */
    private String mailReferences;
    /**
     * 回复类型邮件时，所回复的邮件的第一封的邮件id
     */
    private String rootMailId;
    /**
     * 发送时间
     */
    private Date sendTime;
    /**
     * 接收时间
     */
    private Date receiveTime;
    /**
     * 邮件类型 1收件 2发件 3草稿
     */
    private Integer mailType;
    /**
     * 邮件状态 1已读  2草稿 3垃圾箱 4已回复 5已删除 6新邮件 77自定义标记 999未知
     */
    private Integer mailStatusFlag;
    /**
     * 邮件是否已读1未读2已读
     */
    private Integer isRead;
    /**
     * 保存到已发送 1保存 2不保存
     */
    private Integer saveToSent;
    /**
     * 是否紧急 1紧急 2不紧急
     */
    private Integer urgent;
    /**
     * 已读回执 1需要 2不需要
     */
    private Integer readReceipt;
    /**
     * 定时发送 1设置定时发送  2不设置定时发送
     */
    private Integer timingSend;
    /**
     * 定时发送时间
     */
    private Date timingSendTime;
    /**
     * 分别发送 1分别发送  2不分别发送
     */
    private Integer sendSeparately;
    /**
     * 附件是否可以转发 1可以转发  2不可以转发
     */
    private Integer attaForward;
    /**
     * 是否星标 1星标邮件  2非星标邮件
     */
    private Integer starStatus;
    /**
     * 标签
     */
    private String tag;
    /**
     * 所属文件夹id
     */
    private Long folderId;
    /**
     * 创建时间
     */
    private Date createDate;
    /**
     * 签名id
     */
    private Integer signId;
    /**
     * 签名内容
     */
    private String signContent;
}
