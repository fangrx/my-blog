package com.nonelonely.admin.system.controller;

import com.nonelonely.admin.system.validator.CommentValid;
import com.nonelonely.common.enums.StatusEnum;
import com.nonelonely.common.utils.EntityBeanUtil;
import com.nonelonely.common.utils.ResultVoUtil;
import com.nonelonely.common.utils.StatusUtil;
import com.nonelonely.common.vo.ResultVo;
import com.nonelonely.modules.system.domain.Comment;
import com.nonelonely.modules.system.domain.permission.NBAuth;
import com.nonelonely.modules.system.service.CommentService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.nonelonely.modules.system.domain.permission.NBAuth.Group.ROUTER;
import static com.nonelonely.modules.system.domain.permission.NBSysResource.ResType.NAV_LINK;
import static com.nonelonely.modules.system.domain.permission.NBSysResource.ResType.OTHER;

/**
 * @author nonelonely
 * @date 2020/01/02
 */
@Controller
@RequestMapping("/system/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    /**
     * 列表页面
     */
    @GetMapping("/index")
    @RequiresPermissions("system:comment:index")
    @NBAuth(value = "system:comment:detail", remark = "评论列表页面", type = OTHER, group = ROUTER)
    public String index(Model model, Comment comment) {

        // 创建匹配器，进行动态查询匹配
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withMatcher("comment", match -> match.contains());

        // 获取数据列表
        Example<Comment> example = Example.of(comment, matcher);
        Page<Comment> list = commentService.getPageList(example);

        // 封装数据
        model.addAttribute("list", list.getContent());
        model.addAttribute("page", list);
        return "/system/comment/index";
    }

    /**
     * 跳转到详细页面
     */
    @GetMapping("/detail/{id}")
    @RequiresPermissions("system:comment:detail")
    @NBAuth(value = "system:comment:detail", remark = "评论详细页面", type = NAV_LINK, group = ROUTER)
    public String toDetail(@PathVariable("id") Comment comment, Model model) {
        model.addAttribute("comment",comment);
        return "/system/comment/detail";
    }

    /**
     * 设置一条或者多条数据的状态
     */
    @RequestMapping("/status/{param}")
    @RequiresPermissions("system:comment:status")
    @ResponseBody
    @NBAuth(value = "system:comment:status", remark = "评论数据状态", type = NAV_LINK, group = ROUTER)
    public ResultVo status(
            @PathVariable("param") String param,
            @RequestParam(value = "ids", required = false) List<Long> ids) {
        // 更新状态
        StatusEnum statusEnum = StatusUtil.getStatusEnum(param);
        if (commentService.updateStatus(statusEnum, ids)) {
            return ResultVoUtil.success(statusEnum.getMessage() + "成功");
        } else {
            return ResultVoUtil.error(statusEnum.getMessage() + "失败，请重新操作");
        }
    }
}
