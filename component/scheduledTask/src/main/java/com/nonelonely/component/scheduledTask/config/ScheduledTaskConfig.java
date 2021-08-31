package com.nonelonely.component.scheduledTask.config;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Configuration
public class ScheduledTaskConfig {
    /**
     * 日志
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduledTaskConfig.class);

    @Bean
    public ThreadPoolTaskScheduler threadPoolTaskScheduler() {
        LOGGER.info("创建定时任务调度线程池 start");
        ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setPoolSize(20);
        threadPoolTaskScheduler.setThreadNamePrefix("taskExecutor-");
        threadPoolTaskScheduler.setWaitForTasksToCompleteOnShutdown(true);
        threadPoolTaskScheduler.setAwaitTerminationSeconds(60);
        LOGGER.info("创建定时任务调度线程池 end");
        return threadPoolTaskScheduler;
    }

    /**
     * 初始化定时任务Map
     * key :任务key
     * value : 执行接口实现
     */
    @Bean(name = "scheduledTaskJobMap")
    public Map<String, ScheduledTaskJob> scheduledTaskJobMap() {
        return ScheduledTaskEnum.initScheduledTask();
    }

    /**
     * 初始化定时任务Map
     * key :任务key
     * value : 执行接口实现
     */
    @Bean(name = "blockingQueue")
    public BlockingQueue blockingQueue() {
        return new LinkedBlockingQueue<>(10000);
    }

}

