package com.nonelonely.modules.system.service.mail;

import com.nonelonely.common.enums.StatusEnum;
import com.nonelonely.common.vo.ResultVo;

import com.nonelonely.modules.system.domain.mail.MailBox;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author nonelonely
 * @date 2020/04/19
 */
public interface IMailBoxService {


    /**
     * 根据ID查询数据
     * @param id 主键ID
     */
    MailBox getById(Long id);


    void save(MailBox mailBox);
    /**
     * 描述：获取当前数据库中所有记录
     *
     * @return
     * @author 杨建全
     * @date 2017年4月7日
     */
     List<MailBox> getAllMailBox();
    /**
     * 状态(启用，冻结，删除)/批量状态处理
     */
    @Transactional
    Boolean updateStatus(StatusEnum statusEnum, List<Long> idList);


}
