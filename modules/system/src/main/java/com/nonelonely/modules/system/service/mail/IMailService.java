package com.nonelonely.modules.system.service.mail;







import com.nonelonely.common.vo.ResultVo;
import com.nonelonely.modules.system.domain.mail.Mail;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;


/**
 * <p>
 * 邮件 服务类
 * </p>
 *
 * @author liuming
 * @since 2020-09-15
 */
public interface IMailService {




    /**
     * 获取分页列表数据
     * @param example 查询实例
     * @return 返回分页数据
     */
    Page<Mail> getPageList(Example<Mail> example);

    /**
     * 根据ID查询数据
     * @param id 主键ID
     */
    Mail getById(Long id);


    /**
     * 保存数据
     * @param mail 实体对象
     */
    Mail save(Mail mail);



}
