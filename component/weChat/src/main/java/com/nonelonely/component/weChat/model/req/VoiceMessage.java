package com.nonelonely.component.weChat.model.req;

/**
 * ****************************************************************
 * @UpdateUser: 13434
 * @UpdateDate: 2018-12-08 12:39
 * @UpdateRemark: The modified content
 * ****************************************************************
 * -
 */
public class VoiceMessage extends BaseMessage {

    // 媒体ID
    private String MediaId;
    // 语音格式
    private String Format;

    public String getMediaId() {
        return MediaId;
    }

    public void setMediaId(String mediaId) {
        MediaId = mediaId;
    }

    public String getFormat() {
        return Format;
    }

    public void setFormat(String format) {
        Format = format;
    }
}
