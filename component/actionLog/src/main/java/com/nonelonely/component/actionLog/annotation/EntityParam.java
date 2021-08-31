package com.nonelonely.component.actionLog.annotation;

import java.lang.annotation.*;

/**
 * 控制器实体参数注解
 * @author nonelonely
 * @date 2019/2/25
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER, ElementType.METHOD})
@Documented
public @interface EntityParam {
}
