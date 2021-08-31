package com.nonelonely.config;

import com.nonelonely.component.thymeleaf.utility.ParamUtil;
import com.nonelonely.modules.system.domain.Param;
import com.nonelonely.modules.system.domain.User;
import com.nonelonely.modules.system.repository.ParamRepository;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.util.StringUtils;

import java.util.Optional;

import static com.nonelonely.common.constant.ParamConst.INIT_STATUS;

/**
 * 审核员自动赋值配置
 * @author nonelonely
 * @date 2018/8/14
 */
@Configuration
public class AuditorConfig implements AuditorAware<User> {

    @Override
    public Optional<User> getCurrentAuditor() {
        User user = new User();
        user.setId(Long.parseLong("1"));
        try {
               Subject subject = SecurityUtils.getSubject();
               user = (User) subject.getPrincipal();
           }catch (Exception e){
              // e.printStackTrace();
           }
        return Optional.ofNullable(user);
    }
}
