package com.nonelonely.component.email.service.receiver;


import com.nonelonely.common.utils.DateUtils;
import com.nonelonely.common.utils.SpringContextUtil;
import com.nonelonely.component.email.enums.MailEnum;
import com.nonelonely.component.email.enums.MailEnums;
import com.nonelonely.component.email.enums.MailTypeEnum;


import com.nonelonely.component.email.util.MailBoxCfg;
import com.nonelonely.component.email.util.MailCache;
import com.nonelonely.component.email.util.MailCacheUtil;
import com.nonelonely.modules.system.repository.mail.MailRepository;
import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPStore;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.mail.*;
import javax.mail.internet.MimeMessage;
import javax.mail.search.ComparisonTerm;
import javax.mail.search.SearchTerm;
import javax.mail.search.SentDateTerm;
import java.util.*;


/**
 * <p>
 * IMAP 收取邮件服务类
 * </p>
 *
 * @author yan
 * @since 2020-09-24
 */
@Slf4j
public class FatchImapMailThread extends Thread {
    // TODO: POP3定时拉取延迟时间   - 三分钟
    private static final long DELAY = 180000;

    // 保持活跃时间 5秒
    private static final long LOOPDELAY = 5000;
    //session
    private Session imapSession;
    //邮件
    private IMAPStore imapStore;

    //是否继续执行
    private boolean loopFlag = true;
    private String mailBoxName;


    // 默认拉取历史1天数据
    private static final int PULL_MAIL_DAY = 1;


    /**
     * 获取MailRepository对象的静态方法
     */
    private static MailRepository getMailDao() {
        return SpringContextUtil.getBean(MailRepository.class);
    }

    public FatchImapMailThread(IMAPStore imapStore,String mailBoxName, Session imapSession) {
        super();
        this.imapStore = imapStore;

        this.mailBoxName = mailBoxName;
        this.imapSession = imapSession;
        this.setName("FatchImapMailThread-" + mailBoxName);
    }

    public IMAPStore getIMAPStore() {
        return imapStore;
    }

    public void setIMAPStore(IMAPStore imapStore) {
        this.imapStore = imapStore;
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
        while (loopFlag) {
            try {
                // IMAP
                if (imapStore != null) {
                    getImapMails(imapStore);
                }
            } catch (Exception e) {
                try {
                    Thread.sleep(DELAY);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                // 异步发送日志
                sendLog(MailEnum.MAIL_TYPE_IMAP, MailEnum.MAIL_RECEIVER_STATUS_ERR, e.getMessage());
                log.error("收取邮件线程发生异常终止！尝试重启任务...", e);
            }
        }
    }

    /**
     * 描述：获取邮件方法  -- IMAP
     *
     * @param store
     * @throws Exception
     * @author yan
     * @date 2020年10月1日
     */
    private void getImapMails(final IMAPStore store) throws Exception {
        if (!store.isConnected()) {
            store.connect();
            mailAuht(store);
        }

        // 获取 folder
        // IMAPFolder folder = refreshFolder(store);
        // TODO: 部分邮箱 不进入监听时间 ，如 QQ ，等待排查
        // 加入邮件监听
/*        folder.addMessageCountListener(new MessageCountAdapter() {
            @Override
            public void messagesAdded(MessageCountEvent e) {
                // 新邮件来了！
                Message[] msgs = e.getMessages();
                log.info(mailBoxName + " IMAP收到 " + msgs.length + "封新邮件！");
                for (int i = 0; i < msgs.length; i++) {
                    try {
                        log.info(mailBoxName + " IMAP收到新邮件！ 主题是：" + msgs[i].getSubject());
                    } catch (MessagingException e1) {
                        e1.printStackTrace();
                        log.info(e1.getMessage());
                    }
                    //保存邮件收取记录
                    mailService.offerNewMailQueue(msgs[i]);
                }
            }
        });*/


        // 保持活跃
        //folder.idle();

        // 保持活跃
        loopLineOn(store);

    }

    /**
     * 保持活跃
     *
     * @param store
     */
    private void loopLineOn(IMAPStore store) {
        boolean loopLineOnFlag = true;
        IMAPFolder folder = null;
        while (loopLineOnFlag) {
            try {
                // 重新连接
                if (!store.isConnected()) {
                    store.connect();
                }
                // 每次都校验
                mailAuht(store);
                // 每次获取最新的 folder
                folder = refreshFolder(store);

                // 睡眠一段时间再获取
                Thread.sleep(DELAY);
                // 获取当前邮箱邮件数量
                if (!folder.isOpen()) {
                    // 每次获取最新的 folder
                    folder = refreshFolder(store);
                    // 重新保持活跃
                    //folder.idle();
                }

                // TODO： 暂时采用 POP3的方式 近期邮件 - 与客户端比较
                if (imapNewMailCheck(folder)) {
                    int newMessageCount = folder.getNewMessageCount();
                    log.info("IMAP收到新邮件！  IMAP账户： " + store.getURLName().getUsername() + "最新邮件数量：  " + newMessageCount);
                }

                // 异步发送日志
           //     sendLog(MailEnum.MAIL_TYPE_IMAP, MailEnum.MAIL_RECEIVER_STATUS_OK, ResultEnum.SUCCESS.getMsg());

            } catch (Exception e) {
                log.error(mailBoxName + "收取邮件循环发送异常：", e);
                if (e instanceof FolderClosedException) {
                    try {
                        log.info("尝试重新打开{}收件箱...", mailBoxName);
                        //如果需要在取得邮件数后将邮件置为已读则这里需要使用READ_WRITE,否则READ_ONLY就可以
                        // 每次获取最新的 folder
                        folder = refreshFolder(store);
                        // 重新保持活跃
                        //folder.idle();
                    } catch (MessagingException mex) {
                        log.error("尝试重新打开{}收件箱异常！！！", mailBoxName);
                        loopLineOnFlag = false;
                    }
                } else if (e instanceof InterruptedException) {
                    log.error("收到中断异常，准备停止邮件收取任务....", e);
                    loopLineOnFlag = false;
                    loopFlag = false;
                } else if (e instanceof MessagingException) {
                    loopLineOnFlag = false;
                }
                //睡眠5秒后再次尝试
                try {
                    Thread.sleep(LOOPDELAY);
                    // 异步发送日志
                    sendLog(MailEnum.MAIL_TYPE_IMAP, MailEnum.MAIL_RECEIVER_STATUS_ERR, e.getMessage());
                } catch (InterruptedException err) {
                    //不管
                    log.error("其他错误", err.getMessage());
                    loopFlag = false;
                }
            }
        }
    }

    /**
     * 通用方法 - 网易邮件不支持三方客户端连接，需要身份认证
     *
     * @param store
     */
    private void mailAuht(IMAPStore store) {
        // 获取host地址
        String host = store.getURLName().getHost();
        // 如果是网易邮箱（163、126、yeah 邮箱）
        if (host.indexOf(MailBoxCfg.MAIL_CFG_126) > 0 || host.indexOf(MailBoxCfg.MAIL_CFG_163) > 0 || host.indexOf(MailBoxCfg.MAIL_CFG_YEAH) > 0) {
            //带上IMAP ID信息，由key和value组成，例如name，version，vendor，support-email等。
            HashMap IAM = new HashMap();
            IAM.put(MailEnums.MailAuthEnum.NAME.getCode(), MailEnums.MailAuthEnum.NAME.getDesc());
            IAM.put(MailEnums.MailAuthEnum.VERSION.getCode(), MailEnums.MailAuthEnum.VERSION.getDesc());
            IAM.put(MailEnums.MailAuthEnum.VENDOR.getCode(), MailEnums.MailAuthEnum.VENDOR.getDesc());
            IAM.put(MailEnums.MailAuthEnum.SUPPORT_EMAIL.getCode(), MailEnums.MailAuthEnum.SUPPORT_EMAIL.getDesc());
            try {
                store.id(IAM);
            } catch (MessagingException e) {
                System.out.println(e.getMessage());
            }
        }
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

    /**
     * 采用 POP3 解析新邮件 方法
     *
     * @param folder
     * @return
     */
    private boolean imapNewMailCheck(IMAPFolder folder) {
        List<Message> sendMsgList = null;
        try {
            // 获取收件人信息
            String mailName = folder.getStore().getURLName().getUsername();

            Calendar calendar = Calendar.getInstance();
            // TODO: 腾讯邮箱 不支持 服务器端筛选 ，只能本地筛选 最1 天 。
            calendar.add(Calendar.DAY_OF_MONTH, -PULL_MAIL_DAY);
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
            String sendDate;
            String strBeforeDate = DateUtils.dateFormatStr(beforeDate);
            for (int i = 0; i < msgs.length; i++) {

                try {
                    // 获取邮件所有的 messageId
                    messageId = String.valueOf(folder.getUID(msgs[i]));

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

                // TODO 部分特殊邮件，系统邮件，没有发送时间 .. 暂定按照接收时间为准
                sendDate = DateUtils.dateFormatStr(msgs[i].getSentDate() == null ? msgs[i].getReceivedDate() : msgs[i].getSentDate());
                // TODO: 解析邮件 .... 存入队列  , 已经存入的邮件，不可以重复存入 。
                // TODO: 如果账号删除 ，直接删除数据库数据即可 。
                Integer result = sendDate.compareTo(strBeforeDate);

                //  满足条件的邮件进行比对
                if (result > -1) {
                    flag = true;
                    // TODO: 查询数据库 ，根据messageId 进行比对， 如果不存在则入库 ，存在则不入库。
                    for (String data : mailList) {
                        if (StringUtils.isEmpty(data) || data.equals(messageId) || data.equals(sysMessageId)) {
                            flag = false;
                            //log.info("数据库已经存在 。。");
                            break;
                        }
                    }
                }
                // 没有符合条件邮件
                if (flag) {
                    redisMessageId = MailCacheUtil.get(MailBoxCfg.USER_NEW_MAIL_IMAP + messageId);
                    if (StringUtils.isEmpty(redisMessageId)) {
                        redisMessageId = MailCacheUtil.get(MailBoxCfg.USER_NEW_MAIL_INIT + messageId);
                    }
                    if (StringUtils.isNotEmpty(redisMessageId) && redisMessageId.equals(messageId)) {
                        log.info("正在处理中，请稍后... 主题是：" + msgs[i].getSubject());
                    } else {
                        log.info("数据库不存该邮件 。。主题是：" + msgs[i].getSubject());
                        sendMsgList.add(msgs[i]);
                        MailCacheUtil.set(MailBoxCfg.USER_NEW_MAIL_IMAP + messageId, messageId);
                    }
                }
            }

            // 未读消息 加入redis 队列 进行解析、 入库
            for (Message msg : sendMsgList) {

                MailCache.getNewMailQueue().offer(msg);
            }

        } catch (MessagingException e) {
            e.printStackTrace();
            log.error("IMAP解析新邮件方法失败", e);
        }
        return sendMsgList.isEmpty() ? false : true;
    }

    /**
     * 获取最新收件箱 数据
     *
     * @param store
     * @return
     */
    private IMAPFolder refreshFolder(IMAPStore store) throws MessagingException {
        IMAPFolder folder = null;

        // 每次重新获取收件箱  ， 避免本地缓存导致获取不到最新邮件
        folder = (IMAPFolder) store.getFolder(MailTypeEnum.MAIL_TYPE_INBOX.getMsg());// 获取收件箱
        //如果需要在取得邮件数后将邮件置为已读则这里需要使用READ_WRITE,否则READ_ONLY就可以
        folder.open(Folder.READ_WRITE);

        return folder;
    }
}
