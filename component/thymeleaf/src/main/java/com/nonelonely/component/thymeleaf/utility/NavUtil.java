package com.nonelonely.component.thymeleaf.utility;

import com.nonelonely.common.utils.SpringContextUtil;
import com.nonelonely.modules.system.domain.Menu;
import com.nonelonely.modules.system.service.MenuService;
import com.nonelonely.modules.system.service.impl.MenuServiceImpl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NavUtil {


    public static  Map<Long, Menu> getMenuList(String type){
        MenuService menuService =(MenuServiceImpl) SpringContextUtil.getBean("menuServiceImpl");
        // 增加菜单
        List<Menu> menus= menuService. getListBySortOk(Byte.valueOf(type));
        // 菜单键值对(ID->菜单)
        Map<Long, Menu> keyMenu = new HashMap<>(16);
        menus.forEach(menu -> {
            keyMenu.put(menu.getId(), menu);
        });
        // 封装菜单树形数据
        Map<Long, Menu> treeMenu = new HashMap<>(16);
        keyMenu.forEach((id, menu) -> {
            if(keyMenu.get(menu.getPid()) != null){
                keyMenu.get(menu.getPid()).getChildren().put(Long.valueOf(menu.getSort()), menu);
            }else{
                if(menu.getPid()==0){ //一级
                    treeMenu.put(Long.valueOf(menu.getSort()), menu);
                }
            }

        });

        return  treeMenu;
    }
}
