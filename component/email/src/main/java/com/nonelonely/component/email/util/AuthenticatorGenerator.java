package com.nonelonely.component.email.util;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

public class AuthenticatorGenerator {
	/**
	 * 根据用户名和密码，生成Authenticator
	 * @param userName
	 * @param password
	 * @return
	 */
	public static Authenticator getAuthenticator(final String userName, final String password) {
		return new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(userName, password);
			}
		};
	}
}
