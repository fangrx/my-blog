package com.nonelonely.admin.system.controller;

import com.nonelonely.admin.system.validator.TagValid;
import com.nonelonely.common.enums.StatusEnum;
import com.nonelonely.common.utils.EntityBeanUtil;
import com.nonelonely.common.utils.ResultVoUtil;
import com.nonelonely.common.utils.StatusUtil;
import com.nonelonely.common.vo.ResultVo;
import com.nonelonely.modules.system.domain.Tag;
import com.nonelonely.modules.system.domain.permission.NBAuth;
import com.nonelonely.modules.system.repository.TagRepository;
import com.nonelonely.modules.system.service.TagService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static com.nonelonely.modules.system.domain.permission.NBAuth.Group.ROUTER;
import static com.nonelonely.modules.system.domain.permission.NBSysResource.ResType.NAV_LINK;
import static com.nonelonely.modules.system.domain.permission.NBSysResource.ResType.OTHER;

/**
 * @author nonelonely
 * @date 2020/01/01
 */
@Controller
@RequestMapping("/system/tag")
public class TagController {

    @Autowired
    private TagService tagService;

    /**
     * 列表页面
     */
    @GetMapping("/index")
    @RequiresPermissions("system:tag:index")
    @NBAuth(value = "system:tag:index", remark = "标签列表页面", type = OTHER, group = ROUTER)

    public String index(Model model, Tag tag) {

        // 创建匹配器，进行动态查询匹配
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withMatcher("name", match -> match.contains());

        // 获取数据列表
        Example<Tag> example = Example.of(tag, matcher);
        Page<Tag> list = tagService.getPageList(example);

        // 封装数据
        model.addAttribute("list", list.getContent());
        model.addAttribute("page", list);
        return "/system/tag/index";
    }

    /**
     * 跳转到添加页面
     */
    @GetMapping("/add")
    @RequiresPermissions("system:tag:add")
    @NBAuth(value = "system:tag:add", remark = "标签添加页面", type = NAV_LINK, group = ROUTER)

    public String toAdd() {
        return "/system/tag/add";
    }

    /**
     * 跳转到编辑页面
     */
    @GetMapping("/edit/{id}")
    @RequiresPermissions("system:tag:edit")
    @NBAuth(value = "system:tag:edit", remark = "标签编辑页面", type = NAV_LINK, group = ROUTER)

    public String toEdit(@PathVariable("id") Tag tag, Model model) {
        model.addAttribute("tag", tag);
        return "/system/tag/add";
    }

    /**
     * 保存添加/修改的数据
     * @param valid 验证对象
     */
    @PostMapping("/save")
    @RequiresPermissions({"system:tag:add", "system:tag:edit"})
    @ResponseBody
    public ResultVo save(@Validated TagValid valid, Tag tag) {
        // 复制保留无需修改的数据
        if (tag.getId() != null) {
            Tag beTag = tagService.getById(tag.getId());
            EntityBeanUtil.copyProperties(beTag, tag);
        }

        // 保存数据
        tagService.save(tag);
        return ResultVoUtil.SAVE_SUCCESS;
    }

    /**
     * 跳转到详细页面
     */
    @GetMapping("/detail/{id}")
    @RequiresPermissions("system:tag:detail")
    @NBAuth(value = "system:tag:detail", remark = "标签详细页面", type = NAV_LINK, group = ROUTER)

    public String toDetail(@PathVariable("id") Tag tag, Model model) {
        model.addAttribute("tag",tag);
        return "/system/tag/detail";
    }

    /**
     * 设置一条或者多条数据的状态
     */
    @RequestMapping("/status/{param}")
    @RequiresPermissions("system:tag:status")
    @ResponseBody
    @NBAuth(value = "system:tag:status", remark = "标签数据状态", type = NAV_LINK, group = ROUTER)
    public ResultVo status(
            @PathVariable("param") String param,
            @RequestParam(value = "ids", required = false) List<Long> ids) {
        // 更新状态
        StatusEnum statusEnum = StatusUtil.getStatusEnum(param);
        if (tagService.updateStatus(statusEnum, ids)) {
            return ResultVoUtil.success(statusEnum.getMessage() + "成功");
        } else {
            return ResultVoUtil.error(statusEnum.getMessage() + "失败，请重新操作");
        }
    }

    @RequestMapping("/article/tags")
    @ResponseBody
    public ResultVo editPageArticleTags(String id) {
        if (StringUtils.isEmpty(id)) {
            return ResultVoUtil.custom(0,"文章的ID不能为空",new ArrayList<>());
        } else {
            return ResultVoUtil.custom(0, "",tagService.findSelectedTagsByReferId(Long.valueOf(id),"1"));
        }
    }
}
