package com.nonelonely.frontend.controller;


import com.nonelonely.common.data.ArticleQueryBO;
import com.nonelonely.common.data.PageOrder;
import com.nonelonely.common.data.PageSort;
import com.nonelonely.common.data.Pagination;
import com.nonelonely.common.enums.StatusEnum;
import com.nonelonely.common.exception.ArticleFetchFailedException;
import com.nonelonely.common.utils.ResultVoUtil;

import com.nonelonely.common.vo.ResultVo;
import com.nonelonely.frontend.util.CommentTree;

import com.nonelonely.modules.system.domain.*;

import com.nonelonely.modules.system.repository.*;
import com.nonelonely.modules.system.service.ArticleService;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;


@Controller("frontArticleController")
@RequestMapping({"/article", "/a"})
public class ArticleController {


    private final ArticleRepository articleRepository;
    private final TagRepository tagRepository;
    private final RankingRepository rankingRepository;

    private final CateRepository cateRepository;
    private  final CommentRepository commentRepository;
    @Autowired
    private  ArticleService articleService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CarouselRepository carouselRepository;
    @Autowired
    public ArticleController(ArticleRepository articleRepository,
                             TagRepository tagRepository,
                             RankingRepository rankingRepository, CateRepository cateRepository, CommentRepository commentRepository) {
        this.articleRepository = articleRepository;
        this.tagRepository = tagRepository;
        this.rankingRepository = rankingRepository;
        this.cateRepository = cateRepository;
        this.commentRepository=commentRepository;
    }

    @RequestMapping("/{aId}")
    public String article(@PathVariable("aId") Long aId, Model model) {
        articleRepository.updateViewsById(aId);
    //    List<NBTag> list= tagRepository.findArticleTags(aId, true);
        Optional<Article> fetchArticle = articleRepository.findById(aId);
        Article article = fetchArticle.orElseThrow(() -> new ArticleFetchFailedException("未找到相关文章！"));

        //计算点击量

        Ranking ranking = rankingRepository.countContentIdandDate(aId);
        if(ranking==null || ranking.getId() == 0){
            ranking = new Ranking();
            ranking.setHits(1);
            ranking.setContentId(aId);

        }else{
            ranking.setHits(ranking.getHits()+1);
        }
        rankingRepository.save(ranking);

        List<Comment> dataNodes = commentRepository.findAllByArticleAndStatus(article, StatusEnum.OK.getCode());
        Map beforarticle = articleRepository.findbeforeArticles(article.getId());
        Map afterarticle = articleRepository.findafterArticles(article.getId());
        Long id = article.getCate().getPid() ;
        if (null == id || 0==id){
            id = article.getCate().getId();
        }
        Cate cate = cateRepository.findById(id).get();
        model.addAttribute("article", article);
        model.addAttribute("beforarticle", beforarticle);
        model.addAttribute("afterarticle", afterarticle);
        model.addAttribute("commentCount", dataNodes.size());
        model.addAttribute("carouselList", carouselRepository.findCarouselList());
       // model.addAttribute("aboutarticle",glist);
        model.addAttribute("cate",cate);
        model.addAttribute("comments", CommentTree.buildByRecursive(dataNodes,commentRepository));

        //normal型页面
       // model.addAttribute("cateList", cateRepository.findAll());
        return "/ArticleDetails";
    }

//    @RequestMapping("/u/{urlSeq}")
//    public String articleByUrl(@PathVariable("urlSeq") String urlSeq, Model model, Pagination<NBComment> pagination, CommentQueryBO commentQueryBO) {
//        articleRepository.updateViewsBySeq(urlSeq);
//        Optional<NBArticle> fetchArticle = articleRepository.findNBArticleByUrlSequence(urlSeq);
//        NBArticle article = fetchArticle.orElseThrow(() -> new ArticleFetchFailedException("未找到相关文章！"));
//        model.addAttribute("article", article);
//        model.addAttribute("countMsg", commentService.countByUseredIdandreade());
//        model.addAttribute("tags", tagRepository.findArticleTags(article.getId(), true));
//        model.addAttribute("author", userRepository.getOne(article.getAuthorId()).getNickname());
//        commentQueryBO.setArticleId(article.getId());
//        pagination.setLimit(10);
//
//            model.addAttribute("comments", commentService.findPageInfo(getPageable(pagination), commentQueryBO));
//            model.addAttribute("similarArticles", articleRepository.findSimilarArticles(article.getCateId(), 6));
//
//        return "frontend/content/article";
//    }

//    @RequestMapping(value = "/comments", method = RequestMethod.POST)
//    @ResponseBody
//    public Page<NBComment> comments(Pagination<NBComment> pagination, CommentQueryBO commentQueryBO) {
//        return commentService.findPageInfo(getPageable(pagination), commentQueryBO);
//    }
//
    @PostMapping("/approve")
    @ResponseBody
    public ResultVo approve(@RequestParam Long articleId) {
        return ResultVoUtil.ajaxDone(
                () -> articleRepository.updateApproveCntById(articleId) == 1,
                () -> "点赞"
        );
    }

    @RequestMapping(value = {"", "/index","/pages"})
    public String pageArticle(Pagination<Article> pagination, ArticleQueryBO articleQueryBO, Model model) {
     //  model.addAttribute("randomArticles", articleRepository.findRandArticles(10));
//        model.addAttribute("javaArticles", articleRepository.findSimilarArticles(9,10));
//        model.addAttribute("zhiyuanArticles", articleRepository.findSimilarArticles(10,10));
 //       model.addAttribute("guidang",articleRepository.findArticleGroupByTime());
  //      model.addAttribute("tagList", tagService.findTagsTab(15));
        model.addAttribute("curCateId",articleQueryBO.getCateId());
        List<Cate> cates= cateRepository.findByPidAndIsShow(0,true);
        model.addAttribute("cateList", cates);
//        cates.forEach(cate -> {
//            int n =articleRepository.countByCate(cate);
//            cate.setName(String.valueOf(n));
//        });
        model.addAttribute("carouselList", carouselRepository.findCarouselList());
        model.addAttribute("zong", articleRepository.findLotArticles(10));
        //获取周排名
        model.addAttribute("weekList",rankingRepository.selectWeekList(10));
        //获取月排名
        model.addAttribute("monthList",rankingRepository.selectMonthList(10));
        model.addAttribute("articlesList", getArticle(pagination,articleQueryBO));
 //       model.addAttribute("countMsg", commentService.countByUseredIdandreade());
        PageRequest page = PageSort.pageRequest(12,"createDate", Sort.Direction.DESC);
        model.addAttribute("users", userRepository.findAll(page).getContent());
        return "/Article";
    }


    private Map<String, Object> getArticle(Pagination<Article> pagination, ArticleQueryBO articleQueryBO)
    {

        Map<String, String> orders = new HashMap<>(2);
        orders.put("top", "desc");
        orders.put("updateDate", "desc");
        Sort sort = PageOrder.getJpaSortWithOther(pagination, orders);
        Pageable pageable = PageRequest.of(pagination.getPage() - 1, 30, sort);

        Page<Article> page = articleService.findBlogArticles(pageable, articleQueryBO);
        Map<Long, List<Tag>> articleTagsMap = page.getContent().stream()
                .collect(toMap(
                        Article::getId,
                        article -> tagRepository.findArticleTags(article.getId(), true)
                ));
        Map<String, Object> resultMap = new HashMap<>(5);

        resultMap.put("pageArticle", page.getContent());

      //  resultMap.put("articleComments", commentCounts);
       // resultMap.put("articleAuthors", articleAuthorNames);
        resultMap.put("articleTagsMap", articleTagsMap);

      //  int currentPage=pagination.getPage();

        int totalPages=page.getTotalPages();
        resultMap.put("totalPages", totalPages);
       // resultMap.put("currentPage", (currentPage-1)*5);
      // String url="/article/pages?page={page}";
        String url = "";
        if (articleQueryBO.getCateId()!=null)
            url=url+"&cateId="+articleQueryBO.getCateId();
        if (articleQueryBO.getSearchStr()!=null)
            url=url+"&searchStr="+articleQueryBO.getSearchStr();
        if (articleQueryBO.getTagSearch()!=null)
            url=url+"&tagSearch="+articleQueryBO.getTagSearch();
        if (articleQueryBO.getTime()!=null)
            url=url+"&time="+articleQueryBO.getTime();
     //   String str= laypage.getPage(currentPage,totalPages,url);

       // resultMap.put("layPage", str);
        resultMap.put("url", url);
        return  resultMap;

    }

    @RequestMapping("/cateMenu")
    @ResponseBody
    public Object cateMenu(@RequestParam Long articleId){
        List<Map<String,Object>> aboutarticle = articleRepository.findByCateId(articleId);

        Map<String, List<Map<String, Object>>> glist = aboutarticle.stream().collect(Collectors.groupingBy(e -> e.get("name").toString()));

        return  glist;
    }


    @RequestMapping(value = {"/ajaxPages"})
    @ResponseBody
    public ResultVo ajaxPageArticle(Pagination<Article> pagination, ArticleQueryBO articleQueryBO, Model model) {
        if (pagination.getPage() == 1){
            return ResultVoUtil.success(new ArrayList<>());
        }
        Map<String, String> orders = new HashMap<>(2);
        orders.put("top", "desc");
        orders.put("updateDate", "desc");
        Sort sort = PageOrder.getJpaSortWithOther(pagination, orders);
        Pageable pageable = PageRequest.of(pagination.getPage() - 1, pagination.getPageSize(), sort);

        Page<Article> page = articleService.findBlogArticles(pageable, articleQueryBO);


        return ResultVoUtil.success(page.getContent());
    }

}
