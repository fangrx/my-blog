package com.nonelonely.component.email.service.receiver;



import com.nonelonely.common.utils.SpringContextUtil;
import com.nonelonely.component.email.enums.MailEnum;
import com.nonelonely.component.email.enums.MailTypeEnum;
import com.nonelonely.component.email.enums.ResultEnum;


import com.nonelonely.component.email.util.MailBoxCfg;
import com.nonelonely.component.email.util.MailCache;
import com.nonelonely.component.email.util.MailCacheUtil;
import com.nonelonely.component.thymeleaf.utility.ParamUtil;
import com.nonelonely.modules.system.repository.mail.MailRepository;
import com.sun.mail.pop3.POP3Folder;
import com.sun.mail.pop3.POP3Store;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.mail.*;
import javax.mail.internet.MimeMessage;
import javax.mail.search.ComparisonTerm;
import javax.mail.search.SearchTerm;
import javax.mail.search.SentDateTerm;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


/**
 * <p>
 * POP3 收取邮件服务类
 * </p>
 *
 * @author yan
 * @since 2020-09-24
 */
@Slf4j
@Scope("prototype")
@Component
public class FatchPop3MailThread extends Thread {
    // TODO: POP3定时拉取延迟时间   - 五分钟
    private static final long DELAY = 300000;
    // 保持活跃时间 5秒
    private static final long LOOPDELAY = 5000;
    private POP3Store pop3Store;


    //是否继续执行
    private boolean loopFlag = true;
    private String mailBoxName;


    // 默认拉取历史1天数据
    private static final int PULL_MAIL_DAY = 1;


    /**
     * 获取MailDao对象的静态方法
     */
    private static MailRepository getMailDao() {
        return SpringContextUtil.getBean(MailRepository.class);
    }





    public FatchPop3MailThread(POP3Store pop3Store, String mailBoxName) {
        super();
        this.pop3Store = pop3Store;

        this.mailBoxName = mailBoxName;
        this.setName("FatchPop3MailThread-" + mailBoxName);
    }

    public POP3Store getPOP3Store() {
        return pop3Store;
    }

    public void setPOP3Store(POP3Store pop3Store) {
        this.pop3Store = pop3Store;
    }

    public boolean isLoopFlag() {
        return loopFlag;
    }

    public void setLoopFlag(boolean loopFlag) {
        this.loopFlag = loopFlag;
    }

    public String getMailBoxName() {
        return mailBoxName;
    }

    public void setMailBoxName(String mailBoxName) {
        this.mailBoxName = mailBoxName;
    }


    @Override
    public void run() {
        System.out.println("--------------------111111-----------");
      //  while (loopFlag) {
            try {
                // POP3
                if (pop3Store != null) {
                    System.out.println("--------------------11114444-----------");
                    getPop3Mails(pop3Store);
                }
            } catch (Exception e) {
                e.printStackTrace();
//                try {
//                    Thread.sleep(DELAY);
//                } catch (InterruptedException e1) {
//                    e1.printStackTrace();
//                }
                // 异步发送日志
                sendLog(MailEnum.MAIL_TYPE_POP3, MailEnum.MAIL_RECEIVER_STATUS_ERR, e.getMessage());
                log.error("收取邮件线程发生异常终止！尝试重启任务...", e);

            }
       // }
    }


    /**
     * 描述：获取邮件方法  -- POP3
     *
     * @param store
     * @throws MessagingException
     * @throws FolderClosedException
     * @throws InterruptedException
     * @throws IOException
     * @author 杨建全
     * @date 2017年4月19日
     */
    private void getPop3Mails(final POP3Store store) throws MessagingException {
        if (!store.isConnected()) {
            store.connect();
        }

        POP3Folder folder = null;
      //  while (loopFlag) {
            try {
                // TODO:
                if (!store.isConnected()) {
                    store.connect();
                }
                // TODO： P0P3 协议 需要每次加载的时候重新拉取INBOX ..     Send Messages = 已发送 Drafts = 草稿箱  Deleted Messages 已删除 Junk 垃圾箱
                folder = (POP3Folder) store.getFolder(MailTypeEnum.MAIL_TYPE_INBOX.getMsg());// 获取收件箱

                //如果需要在取得邮件数后将邮件置为已读则这里需要使用READ_WRITE,否则READ_ONLY就可以
                folder.open(Folder.READ_WRITE); // 以读写方式打开

                // 不支持idle，睡眠一段时间再获取
               // Thread.sleep(DELAY);

                // 获取当前邮箱邮件数量
                if (!folder.isOpen()) {
                    //如果需要在取得邮件数后将邮件置为已读则这里需要使用READ_WRITE,否则READ_ONLY就可以
                    folder.open(Folder.READ_WRITE);
                }
                // 获取POP3全部邮件 - 客户端比较
                if (pop3NewMailCheck(folder)) {
                    int newMessageCount = folder.getNewMessageCount();
                    log.info("POP3收到新邮件！  POP3账户： " + store.getURLName().getUsername() + "最新邮件数量：  " + newMessageCount);
                }

                // 异步发送日志
                sendLog(MailEnum.MAIL_TYPE_POP3, MailEnum.MAIL_RECEIVER_STATUS_OK, ResultEnum.SUCCESS.getMsg());

            } catch (Exception e) {
                log.error(mailBoxName + "收取邮件循环发送异常：", e);
                if (e instanceof FolderClosedException) {
                    try {
                        log.info("尝试重新打开{}收件箱...", mailBoxName);
                        //如果需要在取得邮件数后将邮件置为已读则这里需要使用READ_WRITE,否则READ_ONLY就可以
                        folder.open(Folder.READ_WRITE);
                    } catch (MessagingException mex) {
                        log.error("尝试重新打开{}收件箱异常！！！", mailBoxName);
                    }
                } else if (e instanceof InterruptedException) {
                    log.error("InterruptedException....收到中断异常，准备停止邮件收取任务....", e);
                    loopFlag = false;
                } else if (e instanceof MessagingException) {
                    log.error("MessagingException....准备停止邮件收取任务", e);
                    loopFlag = false;
                } else if (e instanceof AuthenticationFailedException) {
                    log.error("AuthenticationFailedException....准备停止邮件收取任务", e);
                    loopFlag = false;
                }

                //睡眠1秒后再次尝试
                try {
                    Thread.sleep(LOOPDELAY);
                    // 异步发送日志
                    sendLog(MailEnum.MAIL_TYPE_POP3, MailEnum.MAIL_RECEIVER_STATUS_ERR, e.getMessage());
                } catch (InterruptedException e1) {
                    //不管
                    log.error("========================", e1);
                }

            }
       // }
    }


    /**
     * POP3 解析新邮件 方法
     * POP3 没法使用IMAP方式 ，只能通过客户端的方式来搞
     *
     * @param folder
     * @return
     */
    private boolean pop3NewMailCheck(POP3Folder folder) {
        List<Message> sendMsgList = null;
        try {
            // 获取收件人信息
            String mailName = folder.getStore().getURLName().getUsername();

            Calendar calendar = Calendar.getInstance();
            // TODO: 腾讯邮箱 不支持 服务器端筛选 ，只能本地筛选 最1 天 。
            String time = ParamUtil.value("mail_pop_times");
            if (time==null ||"".equals(time)){
                time = "365";
            }
            calendar.add(Calendar.DAY_OF_MONTH, -Integer.valueOf(time));
            Date beforeDate = calendar.getTime();
            SearchTerm comparisonTermGe = new SentDateTerm(ComparisonTerm.GE, beforeDate);
            Message[] msgs = folder.search(comparisonTermGe);

            // 获取数据库该帐号邮件信息 -- 收件箱
            List<String> mailList = getMailDao().getMailListByName(mailName);
            String messageId = "";
            String sysMessageId = "";
            boolean flag = false;
            String redisMessageId = "";

            sendMsgList = new ArrayList<Message>();
            MimeMessage mimeMessage;
            for (int i = 0; i < msgs.length; i++) {
                try {
                    // 获取邮件所有的 messageId
                    messageId = folder.getUID(msgs[i]);
                    // 部分邮箱没有messageId的 ，需要用全局messageId 进行判断 如QQ
                    mimeMessage = (MimeMessage) msgs[i];
                    sysMessageId = mimeMessage.getMessageID();
                    if (StringUtils.isEmpty(messageId)) {
                        messageId = sysMessageId;
                    }
                } catch (Exception e) {
                    // 部分系统邮件 是获取不到messageId 的 ，这样的邮件直接抛出
                    if (StringUtils.isEmpty(messageId) && StringUtils.isEmpty(sysMessageId)) {
                        continue;
                    }
                }

                flag = true;
                // TODO: 查询数据库 ，根据messageId 进行比对， 如果不存在则入库 ，存在则不入库。
                // TODO： 只有POP3 需要此操作 ，IMAP直接实时刷新 暂不考虑
                for (String data : mailList) {
                    if (StringUtils.isEmpty(data) || data.equals(messageId) || data.equals(sysMessageId)) {
                        flag = false;
                        //log.info("数据库已经存在 。。");
                        break;
                    }
                }
                // 没有符合条件邮件
                if (flag) {
                    redisMessageId = MailCacheUtil.get(MailBoxCfg.USER_NEW_MAIL_POP3 + messageId);
                    if (StringUtils.isEmpty(redisMessageId)) {
                        redisMessageId = MailCacheUtil.get(MailBoxCfg.USER_NEW_MAIL_POP3 + messageId);
                    }
                    if (StringUtils.isNotEmpty(redisMessageId) && redisMessageId.equals(messageId)) {
                        log.info("正在处理中，请稍后... 主题是：" + msgs[i].getSubject());
                    } else {
                        log.info("数据库不存该邮件 。。主题是：" + msgs[i].getSubject());
                        sendMsgList.add(msgs[i]);
                        MailCacheUtil.set(MailBoxCfg.USER_NEW_MAIL_POP3 + messageId, messageId);
                    }
                }

            }

            // 未读消息 加入队列 进行解析、 入库
            for (Message msg : sendMsgList) {
                MailCache.getNewMailQueue().offer(msg);
            }




        } catch (MessagingException e) {
            e.printStackTrace();
            log.error("POP3解析新邮件方法失败", e);
        }
        return sendMsgList.isEmpty() ? false : true;
    }

    /**
     * 发送执行日志
     *
     * @param type
     * @param status
     * @param msg
     */
    private void sendLog(int type, int status, String msg) {
//        try {
//            // 发送正常日志
//            MailReceiverLog mailReceiverLog = new MailReceiverLog();
//            mailReceiverLog.setMailBox(mailBoxName);
//            mailReceiverLog.setMailBoxType(type);
//            mailReceiverLog.setStatus(status);
//            mailReceiverLog.setException(msg);
//            iMailReceiverLogService().sendLog(mailReceiverLog);
//        } catch (Exception e) {
//            log.error(e.getMessage());
//        }
    }
}
