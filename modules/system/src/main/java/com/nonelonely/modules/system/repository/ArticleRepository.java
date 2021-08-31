package com.nonelonely.modules.system.repository;

import com.nonelonely.modules.system.domain.Article;
import com.nonelonely.modules.system.domain.Cate;
import com.nonelonely.modules.system.domain.bo.CateArticle;
import com.nonelonely.modules.system.repository.BaseRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author nonelonely
 * @date 2020/01/01
 */
public interface ArticleRepository extends BaseRepository<Article, Long> {
    /**
     * 查询是否已存在自定义的url文章
     *
     * @param urlSequence
     * @return
     */
    long countByUrlSequence(String urlSequence);
    /**
     * 查询是否已存在来源的文章
     *
     * @param fromUrls
     * @return
     */
    long countByFromUrls(String fromUrls);
    /**
     * 更新 appreciable 状态
     *
     * @param appreciable
     * @param id
     * @return
     */
    @Modifying
    @Query("update Article a set a.appreciable = ?1 where a.id = ?2")
    @Transactional(rollbackOn = Exception.class)
    int updateAppreciableById(boolean appreciable, long id);

    /**
     * 更新 commented 状态
     *
     * @param commented
     * @param id
     * @return
     */
    @Modifying
    @Query("update Article a set a.commented = ?1 where a.id = ?2")
    @Transactional(rollbackOn = Exception.class)
    int updateCommentedById(boolean commented, long id);

    /**
     * 更新 top 状态
     *
     * @param top
     * @param id
     * @return
     */
    @Modifying
    @Query("update Article a set a.top = ?1 where a.id = ?2")
    @Transactional(rollbackOn = Exception.class)
    int updateTopById(boolean top, long id);

    /**
     * 处理改动 所有 top 对应值
     *
     * @param currentTop
     * @return
     */
    @Modifying
    @Query("update Article a set a.top = a.top - 1 where a.top > ?1")
    @Transactional(rollbackOn = Exception.class)
    void updateTopsByTop(int currentTop);

    /**
     * 查找某个分类下文章的个数
     *
     * @param cateId
     * @return
     */
    long countByCateId(long cateId);

    /**
     * 查找出最大的 top 值
     *
     * @return
     */
    @Query(nativeQuery = true, value = "SELECT MAX(top)FROM or_article where status = 1")
    int findMaxTop();

    /**
     * 根据文章自定义链接查找文章对象
     *
     * @param urlSeq
     * @return
     */
    Optional<Article> findNBArticleByUrlSequence(String urlSeq);

    /**
     * 查找相似的文章
     *
     * @param cateId
     * @param limit
     * @return
     */
    @Query(nativeQuery = true, value = "SELECT * FROM or_article WHERE cate_id = ?1 ORDER BY rand() LIMIT ?2")
    List<Article> findSimilarArticles(long cateId, int limit);



     /**
     * 随机文章
     *
     * @param limit
     * @return
     */
    @Query(nativeQuery = true, value = "SELECT * FROM or_article  where status = 1 ORDER BY rand() LIMIT ?1")
    List<Article> findRandArticles(int limit);
    /**
     * 上一条文章的文章
     *
     * @param id
     * @return
     */
    @Query(nativeQuery = true, value = "select id,title from or_article where status = 1 and id = (select id from or_article where id < ?1 order by id desc limit 1)")
    Map findbeforeArticles(long id);
    /**
     * 下一条文章的文章
     *
     * @param id
     * @return
     */
    @Query(nativeQuery = true, value = "select id,title from or_article where status = 1 and id = (select id from or_article where id > ?1 order by id asc limit 1)")
    Map findafterArticles(long id);
    /**
     * 更新文章点赞数
     *
     * @param articleId
     * @return
     * @throws Exception
     */
    @Query(nativeQuery = true, value = "UPDATE or_article SET approve_cnt = approve_cnt + 1 WHERE id = ?1")
    @Modifying
    @Transactional(rollbackOn = Exception.class)
    int updateApproveCntById(long articleId);

    /**
     * 更新浏览量
     *
     * @param articleId
     * @return
     * @throws Exception
     */
    @Query("UPDATE Article a SET a.views = a.views + 1 WHERE a.id = ?1")
    @Modifying
    @Transactional(rollbackOn = Exception.class)
    void updateViewsById(long articleId);


    /**
     * 更新浏览量
     *
     * @param urlSeq
     * @return
     * @throws Exception
     */
    @Query("UPDATE Article a SET a.views = a.views + 1 WHERE a.urlSequence = ?1")
    @Modifying
    @Transactional(rollbackOn = Exception.class)
    void updateViewsBySeq(String urlSeq);

    /**
     * 随机n篇文章
     *
     * @param limit
     * @return
     */
    @Query(nativeQuery = true, value = "select * from or_article WHERE draft != 1 ORDER BY rand() LIMIT ?1 ")
    List<Article> findRandomArticles(int limit);

    /**
     * 查找在此id集合中的文章集合对象
     *
     * @param articleIds
     * @param start
     * @param pageSize
     * @return
     */
    @Query(nativeQuery = true, value = "select * from or_article where id in (?1) limit ?2,?3")
    List<Article> findByIdIn(List<Long> articleIds, int start, int pageSize);

    @Query(nativeQuery = true, value = "select * from or_article a where DATE_FORMAT(a.create_date,\"%Y-%m\")=?1 limit ?2,?3")
    List<Article> findByTime(String time, int start, int pageSize);

    @Query(nativeQuery = true, value = "select count(*)from or_article a where DATE_FORMAT(a.create_date,\"%Y-%m\")=?1 ")
    int countByTime(String time);

    @Query(nativeQuery = true, value = "select count(*)from or_article where id in (?1) ")
    int countByIdIn(List<Long> articleIds);

    @Query(nativeQuery = true, value = "select count(*)from or_article where status = 1 and ( cate_refer_id in (select id from or_cate where pid = ?1) or  cate_refer_id = ?1)")
    int countByCate(Cate cate);

    @Query(nativeQuery = true, value = "select * from or_article a  where status = 1 ORDER BY a.views desc LIMIT ?1")
    List<Article> findLotArticles(int limit);
    /**
     * 查找相似的文章
     *
     * @param cateId
     * @param
     * @return
     */
    @Query(nativeQuery = true, value = "select id,title,xtitle,(select cn_name from or_cate where id = a.cate_refer_id) name  from or_article a where status = 1 and ( cate_refer_id  in (select id from or_cate where pid = ?1) or  cate_refer_id = ?1 ) order by xtitle")
    List<Map<String,Object>> findByCateId(long cateId);

    @Query(nativeQuery = true, value = "insert  into  or_article (id,content,title,remark,from_urls)value (?1,?2,?3,?4,?5)")
    int saveArticle(long a, String b, String c, String d, String f);

    /***
     * @title 获取置顶文章
     * @description [limit]
     * @author nonelonely
     * @updateTime 2020/11/11 20:19
     * @throws
     */
    @Query(nativeQuery = true, value = "select id,title,(select cn_name from or_cate where id = a.cate_refer_id) catename,a.cate_refer_id cateid from or_article a  where status = 1 and top = 1 ORDER BY a.views desc LIMIT ?1")
    List<Map<String,Object>>  selectTopList(int limit);

    @Query(nativeQuery = true, value = "select title,id,a.views from or_article a  where status = 1 ORDER BY a.views desc LIMIT ?1")
    List<Map<String,Object>> selectLotArticles(int limit);

    Article findArticleById(Long id);

}
