package com.nonelonely.component.email.util;

import com.sun.mail.imap.IMAPStore;
import com.sun.mail.pop3.POP3Store;
import lombok.Data;

import javax.mail.Session;

@Data
public class MailSession {
    private String mailBoxName;
    private Session session;
    private IMAPStore imapStore;
    private POP3Store pop3Store;

}
