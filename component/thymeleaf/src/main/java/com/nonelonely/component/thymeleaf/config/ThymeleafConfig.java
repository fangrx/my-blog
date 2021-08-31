package com.nonelonely.component.thymeleaf.config;

import com.nonelonely.component.thymeleaf.DolphinDialect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author nonelonely
 * @date 2018/8/14
 */
@Configuration
public class ThymeleafConfig {

    /**
     * 配置自定义的CusDialect，用于整合thymeleaf模板
     */
    @Bean
    public DolphinDialect getdolphinDialect(){
        return new DolphinDialect();
    }
}
