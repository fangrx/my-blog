package com.nonelonely.modules.system.domain;

import lombok.Data;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 描述:
 * ${DESCRIPTION}
 *
 * @author nonelonely
 * @create 2020-11-13 20:29
 * 个人博客地址：https://www.nonelonely.com
 */
@Data
@Entity
@Table(name="or_website")
public class WebSite implements Serializable {

    // 主键ID
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    private  Long contentId;


    private boolean baidu = false;
}
