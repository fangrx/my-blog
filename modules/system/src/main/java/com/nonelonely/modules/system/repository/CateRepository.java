package com.nonelonely.modules.system.repository;

import com.nonelonely.modules.system.domain.Cate;
import com.nonelonely.modules.system.repository.BaseRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Map;

/**
 * @author nonelonely
 * @date 2020/01/01
 */
public interface CateRepository extends BaseRepository<Cate, Long> {


    List<Cate> findByPidAndIsShow(long pid, boolean isShow);
}
