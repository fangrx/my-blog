package com.nonelonely.modules.system.repository;

import com.nonelonely.modules.system.domain.Uv;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;

/**
 * @author nonelonely
 * @date 2020/03/12
 */
public interface UvRepository extends BaseRepository<Uv, Long> {
    @Query(nativeQuery = true, value = "SELECT COUNT(DISTINCT uuid)FROM or_uv WHERE TO_DAYS( NOW( ) ) - TO_DAYS(create_date ) <= 1  ")
    int findCountBefore();
    @Query(nativeQuery = true, value = "SELECT COUNT(DISTINCT uuid)FROM or_uv WHERE TO_DAYS( NOW( ) ) = TO_DAYS(create_date )")
    int findCountNow();
    @Query(nativeQuery = true, value = " select SUBSTRING_INDEX(location,'-',-1) as 'name',COUNT(1) as 'value' from or_uv" +
            "    where location is not NULL group by SUBSTRING_INDEX(location,'-',-1)")
    List<Map> findMapCity();
    @Query(nativeQuery = true, value = "select SUBSTRING_INDEX(SUBSTRING_INDEX(location,'-',2),'-',-1) as 'name',COUNT(1) as 'value' from or_uv" +
            "       where date(create_date) = curdate() AND location is not NULL group by SUBSTRING_INDEX(SUBSTRING_INDEX(location,'-',2),'-',-1)")
    List<Map> findMap();
    /**
     *  获取 前10 的入口地址
     * @return
     */
    @Query(nativeQuery = true, value = "select now_url as 'url',COUNT(1) AS 'value' FROM or_uv  WHERE TO_DAYS(create_date ) >= TO_DAYS(str_to_date(?1,'%Y-%m-%d') ) and TO_DAYS(create_date ) <=TO_DAYS(str_to_date(?2,'%Y-%m-%d') ) GROUP BY now_url ORDER BY value Desc limit 10")
    List<Map> findNowUrlTop10(String day1, String day2);
    /**
     *  获取 前10 的来源地址
     * @return
     */
    @Query(nativeQuery = true, value = "select SUBSTRING_INDEX(from_url,'/',3) as 'url',COUNT(1) as 'value' from or_uv where from_url not LIKE '%nonelonely%'and TO_DAYS(create_date ) >= TO_DAYS(str_to_date(?1,'%Y-%m-%d') ) and TO_DAYS(create_date ) <=TO_DAYS(str_to_date(?2,'%Y-%m-%d') ) group by SUBSTRING_INDEX(from_url,'/',3)ORDER BY value Desc limit 10")
    List<Map> findFromUrlTop10(String day1, String day2);

    @Query(nativeQuery = true, value = "SELECT DATE_FORMAT(create_date,'%H') hours,count(DISTINCT uuid) count FROM or_uv where DATE_SUB(CURDATE(), INTERVAL ? DAY) = date(create_date) group by hours")
    List<Map> findUv(int i);

    @Query(nativeQuery = true, value = "SELECT DATE_FORMAT(create_date,'%H') hours,count(uuid) count FROM or_uv where DATE_SUB(CURDATE(), INTERVAL ? DAY) = date(create_date) group by hours")
    List<Map> findPv(int i);

    @Query(nativeQuery = true, value = "SELECT DATE_FORMAT(create_date,'%H') hours,count(DISTINCT ip) count FROM or_uv where DATE_SUB(CURDATE(), INTERVAL ? DAY) = date(create_date) group by hours")
    List<Map> findIp(int i);


    @Query(nativeQuery = true, value = "SELECT COUNT(DISTINCT ip)FROM or_uv WHERE TO_DAYS( NOW( ) ) = TO_DAYS(create_date )")
    int countIp();
    @Modifying
    @Query(nativeQuery = true, value = "delete from or_uv   where  datediff(curdate(), create_date)>=3")
    @Transactional(rollbackOn = Exception.class)
    int deleteUvBefor3();

    @Query(nativeQuery = true, value =
            "SELECT a.browser name,COUNT(1) value FROM(SELECT 'Chrome' browser UNION ALL SELECT 'Firefox' browser UNION ALL SELECT 'Internet Explorer' browser " +
            "UNION ALL SELECT 'Safari' browser UNION ALL SELECT 'Opera' browser UNION ALL SELECT  " +
            "'Safari' browser) a , or_uv b where b.browser like concat('%',a.`browser`,'%') and date(b.create_date) = curdate() GROUP BY a.browser")
    List<Map> findBrowser();
}
