package com.nonelonely.component.email.service.auth;


import com.nonelonely.common.utils.DateUtils;
import com.nonelonely.common.utils.EhCacheUtil;
import com.nonelonely.component.email.enums.MailTypeEnum;
import com.nonelonely.component.email.enums.ResultEnum;
import com.nonelonely.component.email.service.receiver.FatchImapMailThread;
import com.nonelonely.component.email.service.receiver.FatchPop3MailThread;
import com.nonelonely.component.email.service.task.GetMailTask;
import com.nonelonely.component.email.util.MailBoxCfg;
import com.nonelonely.component.email.util.MailCache;
import com.nonelonely.component.email.util.MailCacheUtil;
import com.nonelonely.component.email.util.MailConstant;
import com.nonelonely.component.email.vo.MailAuth;
import com.nonelonely.modules.system.domain.mail.MailBox;
import com.sun.mail.imap.IMAPStore;
import com.sun.mail.pop3.POP3Store;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;


import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import javax.mail.search.ComparisonTerm;
import javax.mail.search.SearchTerm;
import javax.mail.search.SentDateTerm;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


/**
 * <p>
 * 邮箱线程服务类
 * </p>
 *
 * @author yan
 * @since 2020-09-24
 */
@Async
@Slf4j
@Component
public class AuthMailThreadService {

    // 默认30秒
    private static final long SLEEP_DELAY = 30000;

    // 默认拉取历史30天数据
    private static final int PULL_MAIL_DAY = 30;

    /**
     * 帐号初始化的时候调用  获取全部-30天 邮件入库
     * 指定 某一个 账号 操作行为
     *
     * @param folder
     */
    @Async
    MailAuth mailTempInit(MailAuth mailAuth, Folder folder, Integer type) {
        // 只读模式打开
        try {
            if (!folder.isOpen()) {
                //如果需要在取得邮件数后将邮件置为已读则这里需要使用READ_WRITE,否则READ_ONLY就可以
                folder.open(Folder.READ_ONLY);
            }
            // 从文件夹中以数组的形式读取消息并打印出来
            Message[] messages = folder.getMessages();
            if (type == MailTypeEnum.MAIL_TYPE_INBOX.getCode()) {
                // 收件箱
                mailAuth.setAuthSuccess(true);
                mailAuth.setInboxFolderCount(messages.length);
            } else if (type == MailTypeEnum.MAIL_TYPE_SEND_MESSAGES.getCode()) {
                // 已发送
                mailAuth.setAuthSuccess(true);
                mailAuth.setSendMessagesfolderCount(messages.length);

            } else if (type == MailTypeEnum.MAIL_TYPE_DRAFTS.getCode()) {
                // 草稿箱
                mailAuth.setAuthSuccess(true);
                mailAuth.setDraftsFolderCount(messages.length);
            } else if (type == MailTypeEnum.MAIL_TYPE_DELETED_MESSAGES.getCode()) {
                // 已删除
                mailAuth.setAuthSuccess(true);
                mailAuth.setDeletedMessagesfolderCount(messages.length);
            } else if (type == MailTypeEnum.MAIL_TYPE_JUNK.getCode()) {
                // 垃圾箱
                mailAuth.setAuthSuccess(true);
                mailAuth.setJunkFolderCount(messages.length);
            }

        } catch (MessagingException e) {
            // 初始化错误
            log.error(ResultEnum.MAILBOX_INIT_ERROR.getMsg() + e.getMessage());
            return new MailAuth(ResultEnum.MAILBOX_INIT_ERROR.getCode(), ResultEnum.MAILBOX_INIT_ERROR.getMsg() + "  type = " + type + " :" + e.getMessage(), false);
        }

        Calendar calendar = Calendar.getInstance();
        // TODO: 腾讯邮箱 不支持 服务器端筛选 ，只能本地筛选 最30 天 。
        calendar.add(Calendar.DAY_OF_MONTH, -PULL_MAIL_DAY);

        Date beforeDate = calendar.getTime();
        SearchTerm comparisonTermGe = new SentDateTerm(ComparisonTerm.GE, beforeDate);

        try {
            Message[] messages = null;
            messages = folder.search(comparisonTermGe);
            List<String> temp = new ArrayList<>();
            List<Message> tempMessage = new ArrayList<>();
            List<String> strDate = new ArrayList<>();
            String sendDate;
            String strBeforeDate = DateUtils.dateFormatStr(beforeDate);

            for (int i = 0; i < messages.length; i++) {
                try {
                    Message message = messages[i];
                    temp.add(message.getSubject());
                    // TODO 部分特殊邮件，系统邮件，没有发送时间 .. 暂定按照接收时间为准
                    sendDate = DateUtils.dateFormatStr(message.getSentDate() == null ? message.getReceivedDate() : message.getSentDate());
                    strDate.add(sendDate);
                    tempMessage.add(message);

                    // TODO: 解析邮件 .... 存入队列  , 已经存入的邮件，不可以重复存入 。
                    // TODO: 如果账号删除 ，直接删除数据库数据即可 。
                    Integer result = sendDate.compareTo(strBeforeDate);
                    if (result > -1) {
                        // 邮件临时写入redis
                        if (mailRedisTemp(message)) {
                            // 加入邮件队列
                            MailCache.getNewMailQueue().offer(message);

                        } else {
                            // TODO: 获取不到messageId 的邮件 如系统邮件直接过滤
                            continue;
                        }
                    } else {
                        // TODO: 历史邮件处理逻辑 .. 30 天之前的。 不给与处理
                        continue;
                    }
                } catch (Exception e) {
                    log.error(i + "：初始化历史邮件异常：" + e.getMessage());
                    // 解析历史邮件异常， 暂时不给与处理 运营商服务器导致，获取message里面字段为空导致。
                    continue;
                }
            }
        } catch (MessagingException e) {
            log.error(e.getMessage());
            return new MailAuth(ResultEnum.MAILBOX_INIT_ERROR.getCode(), ResultEnum.MAILBOX_INIT_ERROR.getMsg() + "  type = " + type + " :" + e.getMessage(), false);
        }
        return mailAuth;
    }

    /**
     * 新增线程
     *
     * @param mail
     * @param imapStore
     * @param pop3Store
     * @param boxCode
     * @return
     */
    @Async
    void addMailThread(MailBox mail, IMAPStore imapStore, POP3Store pop3Store,  String boxCode, Session imapSession) {
        try {

            // 销毁线程 该账号的所有线程 都停止 ，包括IMAP 和 POP3 不区分具体是哪个协议
            if (StringUtils.isNotEmpty(boxCode)) {
                if (!interuptMailThread(boxCode)) {
                    log.error("邮件线程释放发生错误。。。。参数解锁账号：" + boxCode);
                }
            }
            // 线程暂停
            Thread.sleep(SLEEP_DELAY);

            // 追加线程
            if (imapStore != null) {
                // 创建线程
                FatchImapMailThread fatchImapMailThread = new FatchImapMailThread(imapStore, mail.getMailBoxCode(), imapSession);
                // 设置现成名称
                fatchImapMailThread.setName(MailConstant.fatchImapMailThreadName + mail.getMailBoxCode());
                //启动线程获取邮件
                fatchImapMailThread.start();
                //将当前线程加入到集合中，便于监控
                GetMailTask.imapMailGetThread.add(fatchImapMailThread);
            } else {
                // 线程暂停
                Thread.sleep(SLEEP_DELAY);
                // 创建线程
                FatchPop3MailThread fatchPop3MailThread = new FatchPop3MailThread(pop3Store, mail.getMailBoxCode());
                // 设置现成名称
                fatchPop3MailThread.setName(MailConstant.fatchPop3MailThreadName + mail.getMailBoxCode());
                //启动线程获取邮件
                fatchPop3MailThread.start();
                //将当前线程加入到集合中，便于监控
                GetMailTask.pop3MailGetThread.add(fatchPop3MailThread);
            }

            log.info("邮件线程更新成功");
        } catch (Exception e) {
            log.error("邮件线程发生错误。。。。" + e.getMessage());
        }
    }

    /**
     * 根据线程名称 删除线程数据
     *
     * @param name 邮箱线程格式 为 ：FatchPop3MailTask_xxxx  -- 暂时全称
     * @return
     */
    boolean interuptMailThread(String name) {
        // 获取当前线程
        ThreadGroup currentGroup = Thread.currentThread().getThreadGroup();
        int noThreads = currentGroup.activeCount();
        Thread[] lstThreads = new Thread[noThreads];
        currentGroup.enumerate(lstThreads);
        String nm;
        boolean relustFlag = false;
        for (int i = 0; i < noThreads; i++) {
            nm = lstThreads[i].getName();
            System.out.println("线程号：" + i + " = " + nm);
            // 存在该账号
            if (nm.indexOf(name) > -1) {
                lstThreads[i].interrupt();
                relustFlag = true;
            }
        }
        return relustFlag;
    }

    /**
     * redis 账号初始化存入账号邮件信息 -- 临时保存 不发消息用
     * 用于方重复提交使用
     *
     * @param message
     * @return
     */
    private boolean mailRedisTemp(Message message) {
        MimeMessage mimeMessage = (MimeMessage) message;
        // 全局唯一Id
        String sysMessageId = null;
        try {
            sysMessageId = mimeMessage.getMessageID();
        } catch (MessagingException e) {
            return false;
        }

        // 存入messageId

        if (MailCacheUtil.get(MailBoxCfg.USER_NEW_MAIL_POP3) == null){
            MailCacheUtil.set(MailBoxCfg.USER_NEW_MAIL_POP3 + sysMessageId,sysMessageId);
            return  true;
        }
        // 设置不同协议 存入不同的redis
//        Folder folder = message.getFolder();
//        if (folder instanceof POP3Folder) {
//            stringRedisTemplate.opsForValue().set(MailBoxCfg.USER_NEW_MAIL_POP3 + sysMessageId, sysMessageId, 600, TimeUnit.SECONDS);
//        } else {
//            stringRedisTemplate.opsForValue().set(MailBoxCfg.USER_NEW_MAIL_IMAP + sysMessageId, sysMessageId, 600, TimeUnit.SECONDS);
//        }

        return false;
    }
}
