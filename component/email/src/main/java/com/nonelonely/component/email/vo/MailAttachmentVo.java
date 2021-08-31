package com.nonelonely.component.email.vo;

import lombok.Data;

import java.io.Serializable;


@Data
public class MailAttachmentVo implements Serializable {

    /**
     * 附件名称
     */
    private String name;
    /**
     * 附件大小
     */
    private Long size;
    /**
     * 附件url
     */
    private String url;

}
