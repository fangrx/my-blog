package com.nonelonely.modules.system.service.impl;

import com.nonelonely.common.data.PageSort;
import com.nonelonely.common.enums.StatusEnum;
import com.nonelonely.modules.system.domain.About;
import com.nonelonely.modules.system.repository.AboutRepository;
import com.nonelonely.modules.system.service.AboutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author nonelonely
 * @date 2020/02/13
 */
@Service
public class AboutServiceImpl implements AboutService {

    @Autowired
    private AboutRepository aboutRepository;

    /**
     * 根据ID查询数据
     * @param id 主键ID
     */
    @Override
    @Transactional
    public About getById(Long id) {
        return aboutRepository.findById(id).orElse(null);
    }

    /**
     * 获取分页列表数据
     * @param example 查询实例
     * @return 返回分页数据
     */
    @Override
    public Page<About> getPageList(Example<About> example) {
        // 创建分页对象
        PageRequest page = PageSort.pageRequest();
        return aboutRepository.findAll(example, page);
    }

    /**
     * 保存数据
     * @param about 实体对象
     */
    @Override
    public About save(About about) {
        return aboutRepository.save(about);
    }

    /**
     * 状态(启用，冻结，删除)/批量状态处理
     */
    @Override
    @Transactional
    public Boolean updateStatus(StatusEnum statusEnum, List<Long> idList) {
        return aboutRepository.updateStatus(statusEnum.getCode(), idList) > 0;
    }
}
