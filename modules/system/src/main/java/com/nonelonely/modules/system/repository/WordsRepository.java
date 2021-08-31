package com.nonelonely.modules.system.repository;

import com.nonelonely.modules.system.domain.Words;
import com.nonelonely.modules.system.repository.BaseRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * @author nonelonely
 * @date 2020/02/15
 */
public interface WordsRepository extends BaseRepository<Words, Long> {

    @Query(nativeQuery = true, value = "SELECT * FROM or_words  ORDER BY rand() LIMIT 1")
    Words findRandWords();
}
