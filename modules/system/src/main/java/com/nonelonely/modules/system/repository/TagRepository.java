package com.nonelonely.modules.system.repository;

import com.nonelonely.modules.system.domain.Tag;
import com.nonelonely.modules.system.repository.BaseRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @author nonelonely
 * @date 2020/01/01
 */
public interface TagRepository extends BaseRepository<Tag, Long> {

    Tag findByName(String name);
    /**
     * 查询文章/笔记的相关标签，并selected
     *
     * @param referId
     * @param type
     * @return
     */
    @Query(nativeQuery = true,
            value = "SELECT a.id,a.name, IF(COUNT(1) > 1, 'selected', '') AS selected" +
                    "        FROM ((SELECT t.* FROM nb_tag t)" +
                    "              UNION ALL" +
                    "              (SELECT t.*" +
                    "               FROM nb_tag t" +
                    "               WHERE t.id IN (SELECT tr.tag_id FROM nb_tag_refer tr WHERE tr.refer_id = ?1 AND tr.type = ?2))) a" +
                    "        GROUP BY a.`name`")
    List<Object[]> findTagListSelected(Long referId, String type);


    /**
     * 查询文章的标签
     *
     * @param referId
     * @param show
     * @return
     */
    @Query(nativeQuery = true,
            value = "SELECT * FROM nb_tag WHERE  id IN" +
                    " (SELECT tag_id FROM nb_tag_refer WHERE refer_id =?1 AND `show`= ?2 AND type = '1')")
    List<Tag> findArticleTags(long referId, boolean show);
}
