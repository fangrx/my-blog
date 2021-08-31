package com.nonelonely.modules.system.service;

import com.nonelonely.common.enums.StatusEnum;
import com.nonelonely.modules.system.domain.Art;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author nonelonely
 * @date 2020/02/14
 */
public interface ArtService {

    /**
     * 获取分页列表数据
     * @param example 查询实例
     * @return 返回分页数据
     */
    Page<Art> getPageList(Example<Art> example);

    /**
     * 根据ID查询数据
     * @param id 主键ID
     */
    Art getById(Long id);

    /**
     * 保存数据
     * @param art 实体对象
     */
    Art save(Art art);

    /**
     * 状态(启用，冻结，删除)/批量状态处理
     */
    @Transactional
    Boolean updateStatus(StatusEnum statusEnum, List<Long> idList);
}
