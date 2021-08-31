package com.nonelonely.modules.system.service;

import com.nonelonely.common.data.ArticleQueryBO;
import com.nonelonely.common.enums.StatusEnum;
import com.nonelonely.modules.system.domain.Article;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author nonelonely
 * @date 2020/01/01
 */
public interface ArticleService {

    /**
     * 获取分页列表数据
     * @param example 查询实例
     * @return 返回分页数据
     */
    Page<Article> getPageList(Example<Article> example);

    /**
     * 根据ID查询数据
     * @param id 主键ID
     */
    Article getById(Long id);

    /**
     * 保存数据
     * @param article 实体对象
     */
    Article save(Article article, String tagNames);

    /**
     * 状态(启用，冻结，删除)/批量状态处理
     */
    @Transactional
    Boolean updateStatus(StatusEnum statusEnum, List<Long> idList);
    /**
     * 前端博客页面的文章分页信息
     *
     * @param pageable
     * @param articleQueryBO
     * @return
     */
    Page<Article> findBlogArticles(Pageable pageable, ArticleQueryBO articleQueryBO);
    /**
     * 修改文章的 top 值
     *
     * @param articleId
     * @param top
     * @return
     * @throws Exception
     */
    boolean updateTopById(long articleId, boolean top);

    int updateCommentedById(boolean commented, long id);

    /**
     * 更新浏览量
     * @param id
     */
    void updateViewsById(Long id);
}
