package com.nonelonely.admin.system.controller;

import com.nonelonely.admin.system.validator.DeptValid;
import com.nonelonely.common.enums.StatusEnum;
import com.nonelonely.common.utils.EntityBeanUtil;
import com.nonelonely.common.utils.HttpServletUtil;
import com.nonelonely.common.utils.ResultVoUtil;
import com.nonelonely.common.utils.StatusUtil;
import com.nonelonely.common.vo.ResultVo;
import com.nonelonely.component.actionLog.action.SaveAction;
import com.nonelonely.component.actionLog.action.StatusAction;
import com.nonelonely.component.actionLog.annotation.ActionLog;
import com.nonelonely.component.actionLog.annotation.EntityParam;
import com.nonelonely.modules.system.domain.Dept;
import com.nonelonely.modules.system.domain.permission.NBAuth;
import com.nonelonely.modules.system.service.DeptService;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static com.nonelonely.modules.system.domain.permission.NBAuth.Group.ROUTER;
import static com.nonelonely.modules.system.domain.permission.NBSysResource.ResType.NAV_LINK;
import static com.nonelonely.modules.system.domain.permission.NBSysResource.ResType.OTHER;

/**
 * @author nonelonely
 * @date 2018/12/02
 */
@Controller
@RequestMapping("/system/dept")
public class DeptController {

    @Autowired
    private DeptService deptService;

    /**
     * 跳转到列表页面
     */
    @GetMapping("/index")
    @RequiresPermissions("system:dept:index")
    @NBAuth(value = "system:dept:index", remark = "部门列表页面", type = OTHER, group = ROUTER)
    public String index(Model model) {
        String search = HttpServletUtil.getRequest().getQueryString();
        model.addAttribute("search", search);
        return "/system/dept/index";
    }

    /**
     * 部门数据列表
     */
    @GetMapping("/list")
    @RequiresPermissions(value = {"system:dept:index", "system:user:index"}, logical = Logical.OR)
    @ResponseBody
    public ResultVo list(Dept dept) {
        // 创建匹配器，进行动态查询匹配
        ExampleMatcher matcher = ExampleMatcher.matching().
                withMatcher("title", match -> match.contains());

        // 获取部门列表
        Example<Dept> example = Example.of(dept, matcher);
        Sort sort = new Sort(Sort.Direction.ASC, "sort");
        List<Dept> list = deptService.getListByExample(example, sort);
        return ResultVoUtil.success(list);
    }

    /**
     * 获取排序菜单列表
     */
    @GetMapping("/sortList/{pid}/{notId}")
    @RequiresPermissions({"system:dept:add", "system:dept:edit"})
    @ResponseBody
    public Map<Integer, String> sortList(
            @PathVariable(value = "pid", required = false) Long pid,
            @PathVariable(value = "notId", required = false) Long notId){
        // 本级排序菜单列表
        notId = notId != null ? notId : (long) 0;
        List<Dept> levelDept = deptService.getListByPid(pid, notId);
        Map<Integer, String> sortMap = new TreeMap<>();
        for (int i = 1; i <= levelDept.size(); i++) {
            sortMap.put(i, levelDept.get(i - 1).getTitle());
        }
        return sortMap;
    }

    /**
     * 跳转到添加页面
     */
    @GetMapping({"/add", "/add/{pid}"})
    @RequiresPermissions("system:dept:add")
    @NBAuth(value = "system:dept:add", remark = "部门添加页面", type = NAV_LINK, group = ROUTER)
    public String toAdd(@PathVariable(value = "pid", required = false) Dept pDept, Model model) {
        model.addAttribute("pDept", pDept);
        return "/system/dept/add";
    }

    /**
     * 跳转到编辑页面
     */
    @GetMapping("/edit/{id}")
    @RequiresPermissions("system:dept:edit")
    @NBAuth(value = "system:dept:edit", remark = "部门编辑页面", type = NAV_LINK, group = ROUTER)
    public String toEdit(@PathVariable("id") Dept dept, Model model) {
        Dept pDept = deptService.getById(dept.getPid());
        if (pDept == null) {
            pDept = new Dept();
            pDept.setId((long) 0);
            pDept.setTitle("顶级");
        }

        model.addAttribute("dept", dept);
        model.addAttribute("pDept", pDept);
        return "/system/dept/add";
    }

    /**
     * 保存添加/修改的数据
     * @param valid 表单验证对象
     */
    @PostMapping("/save")
    @RequiresPermissions({"system:dept:add", "system:dept:edit"})
    @ResponseBody
    @ActionLog(name = "部门管理", message = "部门：${title}", action = SaveAction.class)
    public ResultVo save(@Validated DeptValid valid, @EntityParam Dept dept) {
        if (dept.getId() == null) {
            // 排序为空时，添加到最后
            if(dept.getSort() == null){
                Integer sortMax = deptService.getSortMax(dept.getPid());
                dept.setSort(sortMax !=null ? sortMax - 1 : 0);
            }
        }

        // 添加/更新全部上级序号
        if (dept.getPid() != 0) {
            Dept pDept = deptService.getById(dept.getPid());
            dept.setPids(pDept.getPids() + ",[" + dept.getPid() + "]");
        } else {
            dept.setPids("[0]");
        }

        // 将验证的数据复制给实体类
        if (dept.getId() != null) {
            Dept beDept = deptService.getById(dept.getId());
            EntityBeanUtil.copyProperties(beDept, dept, "dept");
        }

        // 排序功能
        Integer sort = dept.getSort();
        Long notId = dept.getId() != null ? dept.getId() : 0;
        List<Dept> levelDept = deptService.getListByPid(dept.getPid(), notId);
        levelDept.add(sort, dept);
        for (int i = 1; i <= levelDept.size(); i++) {
            levelDept.get(i - 1).setSort(i);
        }

        // 保存数据
        deptService.save(levelDept);
        return ResultVoUtil.SAVE_SUCCESS;
    }

    /**
     * 跳转到详细页面
     */
    @GetMapping("/detail/{id}")
    @RequiresPermissions("system:dept:detail")
    @NBAuth(value = "system:dept:detail", remark = "部门详细页面", type = NAV_LINK, group = ROUTER)
    public String toDetail(@PathVariable("id") Dept dept, Model model){
        model.addAttribute("dept", dept);
        return "/system/dept/detail";
    }

    /**
     * 设置一条或者多条数据的状态
     */
    @RequestMapping("/status/{param}")
    @RequiresPermissions("system:dept:status")
    @ResponseBody
    @ActionLog(name = "部门状态", action = StatusAction.class)
    @NBAuth(value = "system:dept:status", remark = "部门数据状态", type = NAV_LINK, group = ROUTER)
    public ResultVo status(
            @PathVariable("param") String param,
            @RequestParam(value = "ids", required = false) List<Long> ids){
        // 更新状态
        StatusEnum statusEnum = StatusUtil.getStatusEnum(param);
        if (deptService.updateStatus(statusEnum, ids)) {
            return ResultVoUtil.success(statusEnum.getMessage() + "成功");
        } else {
            return ResultVoUtil.error(statusEnum.getMessage() + "失败，请重新操作");
        }
    }

}
