package com.nonelonely.admin.system.controller;

import com.nonelonely.admin.system.validator.RoleValid;
import com.nonelonely.common.constant.AdminConst;
import com.nonelonely.common.enums.ResultEnum;
import com.nonelonely.common.enums.StatusEnum;
import com.nonelonely.common.exception.ResultException;
import com.nonelonely.common.utils.EntityBeanUtil;
import com.nonelonely.common.utils.ResultVoUtil;
import com.nonelonely.common.utils.StatusUtil;
import com.nonelonely.common.vo.ResultVo;
import com.nonelonely.component.actionLog.action.RoleAction;
import com.nonelonely.component.actionLog.action.StatusAction;
import com.nonelonely.component.actionLog.annotation.ActionLog;
import com.nonelonely.component.actionLog.annotation.EntityParam;
import com.nonelonely.component.shiro.ShiroUtil;

import com.nonelonely.modules.system.domain.Role;
import com.nonelonely.modules.system.domain.bo.LayuiXTree;
import com.nonelonely.modules.system.domain.permission.NBAuth;
import com.nonelonely.modules.system.domain.permission.NBSysResource;

import com.nonelonely.modules.system.domain.permission.pk.RoleResourceKey;
import com.nonelonely.modules.system.repository.permission.ResourceRepository;

import com.nonelonely.modules.system.service.MenuService;
import com.nonelonely.modules.system.service.RoleService;
import com.nonelonely.modules.system.service.UserService;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import static com.nonelonely.modules.system.domain.permission.NBAuth.Group.ROUTER;
import static com.nonelonely.modules.system.domain.permission.NBSysResource.ResType.NAV_LINK;
import static com.nonelonely.modules.system.domain.permission.NBSysResource.ResType.OTHER;

/**
 * @author nonelonely
 * @date 2018/8/14
 */
@Controller
@RequestMapping("/system/role")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @Autowired
    private MenuService menuService;

    @Autowired
    private ResourceRepository resourceRepository;

    @Autowired
    private UserService userService;

    /**
     * ????????????
     */
    @GetMapping("/index")
    @RequiresPermissions("system:role:index")
    @NBAuth(value = "system:role:index", remark = "??????????????????", type = OTHER, group = ROUTER)
    public String index(Model model, Role role){

        // ??????????????????????????????????????????
        ExampleMatcher matcher = ExampleMatcher.matching().
                withMatcher("title", match -> match.contains());

        // ??????????????????
        Example<Role> example = Example.of(role, matcher);
        Page<Role> list = roleService.getPageList(example);

        // ????????????
        model.addAttribute("list", list.getContent());
        model.addAttribute("page", list);
        return "/system/role/index";
    }

    /**
     * ?????????????????????
     */
    @GetMapping("/add")
    @RequiresPermissions("system:role:add")
    @NBAuth(value = "system:role:add", remark = "??????????????????", type = NAV_LINK, group = ROUTER)
    public String toAdd(){
        return "/system/role/add";
    }

    /**
     * ?????????????????????
     */
    @GetMapping("/edit/{id}")
    @RequiresPermissions("system:role:edit")
    @NBAuth(value = "system:role:edit", remark = "??????????????????", type = NAV_LINK, group = ROUTER)
    public String toEdit(@PathVariable("id") Role role, Model model){
        model.addAttribute("role", role);
        return "/system/role/add";
    }

    /**
     * ????????????/???????????????
     * @param valid ????????????
     * @param role ????????????
     */
    @PostMapping("/save")
    @RequiresPermissions({"system:role:add", "system:role:edit"})
    @ResponseBody
    @ActionLog(key = RoleAction.ROLE_SAVE, action = RoleAction.class)
    public ResultVo save(@Validated RoleValid valid, @EntityParam Role role){
        // ????????????????????????????????????
        if (role.getId() !=null && role.getId().equals(AdminConst.ADMIN_ROLE_ID) &&
                !ShiroUtil.getSubject().getId().equals(AdminConst.ADMIN_ID)){
            throw new ResultException(ResultEnum.NO_ADMINROLE_AUTH);
        }

        // ??????????????????????????????
        if (roleService.repeatByName(role)) {
            throw new ResultException(ResultEnum.ROLE_EXIST);
        }

        // ?????????????????????????????????
        if(role.getId() != null){
            Role beRole = roleService.getById(role.getId());
            String[] fields = {"users", "menus"};
            EntityBeanUtil.copyProperties(beRole, role, fields);
        }

        // ????????????
        roleService.save(role);
        return ResultVoUtil.SAVE_SUCCESS;
    }

    /**
     * ?????????????????????
     */
    @GetMapping("/auth")
    @RequiresPermissions("system:role:auth")
    @NBAuth(value = "system:role:auth", remark = "??????????????????", type = NAV_LINK, group = ROUTER)
    public String toAuth(@RequestParam(value = "ids") Long id, Model model){
        model.addAttribute("id", id);
        return "/system/role/auth";
    }

    /**
     * ????????????????????????
     */
    @GetMapping("/authList")
    @RequiresPermissions("system:role:auth")
    @ResponseBody
    public ResultVo authList(@RequestParam(value = "ids") Role role){
//        // ??????????????????????????????
//        Set<Menu> authMenus = role.getMenus();
//        // ????????????????????????
//        List<Menu> list = menuService.getListBySortOk();
//        // ??????????????????
//        list.forEach(menu -> {
//            if(authMenus.contains(menu)){
//                menu.setRemark("auth:true");
//            }else {
//                menu.setRemark("");
//            }
//        });
        List<NBSysResource> all = resourceRepository.findAll();
        List<NBSysResource> hasResources = resourceRepository.findResourcesByRoleId(role.getId());
        List<LayuiXTree> treeList = new ArrayList<>(all.size());
        treeList.addAll(transTo(all, NBSysResource::getName, NBSysResource::getId, NBSysResource::getPermission, hasResources::contains, res -> false));

        List<LayuiXTree> list =LayuiXTree.buildByRecursive(treeList);

        return ResultVoUtil.success(list);
    }

    /**
     * ??????????????????
     */
    @PostMapping("/auth")
    @RequiresPermissions("system:role:auth")
    @ResponseBody
    @ActionLog(key = RoleAction.ROLE_AUTH, action = RoleAction.class)
    public ResultVo auth(
            @RequestParam(value = "id", required = true) Role role,
            @RequestParam(value = "resourceIds[]", required = false) Long[] resourceIds){
        // ????????????????????????????????????
        if (role.getId().equals(AdminConst.ADMIN_ROLE_ID) &&
                !ShiroUtil.getSubject().getId().equals(AdminConst.ADMIN_ID)){
            throw new ResultException(ResultEnum.NO_ADMINROLE_AUTH);
        }

        role = roleService.getById(role.getId());
        Set<NBSysResource> menus = new HashSet<>();
        if (resourceIds != null && resourceIds.length > 0) {
            for (Long resource : resourceIds) {
                NBSysResource nbSysResource = NBSysResource.builder().id(resource).build();
                menus.add(nbSysResource);

            }
        }
        role.setMenus(menus);
//        // ??????????????????
//        role.setMenus(menus);
//
//        // ????????????
//        roleService.save(role);
        roleService.save(role);
        return ResultVoUtil.SAVE_SUCCESS;
    }

    /**
     * ?????????????????????
     */
    @RequestMapping("/detail/{id}")
    @RequiresPermissions("system:role:detail")
    @ActionLog(name = "?????????????????????", action = StatusAction.class)
    @NBAuth(value = "system:role:detail", remark = "??????????????????", type = NAV_LINK, group = ROUTER)
    public String toDetail(@PathVariable("id") Role role, Model model){
        model.addAttribute("role", role);
        return "/system/role/detail";
    }

    /**
     * ?????????????????????????????????????????????
     */
    @GetMapping("/userList/{id}")
    @RequiresPermissions("system:role:detail")
    @ActionLog(name = "???????????????????????????", action = StatusAction.class)
    public String toUserList(@PathVariable("id") Role role, Model model){
        model.addAttribute("list", userService.getUserList(role.getId()));
        return "/system/role/user_list";
    }

    /**
     * ???????????????????????????????????????
     */
    @RequestMapping("/status/{param}")
    @RequiresPermissions("system:role:status")
    @ResponseBody
    @ActionLog(name = "????????????", action = StatusAction.class)
    @NBAuth(value = "system:role:status", remark = "??????????????????", type = NAV_LINK, group = ROUTER)
    public ResultVo status(
            @PathVariable("param") String param,
            @RequestParam(value = "ids", required = false) List<Long> ids){
        // ???????????????????????????????????????
        if(ids.contains(AdminConst.ADMIN_ROLE_ID)){
            throw new ResultException(ResultEnum.NO_ADMINROLE_STATUS);
        }

        // ????????????
        StatusEnum statusEnum = StatusUtil.getStatusEnum(param);
        if (roleService.updateStatus(statusEnum, ids)) {
            return ResultVoUtil.success(statusEnum.getMessage() + "??????");
        } else {
            return ResultVoUtil.error(statusEnum.getMessage() + "????????????????????????");
        }
    }

    /**
     * ??????????????????LayuiXTree
     *
     * @param data
     * @param title
     * @param value
     * @param checked
     * @param disabled
     * @return
     */
    private List<LayuiXTree> transTo(List<NBSysResource> data,
                                     Function<NBSysResource, String> title,
                                     Function<NBSysResource, Long> value,
                                     Function<NBSysResource, String> group,
                                     Function<NBSysResource, Boolean> checked,
                                     Function<NBSysResource, Boolean> disabled) {

        List<LayuiXTree> treeList = new ArrayList<>();
        data.forEach(res -> {
            LayuiXTree tree = new LayuiXTree(
                    title.apply(res),
                    value.apply(res).toString(),
                    group.apply(res),
                    checked.apply(res),
                    disabled.apply(res));
            treeList.add(tree);
        });
        return treeList;
    }


}
