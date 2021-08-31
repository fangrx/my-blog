package com.nonelonely.admin.system.controller;

import com.nonelonely.admin.system.validator.ArticleValid;
import com.nonelonely.common.enums.StatusEnum;
import com.nonelonely.common.utils.EntityBeanUtil;
import com.nonelonely.common.utils.ResultVoUtil;
import com.nonelonely.common.utils.StatusUtil;
import com.nonelonely.common.utils.ToolUtil;
import com.nonelonely.common.vo.ResultVo;
import com.nonelonely.component.fileUpload.util.HandleHtmlImgUtil;
import com.nonelonely.modules.system.domain.Article;
import com.nonelonely.modules.system.domain.WebSite;
import com.nonelonely.modules.system.domain.permission.NBAuth;
import com.nonelonely.modules.system.repository.CateRepository;
import com.nonelonely.modules.system.repository.WebSiteRepository;
import com.nonelonely.modules.system.service.ArticleService;
import com.nonelonely.modules.system.service.UploadService;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.List;

import static com.nonelonely.modules.system.domain.permission.NBAuth.Group.AJAX;
import static com.nonelonely.modules.system.domain.permission.NBAuth.Group.ROUTER;
import static com.nonelonely.modules.system.domain.permission.NBSysResource.ResType.NAV_LINK;
import static com.nonelonely.modules.system.domain.permission.NBSysResource.ResType.OTHER;

/**
 * @author nonelonely
 * @date 2020/01/01
 */
@Controller
@RequestMapping("/system/article")
public class ArticleController {

    @Autowired
    private ArticleService articleService;
    @Autowired
    private CateRepository cateRepository;
    @Autowired
    private UploadService uploadService;
    @Autowired
    private WebSiteRepository webSiteRepository;

    /**
     * 列表页面
     */
    @GetMapping("/index")
    @RequiresPermissions("system:article:index")
    @NBAuth(value = "system:article:index", remark = "博文列表页面", type = OTHER, group = ROUTER)
    public String index(Model model, Article article, HttpServletRequest request) {

        // 创建匹配器，进行动态查询匹配
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withMatcher("title", match -> match.contains())
               // .withMatcher("content", match -> match.contains());

                 .withIgnorePaths("mdContent");

        // 获取数据列表
        Example<Article> example = Example.of(article, matcher);
        Page<Article> list = articleService.getPageList(example);
//        list.getContent().forEach(x->{
//
//            x.setSummary(ToolUtil.getBaiDu(x.getId()+"",request));
//
//        });
        // 封装数据
        model.addAttribute("list", list.getContent());
        model.addAttribute("article", article);
        model.addAttribute("page", list);
        return "/system/article/index";

    }

    /**
     * 跳转到添加页面
     */
    @GetMapping("/add")
    @RequiresPermissions("system:article:add")
    @NBAuth(value = "system:article:add", remark = "博文添加页面", type = NAV_LINK, group = ROUTER)
    public String toAdd(Model model) {
        model.addAttribute("article", new Article());
        model.addAttribute("cateList", cateRepository.findAll());
        return "/system/article/add";
    }
    /**
     * 跳转到添加页面
     */
    @GetMapping("/addMarkDown")
    @RequiresPermissions("system:article:add")
    @NBAuth(value = "system:article:addMarkDown", remark = "markdown添加页面", type = NAV_LINK, group = ROUTER)
    public String toAddMarkDown(Model model) {
        model.addAttribute("article", new Article());
        model.addAttribute("cateList", cateRepository.findAll());
        return "/system/article/markdown";
    }
    /**
     * 跳转到编辑页面
     */
    @GetMapping("/edit/{id}")
    @RequiresPermissions("system:article:edit")
    @NBAuth(value = "system:article:edit", remark = "博文编辑页面", type = NAV_LINK, group = ROUTER)
    public String toEdit(@PathVariable("id") Article article, Model model) {
        model.addAttribute("article", article);
       // model.addAttribute("cateList", cateRepository.findAll());
        return "/system/article/add";
    }

    /**
     * 保存添加/修改的数据
     * @param valid 验证对象
     */
    @PostMapping("/save")
    @RequiresPermissions({"system:article:add", "system:article:edit"})
    @ResponseBody

    public ResultVo save(@Validated ArticleValid valid,    Article article,String tagNames,HttpServletRequest request) {
        if (article.getId() != null) {
            Article beArticle = articleService.getById(article.getId());
            EntityBeanUtil.copyProperties(beArticle, article);
        }
       HandleHtmlImgUtil.transHtml(article,request,uploadService);

        // 保存数据
        articleService.save(article,tagNames);
        WebSite webSite = webSiteRepository.findByContentId(article.getId());
        if (webSite == null) {
            webSite = new WebSite();
            webSite.setContentId(article.getId());
            webSiteRepository.save(webSite);
        }


        return ResultVoUtil.SAVE_SUCCESS;
    }

    /**
     * 跳转到详细页面
     */
    @GetMapping("/detail/{id}")
    @RequiresPermissions("system:article:detail")
    @NBAuth(value = "system:article:detail", remark = "博文详细页面", type = NAV_LINK, group = ROUTER)
    public String toDetail(@PathVariable("id") Article article, Model model) {
        model.addAttribute("article",article);
        return "/system/article/detail";
    }

    /**
     * 设置一条或者多条数据的状态
     */
    @RequestMapping("/status/{param}")
    @RequiresPermissions("system:article:status")
    @ResponseBody
    @NBAuth(value = "system:article:status", remark = "博文数据状态", type = NAV_LINK, group = ROUTER)
    public ResultVo status(
            @PathVariable("param") String param,
            @RequestParam(value = "ids", required = false) List<Long> ids) {
        // 更新状态
        StatusEnum statusEnum = StatusUtil.getStatusEnum(param);
        if (articleService.updateStatus(statusEnum, ids)) {
            return ResultVoUtil.success(statusEnum.getMessage() + "成功");
        } else {
            return ResultVoUtil.error(statusEnum.getMessage() + "失败，请重新操作");
        }
    }

    @RequestMapping("/update/top/{id}")
    @ResponseBody
    @NBAuth(value = "system:article:update_top", remark = "修改文章的置顶状态", group = AJAX)
    @RequiresPermissions("system:article:update_top")
    public ResultVo top(@PathVariable("id") Long id, Boolean top) {
        if (articleService.updateTopById(id, top)) {
            return ResultVoUtil.success( "修改置顶状态成功");
        } else {
            return ResultVoUtil.error("修改置顶状态失败，请重新操作");
        }
    }

    @RequestMapping("/update/commented/{id}")
    @ResponseBody
    @NBAuth(value = "management:article:update_commented", remark = "修改文章的可评论状态", group = AJAX)
    @RequiresPermissions("management:article:update_commented")
    public ResultVo commented(@PathVariable("id") Long id, Boolean commented) {

        if (articleService.updateCommentedById(commented, id) == 1) {
            return ResultVoUtil.success( "修改可评论状态成功");
        } else {
            return ResultVoUtil.error("修改可评论状态失败，请重新操作");
        }
    }
}
