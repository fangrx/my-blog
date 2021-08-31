package com.nonelonely.component.email.service.auth;

import com.alibaba.fastjson.JSON;


import com.nonelonely.common.enums.StatusEnum;
import com.nonelonely.common.utils.ResultVoUtil;
import com.nonelonely.common.vo.ResultVo;
import com.nonelonely.component.email.enums.MailEnum;
import com.nonelonely.component.email.enums.MailEnums;
import com.nonelonely.component.email.enums.MailSmtpTypeEnum;
import com.nonelonely.component.email.enums.ResultEnum;
import com.nonelonely.component.email.util.MailBoxCfg;
import com.nonelonely.component.email.vo.MailAuth;
import com.nonelonely.modules.system.domain.mail.MailBox;
import com.nonelonely.modules.system.repository.mail.MailBoxRepository;
import com.sun.mail.imap.IMAPStore;
import com.sun.mail.pop3.POP3Store;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.mail.*;
import java.util.HashMap;
import java.util.Properties;

import static com.nonelonely.component.email.util.MailBoxCfg.MAIL_CFG_FALSE;
import static com.nonelonely.component.email.util.MailBoxCfg.MAIL_CFG_TRUE;


/**
 * <p>
 * 邮箱校验服务类
 * </p>
 *
 * @author yan
 * @since 2020-09-24
 */
@Slf4j
@Component
public class AuthMailService {



    @Autowired
    private AuthMailThreadService authMailThreadService;
    @Autowired
    private MailBoxRepository mailBoxRepository;

    /**
     * 邮箱配置检查
     *
     * @param mail       邮箱配置更新
     * @param updateData true ：更新数据  false: 不更新数据
     * @param boxCode    历史账号名称
     * @return
     */
    public ResultVo checkAndUpdate(MailBox mail, boolean updateData, String boxCode) {

        if (mail.getId() ==null || mail.getId()==0) {
            MailBox mailBox1 = mailBoxRepository.getMailBoxByMailBoxCode(mail.getMailBoxCode());
            if (mailBox1 != null && mailBox1.getStatus() == StatusEnum.OK.getCode()) {
                return ResultVoUtil.error("账号已经存在");
            }else if (mailBox1 != null){
                mail.setId(mailBox1.getId());
            }
        }
        // SSL
        String ssl;
        if (mail.getMailIsSsl()) {
            ssl = MAIL_CFG_TRUE;
        } else {
            ssl = MAIL_CFG_FALSE;
        }

        // 返回结果
        MailAuth result = new MailAuth();

        try {
            // 加载发送邮件箱配置
            Properties properties = new Properties();

            // debug 模式
            // properties.put(MailBoxCfg.MAIL_DEBUG, MailBoxCfg.MAIL_CFG_TRUE);

            // 验证发送服务器
            properties.put(MailBoxCfg.MAIL_SMTP_AUTH, MailBoxCfg.MAIL_CFG_TRUE);
            properties.put(MailBoxCfg.MAIL_SMTP_HOST, mail.getMailSmtpUrl());
            properties.put(MailBoxCfg.MAIL_SMTP_PORT, mail.getMailSmtpPort());

            // 开启SSL
            if (mail.getMailSmtpIsSsl()) {
                properties.put(MailBoxCfg.MAIL_SMTP_SOCKETFACTORY_PORT, mail.getMailSmtpPort());
                properties.put(MailEnums.MailAuthEnum.SSL_FACTORY.getCode(), MailEnums.MailAuthEnum.SSL_FACTORY.getDesc());
                properties.put(MailEnums.MailAuthEnum.SSL_FALLBACK.getCode(), MailEnums.MailAuthEnum.SSL_FALLBACK.getDesc());
            } else {
                properties.put(MailBoxCfg.MAIL_STORE_PROTOCOL, MailBoxCfg.MAIL_CFG_SMTP);
            }


            //  发件服务器认证
            Session emailSession = Session.getInstance(properties, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(mail.getMailBoxCode(), mail.getMailBoxPwd());
                }
            });

            // 发件服务器验证
            sendTest(emailSession, mail);

            // 执行更新
            result = updateDataInit(result, mail, properties, ssl, emailSession, boxCode, updateData);
            if (!result.isAuthSuccess()) {
                return ResultVoUtil.error(result.getAuthInfo());
            }


            mailBoxRepository.save(mail);
            return ResultVoUtil.success();
        } catch (NoSuchProviderException e) {
            // 邮箱验证错误
            log.error(e.getMessage());
            return ResultVoUtil.error("邮箱验证错误"+e.getMessage());
        } catch (AuthenticationFailedException e) {
            //  邮箱认证错误
            log.error(e.getMessage());
            return ResultVoUtil.error("邮箱认证错误"+e.getMessage());
        } catch (MessagingException e) {
            if (e.getMessage().indexOf("EOF on socket") > -1) {
                // POP3服务器或者账号信息错误，请检查 :
                result = new MailAuth(ResultEnum.MAILBOX_POP3_CHECK_ERROR.getCode(), ResultEnum.MAILBOX_POP3_CHECK_ERROR.getMsg() + " :" + e.getMessage(), false);
            } else {
                // 授权错误
               // result = new MailAuth(ResultEnum.MAILBOX_CONNECT_ERR.getCode(), ResultEnum.MAILBOX_CONNECT_ERR.getMsg() + " " + mail.getMailUrl() + ":" + mail.getMailPort(), false);
            }
            log.error(e.getMessage());
            e.printStackTrace();
            return ResultVoUtil.error("POP3服务器或者账号信息错误，请检查:"+e.getMessage());
        } catch (Exception e) {
            // 其他错误
            log.error(e.getMessage());
            e.printStackTrace();
            return ResultVoUtil.error("其他错误:"+e.getMessage());
        }


    }

    /**
     * 数据更新
     *
     * @param result
     * @param mail
     * @param properties
     * @param ssl
     * @param emailSession
     * @param boxCode
     * @param updateData
     * @return
     * @throws MessagingException
     */
    private MailAuth updateDataInit(MailAuth result, MailBox mail, Properties properties, String ssl, Session emailSession, String boxCode, boolean updateData) throws MessagingException {
        // 邮箱类初始化
        Folder emailFolder = null;
        POP3Store pop3Store = null;
        IMAPStore imapStore = null;
        String storeType = "";

        // 账号服务器类型
        if (mail.getMailBoxType().equals(MailEnum.MAIL_TYPE_IMAP)) {
            // IMAP
            properties.put(MailBoxCfg.MAIL_IMAP_HOST, mail.getMailImapUrl());
            properties.put(MailBoxCfg.MAIL_IMAP_PORT, mail.getMailImapPort());
            properties.put(MailBoxCfg.MAIL_IMAP_SSL_ENABLE, ssl);
            //properties.setProperty("mail.imap.usesocketchannels", "true");

            storeType = MailBoxCfg.MAIL_CFG_IMAP;

            // 创建imap Sotre对象并连接到amap服务
            imapStore = (IMAPStore) emailSession.getStore(storeType);
            imapStore.connect();

            // 网易邮箱身份认证 -- 特殊处理
            if (!mailAuht(imapStore)) {
                return new MailAuth(ResultEnum.MAILBOX_AUTH_ERROR.getCode(), ResultEnum.MAILBOX_AUTH_ERROR.getMsg(), false);
            }

            // INBOX 收件箱   Send Messages = 已发送 Drafts = 草稿箱  Deleted Messages 已删除 Junk 垃圾箱
            // 创建Folder对象并打开它
            // updateDate = ture 执行更新
            if (updateData) {
                emailFolder = imapStore.getFolder(MailEnums.MailTypeEnum.MAIL_TYPE_INBOX.getMsg());
                result = authMailThreadService.mailTempInit(result, emailFolder, MailEnums.MailTypeEnum.MAIL_TYPE_INBOX.getCode());
                log.info("IMAP--result--->" + JSON.toJSONString(result));
                result = new MailAuth();
                result.setAuthSuccess(true);
                result.setAuthInfo("邮件后台加载中。。。");
                // 错误返回
       /*         if (!result.isAuthSuccess()) {
                    return result;
                }*/
            } else {
                result = new MailAuth(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), true);
            }

            // 新增线程
            authMailThreadService.addMailThread(mail, imapStore, null,  boxCode,emailSession);

        } else if (mail.getMailBoxType().equals(MailEnum.MAIL_TYPE_POP3)) {
            // POP3
            properties.put(MailBoxCfg.MAIL_POP3_HOST, mail.getMailPop3Url());
            properties.put(MailBoxCfg.MAIL_POP3_PORT, mail.getMailPop3Port());
            properties.put(MailBoxCfg.MAIL_POP3_SSL_ENABLE, ssl);
            storeType = MailBoxCfg.MAIL_CFG_POP3;
            // 创建POP3 Sotre对象并连接到pop服务
            pop3Store = (POP3Store) emailSession.getStore(storeType);
            pop3Store.connect();

            // INBOX 收件箱   Send Messages = 已发送 Drafts = 草稿箱  Deleted Messages 已删除 Junk 垃圾箱
            // POP3 无状态 ，只有收件箱
            // 创建Folder对象并打开它
            // updateDate = ture 执行更新
            if (updateData) {
                emailFolder = pop3Store.getFolder(MailEnums.MailTypeEnum.MAIL_TYPE_INBOX.getMsg());
                result = authMailThreadService.mailTempInit(result, emailFolder, MailEnums.MailTypeEnum.MAIL_TYPE_INBOX.getCode());
                log.info("pop3--result--->" + JSON.toJSONString(result));
                result = new MailAuth();
                result.setAuthSuccess(true);
                result.setAuthInfo("邮件后台加载中。。。");
  /*              // 错误返回
                if (!result.isAuthSuccess()) {
                    return result;
                }*/
            } else {
                result = new MailAuth(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), true);
            }
            // 新增线程
            authMailThreadService.addMailThread(mail, null, pop3Store, boxCode,emailSession);
        }

        return result;
    }

    /**
     * 发件服务器链接测试
     *
     * @param emailSession
     * @param mail
     * @throws MessagingException
     */
    private void sendTest(Session emailSession, MailBox mail) throws MessagingException {
        Transport transport = emailSession.getTransport("smtp");
        //打开链接
        if (!transport.isConnected()) {
            transport.connect(mail.getMailSmtpUrl(), Integer.parseInt(mail.getMailSmtpPort()), mail.getMailBoxCode(), mail.getMailBoxPwd());
        }
        transport.close();
    }


    /**
     * 通用方法 - 网易邮件不支持三方客户端连接，需要身份认证
     *
     * @param store
     */
    private boolean mailAuht(IMAPStore store) {
        // 获取host地址
        String host;
        host = store.getURLName().getHost();

        // 如果是网易邮箱（163、126、yeah 邮箱）
        if (host.indexOf(MailBoxCfg.MAIL_CFG_126) > 0 || host.indexOf(MailBoxCfg.MAIL_CFG_163) > 0 || host.indexOf(MailBoxCfg.MAIL_CFG_YEAH) > 0) {
            //带上IMAP ID信息，由key和value组成，例如name，version，vendor，support-email等。
            HashMap IAM = new HashMap();
            IAM.put(MailEnums.MailAuthEnum.NAME.getCode(), MailEnums.MailAuthEnum.NAME.getDesc());
            IAM.put(MailEnums.MailAuthEnum.VERSION.getCode(), MailEnums.MailAuthEnum.VERSION.getDesc());
            IAM.put(MailEnums.MailAuthEnum.VENDOR.getCode(), MailEnums.MailAuthEnum.VENDOR.getDesc());
            IAM.put(MailEnums.MailAuthEnum.SUPPORT_EMAIL.getCode(), MailEnums.MailAuthEnum.SUPPORT_EMAIL.getDesc());

            try {
                if (store == null) {
                    // pstore.id(IAM);
                } else {
                    store.id(IAM);
                }
            } catch (MessagingException e) {
                log.error(e.getMessage());
                return false;
            }
        }
        return true;
    }

}
