package com.nonelonely.modules.system.service.impl;

import com.nonelonely.common.data.PageSort;
import com.nonelonely.common.enums.StatusEnum;
import com.nonelonely.modules.system.domain.Note;
import com.nonelonely.modules.system.repository.NoteRepository;
import com.nonelonely.modules.system.service.NoteService;
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
public class NoteServiceImpl implements NoteService {

    @Autowired
    private NoteRepository noteRepository;

    /**
     * 根据ID查询数据
     * @param id 主键ID
     */
    @Override
    @Transactional
    public Note getById(Long id) {
        return noteRepository.findById(id).orElse(null);
    }

    /**
     * 获取分页列表数据
     * @param example 查询实例
     * @return 返回分页数据
     */
    @Override
    public Page<Note> getPageList(Example<Note> example) {
        // 创建分页对象
        PageRequest page = PageSort.pageRequest();
        return noteRepository.findAll(example, page);
    }

    /**
     * 保存数据
     * @param note 实体对象
     */
    @Override
    public Note save(Note note) {
        return noteRepository.save(note);
    }

    /**
     * 状态(启用，冻结，删除)/批量状态处理
     */
    @Override
    @Transactional
    public Boolean updateStatus(StatusEnum statusEnum, List<Long> idList) {
        return noteRepository.updateStatus(statusEnum.getCode(), idList) > 0;
    }
}
