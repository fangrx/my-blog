package com.nonelonely.frontend.controller;



import com.nonelonely.component.shiro.ShiroUtil;
import com.nonelonely.modules.system.domain.Menu;
import com.nonelonely.modules.system.domain.User;
import com.nonelonely.modules.system.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@ControllerAdvice(basePackages = "com.nonelonely.frontend.controller")
public class GlobalController {
    @Autowired
    private MenuService menuService;
    @ModelAttribute
    public void addSettings(Model model) {
        // 增加登录信息
        User user = ShiroUtil.getSubject();
        model.addAttribute("userInfo",user);
        // 增加菜单
        List<Menu> menus= menuService. getListBySortOk((byte)2);
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
        model.addAttribute("treeMenu",treeMenu);
    }
}
