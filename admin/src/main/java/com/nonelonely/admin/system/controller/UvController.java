package com.nonelonely.admin.system.controller;

import com.nonelonely.admin.system.validator.UvValid;
import com.nonelonely.common.enums.StatusEnum;
import com.nonelonely.common.utils.EntityBeanUtil;
import com.nonelonely.common.utils.ResultVoUtil;
import com.nonelonely.common.utils.StatusUtil;
import com.nonelonely.common.vo.ResultVo;
import com.nonelonely.modules.system.domain.Uv;
import com.nonelonely.modules.system.domain.permission.NBAuth;
import com.nonelonely.modules.system.repository.UvRepository;
import com.nonelonely.modules.system.service.UvService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import static com.nonelonely.modules.system.domain.permission.NBAuth.Group.ROUTER;
import static com.nonelonely.modules.system.domain.permission.NBSysResource.ResType.NAV_LINK;
import static com.nonelonely.modules.system.domain.permission.NBSysResource.ResType.OTHER;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author nonelonely
 * @date 2020/03/12
 */
@Controller
@RequestMapping("/system/uv")
public class UvController {

    @Autowired
    private UvService uvService;
    @Autowired
    private UvRepository uvRepository;
    /**
     * 列表页面
     */
    @GetMapping("/index")
    @RequiresPermissions("system:uv:index")
    @NBAuth(value = "system:uv:index", remark = "统计流量列表页面", type = OTHER, group = ROUTER)
    public String index(Model model, Uv uv) {

        // 创建匹配器，进行动态查询匹配
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withMatcher("fromUrl", match -> match.contains())
                .withMatcher("nowUrl", match -> match.contains());

        // 获取数据列表
        Example<Uv> example = Example.of(uv, matcher);
        Page<Uv> list = uvService.getPageList(example);

        // 封装数据
        model.addAttribute("list", list.getContent());
        model.addAttribute("page", list);
        return "/system/uv/index";
    }

    /**
     * 跳转到详细页面
     */
    @GetMapping("/detail/{id}")
    @RequiresPermissions("system:uv:detail")
    @NBAuth(value = "system:uv:detail", remark = "统计流量详细页面", type = NAV_LINK, group = ROUTER)
    public String toDetail(@PathVariable("id") Uv uv, Model model) {
        model.addAttribute("uv",uv);
        return "/system/uv/detail";
    }

    /**
     * 设置一条或者多条数据的状态
     */
    @RequestMapping("/status/{param}")
    @RequiresPermissions("system:uv:status")
    @ResponseBody
    @NBAuth(value = "system:uv:status", remark = "统计流量数据状态", type = NAV_LINK, group = ROUTER)
    public ResultVo status(
            @PathVariable("param") String param,
            @RequestParam(value = "ids", required = false) List<Long> ids) {
        // 更新状态
        StatusEnum statusEnum = StatusUtil.getStatusEnum(param);
        if (uvService.updateStatus(statusEnum, ids)) {
            return ResultVoUtil.success(statusEnum.getMessage() + "成功");
        } else {
            return ResultVoUtil.error(statusEnum.getMessage() + "失败，请重新操作");
        }
    }

    @GetMapping("/countUv")
    @ResponseBody
    public List countUv(String type){
         if ("Uv".equals(type)){
             List<Map> mapuv = uvRepository.findUv(0);
             List<Map> omapuv = uvRepository.findUv(1);
             return swithHours(mapuv,omapuv);
         }else
        if ("Ip".equals(type)){
            List<Map> mapuv = uvRepository.findIp(0);
            List<Map> omapuv = uvRepository.findIp(1);
            return swithHours(mapuv,omapuv);
        }else {
            List<Map> mapuv = uvRepository.findPv(0);
            List<Map> omapuv = uvRepository.findPv(1);
            return swithHours(mapuv,omapuv);
        }

    }

    private List swithHours(List<Map> maps,List<Map> olds){
        List lists =new ArrayList();
        Map nowMap = new HashMap();
        Map oldMap = new HashMap();
        for(int i=0;i<maps.size();i++){

            Map<String,Object> m=maps.get(i);
            nowMap.put(m.get("hours"),m.get("count"));
        }
        for(int i=0;i<olds.size();i++){
            Map<String,Object> m=olds.get(i);
            oldMap.put(m.get("hours"),m.get("count"));
        }
        for (int i=0;i<24;i++){
            List list =new ArrayList();
            String key ="00";
            if (i<10) {
                key = "0" + i;
            }else {
                key = i+"";
            }
            String now = nowMap.get(key) == null ? "0": nowMap.get(key).toString();
            String old = oldMap.get(key) == null ? "0": oldMap.get(key).toString();
            list.add(key);
            list.add(now);
            list.add(old);
            lists.add(list);
        }

        return lists;
    }

    @GetMapping("/countBrowser")
    @ResponseBody
    public List countBrowser(String type){

        return uvRepository.findBrowser();

    }
}
