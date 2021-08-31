package com.nonelonely.modules.system.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.http.HtmlUtil;
import com.nonelonely.common.data.ArticleQueryBO;
import com.nonelonely.common.data.PageOrder;
import com.nonelonely.common.data.PageSort;
import com.nonelonely.common.enums.StatusEnum;
import com.nonelonely.common.utils.ToolUtil;
import com.nonelonely.modules.system.domain.Article;
import com.nonelonely.modules.system.domain.Cate;
import com.nonelonely.modules.system.domain.NBTagRefer;
import com.nonelonely.modules.system.domain.Tag;
import com.nonelonely.modules.system.repository.ArticleRepository;
import com.nonelonely.modules.system.repository.CateRepository;
import com.nonelonely.modules.system.repository.TagReferRepository;
import com.nonelonely.modules.system.repository.TagRepository;
import com.nonelonely.modules.system.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.*;
import java.util.*;

import static cn.hutool.core.util.RandomUtil.randomInt;
import static java.util.stream.Collectors.toList;

/**
 * @author nonelonely
 * @date 2020/01/01
 */
@Service
public class ArticleServiceImpl implements ArticleService {

    @Autowired
    private ArticleRepository articleRepository;
    @Autowired
    private TagRepository tagRepository;
    @Autowired
    private TagReferRepository tagReferRepository;

    @Autowired
    private CateRepository cateRepository;
    /**
     * 根据ID查询数据
     * @param id 主键ID
     */
    @Override
    @Transactional
    public Article getById(Long id) {
        return articleRepository.findById(id).orElse(null);
    }

    /**
     * 获取分页列表数据
     * @param example 查询实例
     * @return 返回分页数据
     */
    @Override
    public Page<Article> getPageList(Example<Article> example) {

        Map<String, String> orders = new HashMap<>(2);
        orders.put("top", "desc");
        orders.put("updateDate", "desc");
        Sort sort = PageSort.pageSorts(orders);
        PageRequest page = PageSort.pageRequest(sort,10);

        return articleRepository.findAll(example, page);
    }

    /**
     * 保存数据
     * @param article 实体对象
     */
    @Override
    @Transactional
    public Article save(Article article,String tagNames) {


        if (!StringUtils.isEmpty(article.getUrlSequence())) {
            boolean isExistUrl = articleRepository.countByUrlSequence(article.getUrlSequence()) > 0;
            if (isExistUrl) {
                throw new RuntimeException("已存在 url：" + article.getUrlSequence());
            }
        }
        setArticleSummaryAndTxt(article);
        decorateArticle(article);
        articleRepository.save(article);
        tagReferRepository.deleteByReferId(article.getId());
        if(!"".equals(tagNames)) {
            String[] tagNameArray = tagNames.split(",");
            saveTags(article,tagNameArray);
        }
        return article;
    }




    /**
     * 状态(启用，冻结，删除)/批量状态处理
     */
    @Override
    @Transactional
    public Boolean updateStatus(StatusEnum statusEnum, List<Long> idList) {
        return articleRepository.updateStatus(statusEnum.getCode(), idList) > 0;
    }

    /**
     * 保存文章的 tags
     *
     * @param updateArticle
     * @param tagNameArray
     */
    private   void saveTags(Article updateArticle, String[] tagNameArray) {
        int cnt = 0;
        for (String name : tagNameArray) {
            Example<Tag> condition = Example.of(Tag.builder().name(name).build());
            boolean isExist = tagRepository.count(condition) > 0;
            long tagId = isExist ?
                    tagRepository.findByName(name).getId() :
                    tagRepository.save(Tag.builder().name(name).build()).getId();
            tagReferRepository.save(
                    NBTagRefer.builder()
                            .referId(updateArticle.getId())
                            .tagId(tagId)
                            .show(cnt <= 4)
                            .type("1")
                            .build()
            );
            cnt++;
        }
    }

    /**
     * 根据文章内容生成文章摘要
     *
     * @param article
     */
    private static void setArticleSummaryAndTxt(Article article) {
       // ParamService paramService = NBUtils.getBean(ParamService.class);
      //  int summaryLength = Integer.valueOf(paramService.getValueByName(NoteBlogV4.Param.ARTICLE_SUMMARY_WORDS_LENGTH));
        int summaryLength = 200;
        String clearContent = HtmlUtil.cleanHtmlTag(StrUtil.trim(article.getContent()));
        clearContent = StringUtils.trimAllWhitespace(clearContent);
        clearContent = clearContent.substring(0, clearContent.length() < summaryLength ? clearContent.length() : summaryLength);
        int allStandardLength = clearContent.length();
        int fullAngelLength = ToolUtil.fullAngelWords(clearContent);
        int finalLength = allStandardLength - fullAngelLength / 2;
        if (StringUtils.isEmpty(article.getSummary())) {
            article.setSummary(clearContent.substring(0, finalLength < summaryLength ? finalLength : summaryLength));
        }
       // article.setTextContent(clearContent);
    }

    /**
     * 装饰article的一些空值为默认值
     *
     * @param article
     */
    private static void decorateArticle(Article article) {
       // article.setPost(now());
        if (StringUtils.isEmpty(article.getViews())) {
            article.setViews(0);
        }
        if (StringUtils.isEmpty(article.getApproveCnt())) {
            article.setApproveCnt(0);
        }
        if (StringUtils.isEmpty(article.getAppreciable())) {
            article.setAppreciable(false);
        }
        if (StringUtils.isEmpty(article.getCommented())) {
            article.setCommented(false);
        }
        if (StringUtils.isEmpty(article.getTop())) {
            article.setTop(false);
        }
    }

    @Override
    public Page<Article> findBlogArticles(Pageable pageable, ArticleQueryBO articleQueryBO) {

//        if (!StringUtils.isEmpty(articleQueryBO.getTime()))
//            return   findBlogArticlesByTime(pageable,articleQueryBO);
//
//        if (StringUtils.isEmpty(articleQueryBO.getTagSearch())) {
//            String searchStr = articleQueryBO.getSearchStr() == null ? "" : articleQueryBO.getSearchStr();
//            Article prob = Article.builder().content(searchStr)
//                    .title(searchStr).build();
//            if (articleQueryBO.getCateId() != null) {
//                Cate cate = new Cate();
//                cate.setPid(articleQueryBO.getCateId());
//                prob.setCate(cate);
//            }
//            prob.setStatus(StatusEnum.OK.getCode());
//
//            ExampleMatcher matcher = ExampleMatcher.matching()
//                    .withMatcher("title", ExampleMatcher.GenericPropertyMatcher::contains)
//                    .withMatcher("content", ExampleMatcher.GenericPropertyMatcher::contains)
//                    .withMatcher("status", ExampleMatcher.GenericPropertyMatcher::contains)
//                    .withIgnorePaths("views", "approveCnt", "commented", "appreciable", "top","mdContent")
//                    .withIgnoreNullValues();
//            Example<Article> articleExample = Example.of(prob, matcher);
//            //List<NBArticle> pages = articleRepository.findAll(articleExample);
//            Page<Article> page = articleRepository.findAll(articleExample, pageable);
//            List<Article> result = page.getContent();//.stream().filter(article -> !article.getStatus() == StatusEnum.OK.getCode()).collect(toList());
//            return new PageImpl<>(result, pageable, page.getTotalElements());
//        } else {
//            String tag = ToolUtil.stripXSS(URLUtil.decode(articleQueryBO.getTagSearch(), "UTF-8"));
//            long tagId = tagRepository.findByName(tag).getId();
//            List<NBTagRefer> tagRefers = tagReferRepository.findByTagIdAndType(tagId, "article");
//            List<Long> articleIds = tagRefers.stream().map(NBTagRefer::getReferId).distinct().collect(toList());
//            if (CollectionUtils.isEmpty(articleIds)) {
//                return Page.empty(pageable);
//            } else {
//                List<Article> articles = articleRepository.findByIdIn(articleIds, pageable.getPageNumber()*pageable.getPageSize(), pageable.getPageSize());
//                return new PageImpl<>(articles, pageable, articleRepository.countByIdIn(articleIds));
//            }
//        }

        return findBlogArticles2(pageable,articleQueryBO);
    }

    public Page<Article> findBlogArticlesByTime(Pageable pageable, ArticleQueryBO articleQueryBO) {
        List<Article> articles = articleRepository.findByTime(articleQueryBO.getTime(), pageable.getPageNumber()*pageable.getPageSize(), pageable.getPageSize());
        return new PageImpl<>(articles, pageable, articleRepository.countByTime(articleQueryBO.getTime()));
    }

    @Override
    public boolean updateTopById(long articleId, boolean top) {
//        if (top) {
//            int maxTop = articleRepository.findMaxTop();
//            return articleRepository.updateTopById(maxTop + 1, articleId) == 1;
//        } else {
//            boolean currentTop = articleRepository.getOne(articleId).getTop();
//            articleRepository.updateTopsByTop(currentTop);
//            return articleRepository.updateTopById(0, articleId) == 1;
//        }
        return  articleRepository.updateTopById(top, articleId) == 1;
    }

    /**
     * 更新 commented 状态
     *
     * @param commented
     * @param id
     * @return
     */


   public int updateCommentedById(boolean commented, long id){
       return articleRepository.updateCommentedById(commented, id);
    }
    private Page<Article>  findBlogArticles2(Pageable pageable, ArticleQueryBO articleQueryBO) {

        /**
         * 自定义查询条件
         *      1.实现Specification接口（提供泛型：查询的对象类型）
         *      2.实现toPredicate方法（构造查询条件）
         *      3.需要借助方法参数中的两个参数（
         *          root：获取需要查询的对象属性
         *          CriteriaBuilder：构造查询条件的，内部封装了很多的查询条件（模糊匹配，精准匹配）
         *       ）
         */
        //查询满足文章的id
        Set<Long> articleIdss = new HashSet<>();
        if (!StringUtils.isEmpty(articleQueryBO.getTagSearch())) {
            String tag = ToolUtil.stripXSS(URLUtil.decode(articleQueryBO.getTagSearch(), "UTF-8"));
            long tagId = tagRepository.findByName(tag).getId();
            List<NBTagRefer> tagRefers = tagReferRepository.findByTagIdAndType(tagId, "article");
            tagRefers.forEach(tagRefer->
                articleIdss.add(tagRefer.getReferId())
            );
        }
        List<Long> cateIds = new ArrayList<>();
        //查询分类的本级及下级
        if (!StringUtils.isEmpty(articleQueryBO.getCateId())) {
            List<Cate> cates = cateRepository.findAll();

            cateIds.add(articleQueryBO.getCateId());
            findCateIdGroup(cates,articleQueryBO.getCateId(),cateIds);
        }
        Specification<Article> spec = new Specification<Article>() {
            @Override
            public Predicate toPredicate(Root<Article> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> list = new ArrayList<Predicate>();
                if (cateIds.size()>0) {
                    CriteriaBuilder.In<Object> in = cb.in(root.get("cate"));
                    for (int i = 0; i < cateIds.size(); i++) {
                        Cate cate = new Cate();
                        cate.setId(cateIds.get(i));
                        in.value(cate);
                    }
                    list.add(in);
                }
                if (articleIdss.size()>0) {
                    CriteriaBuilder.In<Object> in = cb.in(root.get("id"));
                    for (Long str : articleIdss) {
                        in.value(str);
                    }
                    list.add(in);
                }
                CriteriaBuilder.In<Object> ins = cb.in(root.get("status"));
                ins.value(1L);
                list.add(ins);
                return cb.and(list.toArray(new Predicate[list.size()]));
            }
        };
        Page<Article> customer = articleRepository.findAll(spec,pageable);
       return customer;
    }

    /**
     *  获取分类的本级及下级
     * @param cateId
     * @return
     */

    private void findCateIdGroup( List<Cate> cates ,Long  cateId ,List arrayList){
        cates.forEach(cate -> {
             if (cate.getPid() == cateId){
                 arrayList.add(cate.getId());
                 findCateIdGroup(cates,cate.getId(),arrayList);
             }
        });

    }

    /**
     * 更新浏览量
     * @param id
     */
    public void updateViewsById(Long id){
        articleRepository.updateViewsById(id);
    }
}
