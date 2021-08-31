package com.nonelonely.frontend.controller;



import com.nonelonely.common.data.URL;
import com.nonelonely.common.enums.StatusEnum;
import com.nonelonely.common.utils.EntityBeanUtil;
import com.nonelonely.common.utils.ResultVoUtil;
import com.nonelonely.common.utils.ToolUtil;
import com.nonelonely.common.vo.ResultVo;
import com.nonelonely.component.shiro.ShiroUtil;
import com.nonelonely.frontend.util.CommentTree;
import com.nonelonely.modules.system.domain.*;

import com.nonelonely.modules.system.repository.*;
import com.nonelonely.modules.system.service.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.context.Context;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author nonelonely
 * @date 2020/02/13
 */
@Controller("frontAboutController")
@RequestMapping("/frontend")
public class AboutController {

    @Autowired
    private AboutRepository aboutRepository;
    @Autowired
    private LinkRepository linkRepository;
    @Autowired
    private ArtRepository artRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private UserService userService;

    @Autowired
    private UvService uvService;

    @Autowired
    private UvRepository uvRepository;

    /**
     * 列表页面
     */
    @GetMapping("/about")
    public String index(Model model, About about) {

        // 创建匹配器，进行动态查询匹配
        ExampleMatcher matcher = ExampleMatcher.matching();
        // 获取数据列表
        about.setStatus(StatusEnum.OK.getCode());
        Example<About> example = Example.of(about, matcher);

        Sort sort = Sort.by(Sort.Order.desc("createDate"));

        // 封装数据
        model.addAttribute("lists", aboutRepository.findAll(example, sort));

        return "/About";
    }
    /**
     * 制作者页面
     */
    @GetMapping("/author")
    public String author(Model model, Art art) {

        // 创建匹配器，进行动态查询匹配
        ExampleMatcher matcher = ExampleMatcher.matching();
        // 获取数据列表
        art.setStatus(StatusEnum.OK.getCode());
        Example<Art> example = Example.of(art, matcher);
//        Page<Art> list = artService.getPageList(example);

        // 封装数据
        model.addAttribute("lists", artRepository.findAll(example));

        return "/Author";
    }
    /**
     * 留言
     */
    @GetMapping("/message")
    public String message(Model model, Comment comment) {

        // 创建匹配器，进行动态查询匹配
        ExampleMatcher matcher = ExampleMatcher.matching();
        comment.setType(1);
        comment.setStatus(StatusEnum.OK.getCode());
        // 获取数据列表
        Example<Comment> example = Example.of(comment, matcher);

        List<Comment> dataNodes = commentRepository.findAll(example);

        model.addAttribute("comments", CommentTree.buildByRecursive(dataNodes,commentRepository));

        return "/MessageBoard";
    }
    /**
     * 制作者页面
     */
    @GetMapping("/link")
    public String link(Model model, Link link) {

        // 创建匹配器，进行动态查询匹配
        ExampleMatcher matcher = ExampleMatcher.matching();
        // 获取数据列表
        link.setStatus(StatusEnum.OK.getCode());
        Example<Link> example = Example.of(link, matcher);
     //   Page<Link> list = linkService.(example);

        // 封装数据
        model.addAttribute("lists", linkRepository.findAll(example));

        return "/FriendlyLink";
    }
    /**
     * 保存修改个人信息
     */
    @PostMapping("/userInfo")
    @ResponseBody
    public ResultVo userInfo(User user, String emailCode, HttpServletRequest request){

        String em = (String) request.getSession().getAttribute("emailCode");
        String emailCodeTo = (String) request.getSession().getAttribute("emailCodeTo");
        if (StringUtils.isEmpty(emailCode)&&StringUtils.isEmpty(user.getEmail())){
            return ResultVoUtil.error("验证码错误!");
        }
       if (!StringUtils.isEmpty(emailCode)&&StringUtils.isEmpty(em)){
           return ResultVoUtil.error("验证码过期了，重新生成");
       }
       if (!StringUtils.isEmpty(emailCode)&&!emailCode.equals(em)){
           return ResultVoUtil.error("验证码错误!");
       }
       if (!StringUtils.isEmpty(emailCodeTo)&&!emailCodeTo.equals(user.getEmail())){
           return ResultVoUtil.error("验证码与邮箱不匹配！");
       }

        // 复制保留无需修改的数据
        User subUser = ShiroUtil.getSubject();
        String[] ignores = {"id", "username", "password", "salt", "picture", "dept", "roles","qqOpenId"};
        EntityBeanUtil.copyPropertiesIgnores(user, subUser, ignores);

        // 保存数据
        userService.save(subUser);
        return ResultVoUtil.success("保存成功", new URL("/frontend/userInfo"));
    }

    /**
     * 跳转到个人信息页面
     */
    @GetMapping("/userInfo")
    public String toUserInfo(Model model){
        User user = ShiroUtil.getSubject();
        if (user.getQqOpenId() !=null&&user.getUsername() == null){
            user.setUsername("QQ账户登录");
        }
        model.addAttribute("user", user);
        model.addAttribute("startTime", new Date().getTime());
        return "/userInfo";
    }
    /**
     * 跳转到个人信息页面
     */
    @GetMapping("/dolphi")
    public String dolphi(Model model){

        return "/delphis/index";
    }

    @RequestMapping("/putUv")
    @ResponseBody
    public  String putUv(HttpServletRequest request,HttpSession session,Uv uv){

//        List<Uv> uvs= uvRepository.findAll();
//        for (Uv uv1:uvs){
//            uv1.setLocation(ToolUtil.getIpInfo(uv1.getIp()));
//            uvService.save(uv1);
//        }
        uv.setSessionId(session.getId());
        uv.setIp(ToolUtil.getRemoteAddress(request));
        uv.setLocation(ToolUtil.getIpInfo(uv.getIp()));
        String st =  request.getHeader("user-agent");
        uv.setSystem(ToolUtil.detectOS(st));
        uv.setBrowser(ToolUtil.browser(st).getName()+"("+ToolUtil.browser(st).getVersion(st)+")");
        uvService.save(uv);

        ServletContext ctx = session.getServletContext( );
        Map uvMap =new HashMap();
        ctx.setAttribute("uvMap", uvMap);
        uvMap.put(session.getId(),uv.getId());


        return String.valueOf(uv.getId());
    }
    @RequestMapping("/putUvEnd")
    @ResponseBody
    public  void putUvEnd(HttpServletRequest request,Uv uv){
        // 复制保留无需修改的数据
        if (uv.getId() != null) {
            Uv beUv = uvService.getById(uv.getId());
            beUv.setUpdateDate(new Date());
            //EntityBeanUtil.copyProperties(beUv, uv);
            uvService.save(beUv);
        }
       // request.getSession().invalidate();
    }
    @RequestMapping("/mapCity")
    public String cityMap(Model model){

        model.addAttribute("cityMap",uvRepository.findMapCity());

        return "/delphis/mapCity";
    }

}
