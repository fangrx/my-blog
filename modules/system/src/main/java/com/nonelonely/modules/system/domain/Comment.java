package com.nonelonely.modules.system.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nonelonely.common.enums.StatusEnum;
import com.nonelonely.common.utils.StatusUtil;
import com.nonelonely.modules.system.domain.User;
import lombok.Data;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Where;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

/**
 * @author nonelonely
 * @date 2020/01/02
 */
@Data
@Entity
@Table(name="or_comment")
@EntityListeners(AuditingEntityListener.class)
@Where(clause = StatusUtil.NOT_DELETE)
public class Comment implements Serializable {
    // 主键ID
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    // 父主键
    private Long parentId;

    private Long levelId;
    // 评论内容
    private String comment;
    // IP地址
    private String ipAddr;
    // 浏览器标识
    private String userAgent;
    // IP所在地
    private String ipCnAddr;

    @ManyToOne
    @JoinColumn(name="article_id")
    private Article article;


    private int type;


    // 创建时间
    @CreatedDate
    private Date createDate;
    // 更新时间
    @LastModifiedDate
    private Date updateDate;
    // 创建者
    @CreatedBy
    @ManyToOne(fetch=FetchType.LAZY)
    @NotFound(action=NotFoundAction.IGNORE)
    @JoinColumn(name="create_by")
    @JsonIgnore
    private User createBy;
    // 更新者
    @LastModifiedBy
    @ManyToOne(fetch=FetchType.LAZY)
    @NotFound(action=NotFoundAction.IGNORE)
    @JoinColumn(name="update_by")
    @JsonIgnore
    private User updateBy;
    // 数据状态
    private Byte status = StatusEnum.OK.getCode();
}
