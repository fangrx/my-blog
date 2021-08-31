package com.nonelonely.component.scheduledTask.config;

import com.nonelonely.modules.system.domain.ScheduledTask;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ScheduledTaskServices {
    /**
     * 所有任务列表
     */
    List<ScheduledTask> taskList();

    /**
     * 根据任务key 启动任务
     */
    Boolean start(String taskKey) ;

    /**
     * 根据任务key 停止任务
     */
    Boolean stop(String taskKey) ;

    /**
     * 根据任务key 重启任务
     */
    Boolean restart(String taskKey) ;


    /**
     * 程序启动时初始化  ==> 启动所有正常状态的任务
     */
    void initAllTask(List<ScheduledTask> scheduledTaskBeanList);

    /**
     * 获取分页列表数据
     * @param example 查询实例
     * @return 返回分页数据
     */
    Page<ScheduledTask> getPageList(Example<ScheduledTask> example);
}
