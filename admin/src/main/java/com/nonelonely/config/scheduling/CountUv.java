package com.nonelonely.config.scheduling;

import com.nonelonely.component.scheduledTask.config.ScheduledTaskJob;
import com.nonelonely.modules.system.domain.Param;
import com.nonelonely.modules.system.repository.ParamRepository;
import com.nonelonely.modules.system.repository.UvRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CountUv implements ScheduledTaskJob {

    @Autowired
    UvRepository uvRepository;
    @Autowired
    ParamRepository paramRepository;
    @Override
    public void run() {
        log.info("》》》》》》开始计算访客量");
        Param param =paramRepository.findFirstByName("visitor_counts");
        int newCount= uvRepository.findCountBefore();
        param.setValue(String.valueOf(Integer.valueOf(param.getValue())+newCount));
        paramRepository.save(param);
        log.info("》》》》》》结束计算访客量");
    }
}
