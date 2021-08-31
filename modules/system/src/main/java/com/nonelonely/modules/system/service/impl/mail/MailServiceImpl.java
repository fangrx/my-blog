package com.nonelonely.modules.system.service.impl.mail;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.nonelonely.common.data.PageSort;
import com.nonelonely.common.utils.ObjectUtils;
import com.nonelonely.common.utils.ResultVoUtil;
import com.nonelonely.common.vo.ResultVo;

import com.nonelonely.modules.system.domain.mail.Mail;
import com.nonelonely.modules.system.repository.mail.MailBoxRepository;
import com.nonelonely.modules.system.repository.mail.MailRepository;
import com.nonelonely.modules.system.service.mail.IMailSendLogService;
import com.nonelonely.modules.system.service.mail.IMailService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * <p>
 * 邮件 服务实现类
 * </p>
 *
 * @author liuming
 * @since 2020-09-15
 */
@Service(value = "MailServiceImpl")
@Transactional(rollbackFor = Exception.class)
@Slf4j
public class MailServiceImpl implements IMailService {

    @Autowired
    private MailRepository mailRepository;
    @Autowired
    private MailBoxRepository mailBoxRepository;

    @Autowired
    private IMailSendLogService mailSendLogService;




    /**
     * 根据ID查询数据
     * @param id 主键ID
     */
    @Override
    @Transactional
    public Mail getById(Long id) {
        return mailRepository.findById(id).orElse(null);
    }

    /**
     * 获取分页列表数据
     * @param example 查询实例
     * @return 返回分页数据
     */
    @Override
    public Page<Mail> getPageList(Example<Mail> example) {
        // 创建分页对象
        PageRequest page = PageSort.pageRequest("id", Sort.Direction.DESC);
        return mailRepository.findAll(example, page);
    }

    /**
     * 保存数据
     * @param mail 实体对象
     */
    /**
     * 保存数据
     * @param mail 实体对象
     */
    @Override
    public Mail save(Mail mail) {
        return mailRepository.save(mail);
    }



}
