package com.nonelonely.admin.system.controller;


import com.nonelonely.common.utils.ResultVoUtil;
import com.nonelonely.common.vo.ResultVo;
import com.nonelonely.component.thymeleaf.utility.ParamUtil;
import com.nonelonely.modules.system.domain.Param;
import com.nonelonely.modules.system.domain.permission.NBAuth;
import org.apache.shiro.authz.annotation.RequiresPermissions;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import static com.nonelonely.modules.system.domain.permission.NBAuth.Group.ROUTER;
import static com.nonelonely.modules.system.domain.permission.NBSysResource.ResType.OTHER;

/**
 * @author nonelonely
 * @date 2020/01/01
 */
@Controller
@RequestMapping("/system/deployment")
public class DeploymentController {
    /**
     * 列表页面
     */
    @GetMapping("/index")
    @RequiresPermissions("system:deployment:index")
    @NBAuth(value = "system:deployment:index", remark = "部署管理", type = OTHER, group = ROUTER)
    public String index(Model model, HttpServletRequest request) {

        return "/system/deployment/index";

    }
    /**
     * 列表页面
     */
    @GetMapping("/jiaoben")
    @RequiresPermissions("system:deployment:jiaoben")
    @ResponseBody
    @NBAuth(value = "system:deployment:jiaoben", remark = "执行脚本", type = OTHER, group = ROUTER)
    public ResultVo jiaoben(Model model, HttpServletRequest request, String path) {
       // String path = ParamUtil.value("deployment_jiaoben_path");
        String cmd = path;
        try {
            Runtime rt = Runtime.getRuntime();
            Process proc = rt.exec(cmd);
            InputStream is = proc.getInputStream();
            InputStream es = proc.getErrorStream();
            String line;
            BufferedReader br;
            br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            while ((line = br.readLine()) != null) {

                System.out.println(">>>{}"+line);
                return ResultVoUtil.success(line);
            }
            br = new BufferedReader(new InputStreamReader(es, "UTF-8"));
            while ((line = br.readLine()) != null) {
                System.out.println(">>>{}"+line);
                return ResultVoUtil.error(line);

            }
        } catch (Exception e) {
            System.out.println(">>>异常信息");
            return ResultVoUtil.error(e.getMessage());
        }
        return ResultVoUtil.success("执行成功");
    }
}
