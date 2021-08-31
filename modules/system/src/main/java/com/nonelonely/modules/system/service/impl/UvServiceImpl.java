package com.nonelonely.modules.system.service.impl;

import com.nonelonely.common.data.PageSort;
import com.nonelonely.common.enums.StatusEnum;
import com.nonelonely.modules.system.domain.Uv;
import com.nonelonely.modules.system.repository.UvRepository;
import com.nonelonely.modules.system.service.UvService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author nonelonely
 * @date 2020/03/12
 */
@Service
public class UvServiceImpl implements UvService {

    @Autowired
    private UvRepository uvRepository;

    /**
     * 根据ID查询数据
     * @param id 主键ID
     */
    @Override
    @Transactional
    public Uv getById(Long id) {
        return uvRepository.findById(id).orElse(null);
    }

    /**
     * 获取分页列表数据
     * @param example 查询实例
     * @return 返回分页数据
     */
    @Override
    public Page<Uv> getPageList(Example<Uv> example) {
        // 创建分页对象
        PageRequest page = PageSort.pageRequest();
        return uvRepository.findAll(example, page);
    }

    /**
     * 保存数据
     * @param uv 实体对象
     */
    @Override
    public Uv save(Uv uv) {
        return uvRepository.save(uv);
    }

    /**
     * 状态(启用，冻结，删除)/批量状态处理
     */
    @Override
    @Transactional
    public Boolean updateStatus(StatusEnum statusEnum, List<Long> idList) {
        return uvRepository.updateStatus(statusEnum.getCode(), idList) > 0;
    }
}
