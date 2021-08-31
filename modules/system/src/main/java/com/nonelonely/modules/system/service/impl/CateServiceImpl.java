package com.nonelonely.modules.system.service.impl;

import com.nonelonely.common.data.PageSort;
import com.nonelonely.common.enums.StatusEnum;
import com.nonelonely.modules.system.domain.Cate;
import com.nonelonely.modules.system.repository.CateRepository;
import com.nonelonely.modules.system.service.CateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author nonelonely
 * @date 2020/01/01
 */
@Service
public class CateServiceImpl implements CateService {

    @Autowired
    private CateRepository cateRepository;

    /**
     * 根据ID查询数据
     * @param id 主键ID
     */
    @Override
    @Transactional
    public Cate getById(Long id) {
        return cateRepository.findById(id).orElse(null);
    }

    /**
     * 获取分页列表数据
     * @param example 查询实例
     * @return 返回分页数据
     */
    @Override
    public Page<Cate> getPageList(Example<Cate> example) {
        // 创建分页对象
        PageRequest page = PageSort.pageRequest();
        return cateRepository.findAll(example, page);
    }

    /**
     * 保存数据
     * @param cate 实体对象
     */
    @Override
    public Cate save(Cate cate) {
        return cateRepository.save(cate);
    }

    /**
     * 状态(启用，冻结，删除)/批量状态处理
     */
    @Override
    @Transactional
    public Boolean updateStatus(StatusEnum statusEnum, List<Long> idList) {
        return cateRepository.updateStatus(statusEnum.getCode(), idList) > 0;
    }
}
