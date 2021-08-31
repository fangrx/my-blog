package com.nonelonely.modules.system.repository;

import com.nonelonely.modules.system.domain.ScheduledTask;
import com.nonelonely.modules.system.repository.BaseRepository;

import java.util.List;

/**
 * @author nonelonely
 * @date 2020/04/15
 */
public interface ScheduledTaskRepository extends BaseRepository<ScheduledTask, Long> {



            ScheduledTask findByTaskKey(String teskKey);


            List<ScheduledTask> findAllByInitStartFlagAndStatus(int initStartFlag, Byte status);
}
