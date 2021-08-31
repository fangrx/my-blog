package com.nonelonely.modules.system.service;

import com.nonelonely.common.enums.StatusEnum;
import com.nonelonely.modules.system.domain.ScheduledTask;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author nonelonely
 * @date 2020/04/15
 */
public interface ScheduledTaskService {

    /**
     * 获取分页列表数据
     * @param example 查询实例
     * @return 返回分页数据
     */
    Page<ScheduledTask> getPageList(Example<ScheduledTask> example);

    /**
     * 根据ID查询数据
     * @param id 主键ID
     */
    ScheduledTask getById(Long id);

    /**
     * 保存数据
     * @param scheduledTask 实体对象
     */
    ScheduledTask save(ScheduledTask scheduledTask);

    /**
     * 状态(启用，冻结，删除)/批量状态处理
     */
    @Transactional
    Boolean updateStatus(StatusEnum statusEnum, List<Long> idList);


}
