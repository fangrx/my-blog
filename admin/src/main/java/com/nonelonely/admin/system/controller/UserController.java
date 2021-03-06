package com.nonelonely.admin.system.controller;

import com.nonelonely.admin.system.validator.UserValid;
import com.nonelonely.common.constant.AdminConst;
import com.nonelonely.common.enums.ResultEnum;
import com.nonelonely.common.enums.StatusEnum;
import com.nonelonely.common.exception.ResultException;
import com.nonelonely.common.utils.EntityBeanUtil;
import com.nonelonely.common.utils.ResultVoUtil;
import com.nonelonely.common.utils.SpringContextUtil;
import com.nonelonely.common.utils.StatusUtil;
import com.nonelonely.common.vo.ResultVo;
import com.nonelonely.component.actionLog.action.StatusAction;
import com.nonelonely.component.actionLog.action.UserAction;
import com.nonelonely.component.actionLog.annotation.ActionLog;
import com.nonelonely.component.actionLog.annotation.EntityParam;
import com.nonelonely.component.excel.ExcelUtil;
import com.nonelonely.component.fileUpload.config.properties.UploadProjectProperties;
import com.nonelonely.component.shiro.ShiroUtil;
import com.nonelonely.modules.system.domain.Role;
import com.nonelonely.modules.system.domain.User;
import com.nonelonely.modules.system.domain.permission.NBAuth;
import com.nonelonely.modules.system.repository.UserRepository;
import com.nonelonely.modules.system.service.RoleService;
import com.nonelonely.modules.system.service.UserService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.nonelonely.modules.system.domain.permission.NBAuth.Group.ROUTER;
import static com.nonelonely.modules.system.domain.permission.NBSysResource.ResType.NAV_LINK;
import static com.nonelonely.modules.system.domain.permission.NBSysResource.ResType.OTHER;

/**
 * @author nonelonely
 * @date 2018/8/14
 */
@Controller
@RequestMapping("/system/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    /**
     * ????????????
     */
    @GetMapping("/index")
    @RequiresPermissions("system:user:index")
    @NBAuth(value = "system:user:index", remark = "??????????????????", type = OTHER, group = ROUTER)
    public String index(Model model, User user) {

        // ??????????????????
        Page<User> list = userService.getPageList(user);

        // ????????????
        model.addAttribute("list", list.getContent());
        model.addAttribute("page", list);
        model.addAttribute("dept", user.getDept());
        return "/system/user/index";
    }

    /**
     * ?????????????????????
     */
    @GetMapping("/add")
    @RequiresPermissions("system:user:add")
    @NBAuth(value = "system:user:add", remark = "??????????????????", type = NAV_LINK, group = ROUTER)

    public String toAdd() {
        return "/system/user/add";
    }

    /**
     * ?????????????????????
     */
    @GetMapping("/edit/{id}")
    @RequiresPermissions("system:user:edit")
    @NBAuth(value = "system:user:edit", remark = "??????????????????", type = NAV_LINK, group = ROUTER)

    public String toEdit(@PathVariable("id") User user, Model model) {
        model.addAttribute("user", user);
        return "/system/user/add";
    }

    /**
     * ????????????/???????????????
     * @param valid ????????????
     * @param user ????????????
     */
    @PostMapping("/save")
    @RequiresPermissions({"system:user:add", "system:user:edit"})
    @ResponseBody
    @ActionLog(key = UserAction.USER_SAVE, action = UserAction.class)

    public ResultVo save(@Validated UserValid valid, @EntityParam User user) {

        // ????????????????????????
        if (user.getId() == null) {

            // ????????????????????????
            if (user.getPassword().isEmpty() || "".equals(user.getPassword().trim())) {
                throw new ResultException(ResultEnum.USER_PWD_NULL);
            }

            // ??????????????????????????????
            if (!user.getPassword().equals(valid.getConfirm())) {
                throw new ResultException(ResultEnum.USER_INEQUALITY);
            }

            // ?????????????????????
            String salt = ShiroUtil.getRandomSalt();
            String encrypt = ShiroUtil.encrypt(user.getPassword(), salt);
            user.setPassword(encrypt);
            user.setSalt(salt);
        }

        // ???????????????????????????
        if (userService.repeatByUsername(user)) {
            throw new ResultException(ResultEnum.USER_EXIST);
        }

        // ?????????????????????????????????
        if (user.getId() != null) {
            // ????????????????????????????????????
            if (user.getId().equals(AdminConst.ADMIN_ID) &&
                    !ShiroUtil.getSubject().getId().equals(AdminConst.ADMIN_ID)) {
                throw new ResultException(ResultEnum.NO_ADMIN_AUTH);
            }

            User beUser = userService.getById(user.getId());
            String[] fields = {"password", "salt", "picture", "roles"};
            EntityBeanUtil.copyProperties(beUser, user, fields);
        }

        // ????????????
        userService.save(user);
        return ResultVoUtil.SAVE_SUCCESS;
    }

    /**
     * ?????????????????????
     */
    @GetMapping("/detail/{id}")
    @RequiresPermissions("system:user:detail")
    @NBAuth(value = "system:user:detail", remark = "??????????????????", type = NAV_LINK, group = ROUTER)

    public String toDetail(@PathVariable("id") User user, Model model) {
        model.addAttribute("user", user);
        return "/system/user/detail";
    }

    /**
     * ???????????????????????????
     */
    @GetMapping("/pwd")
    @RequiresPermissions("system:user:pwd")
    @NBAuth(value = "system:user:pwd", remark = "????????????????????????", type = NAV_LINK, group = ROUTER)

    public String toEditPassword(Model model, @RequestParam(value = "ids", required = false) List<Long> ids) {
        model.addAttribute("idList", ids);
        return "/system/user/pwd";
    }

    /**
     * ????????????
     */
    @PostMapping("/pwd")
    @RequiresPermissions("system:user:pwd")
    @ResponseBody
    @ActionLog(key = UserAction.EDIT_PWD, action = UserAction.class)
    public ResultVo editPassword(String password, String confirm,
                                 @RequestParam(value = "ids", required = false) List<Long> ids,
                                 @RequestParam(value = "ids", required = false) List<User> users) {

        // ????????????????????????
        if (password.isEmpty() || "".equals(password.trim())) {
            throw new ResultException(ResultEnum.USER_PWD_NULL);
        }

        // ??????????????????????????????
        if (!password.equals(confirm)) {
            throw new ResultException(ResultEnum.USER_INEQUALITY);
        }

        // ????????????????????????????????????
        if (ids.contains(AdminConst.ADMIN_ID) &&
                !ShiroUtil.getSubject().getId().equals(AdminConst.ADMIN_ID)) {
            throw new ResultException(ResultEnum.NO_ADMIN_AUTH);
        }

        // ????????????????????????????????????
        users.forEach(user -> {
            String salt = ShiroUtil.getRandomSalt();
            String encrypt = ShiroUtil.encrypt(password, salt);
            user.setPassword(encrypt);
            user.setSalt(salt);
        });

        // ????????????
        userService.save(users);
        return ResultVoUtil.success("????????????");
    }

    /**
     * ???????????????????????????
     */
    @GetMapping("/role")
    @RequiresPermissions("system:user:role")
    @NBAuth(value = "system:user:role", remark = "????????????????????????", type = NAV_LINK, group = ROUTER)

    public String toRole(@RequestParam(value = "ids") User user, Model model) {
        // ??????????????????????????????
        Set<Role> authRoles = user.getRoles();
        // ????????????????????????
        Sort sort = new Sort(Sort.Direction.ASC, "createDate");
        List<Role> list = roleService.getListBySortOk(sort);

        model.addAttribute("id", user.getId());
        model.addAttribute("list", list);
        model.addAttribute("authRoles", authRoles);
        return "/system/user/role";
    }

    /**
     * ????????????????????????
     */
    @PostMapping("/role")
    @RequiresPermissions("system:user:role")
    @ResponseBody
    @ActionLog(key = UserAction.EDIT_ROLE, action = UserAction.class)

    public ResultVo auth(
            @RequestParam(value = "id", required = true) User user,
            @RequestParam(value = "roleId", required = false) HashSet<Role> roles) {

        // ????????????????????????????????????
        if (user.getId().equals(AdminConst.ADMIN_ID) &&
                !ShiroUtil.getSubject().getId().equals(AdminConst.ADMIN_ID)) {
            throw new ResultException(ResultEnum.NO_ADMIN_AUTH);
        }

        // ??????????????????
        user.setRoles(roles);

        // ????????????
        userService.save(user);
        return ResultVoUtil.SAVE_SUCCESS;
    }

    /**
     * ??????????????????
     */
    @GetMapping("/picture")
    public void picture(String p, HttpServletResponse response) throws IOException {
        String defaultPath = "/images/user-picture.jpg";
        if (!(StringUtils.isEmpty(p) || p.equals(defaultPath))) {
            UploadProjectProperties properties = SpringContextUtil.getBean(UploadProjectProperties.class);
            String fuPath = properties.getFilePath();
            String spPath = properties.getStaticPath().replace("*", "");
            File file = new File(fuPath + p.replace(spPath, ""));
            if (file.exists()) {
                FileCopyUtils.copy(new FileInputStream(file), response.getOutputStream());
                return;
            }
        }
        Resource resource = new ClassPathResource("static" + defaultPath);
        FileCopyUtils.copy(resource.getInputStream(), response.getOutputStream());
    }

    /**
     * ??????????????????
     */
    @GetMapping("/export")
    @ResponseBody
    public void exportExcel() {
        UserRepository userRepository = SpringContextUtil.getBean(UserRepository.class);
        ExcelUtil.exportExcel(User.class, userRepository.findAll());
    }

    /**
     * ???????????????????????????????????????
     */
    @RequestMapping("/status/{param}")
    @RequiresPermissions("system:user:status")
    @ResponseBody
    @NBAuth(value = "system:user:status", remark = "??????????????????", type = NAV_LINK, group = ROUTER)
    @ActionLog(name = "????????????", action = StatusAction.class)
    public ResultVo updateStatus(
            @PathVariable("param") String param,
            @RequestParam(value = "ids", required = false) List<Long> ids) {

        // ?????????????????????????????????
        if (ids.contains(AdminConst.ADMIN_ID)) {
            throw new ResultException(ResultEnum.NO_ADMIN_STATUS);
        }

        // ????????????
        StatusEnum statusEnum = StatusUtil.getStatusEnum(param);
        if (userService.updateStatus(statusEnum, ids)) {
            return ResultVoUtil.success(statusEnum.getMessage() + "??????");
        } else {
            return ResultVoUtil.error(statusEnum.getMessage() + "????????????????????????");
        }
    }

}
