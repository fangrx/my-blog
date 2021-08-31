package com.nonelonely.component.email.service.sender;




import com.nonelonely.common.utils.HttpServletUtil;
import com.nonelonely.component.email.enums.MailSmtpTypeEnum;
import com.nonelonely.component.email.service.listener.SendMailTransportListener;
import com.nonelonely.component.email.util.MailMessage;
import com.nonelonely.component.email.vo.MailAttachmentVo;
import com.nonelonely.component.thymeleaf.utility.ParamUtil;
import com.nonelonely.modules.system.domain.mail.MailBox;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.ImageHtmlEmail;
import org.apache.commons.mail.resolver.DataSourceUrlResolver;

import javax.mail.MessagingException;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>
 * 邮件发送服务
 * </p>
 *
 * @author liuming
 * @since 2020-09-15
 */
public class CommonMailSender {
    /**
     * 收件人分割符
     */
    private final String SPLIT_CHAR = ",";
    /**
     * 编码
     */
    private final String CHAR_SET = "UTF-8";
    /**
     * 发送协议
     */
    private final String SEND_PROTOCOL = "smtp";
    /**
     * 外部邮件配置对象
     */
    private MailBox mailCfg;
    /**
     * 邮件体
     */
    private ImageHtmlEmail mail = new ImageHtmlEmail();
    /**
     * 邮件发送监听
     */
    private SendMailTransportListener sendMailListener;

    public CommonMailSender(MailBox mailCfg, SendMailTransportListener sendMailListener) {
        super();
        this.sendMailListener = sendMailListener;
        this.mailCfg = mailCfg;
    }

    /**
     * 异步发送邮件，配置邮件监听
     *
     * @param sendingMail
     * @throws EmailException
     * @throws MessagingException
     * @throws MalformedURLException
     */
    public void sendMailAsync(SendingMail sendingMail) throws Exception {
        // 1- 参数校验
        if (sendingMail == null) {
            throw new Exception("邮件发送参数不正确");
        }
        // 2- 构建邮件发送体
        ImageHtmlEmail mail = buildHtmlEmail(sendingMail);
        // 3- 构建邮件对象
        mail.buildMimeMessage();
        MimeMessage mimeMessage = mail.getMimeMessage();
        MailMessage mailMsg = new MailMessage(mimeMessage);
        mailMsg.setId(sendingMail.getId());
        // 4- 获取发送对象
        Transport transport = mail.getMailSession().getTransport(SEND_PROTOCOL);
        transport.addTransportListener(sendMailListener);
        //打开链接
        if (!transport.isConnected()) {
            transport.connect();
        }
        //5- 执行发送
        transport.sendMessage(mailMsg, mimeMessage.getAllRecipients());
    }


    /**
     * 构建邮件体
     *
     * @param sendingMail 邮件发送内容
     * @return
     * @throws EmailException
     * @throws MalformedURLException
     */
    private ImageHtmlEmail buildHtmlEmail(SendingMail sendingMail) throws EmailException, MalformedURLException {
        // 1- 设置基本参数
        setBaseParam();
        // 2- 设置发送信息
        // 标题
        mail.setSubject(sendingMail.getSubject());
        // 内容
        mail.setHtmlMsg(sendingMail.getHtml());
        // 邮件头
        // 设置是否需要回执，默认是不需要，只要设置了就表示需要，noteTo是回执发送的地址（一般是发件人）
        // setHeader("Disposition-Notification-To", noteTo)
        //设置优先级(1:紧急 3:普通 5:低)，紧急的邮件在接收方看到会有叹号标志的
        // setHeader("X-Priority", "1");
        mail.setHeaders(sendingMail.getMailHeader());
        // 接收人
        addReceiver(sendingMail.getToUsers(), 1);
        // 抄送人
        addReceiver(sendingMail.getCcUsers(), 2);
        // 密送人
        addReceiver(sendingMail.getBccUsers(), 3);
        // 3- 设置附件信息
        setAttachment(sendingMail.getAttachmentList());
        return mail;
    }

    /**
     * 添加接收人
     *
     * @param users 接收人
     * @param type  接收人类型 1收件人 2抄送人  3密送人
     * @throws EmailException
     */
    private void addReceiver(String users, int type) throws EmailException {
        // 接收人
        if (StringUtils.isNotBlank(users)) {
            String[] effectives = removeIllegalMail(users.split(SPLIT_CHAR));
            switch (type) {
                case 1:
                    mail.addTo(effectives);
                    break;
                case 2:
                    mail.addCc(effectives);
                    break;
                case 3:
                    mail.addBcc(effectives);
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 移除无效的邮件（格式不合法的）
     *
     * @param mails
     * @return
     */
    private String[] removeIllegalMail(String[] mails) {
        ArrayList<String> effectives = new ArrayList();
        for (int i = 0; i < mails.length; i++) {
            if (isEmail(mails[i])) {
                effectives.add(mails[i]);
            }
        }
        return effectives.toArray(new String[effectives.size()]);
    }

    /**
     * 校验是否是邮件
     *
     * @param email
     * @return
     */
    public static boolean isEmail(String email) {
        if (null == email || "".equals(email)) {
            return false;
        }
        String regEx1 = "^[a-zA-Z0-9_.-]+@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*\\.[a-zA-Z0-9]{2,6}$";
        Pattern p = Pattern.compile(regEx1);
        Matcher m = p.matcher(email);
        if (m.matches()) {
            return true;
        } else {
            return false;
        }
    }

    public static void main(String[] args) {
        String email = "sunjj_14-1205@126.co222m.cn";
        System.out.println(CommonMailSender.isEmail(email));
    }

    /**
     * 设置smtp相关参数
     */
    private void setBaseParam() throws EmailException, MalformedURLException {
        // 1- 设置ssl协议地址、ssl协议的端口号
        mail.setHostName(mailCfg.getMailSmtpUrl());
        mail.setSmtpPort(Integer.parseInt(mailCfg.getMailSmtpPort()));
        // 2- 设置加密类型
//        int smtpType = mailCfg.getMailIsSsl();
//        if (MailSmtpTypeEnum.SSL.getCode() == smtpType) {
//            mail.setSSLOnConnect(true);
//        } else if (MailSmtpTypeEnum.STARTTLS.getCode() == smtpType) {
//            mail.setStartTLSEnabled(true);
//        }

        if (mailCfg.getMailIsSsl()) {
            mail.setSSLOnConnect(true);
        }
        // 3- 设置邮箱地址、邮箱密码
        mail.setAuthentication(mailCfg.getMailBoxCode(), mailCfg.getMailBoxPwd());
        // 4- 设置编码
        mail.setCharset(CHAR_SET);
        // 5- 设置发件人
        mail.setFrom(mailCfg.getMailBoxCode(),mailCfg.getMailBoxName());

        // 6- ImageHtmlEmail通过setDataSourceResolver来识别并嵌入图片，添加网络解析的解析器
        URL url = new URL("http://");
        mail.setDataSourceResolver(new DataSourceUrlResolver(url));
    }

    /**
     * 设置附件信息
     *
     * @param list 附件列表
     * @throws Exception
     */
    private void setAttachment(List<MailAttachmentVo> list) {
        HttpServletRequest request = HttpServletUtil.getRequest();
        StringBuffer url = request.getRequestURL();
        String baseUrl = url.delete(url.length() - request.getRequestURI().length(), url.length())
                    .append(request.getContextPath()).toString();



        if (null != list) {
            list.forEach(mainAttachment -> {
                try {
                    EmailAttachment attachment = new EmailAttachment();
                    attachment.setURL(new URL(baseUrl+mainAttachment.getUrl()));
                    attachment.setName(mainAttachment.getName());
                    attachment.setDisposition(EmailAttachment.ATTACHMENT);
                    attachment.setDescription(mainAttachment.getSize()+"");
                    mail.attach(attachment);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }


    /**
     * 描述：同步方式发送邮件
     *
     * @param sendingMail
     * @throws MalformedURLException
     * @throws EmailException
     * @author 杨建全
     * @date 2017年6月7日
     */
    public void sendMail(SendingMail sendingMail) throws MalformedURLException, EmailException {
        ImageHtmlEmail mail = buildHtmlEmail(sendingMail);
        mail.send();
    }
}
