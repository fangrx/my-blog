package com.nonelonely.component.actionLog.action.model;

import com.nonelonely.modules.system.enums.ActionLogEnum;
import lombok.Getter;

/**
 * @author nonelonely
 * @date 2018/10/15
 */
@Getter
public class SystemMethod extends BusinessMethod{

    /** 日志类型 */
    protected Byte type = ActionLogEnum.SYSTEM.getCode();

    public SystemMethod(String method) {
        super(method);
    }

    public SystemMethod(String name, String method) {
        super(name, method);
    }
}
