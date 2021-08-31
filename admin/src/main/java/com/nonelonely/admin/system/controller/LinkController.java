package com.nonelonely.admin.system.controller;

import com.nonelonely.admin.system.validator.LinkValid;
import com.nonelonely.common.enums.StatusEnum;
import com.nonelonely.common.utils.EntityBeanUtil;
import com.nonelonely.common.utils.ResultVoUtil;
import com.nonelonely.common.utils.StatusUtil;
import com.nonelonely.common.vo.ResultVo;
import com.nonelonely.modules.system.domain.Link;
import com.nonelonely.modules.system.domain.permission.NBAuth;
import com.nonelonely.modules.system.service.LinkService;
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
 * @date 2020/02/14
 */
@Controller
@RequestMapping("/system/link")
public class LinkController {

    @Autowired
    private LinkService linkService;

    /**
     * 列表页面
     */
    @GetMapping("/index")
    @RequiresPermissions("system:link:index")
    @NBAuth(value = "system:link:index", remark = "友链列表页面", type = OTHER, group = ROUTER)
    public String index(Model model, Link link) {

        // 创建匹配器，进行动态查询匹配
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withMatcher("title", match -> match.contains())
                .withMatcher("url", match -> match.contains());

        // 获取数据列表
        Example<Link> example = Example.of(link, matcher);
        Page<Link> list = linkService.getPageList(example);

        // 封装数据
        model.addAttribute("list", list.getContent());
        model.addAttribute("page", list);
        return "/system/link/index";
    }

    /**
     * 跳转到添加页面
     */
    @GetMapping("/add")
    @RequiresPermissions("system:link:add")
    @NBAuth(value = "system:link:add", remark = "友链添加页面", type = NAV_LINK, group = ROUTER)
    public String toAdd() {
        return "/system/link/add";
    }

    /**
     * 跳转到编辑页面
     */
    @GetMapping("/edit/{id}")
    @RequiresPermissions("system:link:edit")
    @NBAuth(value = "system:link:edit", remark = "友链编辑页面", type = NAV_LINK, group = ROUTER)
    public String toEdit(@PathVariable("id") Link link, Model model) {
        model.addAttribute("link", link);
        return "/system/link/add";
    }

    /**
     * 保存添加/修改的数据
     * @param valid 验证对象
     */
    @PostMapping("/save")
    @RequiresPermissions({"system:link:add", "system:link:edit"})
    @ResponseBody
    public ResultVo save(@Validated LinkValid valid, Link link) {
        // 复制保留无需修改的数据
        if (link.getId() != null) {
            Link beLink = linkService.getById(link.getId());
            EntityBeanUtil.copyProperties(beLink, link);
        }

        // 保存数据
        linkService.save(link);
        return ResultVoUtil.SAVE_SUCCESS;
    }

    /**
     * 跳转到详细页面
     */
    @GetMapping("/detail/{id}")
    @RequiresPermissions("system:link:detail")
    @NBAuth(value = "system:link:detail", remark = "友链详细页面", type = NAV_LINK, group = ROUTER)
    public String toDetail(@PathVariable("id") Link link, Model model) {
        model.addAttribute("link",link);
        return "/system/link/detail";
    }

    /**
     * 设置一条或者多条数据的状态
     */
    @RequestMapping("/status/{param}")
    @RequiresPermissions("system:link:status")
    @ResponseBody
    @NBAuth(value = "system:link:status", remark = "友链数据状态", type = NAV_LINK, group = ROUTER)
    public ResultVo status(
            @PathVariable("param") String param,
            @RequestParam(value = "ids", required = false) List<Long> ids) {
        // 更新状态
        StatusEnum statusEnum = StatusUtil.getStatusEnum(param);
        if (linkService.updateStatus(statusEnum, ids)) {
            return ResultVoUtil.success(statusEnum.getMessage() + "成功");
        } else {
            return ResultVoUtil.error(statusEnum.getMessage() + "失败，请重新操作");
        }
    }
}
