package com.nonelonely.component.email.vo;


import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 邮件
 *
 * @author liuming
 * @Date 2020-09-15
 */
@Data
public class MailReq implements Serializable {
    /**
     * 邮件id
     */

    private Long id;

    /**
     * 当前邮箱账号
     */

    private String mailBox;

    /**
     * 接收人
     */

    @NotNull
    private String toUser;

    /**
     * 接收人
     */

    @NotNull
    private List toUserJson;

    /**
     * 抄送人
     */

    private String ccUser;

    /**
     * 抄送人Json
     */

    @NotNull
    private List ccUserJson;

    /**
     * 密送人
     */

    private String ssUser;

    /**
     * 密送人Json
     */

    @NotNull
    private List ssUserJson;

    /**
     * 邮件标题
     */

    @NotNull
    private String title;

    /**
     * HTML内容
     */

    @NotNull
    private String mailContHtml;

    /**
     * 附件ID串
     */

    private String attaIds;

    /**
     * 回复类型邮件时，所回复的邮件的第一封的邮件id
     */

    private String rootMailId;

    /**
     * 保存到已发送 1保存 2不保存
     */

    private Integer saveToSent;

    /**
     * 是否紧急 1紧急 2不紧急
     */

    private Integer urgent;

    /**
     * 已读回执 2需要 3不需要
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
     * 分别发送 0分别发送  1不分别发送
     */

    private Integer sendSeparately;

    /**
     * 附件是否可以转发 0可以转发  1不可以转发
     */

    private Integer attaForward;


    /**
     * 保存到草稿 1保存到草稿 其他不保存到草稿
     */

    private Integer saveToDrag;
    /**
     * 签名id
     */

    private Integer signId;
    /**
     * 签名内容
     */

    private String signContent;
}
