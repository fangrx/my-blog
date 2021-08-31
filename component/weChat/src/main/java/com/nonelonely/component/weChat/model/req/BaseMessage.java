package com.nonelonely.component.weChat.model.req;

import lombok.Getter;
import lombok.Setter;

/**
 * ****************************************************************
 * @UpdateUser: 小海豚博客
 * @UpdateDate: 2018-12-08 12:35
 * @UpdateRemark: The modified content
 * ****************************************************************
 * -
 */
@Setter
@Getter
public class BaseMessage {

    // 开发者微信号
    private String ToUserName;
    // 发送方帐号（一个OpenID）
    private String FromUserName;
    // 消息创建时间 （整型）
    private long CreateTime;
    // 消息类型（text/image/location/link）
    private String MsgType;
    // 消息id，64位整型
    private long MsgId;
}
