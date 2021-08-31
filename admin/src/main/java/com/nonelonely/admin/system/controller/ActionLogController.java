package com.nonelonely.admin.system.controller;

import com.nonelonely.common.utils.ResultVoUtil;
import com.nonelonely.common.vo.ResultVo;
import com.nonelonely.modules.system.domain.ActionLog;
import com.nonelonely.modules.system.domain.permission.NBAuth;
import com.nonelonely.modules.system.service.ActionLogService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import static com.nonelonely.modules.system.domain.permission.NBAuth.Group.PAGE;
import static com.nonelonely.modules.system.domain.permission.NBAuth.Group.ROUTER;
import static com.nonelonely.modules.system.domain.permission.NBSysResource.ResType.NAV_LINK;
import static com.nonelonely.modules.system.domain.permission.NBSysResource.ResType.OTHER;

/**
 * @author nonelonely
 * @date 2018/10/19
 */
@Controller
@RequestMapping("/system/actionLog")
public class ActionLogController {

    @Autowired
    private ActionLogService actionLogService;

    /**
     * 列表页面
     */
    @GetMapping("/index")
    @RequiresPermissions("system:actionLog:index")
    @NBAuth(value = "system:actionLog:index", remark = "日志列表页面", type = OTHER, group = PAGE)
    public String index(Model model, ActionLog actionLog){

        // 创建匹配器，进行动态查询匹配
        ExampleMatcher matcher = ExampleMatcher.matching();

        // 获取日志列表
        Example<ActionLog> example = Example.of(actionLog, matcher);
        Page<ActionLog> list = actionLogService.getPageList(example);

        // 封装数据
        model.addAttribute("list",list.getContent());
        model.addAttribute("page",list);
        return "/system/actionLog/index";
    }

    /**
     * 跳转到详细页面
     */
    @GetMapping("/detail/{id}")
    @RequiresPermissions("system:actionLog:detail")
    @NBAuth(value = "system:actionLog:detail", remark = "日志详细页面", type = NAV_LINK, group = ROUTER)
    public String toDetail(@PathVariable("id") ActionLog actionLog, Model model){
        model.addAttribute("actionLog",actionLog);
        return "/system/actionLog/detail";
    }

    /**
     * 删除指定的日志
     */
    @RequestMapping("/status/delete")
    @RequiresPermissions("system:actionLog:delete")
    @ResponseBody
    @NBAuth(value = "system:actionLog:delete", remark = "删除指定日志", type = NAV_LINK, group = ROUTER)
    public ResultVo delete(
            @RequestParam(value = "ids", required = false) Long id){
        if (id != null){
            actionLogService.deleteId(id);
            return ResultVoUtil.success("删除日志成功");
        }else {
            actionLogService.emptyLog();
            return ResultVoUtil.success("清空日志成功");
        }
    }
}
