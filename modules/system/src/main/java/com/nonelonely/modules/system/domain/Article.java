package com.nonelonely.modules.system.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nonelonely.common.enums.StatusEnum;
import com.nonelonely.common.utils.StatusUtil;
import com.nonelonely.modules.system.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
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
import javax.validation.constraints.NotEmpty;

/**
 * @author nonelonely
 * @date 2020/01/01
 */
@Data
@Entity
@Table(name="or_article")
@EntityListeners(AuditingEntityListener.class)
@Where(clause = StatusUtil.NOT_DELETE)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Article implements Serializable {
    /**
     * 主键id
     * 生成策略为自定义的id生成策略
     */
    @Id
    @Column(length = 20, updatable = false, nullable = false)
    @GeneratedValue(generator = "articleId")
    @GenericGenerator(name = "articleId", strategy = "com.nonelonely.modules.system.domain.strategy.NBArticleStrategy")
    private Long id;
    // 标题
    private String title;

    private String xtitle;
    //
    @Column(columnDefinition = "mediumtext", nullable = false)
    @NotEmpty(message = "文章内容不能为空")
    private String content;
    // 类别
    @ManyToOne
    @JoinColumn(name = "cate_refer_id")
    private Cate cate = new Cate();

    @Column(length = 300)
    private String summary;
    // 浏览次数
    private Integer views;
    // 点赞次数
    private Integer approveCnt;
    // 是否可以评论
    private Boolean commented;
    // 是否可以点赞
    private Boolean appreciable;
    // 自定义链接
    private String urlSequence;
    // 置顶
    private Boolean top;
    // 创建时间
    @CreatedDate
    private Date createDate;
    // 更新时间
    @LastModifiedDate
    private Date updateDate;

    private  String cover;

    private  String fromUrls;

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

    @Column(columnDefinition = "mediumtext")
    @Builder.Default
    private String mdContent = "";

    private  String remark;


}
