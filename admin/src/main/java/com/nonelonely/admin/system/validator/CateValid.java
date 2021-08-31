package com.nonelonely.admin.system.validator;

import lombok.Data;

import java.io.Serializable;
import javax.validation.constraints.NotEmpty;

/**
 * @author nonelonely
 * @date 2020/01/01
 */
@Data
public class CateValid implements Serializable {
    @NotEmpty(message = "分类名称不能为空")
    private String name;
}
