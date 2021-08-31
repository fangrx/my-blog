package com.nonelonely.modules.system.service.impl.mail;



import com.nonelonely.modules.system.domain.mail.MailSendLog;
import com.nonelonely.modules.system.repository.mail.MailSendLogRepository;
import com.nonelonely.modules.system.service.mail.IMailSendLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 邮件发送记录表 服务实现类
 * </p>
 *
 * @author zyy
 * @since 2020-09-18
 */
@Service
public class MailSendLogServiceImpl implements IMailSendLogService {
    @Autowired
    MailSendLogRepository mailSendLogRepository;
    /**
     * 保存邮件发送日志
     *
     *
     * @param mailId      邮件id
     * @param mailAddress 邮件地址
     * @param data        邮件发送数据
     * @param data
     * @return
     */
    public MailSendLog saveMailLog(String mailId, String mailAddress, String data) {
        MailSendLog mailSendLog = new MailSendLog();

        mailSendLog.setMailId(mailId);
        mailSendLog.setMailAddress(mailAddress);
       // mailSendLog.setMailData(data);

        MailSendLog mailSendLog1 = mailSendLogRepository.saveAndFlush(mailSendLog);
        return mailSendLog1;
    }

    /**
     * 更新邮件发送状态 0执行发送 1发送成功 2发送失败
     *
     * @param logId
     * @param status
     * @param exception
     * @return
     */
    public boolean updateMailStatus(Long logId, int status, String exception) {
        MailSendLog mailSendLog = mailSendLogRepository.getById(logId);
        mailSendLog.setStatus(status);
        mailSendLog.setException(exception);
        mailSendLogRepository.save(mailSendLog);
        return true;
    }

    /**
     * 更新邮件发送状态根据发送邮件id 0执行发送 1发送成功 2发送失败
     *
     * @param mailId     邮件id
     * @param status     状态
     * @param sendResult 发送结果
     * @return
     */
    public boolean updateMailStatusByMailId(String mailId, int status, String sendResult) {
        MailSendLog mailSendLog = mailSendLogRepository.findByMailId(mailId);
        mailSendLog.setStatus(status);
        mailSendLog.setSendResult(sendResult);
        mailSendLogRepository.save(mailSendLog);
        return true;
    }

}
