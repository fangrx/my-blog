package com.nonelonely.modules.system.service.impl;

import com.nonelonely.common.data.PageSort;
import com.nonelonely.common.enums.StatusEnum;
import com.nonelonely.modules.system.domain.Words;
import com.nonelonely.modules.system.repository.WordsRepository;
import com.nonelonely.modules.system.service.WordsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author nonelonely
 * @date 2020/02/15
 */
@Service
public class WordsServiceImpl implements WordsService {

    @Autowired
    private WordsRepository wordsRepository;

    /**
     * 根据ID查询数据
     * @param id 主键ID
     */
    @Override
    @Transactional
    public Words getById(Long id) {
        return wordsRepository.findById(id).orElse(null);
    }

    /**
     * 获取分页列表数据
     * @param example 查询实例
     * @return 返回分页数据
     */
    @Override
    public Page<Words> getPageList(Example<Words> example) {
        // 创建分页对象
        PageRequest page = PageSort.pageRequest();
        return wordsRepository.findAll(example, page);
    }

    /**
     * 保存数据
     * @param words 实体对象
     */
    @Override
    public Words save(Words words) {
        return wordsRepository.save(words);
    }

    /**
     * 状态(启用，冻结，删除)/批量状态处理
     */
    @Override
    @Transactional
    public Boolean updateStatus(StatusEnum statusEnum, List<Long> idList) {
        return wordsRepository.updateStatus(statusEnum.getCode(), idList) > 0;
    }
}
