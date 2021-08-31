package com.nonelonely.component.weChat.model.resp;


/**
 * 类名: VideoMessage </br>
 * 描述: 视频消息 </br>
 * 开发人员：小海豚博客 </br>
 * 创建时间：  2015-9-30 </br>
 * 发布版本：V1.0  </br>
 */
public class VideoMessage extends BaseMessage {
    // 视频
    private Video Video;

    public Video getVideo() {
        return Video;
    }

    public void setVideo(Video video) {
        Video = video;
    }
}
