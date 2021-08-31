package com.nonelonely.modules.system.domain.mail;


import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

/**
 * <p>
 * 邮件发送记录表
 * </p>
 *
 * @author liuming
 * @since 2020-09-18
 */
@Data
@Table(name="none_mail_send_log")
@EntityListeners(AuditingEntityListener.class)
@Entity
public class MailSendLog {


    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;
    /**
     * 邮件id
     */

    private String mailId;

    /**
     * 邮箱地址
     */

    private String mailAddress;

    /**
     * 邮件发送结果
     */

    private String sendResult;

    /**
     * 回调状态 0执行发送 1发送成功 2发送失败
     */
    private Integer status;
    /**
     * 异常信息
     */
    @Column(columnDefinition = "mediumtext", nullable = true)
    private String exception;
    // 创建时间
    @CreatedDate
    private Date createDate;
    // 更新时间
    @LastModifiedDate
    private Date updateDate;



}
