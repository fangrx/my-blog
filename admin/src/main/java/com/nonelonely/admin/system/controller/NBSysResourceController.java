package com.nonelonely.admin.system.controller;

import com.nonelonely.admin.system.validator.NBSysResourceValid;
import com.nonelonely.common.enums.StatusEnum;
import com.nonelonely.common.utils.EntityBeanUtil;
import com.nonelonely.common.utils.ResultVoUtil;
import com.nonelonely.common.utils.StatusUtil;
import com.nonelonely.common.vo.ResultVo;
import com.nonelonely.component.actionLog.action.SaveAction;
import com.nonelonely.component.actionLog.annotation.ActionLog;
import com.nonelonely.component.actionLog.annotation.EntityParam;
import com.nonelonely.modules.system.domain.permission.NBAuth;
import com.nonelonely.modules.system.domain.permission.NBSysResource;
import com.nonelonely.modules.system.service.NBSysResourceService;
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
 * @date 2021/02/04
 */
@Controller
@RequestMapping("/system/nBSysResource")
public class NBSysResourceController {

    @Autowired
    private NBSysResourceService nBSysResourceService;

    /**
     * 列表页面
     */
    @GetMapping("/index")
    @RequiresPermissions("system:nBSysResource:index")
    @NBAuth(value = "system:nBSysResource:index", remark = "路径资源列表页面", type = OTHER, group = PAGE)
    public String index(Model model, NBSysResource nBSysResource) {

        // 创建匹配器，进行动态查询匹配
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withMatcher("url", match -> match.contains())
                .withMatcher("name", match -> match.contains())
                .withMatcher("permission", match -> match.contains());

        // 获取数据列表
        Example<NBSysResource> example = Example.of(nBSysResource, matcher);
        Page<NBSysResource> list = nBSysResourceService.getPageList(example);

        // 封装数据
        model.addAttribute("list", list.getContent());
        model.addAttribute("page", list);
        return "/system/nBSysResource/index";
    }

    /**
     * 跳转到添加页面
     */
    @GetMapping("/add")
    @RequiresPermissions("system:nBSysResource:add")
    @NBAuth(value = "system:nBSysResource:add", remark = "路径资源添加页面", type = NAV_LINK, group = PAGE)
    public String toAdd() {
        return "/system/nBSysResource/add";
    }

    /**
     * 跳转到编辑页面
     */
    @GetMapping("/edit/{id}")
    @RequiresPermissions("system:nBSysResource:edit")
    @NBAuth(value = "system:nBSysResource:edit", remark = "路径资源编辑页面", type = NAV_LINK, group = ROUTER)
    public String toEdit(@PathVariable("id") NBSysResource nBSysResource, Model model) {
        model.addAttribute("nBSysResource", nBSysResource);
        return "/system/nBSysResource/add";
    }

    /**
     * 保存添加/修改的数据
     * @param valid 验证对象
     */
    @PostMapping("/save")
    @RequiresPermissions({"system:nBSysResource:add", "system:nBSysResource:edit"})
    @ResponseBody
    @ActionLog(name = "路径资源管理", message = "路径资源：${name}", action = SaveAction.class)
    public ResultVo save(@Validated NBSysResourceValid valid,@EntityParam NBSysResource nBSysResource) {
        // 复制保留无需修改的数据
        if (nBSysResource.getId() != null) {
            NBSysResource beNBSysResource = nBSysResourceService.getById(nBSysResource.getId());
            EntityBeanUtil.copyProperties(beNBSysResource, nBSysResource);
        }

        // 保存数据
        nBSysResourceService.save(nBSysResource);
        return ResultVoUtil.SAVE_SUCCESS;
    }

    /**
     * 跳转到详细页面
     */
    @GetMapping("/detail/{id}")
    @RequiresPermissions("system:nBSysResource:detail")
    @NBAuth(value = "system:nBSysResource:detail", remark = "路径资源详细页面", type = NAV_LINK, group = ROUTER)
    public String toDetail(@PathVariable("id") NBSysResource nBSysResource, Model model) {
        model.addAttribute("nBSysResource",nBSysResource);
        return "/system/nBSysResource/detail";
    }

    /**
     * 设置一条或者多条数据的状态
     */
    @RequestMapping("/status/{param}")
    @RequiresPermissions("system:nBSysResource:status")
    @ResponseBody
    @NBAuth(value = "system:nBSysResource:status", remark = "路径资源数据状态", type = NAV_LINK, group = ROUTER)
    public ResultVo status(
            @PathVariable("param") String param,
            @RequestParam(value = "ids", required = false) List<Long> ids) {
        // 更新状态
        StatusEnum statusEnum = StatusUtil.getStatusEnum(param);
        if (nBSysResourceService.updateStatus(statusEnum, ids)) {
            return ResultVoUtil.success(statusEnum.getMessage() + "成功");
        } else {
            return ResultVoUtil.error(statusEnum.getMessage() + "失败，请重新操作");
        }
    }
}
