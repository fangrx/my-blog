package com.nonelonely.config;


import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
@Component
public class GlobalContext{

        private static ConcurrentHashMap<String, Object> applicationContext = new ConcurrentHashMap<>(100);


        public void setApplicationObj (String key, Object value){
        applicationContext.put(key, value);
    }

        public <T > T getApplicationObj(String key) {
        //noinspection unchecked
        return (T) applicationContext.get(key);
    }

        public void removeApplicationObj (String key){
        applicationContext.remove(key);
    }
    }
