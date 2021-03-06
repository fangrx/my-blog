package com.nonelonely.admin.system.controller;

import cn.hutool.core.map.MapUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.nonelonely.common.config.properties.ProjectProperties;
import com.nonelonely.common.constant.ParamConst;
import com.nonelonely.common.data.URL;
import com.nonelonely.common.enums.ResultEnum;
import com.nonelonely.common.exception.ResultException;
import com.nonelonely.common.utils.*;
import com.nonelonely.common.vo.ResultVo;
import com.nonelonely.component.actionLog.action.UserAction;
import com.nonelonely.component.actionLog.annotation.ActionLog;
import com.nonelonely.component.shiro.ShiroUtil;
import com.nonelonely.component.shiro.UserPassOpenIdToken;
import com.nonelonely.component.thymeleaf.utility.ParamUtil;
import com.nonelonely.config.GlobalContext;
import com.nonelonely.devtools.generate.utils.jAngel.utils.StringUtil;
import com.nonelonely.modules.system.domain.Param;
import com.nonelonely.modules.system.domain.Role;
import com.nonelonely.modules.system.domain.User;
import com.nonelonely.modules.system.domain.permission.NBSysResource;
import com.nonelonely.modules.system.repository.ParamRepository;
import com.nonelonely.modules.system.repository.UserRepository;
import com.nonelonely.modules.system.service.RoleService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.imageio.ImageIO;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author nonelonely
 * @date 2018/8/14
 */
@Controller
public class LoginController implements ErrorController {

    @Autowired
    private RoleService roleService;
    private ParamRepository paramRepository;
    @Autowired
    private GlobalContext context;
    @Autowired
    private UserRepository userRepository;
    /**
     * ?????????????????????
     */
    @GetMapping("/login")
    public String toLogin(Model model) {
        ProjectProperties properties = SpringContextUtil.getBean(ProjectProperties.class);

        Boolean captchaOpen =Boolean.valueOf(ParamUtil.value("captcha_open"));
        if (StringUtils.isEmpty(captchaOpen)){
            captchaOpen = properties.isCaptchaOpen();
        }
        model.addAttribute("isCaptcha",captchaOpen);
        return "/login";
    }

    /**
     * ????????????
     */
    @PostMapping("/login")
    @ResponseBody
    @ActionLog(key = UserAction.USER_LOGIN, action = UserAction.class)
    public ResultVo login(String username, String password, String captcha, String rememberMe) {
        // ??????????????????????????????
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            throw new ResultException(ResultEnum.USER_NAME_PWD_NULL);
        }

        // ???????????????????????????
        ProjectProperties properties = SpringContextUtil.getBean(ProjectProperties.class);
        Boolean captchaOpen =Boolean.valueOf(ParamUtil.value("captcha_open"));
        if (StringUtils.isEmpty(captchaOpen)){
            captchaOpen = properties.isCaptchaOpen();
        }
        if (captchaOpen) {
            Session session = SecurityUtils.getSubject().getSession();
            String sessionCaptcha = (String) session.getAttribute("captcha");
            if (StringUtils.isEmpty(captcha) || StringUtils.isEmpty(sessionCaptcha)
                    || !captcha.toUpperCase().equals(sessionCaptcha.toUpperCase())) {
                throw new ResultException(ResultEnum.USER_CAPTCHA_ERROR);
            }
            session.removeAttribute("captcha");
        }

        // 1.??????Subject????????????
        Subject subject = SecurityUtils.getSubject();

        // 2.??????????????????
        UserPassOpenIdToken token = new UserPassOpenIdToken(username, password,"0");

        // 3.??????????????????????????????Realm??????
        try {
            // ????????????????????????
            if (rememberMe != null) {
                token.setRememberMe(true);
            } else {
                token.setRememberMe(false);
            }
            subject.login(token);

            // ??????????????????????????????
            User user = ShiroUtil.getSubject();
            if (roleService.existsUserOk(user.getId())) {
                return ResultVoUtil.success("????????????", new URL("/system/main/"));
            } else {
                SecurityUtils.getSubject().logout();
                return ResultVoUtil.error("???????????????????????????");
            }
        } catch (LockedAccountException e) {
            return ResultVoUtil.error("?????????????????????");
        } catch (AuthenticationException e) {
            return ResultVoUtil.error("????????????????????????");
        }
    }

    /**
     * ???????????????
     */
    @GetMapping("/captcha")
    public void captcha(HttpServletRequest request, HttpServletResponse response) throws IOException {
        //???????????????????????????????????????????????????
        response.setHeader("Expires", "-1");
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Pragma", "-1");
        response.setContentType("image/jpeg");

        // ???????????????
        String code = CaptchaUtil.getRandomCode();
        // ?????????????????????session??????????????????
        request.getSession().setAttribute("captcha", code);
        // ?????????web??????
        ImageIO.write(CaptchaUtil.genCaptcha(code), "jpg", response.getOutputStream());
    }

    /**
     * ????????????
     */
    @GetMapping("/logout")
    public String logout() {
        SecurityUtils.getSubject().logout();
        return "redirect:/login";
    }

    /**
     * ??????????????????
     */
    @GetMapping("/noAuth")
    public String noAuth() {
        return "/system/main/noAuth";
    }

    /**
     * ?????????????????????
     */
    @Override
    public String getErrorPath() {
        return "/error";
    }

    /**
     * ??????????????????
     */
    @RequestMapping(value="/error",produces = "text/html")
    public String handleError(Model model, HttpServletRequest request) {
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        String errorMsg = "?????????????????????";
        if (statusCode == 404) {
            errorMsg = "??????????????????????????????????????????~";
        }

        model.addAttribute("statusCode", statusCode);
        model.addAttribute("msg", errorMsg);
        return "/system/main/error";
    }
       /**
 ??????????*?????web?????????????????????????????????json/xml???
 ??????????*/
//     @RequestMapping(value="/error")
//     @ResponseBody
//     public ResultCode errorApiHander(HttpServletRequest request){
//         ServletWebRequest requestAttributes = new ServletWebRequest(request);
//         Map<String, Object> attr=this.errorAttributes.getErrorAttributes(requestAttributes,false);
//         return new ErrorCode(false, (int)attr.get("status"), (String) attr.get("message"));
//     }

        @RequestMapping("/font/list")
        public String b(Model model) {
         //   String fontAwesome = ToolUtil.getFilePathInClassesPath("static/css/plugins/font-awesome-4.7.0/css/font-awesome.min.css");
          //  System.out.print("fontAwesome"+fontAwesome);
            InputStream ins =  this.getClass().getClassLoader().getResourceAsStream("static/css/plugins/font-awesome-4.7.0/css/font-awesome.min.css");
            List<String> a = FontAwesomeUtil.getAllFonts(ins);
            model.addAttribute("fonts", a);
            return "/fonts";
        }

        @RequestMapping("/api/qq")
        public String qqLogin(HttpServletRequest request,HttpServletResponse response,String url,Model model) throws IOException{
            CookieUtils.setCookie(response, "backUrl",url, -1);
            String callbackDomain = ToolUtil.basePath(request).concat("api/qqCallback");
            if (ParamUtil.value("qq_callback")!=null){
                callbackDomain= ParamUtil.value("qq_callback");
            }
            String appId = ParamUtil.value(ParamConst.APP_ID);
            if (appId == null || StringUtils.isEmpty(appId)) {
                model.addAttribute("statusCode", "500");
                model.addAttribute("msg", "APP_ID???????????????o(?????????)n");
                return "/system/main/error";
            } else {
               // response.sendRedirect("https://graph.qq.com/oauth2.0/authorize?response_type=code&client_id=" + appId + "&redirect_uri=" + callbackDomain + "&state=" + System.currentTimeMillis());

               return "redirect:https://graph.qq.com/oauth2.0/authorize?response_type=code&client_id=" + appId + "&redirect_uri=" + callbackDomain + "&state=" + System.currentTimeMillis();
            }
        }


        @RequestMapping("/api/qqCallback")
        public String qqCallback(HttpServletRequest request, HttpServletResponse response, String code,Model model) {
            Cookie cookie = CookieUtils.getCookie(request, "backUrl");
            String url="frontend/succes";
            if (cookie != null) {
                url=cookie.getValue();
            }
            String appId =  ParamUtil.value(ParamConst.APP_ID);
            String appKey = ParamUtil.value(ParamConst.APP_KEY);
            String callbackDomain = ToolUtil.basePath(request).concat("api/qqCallback");
            Map<String, Object> p1 = MapUtil.of("grant_type", "authorization_code");
            p1.put("client_id", appId);
            p1.put("client_secret", appKey);
            p1.put("code", code);
            p1.put("redirect_uri", callbackDomain);

            String resp1 = HttpUtil.get("https://graph.qq.com/oauth2.0/token", p1);
            String accessToken = resp1.substring(13, resp1.length() - 66);
            String callback = HttpUtil.get("https://graph.qq.com/oauth2.0/me", MapUtil.of("access_token", accessToken));
            String openId = callback.substring(45, callback.length() - 6);

            Map<String, Object> p2 = MapUtil.of("access_token", accessToken);
            p2.put("oauth_consumer_key", appId);
            p2.put("openid", openId);

            JSONObject json2 = JSONUtil.parseObj(HttpUtil.get("https://graph.qq.com/user/get_user_info", p2));

            User user = userRepository.findByQqOpenId(openId);
            if (user == null){
                String nickname = json2.getStr("nickname");
                String avatar = json2.getStr("figureurl_qq_2").replace("http://", "https://");
                user = new User();
                user.setNickname(nickname);
                user.setPicture(avatar);
                user.setQqOpenId(openId);
                user.setSalt(ShiroUtil.getRandomSalt());
                long webUserRoleId = context.getApplicationObj(ParamConst.WEBUSER_ROLE_ID);
                user.getRoles().add(Role.builder().id(webUserRoleId).build());
                userRepository.save(user);
            }

            // 1.??????Subject????????????
            Subject subject = SecurityUtils.getSubject();
            // 2.??????????????????
            UserPassOpenIdToken token = new UserPassOpenIdToken(openId, "QQ??????","1");
            // 3.??????????????????????????????Realm??????
            try {
                subject.login(token);

                // ??????????????????????????????  ????????????????????????  ?????????????????? ????????????????????????

                Set<Role> roles = ShiroUtil.getSubjectRoles();
                for (Role role :roles){
                    Set<NBSysResource> sysResources =   role.getMenus();
                    for (NBSysResource sysResource :sysResources){
                        if ("/system/main/index".equals(sysResource.getUrl())) {
                            return "redirect:/system/main/";
                        }
                    }
                }
                if ("".equals(url))
                        url = "/";
                    return "redirect:"+url;
                  //  return ResultVoUtil.error("???????????????????????????");

            } catch (LockedAccountException e) {
              //  return ResultVoUtil.error("?????????????????????");
                model.addAttribute("statusCode", "500");
                model.addAttribute("msg", "?????????????????????");
                return "/system/main/error";
            } catch (AuthenticationException e) {
                model.addAttribute("statusCode", "500");
                model.addAttribute("msg", "????????????????????????");
                return "/system/main/error";
               /// return ResultVoUtil.error("????????????????????????");
            }

        }


}
