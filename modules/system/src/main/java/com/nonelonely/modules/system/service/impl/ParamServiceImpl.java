package com.nonelonely.modules.system.service.impl;

import com.nonelonely.common.data.PageSort;
import com.nonelonely.common.enums.StatusEnum;
import com.nonelonely.modules.system.domain.Param;
import com.nonelonely.modules.system.repository.ParamRepository;
import com.nonelonely.modules.system.service.ParamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author nonelonely
 * @date 2020/01/02
 */
@Service
public class ParamServiceImpl implements ParamService {

    @Autowired
    private ParamRepository paramRepository;

    /**
     * 根据ID查询数据
     * @param id 主键ID
     */
    @Override
    @Transactional
    public Param getById(Long id) {
        return paramRepository.findById(id).orElse(null);
    }
    @Override
    @Transactional
    public Param getByName(String name) {
        return paramRepository.findFirstByName(name);
    }

    /**
     * 获取分页列表数据
     * @param example 查询实例
     * @return 返回分页数据
     */
    @Override
    public Page<Param> getPageList(Example<Param> example) {
        // 创建分页对象
        PageRequest page = PageSort.pageRequest();
        return paramRepository.findAll(example, page);
    }

    /**
     * 保存数据
     * @param param 实体对象
     */
    @Override
    public Param save(Param param) {
        return paramRepository.save(param);
    }

    /**
     * 状态(启用，冻结，删除)/批量状态处理
     */
    @Override
    @Transactional
    public Boolean updateStatus(StatusEnum statusEnum, List<Long> idList) {
        return paramRepository.updateStatus(statusEnum.getCode(), idList) > 0;
    }

}
