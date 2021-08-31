package com.nonelonely.modules.system.service.impl;

import com.nonelonely.common.data.PageSort;
import com.nonelonely.common.enums.StatusEnum;
import com.nonelonely.modules.system.domain.Art;
import com.nonelonely.modules.system.repository.ArtRepository;
import com.nonelonely.modules.system.service.ArtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author nonelonely
 * @date 2020/02/14
 */
@Service
public class ArtServiceImpl implements ArtService {

    @Autowired
    private ArtRepository artRepository;

    /**
     * 根据ID查询数据
     * @param id 主键ID
     */
    @Override
    @Transactional
    public Art getById(Long id) {
        return artRepository.findById(id).orElse(null);
    }

    /**
     * 获取分页列表数据
     * @param example 查询实例
     * @return 返回分页数据
     */
    @Override
    public Page<Art> getPageList(Example<Art> example) {
        // 创建分页对象
        PageRequest page = PageSort.pageRequest();
        return artRepository.findAll(example, page);
    }

    /**
     * 保存数据
     * @param art 实体对象
     */
    @Override
    public Art save(Art art) {
        return artRepository.save(art);
    }

    /**
     * 状态(启用，冻结，删除)/批量状态处理
     */
    @Override
    @Transactional
    public Boolean updateStatus(StatusEnum statusEnum, List<Long> idList) {
        return artRepository.updateStatus(statusEnum.getCode(), idList) > 0;
    }
}
