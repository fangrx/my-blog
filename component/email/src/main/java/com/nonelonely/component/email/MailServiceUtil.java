package com.nonelonely.component.email;

import com.nonelonely.modules.system.domain.Article;
import org.thymeleaf.context.Context;


import javax.mail.MessagingException;

/**
 * @ProjectName: noteblogv4
 * @Package: me.wuwenbin.noteblogv4.service.mail
 * @ClassName: ${CLASS_NAME}
 * @Author: nonelonely
 * @CreateDate: 2019-01-23 17:49
 * @Version: 1.0
 * @Copyright: Copyright Reserved (c) 2019, http://www.nonelonely.com
 * @Dependency:
 * @Description: java类作用描述
 * -
 * ****************************************************************
 * @UpdateUser: 13434
 * @UpdateDate: 2019-01-23 17:49
 * @UpdateRemark: The modified content
 * ****************************************************************
 * -
 */
public interface MailServiceUtil {


    /**
     * 发送邮件通知邮件
     */
    void sendMsgMail(Email email);

    void htmlEmail(Context ctx, Email email);
    void htmlEmail(Email email);
    /**
     *  从邮箱系统中获取邮箱
     */
      void getMail();
}

