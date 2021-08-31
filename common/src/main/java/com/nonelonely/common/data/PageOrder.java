package com.nonelonely.common.data;

import cn.hutool.core.util.StrUtil;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 分页排序对象
 * Created by wuwenbin on 2014/11/1
 *
 * @author wuwenbin
 * @since 1.0.0
 */
public class PageOrder {

    /**
     * 排序字段
     */
    protected String sort;

    /**
     * 排序方式
     */
    protected String order;


    /**
     * getters and setters
     */

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public String getOrderBy() {
        if (!StringUtils.isEmpty(this.sort) && !StringUtils.isEmpty(this.order)) {
            return StrUtil.format("`{}` {}", this.sort, this.order);
        }
        return "";
    }

    public void setOrderBy(String orderBy) {
        if (!StringUtils.isEmpty(orderBy)) {
            String[] temp = orderBy.split(" ");
            int expectLength = 2;
            if (temp.length == expectLength) {
                this.sort = temp[0].replace("`", "");
                this.order = temp[1];
            }
        }
    }
    /**
     * 除了前台传过来的分页信息，如果需要额外手动添加一些排序信息，则使用此方法
     *
     * @param page
     * @param otherFields
     * @param <T>
     * @return
     */
    public static  <T> Sort getJpaSortWithOther(Pagination<T> page, Map<String, String> otherFields) {
        final String asc = "asc", desc = "desc";
        String orderField = page.getSort();
        String orderDirection = page.getOrder();
        List<Sort.Order> orders = new ArrayList<>();
        if (!StringUtils.isEmpty(orderField)) {
            if (StringUtils.isEmpty(orderDirection)) {
                orders.add(Sort.Order.by(orderField));
            } else {
                if (asc.equalsIgnoreCase(orderDirection)) {
                    orders.add(Sort.Order.asc(orderField));
                } else if (desc.equalsIgnoreCase(orderDirection)) {
                    orders.add(Sort.Order.desc(orderField));
                }
            }
        }
        for (String filed : otherFields.keySet()) {
            String direction = otherFields.get(filed);
            if (asc.equalsIgnoreCase(direction)) {
                Sort.Order order = Sort.Order.asc(filed);
                orders.add(order);
            } else {
                Sort.Order order = Sort.Order.desc(filed);
                orders.add(order);
            }
        }
        return orders.size() > 0 ? Sort.by(orders) : Sort.unsorted();
    }
}
