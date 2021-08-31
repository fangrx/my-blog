package com.nonelonely.modules.system.service.impl;

import com.nonelonely.common.data.PageSort;
import com.nonelonely.common.enums.StatusEnum;

import com.nonelonely.modules.system.domain.permission.NBSysResource;
import com.nonelonely.modules.system.repository.permission.ResourceRepository;
import com.nonelonely.modules.system.service.NBSysResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author nonelonely
 * @date 2021/02/04
 */
@Service
public class NBSysResourceServiceImpl implements NBSysResourceService {

    @Autowired
    private ResourceRepository nBSysResourceRepository;

    /**
     * 根据ID查询数据
     * @param id 主键ID
     */
    @Override
    @Transactional
    public NBSysResource getById(Long id) {
        return nBSysResourceRepository.findById(id).orElse(null);
    }

    /**
     * 获取分页列表数据
     * @param example 查询实例
     * @return 返回分页数据
     */
    @Override
    public Page<NBSysResource> getPageList(Example<NBSysResource> example) {
        // 创建分页对象
        PageRequest page = PageSort.pageRequest();
        return nBSysResourceRepository.findAll(example, page);
    }

    /**
     * 保存数据
     * @param nBSysResource 实体对象
     */
    @Override
    public NBSysResource save(NBSysResource nBSysResource) {
        return nBSysResourceRepository.save(nBSysResource);
    }

    /**
     * 状态(启用，冻结，删除)/批量状态处理
     */
    @Override
    @Transactional
    public Boolean updateStatus(StatusEnum statusEnum, List<Long> idList) {
        return nBSysResourceRepository.updateStatus(statusEnum.getCode(), idList) > 0;
    }
}
