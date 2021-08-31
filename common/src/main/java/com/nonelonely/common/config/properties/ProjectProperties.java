package com.nonelonely.common.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 项目配置项
 * @author nonelonely
 * @date 2018/11/6
 */
@Data
@Component
@ConfigurationProperties(prefix = "project")
public class ProjectProperties {

    /** 是否开启验证码 */
    private boolean captchaOpen = false;

    /** xss防护设置 */
    private Xxs xxs = new Xxs();

    /**
     * xss防护设置
     */
    @Data
    public static class Xxs {
        /** xss防护开关 */
        private boolean enabled = true;

        /** 拦截规则，可通过“,”隔开多个 */
        private String urlPatterns = "/*";

        /** 默认忽略规则（无需修改） */
        private String defaultExcludes = "/favicon.ico,/img/*,/js/*,/css/*,/Eextend/*,/system/*";

        /** 忽略规则，可通过“,”隔开多个 */
        private String excludes = "/frontend/compiler/*";

        /**
         * 拼接忽略规则
         */
        public String getExcludes() {
            if (!StringUtils.isEmpty(excludes.trim())) {
                return defaultExcludes + "," + excludes;
            }
            return defaultExcludes;
        }
    }
}
