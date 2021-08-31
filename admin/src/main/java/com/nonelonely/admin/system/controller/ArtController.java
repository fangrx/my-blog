package com.nonelonely.admin.system.controller;

import com.nonelonely.admin.system.validator.ArtValid;
import com.nonelonely.common.enums.StatusEnum;
import com.nonelonely.common.utils.EntityBeanUtil;
import com.nonelonely.common.utils.ResultVoUtil;
import com.nonelonely.common.utils.StatusUtil;
import com.nonelonely.common.vo.ResultVo;
import com.nonelonely.component.actionLog.action.SaveAction;
import com.nonelonely.component.actionLog.annotation.ActionLog;
import com.nonelonely.component.actionLog.annotation.EntityParam;
import com.nonelonely.modules.system.domain.Art;
import com.nonelonely.modules.system.domain.permission.NBAuth;
import com.nonelonely.modules.system.service.ArtService;
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
 * @date 2020/02/14
 */
@Controller
@RequestMapping("/system/art")
public class ArtController {

    @Autowired
    private ArtService artService;

    /**
     * 列表页面
     */
    @GetMapping("/index")
    @RequiresPermissions("system:art:index")
    @NBAuth(value = "system:art:index", remark = "技能列表页面", type = OTHER, group = PAGE)
    @ActionLog(name = "技能管理", message = "进入技能列表页面")
    public String index(Model model, Art art) {

        // 创建匹配器，进行动态查询匹配
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withMatcher("title", match -> match.contains());

        // 获取数据列表
        Example<Art> example = Example.of(art, matcher);
        Page<Art> list = artService.getPageList(example);

        // 封装数据
        model.addAttribute("list", list.getContent());
        model.addAttribute("page", list);
        return "/system/art/index";
    }

    /**
     * 跳转到添加页面
     */
    @GetMapping("/add")
    @RequiresPermissions("system:art:add")
    @NBAuth(value = "system:art:add", remark = "技能添加页面", type = NAV_LINK, group = ROUTER)
    @ActionLog(name = "技能管理", message = "进入技能添加页面")
    public String toAdd() {
        return "/system/art/add";
    }

    /**
     * 跳转到编辑页面
     */
    @GetMapping("/edit/{id}")
    @RequiresPermissions("system:art:edit")
    @NBAuth(value = "system:art:edit", remark = "技能编辑页面", type = NAV_LINK, group = ROUTER)
    @ActionLog(name = "技能管理", message = "技能编辑页面")
    public String toEdit(@PathVariable("id") Art art, Model model) {
        model.addAttribute("art", art);
        return "/system/art/add";
    }

    /**
     * 保存添加/修改的数据
     * @param valid 验证对象
     */
    @PostMapping("/save")
    @RequiresPermissions({"system:art:add", "system:art:edit"})
    @ResponseBody
    @ActionLog(name = "技能管理", message = "技能:${title}",action = SaveAction.class)
    public ResultVo save(@Validated ArtValid valid, @EntityParam Art art) {
        // 复制保留无需修改的数据
        if (art.getId() != null) {
            Art beArt = artService.getById(art.getId());
            EntityBeanUtil.copyProperties(beArt, art);
        }

        // 保存数据
        artService.save(art);
        return ResultVoUtil.SAVE_SUCCESS;
    }

    /**
     * 跳转到详细页面
     */
    @GetMapping("/detail/{id}")
    @RequiresPermissions("system:art:detail")
    @ActionLog(name = "技能管理", message = "技能详细页面:${title}")
    @NBAuth(value = "system:art:detail", remark = "技能详细页面", type = NAV_LINK, group = ROUTER)
    public String toDetail(@PathVariable("id") Art art, Model model) {
        model.addAttribute("art",art);
        return "/system/art/detail";
    }

    /**
     * 设置一条或者多条数据的状态
     */
    @RequestMapping("/status/{param}")
    @RequiresPermissions("system:art:status")
    @ResponseBody
    @NBAuth(value = "system:art:status", remark = "技能数据状态", type = NAV_LINK, group = ROUTER)
    public ResultVo status(
            @PathVariable("param") String param,
            @RequestParam(value = "ids", required = false) List<Long> ids) {
        // 更新状态
        StatusEnum statusEnum = StatusUtil.getStatusEnum(param);
        if (artService.updateStatus(statusEnum, ids)) {
            return ResultVoUtil.success(statusEnum.getMessage() + "成功");
        } else {
            return ResultVoUtil.error(statusEnum.getMessage() + "失败，请重新操作");
        }
    }
}
