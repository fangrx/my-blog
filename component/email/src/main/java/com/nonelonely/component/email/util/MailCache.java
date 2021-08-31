package com.nonelonely.component.email.util;

import com.nonelonely.component.email.vo.MailReq;

import javax.mail.Message;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MailCache {
	//待解析的邮件队列
	protected static final ConcurrentLinkedQueue<Message> NEWMAILQUEUE = new ConcurrentLinkedQueue<>();
	//待发送的邮件队列
	protected static final ConcurrentLinkedQueue<MailReq> SENDMAILQUEUE = new ConcurrentLinkedQueue<>();

	public static ConcurrentLinkedQueue<Message> getNewMailQueue() {
		return NEWMAILQUEUE;
	}

	public static ConcurrentLinkedQueue<MailReq> getSendMailQueue() {
		return SENDMAILQUEUE;
	}
}
