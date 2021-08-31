package com.nonelonely.modules.system.domain.permission;

import com.nonelonely.common.enums.StatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * created by Wuwenbin on 2018/7/18 at 14:01
 *
 * @author wuwenbin
 */
@Entity
@Table(name = "sys_resource")
@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class NBSysResource implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false, length = 11)
    private Long id;

    @Column(nullable = false)
    private String url;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, length = 50)
    private String permission;

    @Enumerated(EnumType.STRING)
    private ResType type;

    @Column(name = "[group]")
    private String group;

    private String remark;

    // 创建时间
    @CreatedDate
    private Date createDate;
    // 更新时间
    @LastModifiedDate
    private Date updateDate;
    // 数据状态
    private Byte status = StatusEnum.OK.getCode();
    /**
     * url的类型
     */
    public enum ResType {
        /**
         * 可以做菜单栏的导航链接
         */
        NAV_LINK,

        /**
         * 其他类型
         */
        OTHER
    }

}
