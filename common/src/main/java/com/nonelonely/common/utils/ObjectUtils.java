package com.nonelonely.common.utils;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 实体类操作
 *
 * @author liuming
 * @create 2018-01-30 9:43
 */
public class ObjectUtils {
    private static final Logger logger = LoggerFactory.getLogger(ObjectUtils.class);

    /**
     * 合并同一个类
     *
     * @param targetBean 目标
     * @param sourceBean 来源
     * @return
     */
    public static Object mergeSameClass(Object targetBean, Object sourceBean) {
        try {
            if (targetBean.getClass().isInstance(sourceBean)) {
                Class sourceBeanClass = sourceBean.getClass();
                Class targetBeanClass = targetBean.getClass();

                Field[] sourceFields = sourceBeanClass.getDeclaredFields();
                Field[] targetFields = targetBeanClass.getDeclaredFields();
                for (int i = 0; i < sourceFields.length; i++) {
                    Field sourceField = sourceFields[i];
                    Field targetField = targetFields[i];
                    sourceField.setAccessible(true);
                    targetField.setAccessible(true);
                    if (sourceField.get(sourceBean) != null && !"serialVersionUID".equals(sourceField.getName().toString())) {
                        targetField.set(targetBean, sourceField.get(sourceBean));
                    }
                }
            }
        } catch (IllegalArgumentException | IllegalAccessException e) {
            logger.error(e.getMessage(), e);
        }
        return targetBean;
    }

    /**
     * 将sourceBean中的属性名相同的属性值合并到targetBean
     *
     * @param targetBean
     * @param sourceBean
     * @return
     */
    public static <T> T merge(T targetBean, Object sourceBean) {
        try {
            Class sourceBeanClass = sourceBean.getClass();
            Class targetBeanClass = targetBean.getClass();

            Field[] sourceFields = mergeParent(sourceBeanClass.getDeclaredFields(), sourceBeanClass);
            for (int i = 0; i < sourceFields.length; i++) {
                Field sourceField = sourceFields[i];
                sourceField.setAccessible(true);
                if (sourceField.get(sourceBean) != null && !"serialVersionUID".equals(sourceField.getName().toString())) {
                    try {
                        Field targetField = targetBeanClass.getDeclaredField(sourceField.getName());
                        targetField.setAccessible(true);
                        targetField.set(targetBean, sourceField.get(sourceBean));
                    } catch (Exception e) {
                        // 字段不存在时不做处理
                    }
                }
            }
        } catch (IllegalArgumentException | IllegalAccessException e) {
            logger.error(e.getMessage(), e);
        }
        return targetBean;
    }

    /**
     * 合并属性
     *
     * @return
     */
    private static Field[] mergeParent(Field[] fields, Class sourceBeanClass) {
        Class parentClass = sourceBeanClass.getSuperclass();
        if (parentClass != null) {
            return ArrayUtils.addAll(fields, parentClass.getDeclaredFields());
        }
        return fields;
    }

    /**
     * 检验类中的属性值不为空
     *
     * @param bean
     * @param paramName
     * @return
     */
    public static boolean validParamNotNull(Object bean, String paramName) {
        Class beanClass = bean.getClass();
        Field targetField = null;
        try {
            targetField = beanClass.getDeclaredField(paramName);
            targetField.setAccessible(true);
            if (targetField.get(bean) != null) {
                return true;
            }
        } catch (Exception e) {
        }
        return false;
    }

    /**
     * 分别调用getset方法,copy属性;
     * 必须保证目标对象和来源对象内属性相同
     *
     * @param target 目标对象
     * @param source 来源对象,来源对象可以为Map但是必须保证fields和key一致;
     * @param fields 属性,首拼大小写无所谓,后续必须保持一致;
     * @return
     */
    public static <T> T copyFields(T target, Object source, String... fields) {
        org.springframework.beans.BeanWrapperImpl targetBean = new org.springframework.beans.BeanWrapperImpl(target);
        org.springframework.beans.BeanWrapperImpl sourceBean = new org.springframework.beans.BeanWrapperImpl(source);
        Boolean isMap = source instanceof Map;
        for (String field : fields) {
            if (isMap) {
                targetBean.setPropertyValue(field, ((Map) source).get(field));
            } else {
                //复制A类的属性到B类;首字符大小写无所谓,后续必须保证统一;
                targetBean.setPropertyValue(field, sourceBean.getPropertyValue(field));
            }
        }
        return target;
    }

    public static <T extends Comparable<T>> boolean compareList(List<T> a, List<T> b) {
        if (a.size() != b.size()) {
            return false;
        }
        Collections.sort(a);
        Collections.sort(b);
        for (int i = 0; i < a.size(); i++) {
            if (!a.get(i).equals(b.get(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 得到指定属性的值
     *
     * @param object
     * @param targetFieldName
     * @return
     * @throws Exception
     */
    public static Object getFieldValueByObject(Object object, String targetFieldName) {
        try {
            targetFieldName = upperField(targetFieldName);
            // 获取该对象的Class
            Class objClass = object.getClass();
            // 获取所有的属性数组
            Field[] fields = objClass.getDeclaredFields();
            for (Field field : fields) {
                // 属性名称
                String currentFieldName;
                boolean has_JsonProperty = field.isAnnotationPresent(JsonProperty.class);
                if (has_JsonProperty) {
                    currentFieldName = field.getAnnotation(JsonProperty.class).value();
                } else {
                    currentFieldName = field.getName();
                }
                if (currentFieldName.equals(targetFieldName)) {
                    field.setAccessible(true);
                    return field.get(object); // 通过反射拿到该属性在此对象中的值(也可能是个对象)
                }
            }
        } catch (SecurityException e) {
            logger.error(e.getMessage(), e);
        } catch (IllegalArgumentException e) {
            logger.error(e.getMessage(), e);
        } catch (IllegalAccessException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * 设置指定属性的值
     *
     * @param object
     * @param targetFieldName
     * @return
     * @throws Exception
     */
    public static void setFieldValueByObject(Object object, String targetFieldName, Object value) {
        try {
            Class targetBeanClass = object.getClass();
            Field targetField = targetBeanClass.getDeclaredField(upperField(targetFieldName));
            if (targetField != null) {
                targetField.setAccessible(true);
                targetField.set(object, value);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * 将首字母和带 _ 后第一个字母 转换成大写
     *
     * @param str
     * @return
     */
    public static String upperField(String str) {
        // 字符串缓冲区
        StringBuffer sbf = new StringBuffer();
        // 如果字符串包含 下划线
        if (str.contains("_")) {
            // 按下划线来切割字符串为数组
            String[] split = str.split("_");
            // 添加到字符串缓冲区
            sbf.append(split[0]);
            // 循环数组操作其中的字符串
            for (int i = 1, index = split.length; i < index; i++) {
                // 转换成字符数组
                char[] ch = split[i].toCharArray();
                // 判断首字母是否是字母
                if (ch[0] >= 'a' && ch[0] <= 'z') {
                    // 利用ASCII码实现大写
                    ch[0] = (char) (ch[0] - 32);
                }
                sbf.append(ch);
            }
            return sbf.toString();
        }
        return str;
    }

    /**
     * 将null值转化为空字符串
     *
     * @param object
     * @return
     */
    public static Object toJSONString(Object object) {
        if (object instanceof List) {
            return listToJSONString((List) object);
        }
        if (object == null) {
            return "";
        }
        return object;
    }

    /**
     * 将字符串转换为数组
     *
     * @param str
     * @return
     */
    public static String[] stringToArray(String str) {
        String[] arr = {str};
        return arr;
    }

    /**
     * 将列表转换为字符串
     *
     * @param list 列集合
     * @return
     */
    public static String listToJSONString(List list) {
        StringBuilder stringBuilder = new StringBuilder("[");
        if (!CollectionUtils.isEmpty(list)) {
            boolean isFirst = true;
            for (Object param : list) {
                if (!isFirst) {
                    stringBuilder.append(",");
                }
                if (param instanceof String) {
                    stringBuilder.append("\"").append(param).append("\"");
                } else {
                    stringBuilder.append(param);
                }
            }
        }
        stringBuilder.append("]");
        return stringBuilder.toString();
    }
}
