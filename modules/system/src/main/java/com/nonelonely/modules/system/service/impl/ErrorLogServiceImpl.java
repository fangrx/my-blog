package com.nonelonely.modules.system.service.impl;

import com.nonelonely.common.data.PageSort;
import com.nonelonely.common.enums.StatusEnum;
import com.nonelonely.modules.system.domain.ErrorLog;
import com.nonelonely.modules.system.repository.ErrorLogRepository;
import com.nonelonely.modules.system.service.ErrorLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author nonelonely
 * @date 2021/02/01
 */
@Service
public class ErrorLogServiceImpl implements ErrorLogService {

    @Autowired
    private ErrorLogRepository errorLogRepository;

    /**
     * 根据ID查询数据
     * @param id 主键ID
     */
    @Override
    @Transactional
    public ErrorLog getById(Long id) {
        return errorLogRepository.findById(id).orElse(null);
    }

    /**
     * 获取分页列表数据
     * @param example 查询实例
     * @return 返回分页数据
     */
    @Override
    public Page<ErrorLog> getPageList(Example<ErrorLog> example) {
        // 创建分页对象
        PageRequest page = PageSort.pageRequest();
        return errorLogRepository.findAll(example, page);
    }

    /**
     * 保存数据
     * @param errorLog 实体对象
     */
    @Override
    public ErrorLog save(ErrorLog errorLog) {
        return errorLogRepository.save(errorLog);
    }

    /**
     * 状态(启用，冻结，删除)/批量状态处理
     */
    @Override
    @Transactional
    public Boolean updateStatus(StatusEnum statusEnum, List<Long> idList) {
        return errorLogRepository.updateStatus(statusEnum.getCode(), idList) > 0;
    }
}
