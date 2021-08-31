package com.nonelonely.modules.system.repository;

import com.nonelonely.common.constant.StatusConst;
import com.nonelonely.modules.system.domain.Param;
import com.nonelonely.modules.system.repository.BaseRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author nonelonely
 * @date 2020/01/02
 */
public interface ParamRepository extends BaseRepository<Param, Long> {

     Param findFirstByName(String name);

     @Modifying
     @Transactional
     @Query(value = "update or_param set value = ?1  where id =?2",nativeQuery = true)
      Integer updateValue(String value, Long id);
}
