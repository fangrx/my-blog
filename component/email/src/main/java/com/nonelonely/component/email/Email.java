package com.nonelonely.component.email;

import lombok.*;

import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Email {

    private  String host;
    private  String port;
    private  String from;
    private  String user;
    private  String pass;//
    private  String to; //对方
    private  String subject; //主题
    private  String template; //模板
    private  Map  data;//模板数据
    private  String comment; //内容
    private  String nick;//别名
}
