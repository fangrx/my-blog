package com.nonelonely.modules.system.service.impl;

import com.nonelonely.common.data.PageSort;
import com.nonelonely.common.enums.StatusEnum;
import com.nonelonely.modules.system.domain.Link;
import com.nonelonely.modules.system.repository.LinkRepository;
import com.nonelonely.modules.system.service.LinkService;
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
public class LinkServiceImpl implements LinkService {

    @Autowired
    private LinkRepository linkRepository;

    /**
     * 根据ID查询数据
     * @param id 主键ID
     */
    @Override
    @Transactional
    public Link getById(Long id) {
        return linkRepository.findById(id).orElse(null);
    }

    /**
     * 获取分页列表数据
     * @param example 查询实例
     * @return 返回分页数据
     */
    @Override
    public Page<Link> getPageList(Example<Link> example) {
        // 创建分页对象
        PageRequest page = PageSort.pageRequest();
        return linkRepository.findAll(example, page);
    }

    /**
     * 保存数据
     * @param link 实体对象
     */
    @Override
    public Link save(Link link) {
        return linkRepository.save(link);
    }

    /**
     * 状态(启用，冻结，删除)/批量状态处理
     */
    @Override
    @Transactional
    public Boolean updateStatus(StatusEnum statusEnum, List<Long> idList) {
        return linkRepository.updateStatus(statusEnum.getCode(), idList) > 0;
    }
}
