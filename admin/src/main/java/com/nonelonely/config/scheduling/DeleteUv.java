package com.nonelonely.config.scheduling;


import com.nonelonely.component.scheduledTask.config.ScheduledTaskJob;
import com.nonelonely.modules.system.repository.UvRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DeleteUv  implements ScheduledTaskJob {

    @Autowired
    UvRepository uvRepository;


    @Override
    public void run() {
        int newCount = uvRepository.deleteUvBefor3();
        log.info("删除了Uv数据一共有 "+newCount+" 条");
    }
}
