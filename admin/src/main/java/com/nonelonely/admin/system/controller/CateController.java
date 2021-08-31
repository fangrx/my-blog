package com.nonelonely.admin.system.controller;

import com.nonelonely.admin.system.validator.CateValid;
import com.nonelonely.common.enums.StatusEnum;
import com.nonelonely.common.utils.EntityBeanUtil;
import com.nonelonely.common.utils.HttpServletUtil;
import com.nonelonely.common.utils.ResultVoUtil;
import com.nonelonely.common.utils.StatusUtil;
import com.nonelonely.common.vo.ResultVo;
import com.nonelonely.modules.system.domain.Cate;
import com.nonelonely.modules.system.domain.Param;
import com.nonelonely.modules.system.domain.permission.NBAuth;
import com.nonelonely.modules.system.repository.CateRepository;
import com.nonelonely.modules.system.service.CateService;
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
 * @date 2020/01/01
 */
@Controller
@RequestMapping("/system/cate")
public class CateController {

    @Autowired
    private CateService cateService;
    @Autowired
    private CateRepository cateRepository;

    /**
     * 列表页面
     */
    @GetMapping("/index")
    @RequiresPermissions("system:cate:index")
    @NBAuth(value = "system:cate:index", remark = "类别列表页面", type = OTHER, group = ROUTER)
    public String index(Model model) {
        String search = HttpServletUtil.getRequest().getQueryString();
        model.addAttribute("search", search);
        return "/system/cate/index";
    }
//    public String index(Model model, Cate cate) {
//
//        // 创建匹配器，进行动态查询匹配
//        ExampleMatcher matcher = ExampleMatcher.matching()
//                .withMatcher("name", match -> match.contains())
//                .withMatcher("cnName", match -> match.contains());
//
//        // 获取数据列表
//        Example<Cate> example = Example.of(cate, matcher);
//        Page<Cate> list = cateService.getPageList(example);
//
//        // 封装数据
//        model.addAttribute("list", list.getContent());
//        model.addAttribute("page", list);
//        return "/system/cate/index";
//    }

    /**
     * 跳转到添加页面
     */
    @GetMapping("/add")
    @RequiresPermissions("system:cate:add")
    @NBAuth(value = "system:cate:add", remark = "类别添加页面", type = NAV_LINK, group = ROUTER)
    public String toAdd() {
        return "/system/cate/add";
    }

    /**
     * 跳转到编辑页面
     */
    @GetMapping("/edit/{id}")
    @RequiresPermissions("system:cate:edit")
    @NBAuth(value = "system:cate:edit", remark = "类别编辑页面", type = NAV_LINK, group = ROUTER)
    public String toEdit(@PathVariable("id") Cate cate, Model model) {
        Cate pMenu = cateService.getById(cate.getPid()== null?0:cate.getPid());
        if (pMenu == null ){
            pMenu = Cate.builder().name("顶级").id(0L).build();

        }
        model.addAttribute("pMenu", pMenu);
        model.addAttribute("cate", cate);
        return "/system/cate/add";
    }

    /**
     * 保存添加/修改的数据
     * @param valid 验证对象
     */
    @PostMapping("/save")
    @RequiresPermissions({"system:cate:add", "system:cate:edit"})
    @ResponseBody
    public ResultVo save(@Validated CateValid valid, Cate cate) {
        // 复制保留无需修改的数据
        if (cate.getId() != null) {
            Cate beCate = cateService.getById(cate.getId());
            EntityBeanUtil.copyProperties(beCate, cate);
        }

        // 保存数据
        cateService.save(cate);
        return ResultVoUtil.SAVE_SUCCESS;
    }

    /**
     * 跳转到详细页面
     */
    @GetMapping("/detail/{id}")
    @RequiresPermissions("system:cate:detail")
    @NBAuth(value = "system:cate:detail", remark = "类别详细页面", type = NAV_LINK, group = ROUTER)
    public String toDetail(@PathVariable("id") Cate cate, Model model) {
        model.addAttribute("cate",cate);
        return "/system/cate/detail";
    }

    /**
     * 设置一条或者多条数据的状态
     */
    @RequestMapping("/status/{param}")
    @RequiresPermissions("system:cate:status")
    @ResponseBody
    @NBAuth(value = "system:cate:status", remark = "类别数据状态", type = NAV_LINK, group = ROUTER)
    public ResultVo status(
            @PathVariable("param") String param,
            @RequestParam(value = "ids", required = false) List<Long> ids) {
        // 更新状态
        StatusEnum statusEnum = StatusUtil.getStatusEnum(param);
        if (cateService.updateStatus(statusEnum, ids)) {
            return ResultVoUtil.success(statusEnum.getMessage() + "成功");
        } else {
            return ResultVoUtil.error(statusEnum.getMessage() + "失败，请重新操作");
        }
    }

    /**
     * 类别数据列表
     */
    @GetMapping("/list")
    @RequiresPermissions("system:cate:index")
    @ResponseBody
    public ResultVo list(Cate cate) {
        // 创建匹配器，进行动态查询匹配
        ExampleMatcher matcher = ExampleMatcher.matching().
                 withMatcher("cnName", match -> match.contains())
                .withMatcher("name", match -> match.contains())
                .withIgnorePaths("isShow");
        // 获取菜单列表
        Example<Cate> example = Example.of(cate, matcher);

        List<Cate> list = cateRepository.findAll(example);
        list.forEach(x-> x.setName(x.getCnName())
        );
        return ResultVoUtil.success(list);
    }
//    /**
//     * 类别数据列表
//     */
//    @GetMapping("/listTree")
//    @RequiresPermissions("system:cate:index")
//    @ResponseBody
//    public ResultVo listTree(Param param) {
//        // 创建匹配器，进行动态查询匹配
//        ExampleMatcher matcher = ExampleMatcher.matching().
//                withMatcher("title", match -> match.contains());
//
//        // 获取菜单列表
//        Example<Param> example = Example.of(param, matcher);
//
//        List<Param> list = paramRepository.findAll(example);
//
//        // TODO: 2019/2/25 菜单类型处理方案
//
//        return ResultVoUtil.success(list);
//    }
}
