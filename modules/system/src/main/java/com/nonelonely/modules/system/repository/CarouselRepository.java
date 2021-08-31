package com.nonelonely.modules.system.repository;

import com.nonelonely.modules.system.domain.Carousel;
import com.nonelonely.modules.system.repository.BaseRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @author nonelonely
 * @date 2020/11/16
 */
public interface CarouselRepository extends BaseRepository<Carousel, Long> {
    @Query(nativeQuery = true, value = "select * from or_carousel a WHERE  a.over_time  >= DAY(NOW()) and  status = 1")
    List<Carousel> findCarouselList();
}
