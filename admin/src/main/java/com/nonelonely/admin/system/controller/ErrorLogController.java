package com.nonelonely.admin.system.controller;

import com.nonelonely.admin.system.validator.ErrorLogValid;
import com.nonelonely.common.enums.StatusEnum;
import com.nonelonely.common.utils.EntityBeanUtil;
import com.nonelonely.common.utils.ResultVoUtil;
import com.nonelonely.common.utils.StatusUtil;
import com.nonelonely.common.vo.ResultVo;
import com.nonelonely.modules.system.domain.ErrorLog;
import com.nonelonely.modules.system.domain.permission.NBAuth;
import com.nonelonely.modules.system.service.ErrorLogService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import static com.nonelonely.modules.system.domain.permission.NBAuth.Group.ROUTER;
import static com.nonelonely.modules.system.domain.permission.NBSysResource.ResType.NAV_LINK;
import static com.nonelonely.modules.system.domain.permission.NBSysResource.ResType.OTHER;

import java.util.List;

/**
 * @author nonelonely
 * @date 2021/02/01
 */
@Controller
@RequestMapping("/system/errorLog")
public class ErrorLogController {

    @Autowired
    private ErrorLogService errorLogService;

    /**
     * 列表页面
     */
    @GetMapping("/index")
    @RequiresPermissions("system:errorLog:index")
    @NBAuth(value = "system:errorLog:index", remark = "errorlog列表页面", type = OTHER, group = ROUTER)
    public String index(Model model, ErrorLog errorLog) {

        // 创建匹配器，进行动态查询匹配
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withMatcher("content", match -> match.contains());

        // 获取数据列表
        Example<ErrorLog> example = Example.of(errorLog, matcher);
        Page<ErrorLog> list = errorLogService.getPageList(example);

        // 封装数据
        model.addAttribute("list", list.getContent());
        model.addAttribute("page", list);
        return "/system/errorLog/index";
    }
    /**
     * 实时日志
     */
    @GetMapping("/log")
    @RequiresPermissions("system:errorLog:log")
    @NBAuth(value = "system:errorLog:log", remark = "实时日志", type = OTHER, group = ROUTER)
    public String log() {

        return "/system/errorLog/log";
    }

    /**
     * 跳转到添加页面
     */
    @GetMapping("/add")
    @RequiresPermissions("system:errorLog:add")
    @NBAuth(value = "system:errorLog:add", remark = "errorlog添加页面", type = NAV_LINK, group = ROUTER)
    public String toAdd() {
        return "/system/errorLog/add";
    }

    /**
     * 跳转到编辑页面
     */
    @GetMapping("/edit/{id}")
    @RequiresPermissions("system:errorLog:edit")
    @NBAuth(value = "system:errorLog:edit", remark = "errorlog编辑页面", type = NAV_LINK, group = ROUTER)
    public String toEdit(@PathVariable("id") ErrorLog errorLog, Model model) {
        model.addAttribute("errorLog", errorLog);
        return "/system/errorLog/add";
    }

    /**
     * 保存添加/修改的数据
     * @param valid 验证对象
     */
    @PostMapping("/save")
    @RequiresPermissions({"system:errorLog:add", "system:errorLog:edit"})
    @ResponseBody
    public ResultVo save(@Validated ErrorLogValid valid, ErrorLog errorLog) {
        // 复制保留无需修改的数据
        if (errorLog.getId() != null) {
            ErrorLog beErrorLog = errorLogService.getById(errorLog.getId());
            EntityBeanUtil.copyProperties(beErrorLog, errorLog);
        }

        // 保存数据
        errorLogService.save(errorLog);
        return ResultVoUtil.SAVE_SUCCESS;
    }

    /**
     * 跳转到详细页面
     */
    @GetMapping("/detail/{id}")
    @RequiresPermissions("system:errorLog:detail")
    @NBAuth(value = "system:errorLog:detail", remark = "errorlog详细页面", type = NAV_LINK, group = ROUTER)
    public String toDetail(@PathVariable("id") ErrorLog errorLog, Model model) {
        model.addAttribute("errorLog",errorLog);
        return "/system/errorLog/detail";
    }

    /**
     * 设置一条或者多条数据的状态
     */
    @RequestMapping("/status/{param}")
    @RequiresPermissions("system:errorLog:status")
    @ResponseBody
    @NBAuth(value = "system:errorLog:status", remark = "errorlog数据状态", type = NAV_LINK, group = ROUTER)
    public ResultVo status(
            @PathVariable("param") String param,
            @RequestParam(value = "ids", required = false) List<Long> ids) {
        // 更新状态
        StatusEnum statusEnum = StatusUtil.getStatusEnum(param);
        if (errorLogService.updateStatus(statusEnum, ids)) {
            return ResultVoUtil.success(statusEnum.getMessage() + "成功");
        } else {
            return ResultVoUtil.error(statusEnum.getMessage() + "失败，请重新操作");
        }
    }
}
