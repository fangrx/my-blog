package com.nonelonely.modules.system.service.impl.mail;


import com.nonelonely.common.enums.StatusEnum;
import com.nonelonely.common.utils.ResultVoUtil;
import com.nonelonely.common.vo.ResultVo;

import com.nonelonely.modules.system.domain.mail.MailBox;
import com.nonelonely.modules.system.repository.mail.MailBoxRepository;
import com.nonelonely.modules.system.service.mail.IMailBoxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


/**
 * @author nonelonely
 * @date 2020/04/19
 */
@Service
public class MailBoxServiceImpl implements IMailBoxService {

    @Autowired
    private MailBoxRepository mailBoxRepository;

    /**
     * 根据ID查询数据
     * @param id 主键ID
     */
    @Override
    @Transactional
    public MailBox getById(Long id) {
        return mailBoxRepository.findById(id).orElse(null);
    }



    /**
     * 保存数据
     * @param mailBox 实体对象
     */
    @Override
    public void save(MailBox mailBox) {
        //验证Pop3是否正确
        // 服务器验证
        mailBoxRepository.save(mailBox);

    }

    /**
     * 状态(启用，冻结，删除)/批量状态处理
     */
    @Override
    @Transactional
    public Boolean updateStatus(StatusEnum statusEnum, List<Long> idList) {
        return mailBoxRepository.updateStatus(statusEnum.getCode(), idList) > 0;
    }

    /**
     * 描述：获取当前数据库中所有记录
     *
     * @return
     * @author 杨建全
     * @date 2017年4月7日
     */
    public List<MailBox> getAllMailBox(){
        return  mailBoxRepository.findAll();
    }
}
