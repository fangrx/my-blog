package com.nonelonely.component.weChat.model.req;

/**
 * ****************************************************************
 * @UpdateUser: 13434
 * @UpdateDate: 2018-12-08 12:39
 * @UpdateRemark: The modified content
 * ****************************************************************
 * -
 */
public class ImageMessage extends BaseMessage {
    // 图片链接
    private String PicUrl;
    private String MediaId;

    public String getPicUrl() {
        return PicUrl;
    }

    public void setPicUrl(String picUrl) {
        PicUrl = picUrl;
    }

    public String getMediaId() {
        return MediaId;
    }

    public void setMediaId(String mediaId) {
        MediaId = mediaId;
    }

}
