package com.nonelonely.modules.system.domain;

import com.nonelonely.common.enums.StatusEnum;
import com.nonelonely.common.utils.StatusUtil;
import com.nonelonely.component.excel.annotation.Excel;
import lombok.Data;
import org.hibernate.annotations.Where;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

/**
 * @author nonelonely
 * @date 2020/03/12
 */
@Data
@Entity
@Table(name="or_uv")
@EntityListeners(AuditingEntityListener.class)
@Where(clause = StatusUtil.NOT_DELETE)
public class Uv implements Serializable {
    // 主键ID
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    // 来源
    private String fromUrl;
    // 当前位置
    private String nowUrl;
    // 创建时间
    @CreatedDate
    private Date createDate;
    // 客户端标识
    private String uuid;
    // Ip
    private String ip;
    // 会话标识
    private String sessionId;

    private String location;

    private Date updateDate;
    @Column(name = "useSystem")
    private String system;

    private String browser;

    // 数据状态
    private Byte status = StatusEnum.OK.getCode();
}
