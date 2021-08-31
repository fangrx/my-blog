package com.nonelonely.component.weChat.model.resp;

/**
 * 类名: ImageMessage </br>
 * 描述: 图片消息</br>
 * 开发人员： 小海豚博客 </br>
 * 创建时间：  2015-9-30 </br>
 * 发布版本：V1.0  </br>
 */
public class ImageMessage extends BaseMessage {

    private Image Image;

    public Image getImage() {
        return Image;
    }

    public void setImage(Image image) {
        Image = image;
    }
}
