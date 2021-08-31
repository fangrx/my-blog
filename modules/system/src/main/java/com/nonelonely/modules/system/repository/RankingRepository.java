package com.nonelonely.modules.system.repository;


import com.nonelonely.modules.system.domain.Ranking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Map;

/**
 * 描述:
 * ${DESCRIPTION}
 *
 * @author nonelonely
 * @create 2020-11-11 20:41
 * 个人博客地址：https://www.nonelonely.com
 */
public interface RankingRepository extends JpaRepository<Ranking,Long>, JpaSpecificationExecutor<Ranking> {
     /**
      * @title 周排名
      * @description [limit]
      * @author nonelonely
      * @updateTime 2020/11/11 20:46
      * @throws
      */

    @Query(nativeQuery = true, value = "SELECT " +
            " b.title," +
            " content_id id," +
            " sum(hits)" +
            " FROM  or_ranking a,or_article b WHERE DAY (a.create_Date) = DAY (now()) AND a.content_id = b.id and status = 1 GROUP BY content_id" +
            " ORDER BY sum(hits) DESC LIMIT ?1")
    List<Map<String,Object>> selectWeekList(int limit);
    /**
     * @title 月排名
     * @description [limit]
     * @author nonelonely
     * @updateTime 2020/11/11 20:46
     * @throws
     */
    @Query(nativeQuery = true, value = "SELECT " +
            " b.title," +
            " content_id id," +
            " sum(hits)" +
            " FROM  or_ranking a,or_article b WHERE MONTH (a.create_Date) = MONTH (now()) AND a.content_id = b.id and status = 1 GROUP BY content_id" +
            " ORDER BY sum(hits) DESC LIMIT ?1")
    List<Map<String,Object>> selectMonthList(int limit);
    /**
     * @title
     * @description 计算今天这个文章的数量
     * @author nonelonely
     * @updateTime 2020/11/11 21:33
     * @throws
     */

    @Query(nativeQuery = true, value = "select * from or_ranking a WHERE  a.content_id = ?1 AND DAY(a.create_date) = DAY(NOW()) ")
    Ranking countContentIdandDate(Long id);
}
