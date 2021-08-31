package com.nonelonely.modules.system.repository.mail;


import com.nonelonely.modules.system.domain.mail.MailBox;
import com.nonelonely.modules.system.repository.BaseRepository;

/**
 * <p>
 * 邮件 Mapper 接口
 * </p>
 *
 * @author liuming
 * @since 2020-09-15
 */
public interface MailBoxRepository extends BaseRepository<MailBox, Long> {

     MailBox getMailBoxByMailBoxCode(String mailBoxCode);

}
