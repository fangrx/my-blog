package com.nonelonely.modules.system.domain.permission;


import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;


@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NBAuth {

    /**
     * 权限标识，类似 shiro
     * 推荐规则：[aaa]:[bbb]:[ccc]
     * aaa->填写某个模块的标识
     * bbb->填写模块下某个功能的标识
     * ccc->填写某个功能的某种操作的标识
     * eg：例如管理员模块下的用户模块的添加操作-> management:user:create
     *
     * @return 该资源的权限标识，唯一
     */
    String value();

    /**
     * 权限标识，同{@code value()}
     *
     * @return
     */
    @AliasFor("value")
    String permission() default "";

    /**
     * 该权限标识的意义
     *
     * @return 说明
     */
    String remark() default "";


    /**
     * url的类型
     * 可做导航或者其他两种类型
     *
     * @return
     */
    NBSysResource.ResType type() default NBSysResource.ResType.OTHER;

    /**
     * 资源分组
     *
     * @return
     */
    Group group();

    /**
     * 资源的三种类别
     * @author wuwenbin
     */
    enum Group {
        /**
         * ajax、router、page
         */
        AJAX,
        ROUTER,
        PAGE
    }
}
