package com.nonelonely.admin.system.controller;

import com.nonelonely.admin.system.validator.WordsValid;
import com.nonelonely.common.enums.StatusEnum;
import com.nonelonely.common.utils.EntityBeanUtil;
import com.nonelonely.common.utils.ResultVoUtil;
import com.nonelonely.common.utils.StatusUtil;
import com.nonelonely.common.vo.ResultVo;
import com.nonelonely.modules.system.domain.Words;
import com.nonelonely.modules.system.domain.permission.NBAuth;
import com.nonelonely.modules.system.service.WordsService;
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
 * @date 2020/02/15
 */
@Controller
@RequestMapping("/system/words")
public class WordsController {

    @Autowired
    private WordsService wordsService;

    /**
     * 列表页面
     */
    @GetMapping("/index")
    @RequiresPermissions("system:words:index")
    @NBAuth(value = "system:words:index", remark = "美句列表页面", type = OTHER, group = ROUTER)
    public String index(Model model, Words words) {

        // 创建匹配器，进行动态查询匹配
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withMatcher("title", match -> match.contains());

        // 获取数据列表
        Example<Words> example = Example.of(words, matcher);
        Page<Words> list = wordsService.getPageList(example);

        // 封装数据
        model.addAttribute("list", list.getContent());
        model.addAttribute("page", list);
        return "/system/words/index";
    }

    /**
     * 跳转到添加页面
     */
    @GetMapping("/add")
    @RequiresPermissions("system:words:add")
    @NBAuth(value = "system:words:add", remark = "美句添加页面", type = NAV_LINK, group = ROUTER)
    public String toAdd() {
        return "/system/words/add";
    }

    /**
     * 跳转到编辑页面
     */
    @GetMapping("/edit/{id}")
    @RequiresPermissions("system:words:edit")
    @NBAuth(value = "system:words:edit", remark = "美句编辑页面", type = NAV_LINK, group = ROUTER)
    public String toEdit(@PathVariable("id") Words words, Model model) {
        model.addAttribute("words", words);
        return "/system/words/add";
    }

    /**
     * 保存添加/修改的数据
     * @param valid 验证对象
     */
    @PostMapping("/save")
    @RequiresPermissions({"system:words:add", "system:words:edit"})
    @ResponseBody
    public ResultVo save(@Validated WordsValid valid, Words words) {
        // 复制保留无需修改的数据
        if (words.getId() != null) {
            Words beWords = wordsService.getById(words.getId());
            EntityBeanUtil.copyProperties(beWords, words);
        }

        // 保存数据
        wordsService.save(words);
        return ResultVoUtil.SAVE_SUCCESS;
    }

    /**
     * 跳转到详细页面
     */
    @GetMapping("/detail/{id}")
    @RequiresPermissions("system:words:detail")
    @NBAuth(value = "system:words:detail", remark = "美句详细页面", type = NAV_LINK, group = ROUTER)
    public String toDetail(@PathVariable("id") Words words, Model model) {
        model.addAttribute("words",words);
        return "/system/words/detail";
    }

    /**
     * 设置一条或者多条数据的状态
     */
    @RequestMapping("/status/{param}")
    @RequiresPermissions("system:words:status")
    @ResponseBody
    @NBAuth(value = "system:words:status", remark = "美句数据状态", type = NAV_LINK, group = ROUTER)
    public ResultVo status(
            @PathVariable("param") String param,
            @RequestParam(value = "ids", required = false) List<Long> ids) {
        // 更新状态
        StatusEnum statusEnum = StatusUtil.getStatusEnum(param);
        if (wordsService.updateStatus(statusEnum, ids)) {
            return ResultVoUtil.success(statusEnum.getMessage() + "成功");
        } else {
            return ResultVoUtil.error(statusEnum.getMessage() + "失败，请重新操作");
        }
    }
}
