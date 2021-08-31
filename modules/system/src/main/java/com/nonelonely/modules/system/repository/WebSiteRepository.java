package com.nonelonely.modules.system.repository;


import com.nonelonely.modules.system.domain.WebSite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * 描述:
 * ${DESCRIPTION}
 *
 * @author nonelonely
 * @create 2020-11-13 20:37
 * 个人博客地址：https://www.nonelonely.com
 */
public interface WebSiteRepository extends JpaRepository<WebSite,Long>, JpaSpecificationExecutor<WebSite> {

    /**
     * @title 获取没有向百度推给的数据
     * @description [baidu]
     * @author nonelonely
     * @updateTime 2020/11/13 20:40
     * @throws
     */
    List<WebSite> findAllByBaidu(Boolean baidu);
    /**
     * @title 根据网站id获取数据
     * @description
     * @author nonelonely
     * @updateTime 2020/11/13 20:40
     * @throws
     */
    WebSite findByContentId(Long content_id);
}
