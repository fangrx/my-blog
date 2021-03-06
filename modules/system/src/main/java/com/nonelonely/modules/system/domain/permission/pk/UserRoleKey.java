package com.nonelonely.modules.system.domain.permission.pk;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * @author wuwenbin
 */
@Data
@Embeddable
@NoArgsConstructor
public class UserRoleKey implements Serializable {

    @Column(length = 11, nullable = false)
    private Long userId;

    @Column(length = 11, nullable = false)
    private Long roleId;
}
