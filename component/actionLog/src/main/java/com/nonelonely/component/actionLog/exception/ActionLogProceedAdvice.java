package com.nonelonely.component.actionLog.exception;

import com.nonelonely.common.exception.advice.ExceptionAdvice;
import com.nonelonely.component.actionLog.action.SystemAction;
import com.nonelonely.component.actionLog.annotation.ActionLog;

/**
 * 运行时抛出的异常进行日志记录
 * @author nonelonely
 * @date 2019/4/6
 */
public class ActionLogProceedAdvice implements ExceptionAdvice {

    @Override
    @ActionLog(key = SystemAction.RUNTIME_EXCEPTION, action = SystemAction.class)
    public void run(RuntimeException e) {}
}
