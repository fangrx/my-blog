package com.nonelonely.devtools.build;

import com.nonelonely.modules.system.domain.permission.NBAuth;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import static com.nonelonely.modules.system.domain.permission.NBAuth.Group.ROUTER;
import static com.nonelonely.modules.system.domain.permission.NBSysResource.ResType.OTHER;

/**
 * @author nonelonely
 * @date 2018/8/14
 */
@Controller
@RequestMapping("/dev/build")
public class BuildController {

    @GetMapping
    @NBAuth(value = "dev:build:index", remark = "表单生成页面", type = OTHER, group = ROUTER)
    public String index(Model model){
        return "/devtools/build/index";
    }
}
