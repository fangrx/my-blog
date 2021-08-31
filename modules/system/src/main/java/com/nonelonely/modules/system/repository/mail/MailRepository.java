package com.nonelonely.modules.system.repository.mail;



import com.nonelonely.modules.system.domain.mail.Mail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * <p>
 * 邮件 Mapper 接口
 * </p>
 *
 * @author liuming
 * @since 2020-09-15
 */
public interface MailRepository extends JpaRepository<Mail,Long>, JpaSpecificationExecutor<Mail> {


    /**
     * 根据账户名称获取邮件集合
     *
     * @param name
     * @return
     */
    @Query(nativeQuery = true, value = " SELECT " +
            "    message_id messageId " +
            "    FROM " +
            "            none_mail " +
            "    WHERE " +
            "    mail_box = ?1" +
            "    AND mail_type = 1" +
            "    UNION" +
            "            SELECT" +
            "    sys_mail_id messageId" +
            "    FROM" +
            "            none_mail" +
            "    WHERE" +
            "    mail_box = ?1" +
            "    AND mail_type = 1")
    List<String> getMailListByName(String name);

    /**
     * 根据messageId 获取邮件数量
     *
     * @return
     */
    @Query(nativeQuery = true, value = "   SELECT" +
            "            count(1)" +
            "        FROM" +
            "            none_mail" +
            "        WHERE" +
            "            (" +
            "                sys_mail_id = ?1" +
            "                OR message_id = ?1" +
            "            )" +
            "        AND mail_box = ?2" +
            "        AND del_flag = 1")
    int getMailByMessageId(String messageUID, String mailName);


    Mail getById(Long id);


}
