package com.nonelonely.modules.system.repository.mail;


import com.nonelonely.modules.system.domain.mail.MailSendLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * <p>
 * 邮件 Mapper 接口
 * </p>
 *
 * @author liuming
 * @since 2020-09-15
 */
public interface MailSendLogRepository extends JpaRepository<MailSendLog,Long>, JpaSpecificationExecutor<MailSendLog> {


    MailSendLog findByMailId(String mailId);

    MailSendLog getById(Long id);


}
