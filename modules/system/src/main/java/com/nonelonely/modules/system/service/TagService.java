package com.nonelonely.modules.system.service;

import com.nonelonely.common.enums.StatusEnum;
import com.nonelonely.modules.system.domain.Tag;
import com.nonelonely.modules.system.domain.bo.TagBO;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * @author nonelonely
 * @date 2020/01/01
 */
public interface TagService {

    /**
     * 获取分页列表数据
     * @param example 查询实例
     * @return 返回分页数据
     */
    Page<Tag> getPageList(Example<Tag> example);

    /**
     * 根据ID查询数据
     * @param id 主键ID
     */
    Tag getById(Long id);

    /**
     * 保存数据
     * @param tag 实体对象
     */
    Tag save(Tag tag);

    /**
     * 状态(启用，冻结，删除)/批量状态处理
     */
    @Transactional
    Boolean updateStatus(StatusEnum statusEnum, List<Long> idList);


    /**
     * 查找文章/笔记相关tag并selected
     *
     * @param referId
     * @param type    文章还是笔记{@code TagType}
     * @return
     */
    List<TagBO> findSelectedTagsByReferId(Long referId, String type);

    /**
     * 查询标签使用数到首页标签面板上显示
     *
     * @return
     */
    List<Map<String, Object>> findTagsTab(int limlt);
}
