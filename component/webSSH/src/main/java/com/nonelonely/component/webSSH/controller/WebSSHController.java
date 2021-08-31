package com.nonelonely.component.webSSH.controller;

import com.nonelonely.modules.system.domain.permission.NBAuth;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import static com.nonelonely.modules.system.domain.permission.NBAuth.Group.ROUTER;
import static com.nonelonely.modules.system.domain.permission.NBSysResource.ResType.OTHER;

@Controller
public class WebSSHController {
    @RequestMapping("/system/webssh/index")
    @RequiresPermissions("system:webssh:index")
    @NBAuth(value = "system:webssh:index", remark = "webSSHҳ��", type = OTHER, group = ROUTER)
    public String websshpage(){
        return "/webssh";
    }
}
