package com.nonelonely.common.utils;

import com.nonelonely.common.enums.ResultEnum;
import com.nonelonely.common.vo.ResultVo;
import com.nonelonely.common.data.URL;
import org.springframework.validation.FieldError;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * 响应数据(结果)最外层对象工具
 * @author nonelonely
 * @date 2018/10/15
 */
public class ResultVoUtil {

    public static ResultVo SAVE_SUCCESS = success("保存成功");

    /**
     * 操作成功
     * @param msg 提示信息
     * @param object 对象
     */
    public static ResultVo success(String msg, Object object){
        ResultVo<Object> resultVo = new ResultVo<>();
        resultVo.setMsg(msg);
        resultVo.setCode(ResultEnum.SUCCESS.getCode());
        resultVo.setData(object);
        return resultVo;
    }
    /**
     * 操作成功
     * @param msg 提示信息
     * @param object 对象
     */
    public static ResultVo custom(Integer code,String msg, Object object){
        ResultVo<Object> resultVo = new ResultVo<>();
        resultVo.setMsg(msg);
        resultVo.setCode(code);
        resultVo.setData(object);
        return resultVo;
    }


    /**
     * 操作成功，返回url地址
     * @param msg 提示信息
     * @param url URL包装对象
     */
    public static ResultVo success(String msg, URL url){
        return success(msg, url.getUrl());
    }

    /**
     * 操作成功，使用默认的提示信息
     * @param object 对象
     */
    public static ResultVo success(Object object){
        String message = ResultEnum.SUCCESS.getMessage();
        return success(message, object);
    }

    /**
     * 操作成功，返回提示信息，不返回数据
     */
    public static ResultVo success(String msg){
        Object object = null;
        return success(msg, object);
    }

    /**
     * 操作成功，不返回数据
     */
    public static ResultVo success(){
        return success(null);
    }

    /**
     * 操作有误
     * @param code 错误码
     * @param msg 提示信息
     */
    public static ResultVo error(Integer code, String msg){
        ResultVo resultVo = new ResultVo();
        resultVo.setMsg(msg);
        resultVo.setCode(code);
        return resultVo;
    }

    /**
     * 操作有误，使用默认400错误码
     * @param msg 提示信息
     */
    public static ResultVo error(String msg){
        Integer code = ResultEnum.ERROR.getCode();
        return error(code, msg);
    }

    /**
     * 操作有误，只返回默认错误状态码
     */
    public static ResultVo error(){
        return error(null);
    }

    /**
     * try catch且带有if条件判断的ajax处理
     *
     * @param ifCondition
     * @return
     */
    public static ResultVo ajaxDone(Supplier<Boolean> ifCondition, Supplier<String> operationInfo) {
        try {
            boolean res = ifCondition.get();
            if (res) {
                String msg = operationInfo.get() == null ? "操作成功" : operationInfo.get().concat("成功！");
                return success(msg);
            } else {
                String msg = operationInfo.get() == null ? "操作失败" : operationInfo.get().concat("失败！");
                return error(msg);
            }
        } catch (Exception e) {
            String msg = operationInfo.get() == null ? "操作出现异常" : operationInfo.get().concat("异常，异常信息：").concat(e.getMessage());
            return error(msg);
        }
    }

    /**
     * 没有判断的返回请求
     *
     * @param operation
     * @param param
     * @param ok
     * @param err
     * @param <T>
     * @return
     */
    public static <T> ResultVo ajaxDone(Consumer<T> operation, T param, String ok, String err) {
        try {
            operation.accept(param);
            return success(ok,param);
        } catch (Exception e) {
            e.printStackTrace();
            return error(err);
        }
    }
    /**
     * jsr303验证处理的错误信息
     *
     * @param fieldErrors
     * @return
     */
    public static ResultVo ajaxJsr303(List<FieldError> fieldErrors) {
        StringBuilder message = new StringBuilder();
        for (FieldError error : fieldErrors) {
            message.append(error.getField()).append(":").append(error.getDefaultMessage()).append("<br/>");
        }
        return error(message.toString());
    }
    /**
     * 类似如下的判定方式使用此方法：
     * if(ifc){
     * ifo
     * }else{
     * elseMsg
     * }
     *
     * @param ifc
     * @param ifo
     * @param elseMsg
     * @return
     */
    public static ResultVo ajaxDone(Supplier<Boolean> ifc, Supplier<ResultVo> ifo, Supplier<String> elseMsg) {
        if (ifc.get()) {
            return ifo.get();
        } else {
            return error(elseMsg.get());
        }
    }
}
