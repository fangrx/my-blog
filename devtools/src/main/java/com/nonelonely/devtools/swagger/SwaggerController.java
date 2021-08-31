package com.nonelonely.devtools.swagger;

import com.nonelonely.modules.system.domain.permission.NBAuth;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import static com.nonelonely.modules.system.domain.permission.NBAuth.Group.ROUTER;
import static com.nonelonely.modules.system.domain.permission.NBSysResource.ResType.OTHER;

/**
 * @author nonelonely
 * @date 2018/12/9
 */
@Controller
public class SwaggerController {

    @GetMapping("/dev/swagger")
    @RequiresPermissions("/dev/swagger")
    @NBAuth(value = "dev:build:index", remark = "文档生成页面", type = OTHER, group = ROUTER)
    public String index(){
        return "redirect:/swagger-ui.html";
    }
}
