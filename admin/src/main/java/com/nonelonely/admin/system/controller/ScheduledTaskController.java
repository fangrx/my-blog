package com.nonelonely.admin.system.controller;

import com.nonelonely.admin.system.validator.ScheduledTaskValid;
import com.nonelonely.common.enums.StatusEnum;
import com.nonelonely.common.utils.EntityBeanUtil;
import com.nonelonely.common.utils.ResultVoUtil;
import com.nonelonely.common.utils.StatusUtil;
import com.nonelonely.common.vo.ResultVo;

import com.nonelonely.component.scheduledTask.config.ScheduledTaskServices;
import com.nonelonely.modules.system.domain.ScheduledTask;
import com.nonelonely.modules.system.domain.permission.NBAuth;
import com.nonelonely.modules.system.service.ScheduledTaskService;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.quartz.TriggerUtils;
import org.quartz.impl.triggers.CronTriggerImpl;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author nonelonely
 * @date 2020/04/15
 */
@Controller
@RequestMapping("/system/scheduledTask")
public class ScheduledTaskController {

    @Autowired
    private ScheduledTaskService scheduledTaskService;
    @Autowired
    private ScheduledTaskServices scheduledTaskServices;
    /**
     * 列表页面
     */
    @GetMapping("/index")
    @RequiresPermissions("system:scheduledTask:index")
    @NBAuth(value = "system:scheduledTask:index", remark = "任务调度列表页面", type = OTHER, group = ROUTER)
    public String index(Model model, ScheduledTask scheduledTask) {

        // 创建匹配器，进行动态查询匹配
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withMatcher("taskKey", match -> match.contains())
                .withMatcher("taskDesc", match -> match.contains());

        // 获取数据列表
        Example<ScheduledTask> example = Example.of(scheduledTask, matcher);
        Page<ScheduledTask> list = scheduledTaskServices.getPageList(example);

        // 封装数据
        model.addAttribute("list", list.getContent());
        model.addAttribute("page", list);
        return "/system/scheduledTask/index";
    }

    /**
     * 跳转到添加页面
     */
    @GetMapping("/add")
    @RequiresPermissions("system:scheduledTask:add")
    @NBAuth(value = "system:scheduledTask:add", remark = "任务调度添加页面", type = NAV_LINK, group = ROUTER)
    public String toAdd() {
        return "/system/scheduledTask/add";
    }

    /**
     * 跳转到编辑页面
     */
    @GetMapping("/edit/{id}")
    @RequiresPermissions("system:scheduledTask:edit")
    @NBAuth(value = "system:scheduledTask:edit", remark = "任务调度编辑页面", type = NAV_LINK, group = ROUTER)
    public String toEdit(@PathVariable("id") ScheduledTask scheduledTask, Model model) {
        model.addAttribute("scheduledTask", scheduledTask);
        return "/system/scheduledTask/add";
    }

    /**
     * 保存添加/修改的数据
     * @param valid 验证对象
     */
    @PostMapping("/save")
    @RequiresPermissions({"system:scheduledTask:add", "system:scheduledTask:edit"})
    @ResponseBody
    public ResultVo save(@Validated ScheduledTaskValid valid, ScheduledTask scheduledTask) {
        // 复制保留无需修改的数据
        if (scheduledTask.getId() != null) {
            ScheduledTask beScheduledTask = scheduledTaskService.getById(scheduledTask.getId());
            EntityBeanUtil.copyProperties(beScheduledTask, scheduledTask);
        }

        // 保存数据
        scheduledTaskService.save(scheduledTask);
        return ResultVoUtil.SAVE_SUCCESS;
    }

    /**
     * 跳转到详细页面
     */
    @GetMapping("/detail/{id}")
    @RequiresPermissions("system:scheduledTask:detail")
    @NBAuth(value = "system:scheduledTask:detail", remark = "任务调度详细页面", type = NAV_LINK, group = ROUTER)
    public String toDetail(@PathVariable("id") ScheduledTask scheduledTask, Model model) {
        model.addAttribute("scheduledTask",scheduledTask);
        return "/system/scheduledTask/detail";
    }

    /**
     * 设置一条或者多条数据的状态
     */
    @RequestMapping("/status/{param}")
    @RequiresPermissions("system:scheduledTask:status")
    @ResponseBody
    @NBAuth(value = "system:scheduledTask:status", remark = "任务调度数据状态", type = NAV_LINK, group = ROUTER)
    public ResultVo status(
            @PathVariable("param") String param,
            @RequestParam(value = "ids", required = false) List<Long> ids) {
        // 更新状态
        StatusEnum statusEnum = StatusUtil.getStatusEnum(param);
        if (scheduledTaskService.updateStatus(statusEnum, ids)) {
            return ResultVoUtil.success(statusEnum.getMessage() + "成功");
        } else {
            return ResultVoUtil.error(statusEnum.getMessage() + "失败，请重新操作");
        }
    }

    /**
     * 所有任务列表
     */
    @RequestMapping("/taskList")
    public List<ScheduledTask> taskList() {
        return scheduledTaskServices.taskList();
    }

    /**
     * 根据任务key => 启动任务
     */
    @RequestMapping("/start")
    @ResponseBody
    @RequiresPermissions("system:scheduledTask:start")
    @NBAuth(value = "system:scheduledTask:start", remark = "启动任务", type = NAV_LINK, group = ROUTER)
    public ResultVo start(@RequestParam("taskKey") String taskKey) {

        try {
            scheduledTaskServices.start(taskKey);
            return ResultVoUtil.success("成功");
        }catch (Exception e){

            return ResultVoUtil.error(e.getMessage() + "失败，请重新操作");
        }




    }

    /**
     * 根据任务key => 停止任务
     */
    @RequestMapping("/stop")
    @ResponseBody
    @RequiresPermissions("system:scheduledTask:stop")
    @NBAuth(value = "system:scheduledTask:stop", remark = "停止任务", type = NAV_LINK, group = ROUTER)
    public ResultVo stop(@RequestParam("taskKey") String taskKey) {

        try {
            scheduledTaskServices.stop(taskKey);
            return ResultVoUtil.success("成功");
        }catch (Exception e){

            return ResultVoUtil.error(e.getMessage() + "失败，请重新操作");
        }
    }

    /**
     * 根据任务key => 重启任务
     */
    @RequestMapping("/restart")
    @ResponseBody
    @RequiresPermissions("system:scheduledTask:restart")
    @NBAuth(value = "system:scheduledTask:restart", remark = "重启任务", type = NAV_LINK, group = ROUTER)
    public ResultVo restart(@RequestParam("taskKey") String taskKey) {
        try {
            scheduledTaskServices.restart(taskKey);
            return ResultVoUtil.success("成功");
        }catch (Exception e){

            return ResultVoUtil.error(e.getMessage() + "失败，请重新操作");
        }
    }


    /**
     *
     * @param cron    cron表达式
     * @param numTimes    下一(几)次运行的时间
     * @return
     */
    @RequestMapping("/getNextExecTime")
    @ResponseBody
    public static ResultVo getNextExecTime(String cron,Integer numTimes) {
        List<String> list = new ArrayList<>();
        CronTriggerImpl cronTriggerImpl = new CronTriggerImpl();
        try {
            cronTriggerImpl.setCronExpression(cron);
        } catch(ParseException e) {
            e.printStackTrace();
        }
        // 这个是重点，一行代码搞定
        List<Date> dates = TriggerUtils.computeFireTimes(cronTriggerImpl, null, numTimes);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (Date date : dates) {
            list.add(dateFormat.format(date));
        }
        return ResultVoUtil.success("",list);
    }
}
