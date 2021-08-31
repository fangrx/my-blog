package com.nonelonely.modules.system.domain.bo;

import com.nonelonely.modules.system.domain.Tag;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


/**
 * created by Wuwenbin on 2018/8/10 at 10:31
 * @author wuwenbin
 */
@Setter
@Getter
@ToString
public class TagBO extends Tag {
    private String selected;
}
