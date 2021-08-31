package com.nonelonely.admin.system.controller;

import com.nonelonely.admin.system.validator.UserValid;
import com.nonelonely.common.constant.AdminConst;
import com.nonelonely.common.data.URL;
import com.nonelonely.common.enums.ResultEnum;

import com.nonelonely.common.exception.ResultException;
import com.nonelonely.common.utils.EntityBeanUtil;
import com.nonelonely.common.utils.ResultVoUtil;

import com.nonelonely.common.utils.SpringContextUtil;
import com.nonelonely.common.vo.ResultVo;



import com.nonelonely.component.oshi.model.Server;
import com.nonelonely.component.shiro.ShiroUtil;

import com.nonelonely.component.thymeleaf.utility.ParamUtil;
import com.nonelonely.modules.system.domain.*;
import com.nonelonely.modules.system.domain.permission.NBAuth;
import com.nonelonely.modules.system.repository.CommentRepository;

import com.nonelonely.modules.system.repository.UserRepository;
import com.nonelonely.modules.system.repository.UvRepository;
import com.nonelonely.modules.system.service.MenuService;
import com.nonelonely.modules.system.service.ParamService;
import com.nonelonely.modules.system.service.UserService;
import org.apache.shiro.authz.annotation.RequiresPermissions;

import org.apache.shiro.authz.annotation.RequiresUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


import static com.nonelonely.modules.system.domain.permission.NBAuth.Group.ROUTER;

import static com.nonelonely.modules.system.domain.permission.NBSysResource.ResType.OTHER;

/**
 * @author nonelonely
 * @date 2018/8/14
 */
@Controller
@RequestMapping("/system/main")
public class MainController{

    @Autowired
    private UserService userService;

    @Autowired
    private MenuService menuService;

    @Autowired
    private UvRepository uvRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ParamService paramService;


    /**
     * 后台主体内容
     */
    @GetMapping("/")
    @RequiresPermissions("system:main:index")
    public String main(Model model){
        // 获取当前登录的用户
        User user = ShiroUtil.getSubject();
        model.addAttribute("user", user);

        String adminType =ParamUtil.value("admin_type");

        if (adminType==null||"1".equals(adminType)){
            return "/win10";
        }else {
            Map treeMenu = getMum(user, (byte) 1);
            model.addAttribute("treeMenu", treeMenu);
            return "/main";
        }
    }

    private Map getMum(User user,Byte type){
        // 菜单键值对(ID->菜单)
        Map<Long, Menu> keyMenu = new HashMap<>(16);

        // 管理员实时更新菜单
        if(user.getId().equals(AdminConst.ADMIN_ID)){
            List<Menu> menus = menuService.getListBySortOk(type);
            menus.forEach(menu -> keyMenu.put(menu.getId(), menu));
        }else{
            // 其他用户需从相应的角色中获取菜单资源
            Set<Role> roles = ShiroUtil.getSubjectRoles();
            roles.forEach(role -> {
                List<Menu> menus = menuService.getListBySortOk(type);
                role.getMenus().forEach(rs -> {
                    menus.forEach(menu -> {
                        if (rs.getUrl().equals(menu.getUrl())||menu.getUrl().equals("#")) {
                            keyMenu.put(menu.getId(), menu);
                        }
                    });
                });
            });
        }

        // 封装菜单树形数据
        Map<Long, Menu> treeMenu = new HashMap<>(16);
        keyMenu.forEach((id, menu) -> {

                if(keyMenu.get(menu.getPid()) != null){
                    keyMenu.get(menu.getPid()).getChildren().put(Long.valueOf(menu.getSort()), menu);
                }else{
                    if(menu.getPid()==0){ //一级
                        treeMenu.put(Long.valueOf(menu.getSort()), menu);
                    }
                }

        });

        return treeMenu;
    }
    /**
     * 主页
     */
    @GetMapping("/index")
    @RequiresPermissions("system:main:index")
    @NBAuth(value = "system:main:index", remark = "后台主体内容", type = OTHER, group = ROUTER)
    public String index(Model model,String start,String end){
        LocalDateTime date = LocalDateTime.now();
        DateTimeFormatter format1 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String str = date.format(format1);
        if (StringUtils.isEmpty(start)){
            start = str;
        }
        if (StringUtils.isEmpty(end)){
            end =str;
        }
        List<Map> nowUrls = uvRepository.findNowUrlTop10(start,end);
        model.addAttribute("nowUrls",nowUrls);

        List<Map> fromUrls = uvRepository.findFromUrlTop10(start,end);
        model.addAttribute("fromUrls",fromUrls);

        model.addAttribute("start",start);
        model.addAttribute("end",end);

        model.addAttribute("nowPv",uvRepository.findCountNow());
        model.addAttribute("ip",0);


        model.addAttribute("comments",commentRepository.countCommentNow());

        model.addAttribute("users",userRepository.countUserNow());

        return "/system/main/index";
    }


    /**
     * 跳转到个人信息页面
     */
    @GetMapping("/userInfo")
    @RequiresPermissions("system:main:index")
    public String toUserInfo(Model model){
        User user = ShiroUtil.getSubject();
        model.addAttribute("user", user);
        return "/system/main/userInfo";
    }

    /**
     * 修改用户头像
     */
    @PostMapping("/userPicture")
    @RequiresPermissions("system:main:index")
    @ResponseBody
    public ResultVo userPicture(@RequestParam("picture") MultipartFile picture){
        UploadController uploadController = SpringContextUtil.getBean(UploadController.class);
        ResultVo imageResult = uploadController.uploadPicture(picture);
        if(imageResult.getCode().equals(ResultEnum.SUCCESS.getCode())){
            User subject = ShiroUtil.getSubject();
            subject.setPicture("/system/user/picture?p="+((Upload) imageResult.getData()).getPath());
            userService.save(subject);
            return ResultVoUtil.SAVE_SUCCESS;
        }else {
            return imageResult;
        }
    }

    /**
     * 保存修改个人信息
     */
    @PostMapping("/userInfo")
    @RequiresPermissions("system:main:index")
    @ResponseBody
    public ResultVo userInfo(@Validated UserValid valid, User user){

        // 复制保留无需修改的数据
        User subUser = ShiroUtil.getSubject();
        String[] ignores = {"id", "username", "password", "salt", "picture", "dept", "roles"};
        EntityBeanUtil.copyPropertiesIgnores(user, subUser, ignores);

        // 保存数据
        userService.save(subUser);
        return ResultVoUtil.success("保存成功", new URL("/userInfo"));
    }

    /**
     * 跳转到修改密码页面
     */
    @GetMapping("/editPwd")
    @RequiresPermissions("index")
    public String toEditPwd(){
        return "/system/main/editPwd";
    }

    /**
     * 保存修改密码
     */
    @PostMapping("/editPwd")
    @RequiresPermissions("system:main:index")
    @ResponseBody

    public ResultVo editPwd(String original, String password, String confirm){
        // 判断原来密码是否有误
        User subUser = ShiroUtil.getSubject();
        String oldPwd = ShiroUtil.encrypt(original, subUser.getSalt());
        if (original.isEmpty() || "".equals(original.trim()) || !oldPwd.equals(subUser.getPassword())) {
            throw new ResultException(ResultEnum.USER_OLD_PWD_ERROR);
        }

        // 判断密码是否为空
        if (password.isEmpty() || "".equals(password.trim())) {
            throw new ResultException(ResultEnum.USER_PWD_NULL);
        }

        // 判断两次密码是否一致
        if (!password.equals(confirm)) {
            throw new ResultException(ResultEnum.USER_INEQUALITY);
        }

        // 修改密码，对密码进行加密
        String salt = ShiroUtil.getRandomSalt();
        String encrypt = ShiroUtil.encrypt(password, salt);
        subUser.setPassword(encrypt);
        subUser.setSalt(salt);

        // 保存数据
        userService.save(subUser);
        return ResultVoUtil.success("修改成功");
    }


    /**
     * 跳转到修改密码页面
     */
    @GetMapping("/map")
    @ResponseBody
    public List<Map>  getMap(){

       return uvRepository.findMap();
    }


    @GetMapping("/getDeskTop")
    @ResponseBody
    public ResultVo getDeskTop(){

        // 获取当前登录的用户
        User user = ShiroUtil.getSubject();
        Map treeMenu = getMum(user,(byte)1);

        return ResultVoUtil.success(treeMenu);

    }
    /**
     * 获取服务器监控信息
     */
    @NBAuth(value = "system:main:monitor", remark = "获取服务器监控信息", type = OTHER, group = ROUTER)
    @GetMapping("/monitor")
    public String getInfo(Model model) throws Exception {
        Server server = new Server();
        server.copyTo();
        model.addAttribute("server",server);
        return "/system/main/monitor";
    }

    /**
     * 获取服务器监控信息
     */
    @GetMapping("/changeAdmin")
    @ResponseBody
    @RequiresUser
    public ResultVo changeAdmin(String value) throws Exception {
        Param beParam = paramService.getByName("admin_type");
        if (beParam ==null){
            beParam = new Param();
            beParam.setName("admin_type");
            beParam.setValue("1");
        }else{
            beParam.setValue(value);
        }
        paramService.save(beParam);
        if(beParam.getId() != null){
            ParamUtil.clearCache(beParam.getName());
        }
        return ResultVoUtil.success();
    }



    public static void main(String[] args)
    {
        String password="123456";
        String salt = ShiroUtil.getRandomSalt();
        String encrypt = ShiroUtil.encrypt(password, salt);
        System.out.println(salt);
        System.out.println(encrypt);

    }

}
