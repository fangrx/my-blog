package com.nonelonely.common.exception;

import com.nonelonely.common.exception.advice.ResultExceptionAdvice;
import com.nonelonely.common.utils.ResultVoUtil;
import com.nonelonely.common.utils.SpringContextUtil;
import com.nonelonely.common.vo.ResultVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Objects;

/**
 * 全局统一异常处理
 * @author nonelonely
 * @date 2018/8/14
 */
@ControllerAdvice
@Slf4j
public class ResultExceptionHandler {

    /** 拦截自定义异常 */
    @ExceptionHandler(ResultException.class)
    @ResponseBody
    public ResultVo resultException(ResultException e){

        return ResultVoUtil.error(e.getCode(), e.getMessage());
    }

    /** 拦截表单验证异常 */
    @ExceptionHandler(BindException.class)
    @ResponseBody
    public ResultVo bindException(BindException e){
        BindingResult bindingResult = e.getBindingResult();

        return ResultVoUtil.error(Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage());
    }

    /** 拦截其他的运行时异常 并且保存到日志表里*/
    @ExceptionHandler(RuntimeException.class)
    @ResponseBody
    public ResultVo runtimeException(RuntimeException e) {
        ResultExceptionAdvice resultExceptionAdvice = SpringContextUtil.getBean(ResultExceptionAdvice.class);
        resultExceptionAdvice.runtimeException(e);

        return ResultVoUtil.error(500, e.getMessage());
    }
}
