package com.nonelonely.modules.system.domain.mail;

import com.nonelonely.common.enums.StatusEnum;
import com.nonelonely.common.utils.StatusUtil;
import lombok.Data;
import org.hibernate.annotations.Where;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

/**
 * 描述:用于封装邮箱相关配置
 *
 * @author 杨建全
 * @date 2017年4月7日 下午3:02:25
 */
@Data
@Table(name="none_mail_box")
@Entity
@EntityListeners(AuditingEntityListener.class)
@Where(clause = StatusUtil.NOT_DELETE)
public class MailBox  {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;  //  'id',
    private String mailBoxName;//  '账户名称',发送名称
    private String mailBoxCode;  //  '账户=登录CODE',admin.nonelonely.com
    private String mailBoxPwd;   //  '密码',
    private Integer mailBoxType;  //  '类型',
    private String mailSmtpUrl;  //  'smtp-url',
    private String mailImapUrl; //  'imap-url',
    private String mailPop3Url;   //  'pop3-url',
    private String mailSmtpPort; //  'smtp-port',
    private String mailImapPort;  //  'imap-port',
    private String mailPop3Port;   //  'pop3-port','[
    private Boolean mailIsDefault;  //  '是否默认 1：是 0：否',
    private Boolean mailIsSsl = false;  //  '是否启动ssl  1：是 0：否',
    private Boolean mailIsReceipt; //  '是否回执 1：是 0: 否',
    private Boolean mailIsDel; //  '是否删除服务器邮件 1：是 0: 否',
    /**
     * 是否启用签名 1：是 2：否
     */

    private Integer mailEnableSign;

    /**
     * 发件是否启动ssl  1：是 0：否
     */
    private Boolean mailSmtpIsSsl = false;



    // 创建时间
    @CreatedDate
    private Date createDate;
    // 更新时间
    @LastModifiedDate
    private Date updateDate;

    private String remarks;   //  '备注信息',
    // 数据状态
    private Byte status = StatusEnum.OK.getCode();


}
