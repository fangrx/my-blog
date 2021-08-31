package com.nonelonely.component.email.service.sender;



import com.nonelonely.component.email.vo.MailAttachmentVo;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.HashMap;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 邮件发送实体
 * </p>
 *
 * @author nonelonely
 * @since 2020-09-15
 */
@Data
public class SendingMail implements Serializable {

    private static final long serialVersionUID = -2137174108606584452L;
    /**
     * 邮件id
     */
    private String id;
    /**
     * 回复的邮件id
     */
    private String replyMailId;
    @NotNull(message = "标题不能为空！")
    /**
     * 邮件标题
     */
    private String subject;
    /**
     * 邮件内容
     */
    @NotNull(message = "内容不能为空！")
    private String html;
    /**
     * 收件人
     */
    @NotNull(message = "收件人不能为空！")
    private String toUsers;
    /**
     * 抄送人
     */
    private String ccUsers;
    /**
     * 密送人
     */
    private String bccUsers;
    /**
     * 附件列表
     */
    private List<MailAttachmentVo> attachmentList;
    /**
     * 发送失败次数
     */
    private int sendFailedTimes;
    /**
     * 邮件头，用于回复邮件
     */
    private Map<String, String> mailHeader = new HashMap<>();

}
