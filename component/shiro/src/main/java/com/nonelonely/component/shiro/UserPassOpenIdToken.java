package com.nonelonely.component.shiro;

import org.apache.shiro.authc.UsernamePasswordToken;

public class UserPassOpenIdToken extends UsernamePasswordToken {

    public UserPassOpenIdToken(){
        super();
    }

    public String getLoginType() {
        return loginType;
    }
    public void setLoginType(String loginType) {
        this.loginType = loginType;
    }


    private String loginType = "0";// 0为用户密码登录，1为第三方登录登录

    public UserPassOpenIdToken(final String username, final String password,
                                          String loginType) {
        super(username, password);
        this.loginType = loginType;

    }


}
