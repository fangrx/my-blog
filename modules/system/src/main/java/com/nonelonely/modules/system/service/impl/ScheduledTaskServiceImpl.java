package com.nonelonely.modules.system.service.impl;

import com.nonelonely.common.data.PageSort;
import com.nonelonely.common.enums.StatusEnum;
import com.nonelonely.modules.system.domain.ScheduledTask;
import com.nonelonely.modules.system.repository.ScheduledTaskRepository;
import com.nonelonely.modules.system.service.ScheduledTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author nonelonely
 * @date 2020/04/15
 */
@Service
public class ScheduledTaskServiceImpl implements ScheduledTaskService {

    @Autowired
    private ScheduledTaskRepository scheduledTaskRepository;

    /**
     * 根据ID查询数据
     * @param id 主键ID
     */
    @Override
    @Transactional
    public ScheduledTask getById(Long id) {
        return scheduledTaskRepository.findById(id).orElse(null);
    }

    /**
     * 获取分页列表数据
     * @param example 查询实例
     * @return 返回分页数据
     */
    @Override
    public Page<ScheduledTask> getPageList(Example<ScheduledTask> example) {
        // 创建分页对象
        PageRequest page = PageSort.pageRequest();
        return scheduledTaskRepository.findAll(example, page);
    }

    /**
     * 保存数据
     * @param scheduledTask 实体对象
     */
    @Override
    public ScheduledTask save(ScheduledTask scheduledTask) {
        return scheduledTaskRepository.save(scheduledTask);
    }

    /**
     * 状态(启用，冻结，删除)/批量状态处理
     */
    @Override
    @Transactional
    public Boolean updateStatus(StatusEnum statusEnum, List<Long> idList) {
        return scheduledTaskRepository.updateStatus(statusEnum.getCode(), idList) > 0;
    }

}
