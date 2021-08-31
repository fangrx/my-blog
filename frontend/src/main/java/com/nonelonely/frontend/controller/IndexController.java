package com.nonelonely.frontend.controller;


import com.nonelonely.common.data.PageSort;

import com.nonelonely.modules.system.domain.*;
import com.nonelonely.modules.system.repository.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.RequestMapping;


import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * created by Wuwenbin on 2018/7/31 at 21:33
 *
 * @author wuwenbin
 */
@Controller
public class IndexController  {

    @Autowired
    private WordsRepository wordsRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ArticleRepository articleRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    UvRepository uvRepository;
    @Autowired
    ParamRepository paramRepository;

     @RequestMapping(value = {"", "/index"})
    public String index(Model model) {

        // 创建分页对象  查询热门的  数量为6
        // 创建分页对象

        // 创建匹配器，进行动态查询匹配
        ExampleMatcher matcher = ExampleMatcher.matching();


        Map<String, String> orders = new HashMap<>(2);
        orders.put("top", "desc");
        orders.put("views", "desc");

        Sort sort = PageSort.pageSorts(orders);
        PageRequest page = PageSort.pageRequest(sort,6);

        // 封装数据 获取热门的博客数据
        List<Article> lists=articleRepository.findAll(page).getContent();
        model.addAttribute("list", lists);

      // 创建分页对象  查询热门的  数量为6
        // 创建匹配器，进行动态查询匹配
         matcher = ExampleMatcher.matching();
        // 获取评论数据列表
        Comment comment = new Comment();
        comment.setParentId(0L);
        Example<Comment> exampleComment = Example.of(comment, matcher);
        page = PageSort.pageRequest(10,"createDate", Sort.Direction.DESC);
        model.addAttribute("messages", commentRepository.findAll(exampleComment,page).getContent());
        //总评论的数量
        model.addAttribute("countComment", commentRepository.countComment());
        //最近登录人数据  30个
        page = PageSort.pageRequest(30,"createDate", Sort.Direction.DESC);
        model.addAttribute("users", userRepository.findAll(page).getContent());


        model.addAttribute("words", wordsRepository.findRandWords());
        //浏览总数
        Param param =paramRepository.findFirstByName("visitor_counts");
        int newCount= uvRepository.findCountBefore();
        model.addAttribute("visitor",Integer.valueOf(param.getValue())+newCount);
        return "/Indexblog";
    }

}
