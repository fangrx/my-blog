package com.nonelonely.component.email.enums;

/**
 * //处理状态 0未分配， 1 已分配给坐席 2 坐席已回复 3 不处理  4 未读5 已读
 * @author YangJianquan
 */
public class MailStatus {

	/**
	 * 发送中
	 */
	public static final Integer MAIL_SENDING = 0;
	/**
	 * 发送成功
	 */
	public static final Integer MAIL_SENDSUCCESS = 1;
	/**
	 * 发送失败
	 */
	public static final Integer MAIL_SENDFAILED = 2;
}
