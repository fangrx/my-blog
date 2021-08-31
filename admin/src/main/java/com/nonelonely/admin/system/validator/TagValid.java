package com.nonelonely.admin.system.validator;

import lombok.Data;

import java.io.Serializable;
import javax.validation.constraints.NotEmpty;

/**
 * @author nonelonely
 * @date 2020/01/01
 */
@Data
public class TagValid implements Serializable {
    @NotEmpty(message = "标签名称不能为空")
    private String name;
}
