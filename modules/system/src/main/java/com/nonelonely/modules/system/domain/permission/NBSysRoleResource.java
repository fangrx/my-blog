package com.nonelonely.modules.system.domain.permission;

import com.nonelonely.modules.system.domain.permission.pk.RoleResourceKey;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

///**
// * created by Wuwenbin on 2018/7/18 at 14:09
// * @author wuwenbin
// */
//@Entity
//@Table(name = "sys_role_resource")
//@Data
//@Builder
//@AllArgsConstructor
//@NoArgsConstructor
//public class NBSysRoleResource implements Serializable {
//
//    @EmbeddedId
//    private RoleResourceKey pk;
//
//    @Column(nullable = false, columnDefinition = "tinyint(1)")
//    @Builder.Default
//    private Boolean enable = Boolean.TRUE;
//}
