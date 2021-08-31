package com.nonelonely.admin.system.controller;

import com.nonelonely.admin.system.validator.AboutValid;
import com.nonelonely.common.enums.StatusEnum;
import com.nonelonely.common.utils.EntityBeanUtil;
import com.nonelonely.common.utils.ResultVoUtil;
import com.nonelonely.common.utils.StatusUtil;
import com.nonelonely.common.vo.ResultVo;
import com.nonelonely.component.actionLog.annotation.ActionLog;
import com.nonelonely.modules.system.domain.About;
import com.nonelonely.modules.system.domain.permission.NBAuth;
import com.nonelonely.modules.system.service.AboutService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.nonelonely.modules.system.domain.permission.NBAuth.Group.PAGE;
import static com.nonelonely.modules.system.domain.permission.NBAuth.Group.ROUTER;
import static com.nonelonely.modules.system.domain.permission.NBSysResource.ResType.NAV_LINK;
import static com.nonelonely.modules.system.domain.permission.NBSysResource.ResType.OTHER;

import java.util.List;

/**
 * @author nonelonely
 * @date 2020/02/13
 */
@Controller
@RequestMapping("/system/about")
public class AboutController {

    @Autowired
    private AboutService aboutService;

    /**
     * 列表页面
     */
    @GetMapping("/index")
    @RequiresPermissions("system:about:index")
    @NBAuth(value = "system:about:index", remark = "关于列表页面", type = OTHER, group = PAGE)
    @ActionLog(name = "关于管理", message = "进入关于列表页面")
    public String index(Model model, About about) {

        // 创建匹配器，进行动态查询匹配
        ExampleMatcher matcher = ExampleMatcher.matching();

        // 获取数据列表
        Example<About> example = Example.of(about, matcher);
        Page<About> list = aboutService.getPageList(example);

        // 封装数据
        model.addAttribute("list", list.getContent());
        model.addAttribute("page", list);
        return "/system/about/index";
    }

    /**
     * 跳转到添加页面
     */
    @GetMapping("/add")
    @RequiresPermissions("system:about:add")
    @NBAuth(value = "system:about:add", remark = "关于添加页面", type = NAV_LINK, group = PAGE)
    @ActionLog(name = "关于管理", message = "进入添加页面")
    public String toAdd() {
        return "/system/about/add";
    }

    /**
     * 跳转到编辑页面
     */
    @GetMapping("/edit/{id}")
    @RequiresPermissions("system:about:edit")
    @NBAuth(value = "system:about:edit", remark = "关于编辑页面", type = NAV_LINK, group = PAGE)
    @ActionLog(name = "关于管理", message = "进入编辑页面：${id}")
    public String toEdit(@PathVariable("id") About about, Model model) {
        model.addAttribute("about", about);
        return "/system/about/add";
    }

    /**
     * 保存添加/修改的数据
     * @param valid 验证对象
     */
    @PostMapping("/save")
    @RequiresPermissions({"system:about:add", "system:about:edit"})
    @ResponseBody
    public ResultVo save(@Validated AboutValid valid, About about) {
        // 复制保留无需修改的数据
        if (about.getId() != null) {
            About beAbout = aboutService.getById(about.getId());
            EntityBeanUtil.copyProperties(beAbout, about);
        }

        // 保存数据
        aboutService.save(about);
        return ResultVoUtil.SAVE_SUCCESS;
    }

    /**
     * 跳转到详细页面
     */
    @GetMapping("/detail/{id}")
    @RequiresPermissions("system:about:detail")
    @NBAuth(value = "system:about:detail", remark = "关于详细页面", type = NAV_LINK, group = PAGE)
    @ActionLog(name = "关于管理", message = "进入详细页面：${id}")
    public String toDetail(@PathVariable("id") About about, Model model) {
        model.addAttribute("about",about);
        return "/system/about/detail";
    }

    /**
     * 设置一条或者多条数据的状态
     */
    @RequestMapping("/status/{param}")
    @RequiresPermissions("system:about:status")
    @ResponseBody
    @NBAuth(value = "system:about:status", remark = "关于数据状态", type = NAV_LINK, group = ROUTER)
    public ResultVo status(
            @PathVariable("param") String param,
            @RequestParam(value = "ids", required = false) List<Long> ids) {
        // 更新状态
        StatusEnum statusEnum = StatusUtil.getStatusEnum(param);
        if (aboutService.updateStatus(statusEnum, ids)) {
            return ResultVoUtil.success(statusEnum.getMessage() + "成功");
        } else {
            return ResultVoUtil.error(statusEnum.getMessage() + "失败，请重新操作");
        }
    }
}
