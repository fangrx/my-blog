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
     * 跳转到列表页面
     */
    @GetMapping("/index")
    @RequiresPermissions("system:menu:index")
    @NBAuth(value = "system:menu:index", remark = "后台菜单列表页面", type = OTHER, group = PAGE)
    @ActionLog(name = "菜单管理", message = "进入后台菜单列表页面")
    public String index(Model model) {
        String search = HttpServletUtil.getRequest().getQueryString();
        model.addAttribute("search", search+"&type=1");
        return "/system/menu/index";
    }
    /**
     * 跳转到列表页面
     */
    @GetMapping("/index2")
    @RequiresPermissions("system:menu:index2")
    @NBAuth(value = "system:menu:index2", remark = "前台菜单列表页面", type = OTHER, group = PAGE)
    @ActionLog(name = "菜单管理", message = "进入前台菜单列表页面")
    public String index2(Model model) {
        String search = HttpServletUtil.getRequest().getQueryString();
        model.addAttribute("search", search+"&type=2");
        return "/system/menu/index2";
    }
    /**
     * 菜单数据列表
     */
    @GetMapping("/list")
    @RequiresPermissions("system:menu:index")
    @ResponseBody
    public ResultVo list(Menu menu) {
        // 创建匹配器，进行动态查询匹配

        ExampleMatcher matcher = ExampleMatcher.matching().
                withMatcher("title", match -> match.contains());

        // 获取菜单列表
        Example<Menu> example = Example.of(menu, matcher);
        Sort sort = new Sort(Sort.Direction.ASC, "sort");
        List<Menu> list = menuService.getListByExample(example, sort);

        // TODO: 2019/2/25 菜单类型处理方案
        list.forEach(editMenu -> {
            String type = String.valueOf(editMenu.getType());
            editMenu.setRemark(DictUtil.keyValue("MENU_TYPE", type));
        });
        return ResultVoUtil.success(list);
    }

    /**
     * 获取排序菜单列表
     */
    @GetMapping("/sortList/{pid}/{notId}/{type}")
    @RequiresPermissions({"system:menu:add", "system:menu:edit"})
    @ResponseBody
    public Map<Integer, String> sortList(
            @PathVariable(value = "pid", required = false) Long pid,
            @PathVariable(value = "notId", required = false) Long notId,
            @PathVariable(value = "type", required = false) Byte type){
        // 本级排序菜单列表
        notId = notId != null ? notId : (long) 0;

        List<Menu> levelMenu = menuService.getListByPid(pid, notId,type);
        Map<Integer, String> sortMap = new TreeMap<>();
        for (int i = 1; i <= levelMenu.size(); i++) {
            sortMap.put(i, levelMenu.get(i - 1).getTitle());
        }
        return sortMap;
    }

    /**
     * 跳转到添加页面
     */
    @GetMapping({"/add", "/add/{pid}"})
    @RequiresPermissions("system:menu:add")
    @NBAuth(value = "system:menu:index", remark = "菜单添加页面", type = NAV_LINK, group = ROUTER)
    public String toAdd(@PathVariable(value = "pid", required = false) Menu pMenu, Model model) {
        model.addAttribute("pMenu", pMenu);
        // 赋予角色和资源授权
        Set<Role> roles = ShiroUtil.getSubjectRoles();

        Set<NBSysResource> menus = new HashSet<>();
        roles.forEach(role -> {
            menus.addAll(role.getMenus());
        });
        model.addAttribute("resources", menus);
        return "/system/menu/add";
    }

    /**
     * 跳转到编辑页面
     */
    @GetMapping("/edit/{id}")
    @RequiresPermissions("system:menu:edit")
    @NBAuth(value = "system:menu:edit", remark = "菜单编辑页面", type = NAV_LINK, group = PAGE)
    @ActionLog(name = "菜单管理", message = "打开${title}编辑页面")
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
     * 保存添加/修改的数据
     * @param valid 验证对象
     * @param menu 实体对象
     */
    @PostMapping("/save")
    @RequiresPermissions({"system:menu:add", "system:menu:edit"})
    @ResponseBody
    @ActionLog(name = "菜单管理", message = "菜单：${title}", action = SaveAction.class)
    public ResultVo save(@Validated MenuValid valid, @EntityParam Menu menu) {
        if (menu.getId() == null) {
            // 排序为空时，添加到最后
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
        // 添加/更新全部上级序号
        Menu pMenu = menuService.getById(menu.getPid());
        menu.setPids(pMenu.getPids() + ",[" + menu.getPid() + "]");

        // 复制保留无需修改的数据
        if (menu.getId() != null) {
            Menu beMenu = menuService.getById(menu.getId());
            EntityBeanUtil.copyProperties(beMenu, menu);
        }

        // 排序功能
        Integer sort = menu.getSort();
        Long notId = menu.getId() != null ? menu.getId() : 0;
        List<Menu> levelMenu = menuService.getListByPid(menu.getPid(), notId,menu.getType());
        levelMenu.add(sort, menu);
        for (int i = 1; i <= levelMenu.size(); i++) {
            levelMenu.get(i - 1).setSort(i);
        }

        // 保存数据
        menuService.save(levelMenu);
        return ResultVoUtil.SAVE_SUCCESS;
    }

    /**
     * 跳转到详细页面
     */
    @GetMapping("/detail/{id}")
    @RequiresPermissions("system:menu:detail")
    @NBAuth(value = "system:menu:detail", remark = "菜单详细页面", type = NAV_LINK, group = ROUTER)
    @ActionLog(name = "菜单管理", message = "打开${title}详细页面")
    public String toDetail(@PathVariable("id") Menu menu, Model model) {
        model.addAttribute("menu", menu);
        return "/system/menu/detail";
    }

    /**
     * 设置一条或者多条数据的状态
     */
    @RequestMapping("/status/{param}")
    @RequiresPermissions("system:menu:status")
    @ResponseBody
    @ActionLog(name = "菜单状态", action = StatusAction.class)
    @NBAuth(value = "system:menu:detail", remark = "菜单数据状态", type = NAV_LINK, group = ROUTER)
    public ResultVo status(
            @PathVariable("param") String param,
            @RequestParam(value = "ids", required = false) List<Long> ids) {
        // 更新状态
        StatusEnum statusEnum = StatusUtil.getStatusEnum(param);
        if (menuService.updateStatus(statusEnum, ids)) {
            return ResultVoUtil.success(statusEnum.getMessage() + "成功");
        } else {
            return ResultVoUtil.error(statusEnum.getMessage() + "失败，请重新操作");
        }
    }


}
