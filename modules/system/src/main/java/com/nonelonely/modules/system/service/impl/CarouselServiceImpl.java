package com.nonelonely.modules.system.service.impl;

import com.nonelonely.common.data.PageSort;
import com.nonelonely.common.enums.StatusEnum;
import com.nonelonely.modules.system.domain.Carousel;
import com.nonelonely.modules.system.repository.CarouselRepository;
import com.nonelonely.modules.system.service.CarouselService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author nonelonely
 * @date 2020/11/16
 */
@Service
public class CarouselServiceImpl implements CarouselService {

    @Autowired
    private CarouselRepository carouselRepository;

    /**
     * 根据ID查询数据
     * @param id 主键ID
     */
    @Override
    @Transactional
    public Carousel getById(Long id) {
        return carouselRepository.findById(id).orElse(null);
    }

    /**
     * 获取分页列表数据
     * @param example 查询实例
     * @return 返回分页数据
     */
    @Override
    public Page<Carousel> getPageList(Example<Carousel> example) {
        // 创建分页对象
        PageRequest page = PageSort.pageRequest();
        return carouselRepository.findAll(example, page);
    }

    /**
     * 保存数据
     * @param carousel 实体对象
     */
    @Override
    public Carousel save(Carousel carousel) {
        return carouselRepository.save(carousel);
    }

    /**
     * 状态(启用，冻结，删除)/批量状态处理
     */
    @Override
    @Transactional
    public Boolean updateStatus(StatusEnum statusEnum, List<Long> idList) {
        return carouselRepository.updateStatus(statusEnum.getCode(), idList) > 0;
    }
}
