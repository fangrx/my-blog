package com.nonelonely.component.weChat.model.req;

/**
 * ****************************************************************
 * @UpdateUser: 13434
 * @UpdateDate: 2018-12-08 12:38
 * @UpdateRemark: The modified content
 * ****************************************************************
 * -
 */
public class TextMessage extends BaseMessage {

    // 消息内容
    private String Content;

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }
}
