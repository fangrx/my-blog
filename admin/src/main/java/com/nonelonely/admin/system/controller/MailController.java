package com.nonelonely.admin.system.controller;


import com.nonelonely.admin.system.validator.LinkValid;
import com.nonelonely.common.enums.StatusEnum;
import com.nonelonely.common.utils.EntityBeanUtil;
import com.nonelonely.common.utils.ResultVoUtil;
import com.nonelonely.common.utils.SpringContextUtil;
import com.nonelonely.common.utils.StatusUtil;
import com.nonelonely.common.vo.ResultVo;
import com.nonelonely.component.email.service.auth.AuthMailService;
import com.nonelonely.component.email.service.sender.SendMailService;
import com.nonelonely.component.email.vo.MailReq;
import com.nonelonely.modules.system.domain.Link;
import com.nonelonely.modules.system.domain.mail.Mail;
import com.nonelonely.modules.system.domain.mail.MailBox;
import com.nonelonely.modules.system.domain.permission.NBAuth;
import com.nonelonely.modules.system.service.mail.IMailBoxService;
import com.nonelonely.modules.system.service.mail.IMailService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.nonelonely.modules.system.domain.permission.NBAuth.Group.PAGE;
import static com.nonelonely.modules.system.domain.permission.NBAuth.Group.ROUTER;
import static com.nonelonely.modules.system.domain.permission.NBSysResource.ResType.NAV_LINK;
import static com.nonelonely.modules.system.domain.permission.NBSysResource.ResType.OTHER;

/**
 * @author nonelonely
 * @date 2020/04/19
 */
@Controller
@RequestMapping("/system/mail")
public class MailController {

    @Autowired
    private IMailService imailService;
    @Autowired
    private IMailBoxService iMailBoxService;
   @Autowired
   private AuthMailService authMailService;
    @Autowired
    private SendMailService sendMailService;

    /**
     * 列表页面
     */
    @GetMapping("/index")
    @RequiresPermissions("system:mail:index")
    @NBAuth(value = "system:mail:index", remark = "邮箱系统列表页面", type = OTHER, group = ROUTER)
    public String index(Model model, Mail mail) {

        // 创建匹配器，进行动态查询匹配
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withMatcher("title", match -> match.contains())
                .withMatcher("fromUser", match -> match.contains())
                .withMatcher("toUser", match -> match.contains()) ;

        // 获取数据列表
        Example<Mail> example = Example.of(mail, matcher);
        Page<Mail> list = imailService.getPageList(example);

        // 封装数据
        model.addAttribute("list", list.getContent());
        model.addAttribute("page", list);
        model.addAttribute("mailBoxList", iMailBoxService.getAllMailBox());

        return "/system/mail/index";
    }

    /**
     * 邮箱账号设置页面
     */
    @GetMapping("/setMailBox")
    @RequiresPermissions("system:mail:setMailBox")
    @NBAuth(value = "system:mail:setMailBox", remark = "邮箱账号设置页面", type = OTHER, group = PAGE)
    public String setMailBox(Model model) {


        model.addAttribute("mailBoxList", iMailBoxService.getAllMailBox());

        return "/system/mail/set";
    }
    /**
     * 跳转到添加页面
     */
    @GetMapping("/add")
    @RequiresPermissions("system:mail:add")
    @NBAuth(value = "system:mail:add", remark = "邮箱系统添加页面", type = NAV_LINK, group = ROUTER)
    public String toAdd(Model model) {
        model.addAttribute("mailBoxList", iMailBoxService.getAllMailBox());
        return "/system/mail/add";
    }

    /**
     * 跳转到编辑页面
     */
    @GetMapping("/edit/{id}")
    @RequiresPermissions("system:mail:edit")
    @NBAuth(value = "system:mail:edit", remark = "邮箱系统编辑页面", type = NAV_LINK, group = ROUTER)
    public String toEdit(@PathVariable("id") Mail mail, Model model) {
        model.addAttribute("mail", mail);
        return "/system/mail/add";
    }
    /**
     * 跳转到详细页面
     */
    @GetMapping("/mailBoxDetail/{id}")
    @RequiresPermissions("system:mailBox:detail")
    @ResponseBody
    @NBAuth(value = "system:mailBox:detail", remark = "邮箱系统详细页面", type = NAV_LINK, group = ROUTER)
    public ResultVo mailBoxDetail(@PathVariable("id") MailBox mailBox, Model model) throws  Exception{

        return ResultVoUtil.success(mailBox);
    }
    /**
     * 保存添加/修改的数据
     *
     */
    @PostMapping("/save")
    @RequiresPermissions({"system:mail:add", "system:mail:edit"})
    @ResponseBody
    public ResultVo save(MailReq mailReq) {
        // 保存数据
        return  sendMailService.sendMail(mailReq);
    }

    /**
     * 跳转到详细页面
     */
    @GetMapping("/detail/{id}")
    @RequiresPermissions("system:mail:detail")

    @NBAuth(value = "system:mail:detail", remark = "邮箱系统详细页面", type = NAV_LINK, group = ROUTER)
    public String toDetail(@PathVariable("id") Mail mail, Model model) throws  Exception{
        mail.setIsRead(2);
        imailService.save(mail);
        model.addAttribute("mail",mail);
        return "/system/mail/detail";
    }
    /**
     * 保存添加/修改的数据
     * @param
     */
    @PostMapping("/saveMailBox")
    @RequiresPermissions({"system:mail:saveMailBox", "system:mail:saveMailBox"})
    @ResponseBody
    public ResultVo saveMailBox(MailBox mailBox) {
        // 复制保留无需修改的数据
        if (mailBox.getId() != null) {
            MailBox beMailBox = iMailBoxService.getById(mailBox.getId());
            EntityBeanUtil.copyProperties(beMailBox, mailBox);
        }
        // 保存数据
        return  authMailService.checkAndUpdate(mailBox,true,mailBox.getMailBoxCode());
    }
//    /**
//     * 设置一条或者多条数据的状态
//     */
//    @RequestMapping("/status/{param}")
//    @RequiresPermissions("system:mail:status")
//    @ResponseBody
//    @NBAuth(value = "system:mail:status", remark = "邮箱系统数据状态", type = NAV_LINK, group = ROUTER)
//    public ResultVo status(
//            @PathVariable("param") String param,
//            @RequestParam(value = "ids", required = false) List<Long> ids) {
//        // 更新状态
//        StatusEnum statusEnum = StatusUtil.getStatusEnum(param);
//        if (mailService.updateStatus(statusEnum, ids)) {
//            return ResultVoUtil.success(statusEnum.getMessage() + "成功");
//        } else {
//            return ResultVoUtil.error(statusEnum.getMessage() + "失败，请重新操作");
//        }
//    }
//
//    /**
//     * 设置一条或者多条数据的状态
//     */
//    @RequestMapping("/getMail")
//    @RequiresPermissions("system:mail:status")
//    @ResponseBody public ResultVo getMail() {
//        mailServiceUtil.getMail();
//        return ResultVoUtil.success("成功");
//    }
    /**
     * 列表页面
     */
    @GetMapping("/getDataList")
    @RequiresPermissions("system:mail:getDataList")
    @NBAuth(value = "system:mail:getDataList", remark = "获取邮箱系统列表", type = OTHER, group = ROUTER)
    @ResponseBody
    public ResultVo getDataList( Mail mail) {
        // 创建匹配器，进行动态查询匹配
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withMatcher("title", match -> match.contains())
                .withMatcher("fromUser", match -> match.contains())
                .withMatcher("toUser", match -> match.contains()) ;

        // 获取数据列表
        Example<Mail> example = Example.of(mail, matcher);
        Page<Mail> list = imailService.getPageList(example);

        return ResultVoUtil.success(list);
    }

    /**
     * 设置一条或者多条数据的状态
     */
    @RequestMapping("/mailBox/status/{param}")
    @RequiresPermissions("system:mailBox:status")
    @ResponseBody
    @NBAuth(value = "system:mailBox:status", remark = "邮箱账号数据状态", type = NAV_LINK, group = ROUTER)
    public ResultVo status(
            @PathVariable("param") String param,
            @RequestParam(value = "ids", required = false) List<Long> ids) {
        // 更新状态
        StatusEnum statusEnum = StatusUtil.getStatusEnum(param);
        if (iMailBoxService.updateStatus(statusEnum, ids)) {
            return ResultVoUtil.success(statusEnum.getMessage() + "成功");
        } else {
            return ResultVoUtil.error(statusEnum.getMessage() + "失败，请重新操作");
        }
    }
}
