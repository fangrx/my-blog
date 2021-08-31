package com.nonelonely.component.email.service.task;



import com.nonelonely.common.enums.StatusEnum;

import com.nonelonely.component.email.enums.MailEnum;
import com.nonelonely.component.email.enums.MailEnums;
import com.nonelonely.component.email.enums.MailSmtpTypeEnum;

import com.nonelonely.component.email.service.receiver.FatchImapMailThread;
import com.nonelonely.component.email.service.receiver.FatchPop3MailThread;
import com.nonelonely.component.email.util.*;
import com.nonelonely.modules.system.domain.mail.MailBox;
import com.nonelonely.modules.system.repository.mail.MailBoxRepository;
import com.nonelonely.modules.system.service.mail.IMailService;
import com.sun.mail.imap.IMAPStore;
import com.sun.mail.pop3.POP3Store;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * 获取邮件的定时任务 每间隔多少时间获取，邮件网关的集群逻辑
 *
 * @author nonelonely
 */
@Component
@EnableScheduling
@Slf4j
public class GetMailTask {


    @Autowired
    private IMailService mailService;

    @Autowired
    private MailBoxRepository mailBoxRepository;



    private static List<MailSession> sessionList = new ArrayList<>();
    public static List<FatchImapMailThread> imapMailGetThread = new ArrayList<FatchImapMailThread>();
    public static List<FatchPop3MailThread> pop3MailGetThread = new ArrayList<FatchPop3MailThread>();


    public static List<FatchImapMailThread> getMailGetThread() {
        return imapMailGetThread;
    }

    public static void setMailGetThread(List<FatchImapMailThread> mailGetThread) {
        GetMailTask.imapMailGetThread = mailGetThread;
    }

    public static List<FatchPop3MailThread> getPop3MailGetThread() {
        return pop3MailGetThread;
    }

    public static void setPop3MailGetThread(List<FatchPop3MailThread> mailGetThread) {
        GetMailTask.pop3MailGetThread = mailGetThread;
    }

    static {
        System.setProperty(MailBoxCfg.SPLIT_LONGPARAMETERS, MailBoxCfg.MAIL_CFG_FALSE);
    }

    /**
     * 描述：初始化邮件数据，从数据库中读取开放的邮箱
     *
     * @throws NoSuchProviderException
     */
    private void init() throws NoSuchProviderException {

        //读取配置
        List<MailBox> mailBoxList = mailBoxRepository.findAll();
        //循环邮箱
        for (MailBox mailBox : mailBoxList) {
            // 关闭状态 或者删除状态 屏蔽掉
            if (!(mailBox.getStatus() == StatusEnum.OK.getCode())) {
                // 删除无用key
              //  stringRedisTemplate.opsForHash().delete(MailBoxCfg.USER_MAIL_CFG, mailBox.getMailBoxCode());
                //过滤掉关闭的邮箱
                continue;
            }

            // 更新最新的key
           // stringRedisTemplate.opsForHash().put(MailBoxCfg.USER_MAIL_CFG, mailBox.getMailBoxCode(), mailBox.getUserId());
            MailSession  ms = getMailSession(mailBox);
            if(ms == null)
            {
                continue;
            }

            GetMailTask.sessionList.add(ms);
        }
    }

    /**
     * 描述：启动邮件获取任务
     *
     * @throws NoSuchProviderException
     * @throws MessagingException
     */
    public void start() {
        try {
            init();
        } catch (NoSuchProviderException e) {
            log.error("邮件收取任务初始化失败！！！,异常原因：", e);
            return;
        }
        FatchImapMailThread fatchImapMailThread;
        FatchPop3MailThread fatchPop3MailThread;
        for (final MailSession ms : GetMailTask.sessionList) {

            if (ms.getImapStore() != null) {
                fatchImapMailThread = new FatchImapMailThread(ms.getImapStore(),  ms.getMailBoxName(),ms.getSession());
                // 设置现成名称
                fatchImapMailThread.setName(MailConstant.fatchImapMailThreadName + ms.getMailBoxName());
                //启动线程获取邮件
                fatchImapMailThread.start();
                //将当前线程加入到集合中，便于监控
                imapMailGetThread.add(fatchImapMailThread);
            } else {

                fatchPop3MailThread = new FatchPop3MailThread(ms.getPop3Store(), ms.getMailBoxName());
                // 设置现成名称
                fatchPop3MailThread.setName(MailConstant.fatchPop3MailThreadName + ms.getMailBoxName());
                //启动线程获取邮件
                fatchPop3MailThread.start();
                //将当前线程加入到集合中，便于监控
                pop3MailGetThread.add(fatchPop3MailThread);

            }

            log.info("【" + ms.getMailBoxName() + "】邮箱收取任务已启动！");
        }
    }

    /**
     * 描述：重新启动邮件抓取任务
     */
    public void stop() {

        // IMAP 线程停止
        for (FatchImapMailThread fatchImapMailThread : imapMailGetThread) {
            fatchImapMailThread.interrupt();
            log.info("IMAP邮箱：" + fatchImapMailThread.getMailBoxName() + "收取任务已停止！");
        }

        // POP3 线程停止
        for (FatchPop3MailThread fatchMailThread : pop3MailGetThread) {
            fatchMailThread.interrupt();
            log.info("邮箱：" + fatchMailThread.getMailBoxName() + "收取任务已停止！");
        }

        // Session清空
        imapMailGetThread.clear();
        pop3MailGetThread.clear();
        sessionList.clear();

    }

    /**
     * 描述：重启邮件抓取任务
     */
    public void restart() {
        log.info("准备重启邮件抓取任务...");
        this.stop();
        this.start();
    }

    public  static MailSession getMailSession(MailBox mailBox) throws NoSuchProviderException{
        //获取用户名密码
        String userName = mailBox.getMailBoxCode();
        String password = mailBox.getMailBoxPwd();
        Integer boxType = mailBox.getMailBoxType();
        String ssl;
        if (mailBox.getMailIsSsl()) {
            ssl = MailBoxCfg.MAIL_CFG_TRUE;
        } else {
            ssl = MailBoxCfg.MAIL_CFG_FALSE;
        }

        //获取配置项
        Properties prop = new Properties();

        prop.put(MailBoxCfg.SPLIT_LONGPARAMETERS, MailBoxCfg.MAIL_CFG_FALSE);
        prop.put(MailBoxCfg.MAIL_FROM, userName);
        String imapName = null;
        String pop3Name = null;
        if (boxType == MailEnum.MAIL_TYPE_IMAP) {
            prop.put(MailBoxCfg.MAIL_IMAP_HOST, mailBox.getMailImapUrl());
            prop.put(MailBoxCfg.MAIL_IMAP_PORT, mailBox.getMailImapPort());
            prop.put(MailBoxCfg.MAIL_IMAP_SSL_ENABLE, ssl);
            //prop.setProperty("mail.imap.usesocketchannels", "true");

            // 开启SSL
            if (mailBox.getMailIsSsl()) {
                imapName = MailBoxCfg.MAIL_CFG_IMAPS;
            } else {
                imapName = MailBoxCfg.MAIL_CFG_IMAP;
            }
            prop.put(MailBoxCfg.MAIL_STORE_PROTOCOL, imapName);

            prop.put(MailEnums.MailConfigEnum.MAIL_IMAP_FETCHSIZE.getDesc(), MailEnums.MailConfigEnum.MAIL_IMAP_FETCHSIZE.getCode());
        } else if (boxType == MailEnum.MAIL_TYPE_POP3) {
            prop.put(MailBoxCfg.MAIL_POP3_HOST, mailBox.getMailPop3Url());
            prop.put(MailBoxCfg.MAIL_POP3_PORT, mailBox.getMailPop3Port());
            prop.put(MailBoxCfg.MAIL_POP3_SSL_ENABLE, ssl);
            // 开启SSL
            if (mailBox.getMailIsSsl()) {
                pop3Name = MailBoxCfg.MAIL_CFG_POP3S;
            } else {
                pop3Name = MailBoxCfg.MAIL_CFG_POP3;
            }
            prop.put(MailBoxCfg.MAIL_STORE_PROTOCOL, pop3Name);
            prop.put(MailEnums.MailConfigEnum.MAIL_POP3_FETCHSIZE.getDesc(), MailEnums.MailConfigEnum.MAIL_POP3_FETCHSIZE.getCode());
        } else {
            log.error("未知帐号类型 。。 帐号为 ：  " + mailBox.getMailBoxCode());
           return  null;
        }

        POP3Store pop3Store = null;
        IMAPStore imapStore = null;
        Session session = Session.getInstance(prop, AuthenticatorGenerator.getAuthenticator(userName, password));
        if (boxType.equals(MailEnum.MAIL_TYPE_IMAP)) {
            imapStore = (IMAPStore) session.getStore(MailBoxCfg.MAIL_CFG_IMAP);
        } else {
            pop3Store = (POP3Store) session.getStore(MailBoxCfg.MAIL_CFG_POP3);
        }

        //保存起来
        //session.setDebug(true);
        MailSession ms = new MailSession();
        ms.setSession(session);
        ms.setMailBoxName(userName);
        if (boxType.equals(MailEnum.MAIL_TYPE_IMAP)) {
            ms.setImapStore(imapStore);
        } else {
            ms.setPop3Store(pop3Store);
        }
        return ms;
    }
}
