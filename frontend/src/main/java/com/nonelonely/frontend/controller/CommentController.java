package com.nonelonely.frontend.controller;


import cn.hutool.core.util.StrUtil;

import com.nonelonely.common.utils.CaptchaUtil;
import com.nonelonely.common.utils.ResultVoUtil;
import com.nonelonely.common.utils.ToolUtil;
import com.nonelonely.common.vo.ResultVo;
import com.nonelonely.component.email.Email;

import com.nonelonely.component.email.util.MailCache;
import com.nonelonely.component.email.vo.MailReq;
import com.nonelonely.component.thymeleaf.utility.ParamUtil;
import com.nonelonely.modules.system.domain.Comment;

import com.nonelonely.modules.system.domain.permission.NBAuth;
import com.nonelonely.modules.system.repository.ArticleRepository;

import com.nonelonely.modules.system.repository.CommentRepository;
import com.nonelonely.modules.system.repository.ParamRepository;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.servlet.http.HttpServletRequest;

import java.util.*;


import static com.nonelonely.common.constant.ParamConst.ALL_COMMENT_OPEN;
import static com.nonelonely.common.constant.ParamConst.COMMENT_KEYWORD;
import static com.nonelonely.common.constant.ParamConst.IS_OPEN_MESSAGE;
import static com.nonelonely.modules.system.domain.permission.NBAuth.Group.ROUTER;
import static com.nonelonely.modules.system.domain.permission.NBSysResource.ResType.NAV_LINK;

@Controller("fontCommentController")
@RequestMapping("frontend")
public class CommentController {
    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ParamRepository paramRepository;
    @Autowired
    private ArticleRepository articleRepository;
    @Autowired
    private TemplateEngine templateEngine;




    @RequestMapping("/comment/sub")
    @RequiresPermissions("frontend:comment:sub")
    @ResponseBody
    @NBAuth(value = "frontend:comment:sub", remark = "????????????", type = NAV_LINK, group = ROUTER)
    public ResultVo sub(@RequestBody Comment comment, BindingResult bindingResult, HttpServletRequest request) {
        final String initSure = "1";
        ResultVo resultVo= ResultVoUtil.ajaxDone(
                () -> initSure.equals(paramRepository.findFirstByName(ALL_COMMENT_OPEN).getValue()) && articleRepository.findArticleById(comment.getArticle().getId()).getCommented(),
                () -> {
                    if (!bindingResult.hasErrors()) {
                        comment.setIpAddr(ToolUtil.getRemoteAddress(request));

                        try {
                            comment.setIpCnAddr(ToolUtil.getIpInfo(comment.getIpAddr()));
                        }catch (Exception e)
                        {
                            comment.setIpCnAddr("???????????????");
                            e.printStackTrace();
                        }
                        comment.setUserAgent(request.getHeader("user-agent"));
                        comment.setComment(ToolUtil.stripSqlXSS(comment.getComment()));

                        //List<NBKeyword> keywords = keywordRepository.findAll();
                        String  keyword =  paramRepository.findFirstByName(COMMENT_KEYWORD).getValue();
                        String[] keywords = keyword.split("\\|");
                        Arrays.stream(keywords).forEach(kw -> comment.setComment(comment.getComment().replace(kw, StrUtil.repeat("*", kw.length()))));
                        return ResultVoUtil.ajaxDone(commentRepository::save, comment, "??????????????????", "??????????????????");
                    } else {
                        return ResultVoUtil.ajaxJsr303(bindingResult.getFieldErrors());
                    }
                },
                () -> "?????????????????????????????????????????????"
        );

        if (resultVo.getCode() == 200&&"1".equals( ParamUtil.value("message_send_mail"))){

               Context ctx = new Context();
                ctx.setVariable("comment",comment.getComment());
                //??????????????????????????????????????????????????????????????????????????????
                commentRepository.findById(comment.getParentId()).ifPresent(k->{
                    if (!StringUtils.isEmpty(k.getCreateBy().getEmail())){
                        ctx.setVariable("userName",k.getCreateBy().getNickname());
                        ctx.setVariable("commentUrl",k.getComment());

                    }
                });
                String emailText = templateEngine.process("/emailCommend", ctx);
                MailReq email= new MailReq();
                email.setTitle("????????????");
                email.setToUser(ParamUtil.value("message_to_user"));
                email.setMailBox(ParamUtil.value("message_to_from"));
                email.setMailContHtml(emailText);
                MailCache.getSendMailQueue().add(email);
        }
        return  resultVo;
    }
    @RequestMapping("/message/sub")
    @RequiresPermissions("frontend:message:sub")
    @ResponseBody
    @NBAuth(value = "frontend:message:sub", remark = "????????????", type = NAV_LINK, group = ROUTER)
    public ResultVo subs(@RequestBody Comment comment, BindingResult bindingResult, HttpServletRequest request) {
        final String initSure = "1";
        ResultVo resultVo= ResultVoUtil.ajaxDone(
                () -> initSure.equals(paramRepository.findFirstByName(IS_OPEN_MESSAGE).getValue()),
                () -> {
                    if (!bindingResult.hasErrors()) {
                        comment.setIpAddr(ToolUtil.getRemoteAddress(request));

                        try {
                            comment.setIpCnAddr(ToolUtil.getIpInfo(comment.getIpAddr()));
                        }catch (Exception e)
                        {
                            comment.setIpCnAddr("???????????????");
                            e.printStackTrace();
                        }
                        comment.setUserAgent(request.getHeader("user-agent"));
                        comment.setComment(ToolUtil.stripSqlXSS(comment.getComment()));

                        //List<NBKeyword> keywords = keywordRepository.findAll();
                        String  keyword =  paramRepository.findFirstByName(COMMENT_KEYWORD).getValue();
                        String[] keywords = keyword.split("\\|");
                        Arrays.stream(keywords).forEach(kw -> comment.setComment(comment.getComment().replace(kw, StrUtil.repeat("*", kw.length()))));
                        return ResultVoUtil.ajaxDone(commentRepository::save, comment, "??????????????????", "??????????????????");
                    } else {
                        return ResultVoUtil.ajaxJsr303(bindingResult.getFieldErrors());
                    }
                },
                () -> "?????????????????????????????????????????????"
        );
        String message_send_mail = ParamUtil.value("message_send_mail");
        if (resultVo.getCode() == 200&&"1".equals( ParamUtil.value("message_send_mail"))){

            Context ctx = new Context();
            ctx.setVariable("comment",comment.getComment());
            //??????????????????????????????????????????????????????????????????????????????
            commentRepository.findById(comment.getParentId()).ifPresent(k->{
                if (!StringUtils.isEmpty(k.getCreateBy().getEmail())){
                    ctx.setVariable("userName",k.getCreateBy().getNickname());
                    ctx.setVariable("commentUrl",k.getComment());

                }
            });
            String emailText = templateEngine.process("/emailCommend", ctx);
            MailReq email= new MailReq();
            email.setTitle("????????????");
            email.setToUser(ParamUtil.value("message_to_user"));
            email.setMailBox(ParamUtil.value("message_to_from"));
            email.setMailContHtml(emailText);
            MailCache.getSendMailQueue().add(email);

      }
        return  resultVo;
    }

    /**
     * ?????????????????????
     */
    @GetMapping("/emailCode")
    @ResponseBody
    public ResultVo emailCode(HttpServletRequest request,String to){
        // ???????????????
        String code = CaptchaUtil.getRandomCode();
        Email email = Email.builder().subject("??????????????????").to(to).template("/emailCode").build();
        Context ctx = new Context();
        ctx.setVariable("code",code);
        // ?????????????????????session??????????????????
        request.getSession().setMaxInactiveInterval(20*60);
        request.getSession() .setAttribute("emailCode", code);
        request.getSession() .setAttribute("emailCodeTo", to);
        Map<String, Long> map =new HashMap<>();
        Calendar calender = Calendar.getInstance();
        map.put("serverTime",calender.getTime().getTime());
        calender.add(Calendar.MINUTE,1);
        map.put("endTime",calender.getTime().getTime());
       // mailService.htmlEmail(ctx,email);
        return ResultVoUtil.success("???????????????????????????",map);
    }
}
