package com.nonelonely.modules.system.domain;

import com.nonelonely.common.utils.StatusUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 描述:
 * ${DESCRIPTION}
 *
 * @author nonelonely
 * @create 2020-11-11 20:32
 * 个人博客地址：https://www.nonelonely.com
 */
@Data
@Entity
@Table(name="or_ranking")
@EntityListeners(AuditingEntityListener.class)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Ranking implements Serializable {
    // 主键ID
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;  //

    private Long contentId; //与文章ID关联

    private  Integer hits; //记录每天点击率

    // 创建时间
    @CreatedDate()
    private Date createDate;



}
