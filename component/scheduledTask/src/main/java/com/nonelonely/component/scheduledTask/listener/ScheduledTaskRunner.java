package com.nonelonely.component.scheduledTask.listener;

import com.nonelonely.common.enums.StatusEnum;
import com.nonelonely.component.scheduledTask.config.ScheduledTaskServices;
import com.nonelonely.modules.system.domain.ScheduledTask;
import com.nonelonely.modules.system.repository.ScheduledTaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @see @Order注解的执行优先级是按value值从小到大顺序。
 * 项目启动完毕后开启需要自启的任务
 */
@Component
@Order(value = 3)
public class ScheduledTaskRunner implements ApplicationRunner {
    /**
     * 日志
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduledTaskRunner.class);



    @Autowired
    private ScheduledTaskServices scheduledTaskServices;
    @Autowired
    private ScheduledTaskRepository scheduledTaskRepository;

    /**
     * 程序启动完毕后,需要自启的任务
     */
    @Override
    public void run(ApplicationArguments applicationArguments) throws Exception {
        LOGGER.info(" >>>>>> 项目启动完毕, 开启 => 需要自启的任务 开始!");
        List<ScheduledTask> scheduledTaskBeanList = scheduledTaskRepository.findAllByInitStartFlagAndStatus(1, StatusEnum.OK.getCode());
        scheduledTaskServices.initAllTask(scheduledTaskBeanList);
        LOGGER.info(" >>>>>> 项目启动完毕, 开启 => 需要自启的任务 结束！");
    }
}
