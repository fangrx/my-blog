package com.nonelonely.component.email.util;

import lombok.Data;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.Serializable;

/**
 * <p>
 * 邮件消息
 * </p>
 *
 * @author liuming
 * @since 2020-09-15
 */
@Data
public class MailMessage extends MimeMessage implements Serializable {

    private static final long serialVersionUID = 3008195641270230344L;
    /**
     * 邮件id
     */
    private String id;

    public MailMessage(MimeMessage source) throws MessagingException {
        super(source);
    }

}
