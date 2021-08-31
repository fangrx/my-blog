package com.nonelonely.frontend.controller;

import com.nonelonely.common.data.RunInfo;
import com.nonelonely.common.utils.CompilerUtil;

import com.nonelonely.common.utils.ResultVoUtil;
import com.nonelonely.common.vo.ResultVo;
import com.nonelonely.component.fileUpload.FileUpload;
import com.nonelonely.modules.system.domain.permission.NBAuth;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.*;

import static com.nonelonely.modules.system.domain.permission.NBAuth.Group.ROUTER;
import static com.nonelonely.modules.system.domain.permission.NBSysResource.ResType.OTHER;

/**
 * @author nonelonely
 * @date 2020/02/13
 */
@Controller
@RequestMapping("/frontend")
public class CompilerController {



    /**
     * 列表页面
     */
    @RequestMapping("/compiler/compiler")
    @ResponseBody
    @RequiresPermissions("frontend:compiler:compiler")
    @NBAuth(value = "frontend:compiler:compiler", remark = "编译按钮", type = OTHER, group = ROUTER)
    public ResultVo compiler(String code) {

        return ResultVoUtil.success(CompilerUtil.getRunInfo(code));
    }
    /**
     * 列表页面
     */
    @RequestMapping("/compiler/index")
    public String index(String path, Model model) {
        if (path == null || path.equals("")) {
            path = "codes/1605621311300.txt";
        }
            path = path.replace("_","/");
            path = FileUpload.getUploadPath() + "/" + path;
            File file = new File(path);
            StringBuilder result = new StringBuilder();
            try {
                InputStreamReader read = new InputStreamReader(new FileInputStream(file),"UTF-8");
                BufferedReader br = new BufferedReader(read);//构造一个BufferedReader类来读取文件
                String s = null;
                while ((s = br.readLine()) != null) {//使用readLine方法，一次读一行
                    result.append(System.lineSeparator() + s);
                }
                br.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            model.addAttribute("code", result.toString());

        return  "/compiler";
    }
    /**
     * 列表页面
     */
    @RequestMapping("/json")
    public String json() {

        return  "/json";
    }
}
