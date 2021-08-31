package com.nonelonely.modules.system.service.impl;

import com.nonelonely.common.data.PageSort;
import com.nonelonely.common.enums.StatusEnum;
import com.nonelonely.modules.system.domain.Tag;
import com.nonelonely.modules.system.domain.bo.TagBO;
import com.nonelonely.modules.system.repository.TagReferRepository;
import com.nonelonely.modules.system.repository.TagRepository;
import com.nonelonely.modules.system.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author nonelonely
 * @date 2020/01/01
 */
@Service
public class TagServiceImpl implements TagService {

    @Autowired
    private TagRepository tagRepository;
    @Autowired
    private TagReferRepository tagReferRepository;
    /**
     * 根据ID查询数据
     * @param id 主键ID
     */
    @Override
    @Transactional
    public Tag getById(Long id) {
        return tagRepository.findById(id).orElse(null);
    }

    /**
     * 获取分页列表数据
     * @param example 查询实例
     * @return 返回分页数据
     */
    @Override
    public Page<Tag> getPageList(Example<Tag> example) {
        // 创建分页对象
        PageRequest page = PageSort.pageRequest();
        return tagRepository.findAll(example, page);
    }

    /**
     * 保存数据
     * @param tag 实体对象
     */
    @Override
    public Tag save(Tag tag) {
        return tagRepository.save(tag);
    }

    /**
     * 状态(启用，冻结，删除)/批量状态处理
     */
    @Override
    @Transactional
    public Boolean updateStatus(StatusEnum statusEnum, List<Long> idList) {
        return tagRepository.updateStatus(statusEnum.getCode(), idList) > 0;
    }

    @Override
    public List<TagBO> findSelectedTagsByReferId(Long referId, String type) {
        List<Object[]> tags = tagRepository.findTagListSelected(referId, type);
        List<TagBO> tagVOList = new ArrayList<>(tags.size());
        for (Object[] objArr : tags) {
            TagBO nbTagVO = new TagBO();
            nbTagVO.setId(Long.valueOf(objArr[0].toString()));
            nbTagVO.setName(objArr[1].toString());
            nbTagVO.setSelected(objArr[2].toString());
            tagVOList.add(nbTagVO);
        }
        return tagVOList;
    }

    @Override
    public List<Map<String, Object>> findTagsTab(int limit) {
        List<Object[]> tags = tagReferRepository.findTagsTab(limit);
        List<Map<String, Object>> tagPanelList = new ArrayList<>(tags.size());
        for (Object[] objArr : tags) {
            Map<String, Object> m = new HashMap<>(3);
            m.put("id", objArr[0]);
            m.put("name", objArr[1]);
            m.put("cnt", objArr[2]);
            tagPanelList.add(m);
        }
        return tagPanelList;
    }
}
