package com.nonelonely.admin.system.controller;

import com.nonelonely.admin.system.validator.MenuValid;
import com.nonelonely.common.enums.StatusEnum;
import com.nonelonely.common.utils.EntityBeanUtil;
import com.nonelonely.common.utils.HttpServletUtil;
import com.nonelonely.common.utils.ResultVoUtil;
import com.nonelonely.common.utils.StatusUtil;
import com.nonelonely.common.vo.ResultVo;
import com.nonelonely.component.actionLog.action.SaveAction;
import com.nonelonely.component.actionLog.action.StatusAction;
import com.nonelonely.component.actionLog.annotation.ActionLog;
import com.nonelonely.component.actionLog.annotation.EntityParam;
import com.nonelonely.component.shiro.ShiroUtil;
import com.nonelonely.component.thymeleaf.utility.DictUtil;
import com.nonelonely.component.thymeleaf.utility.ParamUtil;
import com.nonelonely.modules.system.domain.Menu;
import com.nonelonely.modules.system.domain.Role;
import com.nonelonely.modules.system.domain.permission.NBAuth;
import com.nonelonely.modules.system.domain.permission.NBSysResource;
import com.nonelonely.modules.system.repository.permission.ResourceRepository;

import com.nonelonely.modules.system.service.MenuService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

import static com.nonelonely.modules.system.domain.permission.NBAuth.Group.PAGE;
import static com.nonelonely.modules.system.domain.permission.NBAuth.Group.ROUTER;
import static com.nonelonely.modules.system.domain.permission.NBSysResource.ResType.NAV_LINK;
import static com.nonelonely.modules.system.domain.permission.NBSysResource.ResType.OTHER;

/**
 * @author nonelonely
 * @date 2018/8/14
 */
@Controller
@RequestMapping("/system/menu")
public class MenuController {

    @Autowired
    private MenuService menuService;
    @Autowired
    private ResourceRepository resourceRepository;

    /**
     * ?????????????????????
     */
    @GetMapping("/index")
    @RequiresPermissions("system:menu:index")
    @NBAuth(value = "system:menu:index", remark = "????????????????????????", type = OTHER, group = PAGE)
    @ActionLog(name = "????????????", message = "??????????????????????????????")
    public String index(Model model) {
        String search = HttpServletUtil.getRequest().getQueryString();
        model.addAttribute("search", search+"&type=1");
        return "/system/menu/index";
    }
    /**
     * ?????????????????????
     */
    @GetMapping("/index2")
    @RequiresPermissions("system:menu:index2")
    @NBAuth(value = "system:menu:index2", remark = "????????????????????????", type = OTHER, group = PAGE)
    @ActionLog(name = "????????????", message = "??????????????????????????????")
    public String index2(Model model) {
        String search = HttpServletUtil.getRequest().getQueryString();
        model.addAttribute("search", search+"&type=2");
        return "/system/menu/index2";
    }
    /**
     * ??????????????????
     */
    @GetMapping("/list")
    @RequiresPermissions("system:menu:index")
    @ResponseBody
    public ResultVo list(Menu menu) {
        // ??????????????????????????????????????????

        ExampleMatcher matcher = ExampleMatcher.matching().
                withMatcher("title", match -> match.contains());

        // ??????????????????
        Example<Menu> example = Example.of(menu, matcher);
        Sort sort = new Sort(Sort.Direction.ASC, "sort");
        List<Menu> list = menuService.getListByExample(example, sort);

        // TODO: 2019/2/25 ????????????????????????
        list.forEach(editMenu -> {
            String type = String.valueOf(editMenu.getType());
            editMenu.setRemark(DictUtil.keyValue("MENU_TYPE", type));
        });
        return ResultVoUtil.success(list);
    }

    /**
     * ????????????????????????
     */
    @GetMapping("/sortList/{pid}/{notId}/{type}")
    @RequiresPermissions({"system:menu:add", "system:menu:edit"})
    @ResponseBody
    public Map<Integer, String> sortList(
            @PathVariable(value = "pid", required = false) Long pid,
            @PathVariable(value = "notId", required = false) Long notId,
            @PathVariable(value = "type", required = false) Byte type){
        // ????????????????????????
        notId = notId != null ? notId : (long) 0;

        List<Menu> levelMenu = menuService.getListByPid(pid, notId,type);
        Map<Integer, String> sortMap = new TreeMap<>();
        for (int i = 1; i <= levelMenu.size(); i++) {
            sortMap.put(i, levelMenu.get(i - 1).getTitle());
        }
        return sortMap;
    }

    /**
     * ?????????????????????
     */
    @GetMapping({"/add", "/add/{pid}"})
    @RequiresPermissions("system:menu:add")
    @NBAuth(value = "system:menu:index", remark = "??????????????????", type = NAV_LINK, group = ROUTER)
    public String toAdd(@PathVariable(value = "pid", required = false) Menu pMenu, Model model) {
        model.addAttribute("pMenu", pMenu);
        // ???????????????????????????
        Set<Role> roles = ShiroUtil.getSubjectRoles();

        Set<NBSysResource> menus = new HashSet<>();
        roles.forEach(role -> {
            menus.addAll(role.getMenus());
        });
        model.addAttribute("resources", menus);
        return "/system/menu/add";
    }

    /**
     * ?????????????????????
     */
    @GetMapping("/edit/{id}")
    @RequiresPermissions("system:menu:edit")
    @NBAuth(value = "system:menu:edit", remark = "??????????????????", type = NAV_LINK, group = PAGE)
    @ActionLog(name = "????????????", message = "??????${title}????????????")
    public String toEdit(@PathVariable("id") Menu menu, Model model) {
        Menu pMenu = menuService.getById(menu.getPid());
        model.addAttribute("menu", menu);
        model.addAttribute("pMenu", pMenu);
        Set<Role> roles = ShiroUtil.getSubject().getRoles();
        Set<NBSysResource> menus = new HashSet<>();
        roles.forEach(role -> {
            menus.addAll(role.getMenus());
        });
        model.addAttribute("resources", menus);
        return "/system/menu/add";
    }

    /**
     * ????????????/???????????????
     * @param valid ????????????
     * @param menu ????????????
     */
    @PostMapping("/save")
    @RequiresPermissions({"system:menu:add", "system:menu:edit"})
    @ResponseBody
    @ActionLog(name = "????????????", message = "?????????${title}", action = SaveAction.class)
    public ResultVo save(@Validated MenuValid valid, @EntityParam Menu menu) {
        if (menu.getId() == null) {
            // ?????????????????????????????????
            if(menu.getSort() == null){
                Integer sortMax = menuService.getSortMax(menu.getPid());
                menu.setSort(sortMax !=null ? sortMax - 1 : 0);
            }
        }
        if (menu.getIcon() == null || "".equals(menu.getIcon().trim())){
            String icon = ParamUtil.value("menu_default_icon") == null ? "fa fa-file-o" : ParamUtil.value("menu_default_icon");
            menu.setIcon(icon);
        }
       if (menu.getUrl() == null || "".equals(menu.getUrl()) ){
           menu.setUrl("#");
       }
        // ??????/????????????????????????
        Menu pMenu = menuService.getById(menu.getPid());
        menu.setPids(pMenu.getPids() + ",[" + menu.getPid() + "]");

        // ?????????????????????????????????
        if (menu.getId() != null) {
            Menu beMenu = menuService.getById(menu.getId());
            EntityBeanUtil.copyProperties(beMenu, menu);
        }

        // ????????????
        Integer sort = menu.getSort();
        Long notId = menu.getId() != null ? menu.getId() : 0;
        List<Menu> levelMenu = menuService.getListByPid(menu.getPid(), notId,menu.getType());
        levelMenu.add(sort, menu);
        for (int i = 1; i <= levelMenu.size(); i++) {
            levelMenu.get(i - 1).setSort(i);
        }

        // ????????????
        menuService.save(levelMenu);
        return ResultVoUtil.SAVE_SUCCESS;
    }

    /**
     * ?????????????????????
     */
    @GetMapping("/detail/{id}")
    @RequiresPermissions("system:menu:detail")
    @NBAuth(value = "system:menu:detail", remark = "??????????????????", type = NAV_LINK, group = ROUTER)
    @ActionLog(name = "????????????", message = "??????${title}????????????")
    public String toDetail(@PathVariable("id") Menu menu, Model model) {
        model.addAttribute("menu", menu);
        return "/system/menu/detail";
    }

    /**
     * ???????????????????????????????????????
     */
    @RequestMapping("/status/{param}")
    @RequiresPermissions("system:menu:status")
    @ResponseBody
    @ActionLog(name = "????????????", action = StatusAction.class)
    @NBAuth(value = "system:menu:detail", remark = "??????????????????", type = NAV_LINK, group = ROUTER)
    public ResultVo status(
            @PathVariable("param") String param,
            @RequestParam(value = "ids", required = false) List<Long> ids) {
        // ????????????
        StatusEnum statusEnum = StatusUtil.getStatusEnum(param);
        if (menuService.updateStatus(statusEnum, ids)) {
            return ResultVoUtil.success(statusEnum.getMessage() + "??????");
        } else {
            return ResultVoUtil.error(statusEnum.getMessage() + "????????????????????????");
        }
    }


}
