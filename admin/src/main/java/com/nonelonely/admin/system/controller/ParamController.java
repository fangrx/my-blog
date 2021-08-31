package com.nonelonely.admin.system.controller;

import com.nonelonely.admin.system.validator.ParamValid;
import com.nonelonely.common.enums.StatusEnum;
import com.nonelonely.common.utils.EntityBeanUtil;
import com.nonelonely.common.utils.HttpServletUtil;
import com.nonelonely.common.utils.ResultVoUtil;
import com.nonelonely.common.utils.StatusUtil;
import com.nonelonely.common.vo.ResultVo;
import com.nonelonely.component.actionLog.annotation.ActionLog;
import com.nonelonely.component.thymeleaf.utility.DictUtil;
import com.nonelonely.component.thymeleaf.utility.ParamUtil;
import com.nonelonely.modules.system.domain.Menu;
import com.nonelonely.modules.system.domain.Param;
import com.nonelonely.modules.system.domain.permission.NBAuth;
import com.nonelonely.modules.system.repository.ParamRepository;
import com.nonelonely.modules.system.service.ParamService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static com.nonelonely.modules.system.domain.permission.NBAuth.Group.PAGE;
import static com.nonelonely.modules.system.domain.permission.NBAuth.Group.ROUTER;
import static com.nonelonely.modules.system.domain.permission.NBSysResource.ResType.NAV_LINK;
import static com.nonelonely.modules.system.domain.permission.NBSysResource.ResType.OTHER;

/**
 * @author nonelonely
 * @date 2020/01/02
 */
@Controller
@RequestMapping("/system/param")
public class ParamController {

    @Autowired
    private ParamService paramService;
    @Autowired
    private ParamRepository paramRepository;

    /**
     * 列表页面
     */
    @GetMapping("/index")
    @RequiresPermissions("system:param:index")
    @NBAuth(value = "system:param:index", remark = "系统参数列表页面", type = NAV_LINK, group = PAGE)
    @ActionLog(name = "系统参数", message = "系统参数列表页面")
    public String index(Model model) {
        String search = HttpServletUtil.getRequest().getQueryString();
        model.addAttribute("search", search);
        return "/system/param/index";
    }

    /**
     * 跳转到添加页面
     */
    @GetMapping("/add")
    @RequiresPermissions("system:param:add")
    @NBAuth(value = "system:param:add", remark = "系统参数添加页面", type = NAV_LINK, group = PAGE)
    @ActionLog(name = "系统参数", message = "系统参数添加页面")
    public String toAdd() {
        return "/system/param/add";
    }

    /**
     * 跳转到编辑页面
     */
    @GetMapping("/edit/{id}")
    @RequiresPermissions("system:param:edit")
    @NBAuth(value = "system:param:edit", remark = "系统参数编辑页面", type = NAV_LINK, group = ROUTER)
    @ActionLog(name = "系统参数", message = "系统参数编辑页面")
    public String toEdit(@PathVariable("id") Param param, Model model) {
        Param pMenu = paramService.getById(param.getPid());
        if (pMenu == null ){
            pMenu = Param.builder().name("顶级").id(0L).build();
        }
        model.addAttribute("param2", param);
        model.addAttribute("pMenu", pMenu);
        return "/system/param/add";
    }

    /**
     * 保存添加/修改的数据
     * @param valid 验证对象
     */
    @PostMapping("/save")
    @RequiresPermissions({"system:param:add", "system:param:edit"})
    @ResponseBody
    public ResultVo save(@Validated ParamValid valid, Param param) {
        // 复制保留无需修改的数据
        if (param.getId() != null) {
            Param beParam = paramService.getById(param.getId());
            EntityBeanUtil.copyProperties(beParam, param);
            paramService.save(param);
        }else{
            Param beParam = paramService.getByName(param.getName());
            if (beParam !=null){
                throw new RuntimeException("系统参数名称已经存在！");
            }else {
                // 保存数据
                paramService.save(param);
            }
        }


        if(param.getId() != null){
            ParamUtil.clearCache(param.getName());
        }
        return ResultVoUtil.SAVE_SUCCESS;
    }

    /**
     * 跳转到详细页面
     */
    @GetMapping("/detail/{id}")
    @RequiresPermissions("system:param:detail")
    @NBAuth(value = "system:param:detail", remark = "系统参数详细页面", type = NAV_LINK, group = ROUTER)
    @ActionLog(name = "系统参数", message = "详细页面:${name}")
    public String toDetail(@PathVariable("id") Param param, Model model) {
        model.addAttribute("params",param);

        return "/system/param/detail";
    }

    /**
     * 设置一条或者多条数据的状态
     */
    @RequestMapping("/status/{param}")
    @RequiresPermissions("system:param:status")
    @ResponseBody
    @NBAuth(value = "system:param:status", remark = "系统参数状态", type = NAV_LINK, group = ROUTER)
    public ResultVo status(
            @PathVariable("param") String param,
            @RequestParam(value = "ids", required = false) List<Long> ids) {
        // 更新状态
        StatusEnum statusEnum = StatusUtil.getStatusEnum(param);
        if (paramService.updateStatus(statusEnum, ids)) {
            return ResultVoUtil.success(statusEnum.getMessage() + "成功");
        } else {
            return ResultVoUtil.error(statusEnum.getMessage() + "失败，请重新操作");
        }
    }

    /**
     * 菜单数据列表
     */
    @GetMapping("/list")
    @RequiresPermissions("system:param:index")
    @ResponseBody
    public ResultVo list(Param param) {
        // 创建匹配器，进行动态查询匹配
        ExampleMatcher matcher = ExampleMatcher.matching().
                withMatcher("title", match -> match.contains());

        // 获取菜单列表
        Example<Param> example = Example.of(param, matcher);

        List<Param> list = paramRepository.findAll(example);

//        // TODO: 2019/2/25 菜单类型处理方案
//        list.forEach(editMenu -> {
//            String type = String.valueOf(editMenu.getDataType());
//            editMenu.setRemark(DictUtil.keyValue("MENU_TYPE", type));
//        });
        return ResultVoUtil.success(list);
    }
    /**
     * 菜单数据列表
     */
    @GetMapping("/listTree")
    @RequiresPermissions("system:param:index")
    @ResponseBody
    public ResultVo listTree(Param param) {
        // 创建匹配器，进行动态查询匹配
        ExampleMatcher matcher = ExampleMatcher.matching().
                withMatcher("title", match -> match.contains());

        // 获取菜单列表
        Example<Param> example = Example.of(param, matcher);

        List<Param> list = paramRepository.findAll(example);

        // TODO: 2019/2/25 菜单类型处理方案

        return ResultVoUtil.success(list);
    }
}
