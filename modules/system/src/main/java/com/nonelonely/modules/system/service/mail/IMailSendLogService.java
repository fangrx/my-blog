package com.nonelonely.modules.system.service.mail;


import com.nonelonely.modules.system.domain.mail.MailSendLog;

/**
 * <p>
 * 邮件发送记录表 服务类
 * </p>
 *
 * @author zyy
 * @since 2020-09-18
 */
public interface IMailSendLogService  {
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
    MailSendLog saveMailLog(String mailId, String mailAddress, String data);

    /**
     * 更新邮件发送状态根据发送日志id 0执行发送 1发送成功 2发送失败
     *
     * @param logId             日志id
     * @param status            状态
     * @param exception         异常
     * @return
     */
    boolean updateMailStatus(Long logId, int status, String exception);

    /**
     * 更新邮件发送状态根据发送邮件id 0执行发送 1发送成功 2发送失败
     *
     * @param mailId            邮件id
     * @param status            状态
     * @param sendResult        发送结果
     * @return
     */
    boolean updateMailStatusByMailId(String mailId, int status, String sendResult);
}
