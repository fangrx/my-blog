package com.nonelonely.component.email.vo;

import lombok.Data;

import java.io.Serializable;


/**
 * 邮箱账户认证相关
 */
@Data
public class MailAuth implements Serializable {

    private static final long serialVersionUID = 1L;
    //  帐号'id',
    private Long id;
    // 认证信息
    private String authInfo;
    // 认证信息是否成功  1：成功 2：失败
    private boolean isAuthSuccess;
    // 认证编码
    private Integer authCode;
    // 收件箱
    private Integer InboxFolderCount;  // 所属文件夹id 1收件箱 2草稿箱 3已发送 4已删除',
    // 草稿箱
    private Integer draftsFolderCount;  // 所属文件夹id 1收件箱 2草稿箱 3已发送 4已删除',
    // 已发送箱
    private Integer sendMessagesfolderCount;  // 所属文件夹id 1收件箱 2草稿箱 3已发送 4已删除',
    // 已删除箱
    private Integer deletedMessagesfolderCount;  // 所属文件夹id 1收件箱 2草稿箱 3已发送 4已删除',
    // 垃圾箱
    private Integer junkFolderCount;  // 所属文件夹id 1收件箱 2草稿箱 3已发送 4已删除',

    public MailAuth() {

    }

    public MailAuth(int authCode, String authInfo, boolean isAuthSuccess) {
        this.authCode = authCode;
        this.authInfo = authInfo;
        this.isAuthSuccess = isAuthSuccess;
    }
}
