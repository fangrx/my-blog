package com.nonelonely.common.utils;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;

/**
 * EhCache缓存操作工具
 * @author nonelonely
 * @date 2018/11/7
 */
public class EhCacheUtil {

    /**
     * 获取EhCacheManager管理对象
     */
    public static CacheManager getCacheManager(){
        return SpringContextUtil.getBean(CacheManager.class);
    }

    /**
     * 获取EhCache缓存对象
     */
    public static Cache getCache(String name){
        return getCacheManager().getCache(name);
    }

    /**
     * 获取字典缓存对象
     */
    public static Cache getDictCache(){
        return getCacheManager().getCache("dictionary");
    }
    /**
     * 获取系统参数
     */
    public static Cache getParamCache(){
        return getCacheManager().getCache("param");
    }

    /**
     * 获取邮件Id缓存
     */
    public static Cache getMailCache(){
        return getCacheManager().getCache("mail");
    }

}
