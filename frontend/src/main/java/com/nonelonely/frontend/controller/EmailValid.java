package com.nonelonely.frontend.controller;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

/**
 * @author nonelonely
 * @date 2020/01/01
 */
@Data
public class EmailValid implements Serializable {
    @NotEmpty(message = "邮箱不能为空")
    private String email;
    @NotEmpty(message = "验证码不能为空")
    private String emailCode;
}
