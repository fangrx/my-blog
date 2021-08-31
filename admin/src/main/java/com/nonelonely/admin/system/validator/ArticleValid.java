package com.nonelonely.admin.system.validator;

import lombok.Data;

import java.io.Serializable;
import javax.validation.constraints.NotEmpty;

/**
 * @author nonelonely
 * @date 2020/01/01
 */
@Data
public class ArticleValid implements Serializable {
    @NotEmpty(message = "标题不能为空")
    private String title;
    @NotEmpty(message = "文章不能为空")
    private String content;
}
