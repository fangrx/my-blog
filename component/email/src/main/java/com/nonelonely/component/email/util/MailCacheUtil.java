package com.nonelonely.component.email.util;

import com.nonelonely.common.utils.EhCacheUtil;
import com.nonelonely.common.utils.SpringContextUtil;
import com.nonelonely.modules.system.domain.Param;
import com.nonelonely.modules.system.service.ParamService;
import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MailCacheUtil {

    private static Cache mailCache = EhCacheUtil.getMailCache();

    /**
     * 获取系统参数值
     * @param name 系统参数名称
     */
    @SuppressWarnings("unchecked")
    public static String get(String name){
       String value = null;
        Element dictEle = mailCache.get(name);
        if(dictEle != null){
            value = dictEle.getObjectValue().toString();
        }
        return value;
    }
    public static void set(String name,String value){

        mailCache.put(new Element(name, value));

    }
    /**
     * 清除缓存中指定的数据
     * @param label 字典标识
     */
    public static void clearCache(String label){
        Element dictEle = mailCache.get(label);
        if (dictEle != null){
            mailCache.remove(label);
        }
    }


}
