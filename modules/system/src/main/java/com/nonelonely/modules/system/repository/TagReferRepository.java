package com.nonelonely.modules.system.repository;

import com.nonelonely.modules.system.domain.NBTagRefer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TagReferRepository extends JpaRepository<NBTagRefer, Long> {

    /**
     * 根据 refer_id 删除相关的记录
     *
     * @param referId
     */
    void deleteByReferId(Long referId);

    /**
     * 查找是否有内容使用此标签，返回使用次数
     *
     * @param tagId
     * @return
     */
    @Query("SELECT count(tr) FROM NBTagRefer tr WHERE tr.tagId = ?1")
    long countByTagId(Long tagId);

    /**
     * 查询tags标签面板中的数据并且统计每个标签的使用次数
     *
     * @return
     */
    @Query(nativeQuery = true, value = "SELECT tt.id, tt.`name`, count(ttr.tag_id) AS cnt " +
            "FROM nb_tag_refer ttr LEFT JOIN nb_tag tt ON tt.id = ttr.tag_id " +
            "GROUP BY ttr.tag_id ORDER BY cnt DESC LIMIT ?")
    List<Object[]> findTagsTab(int limit);

    /**
     * 根据tag参考类型和tagId查找对应的tag参照对象集合
     *
     * @param tagId
     * @return
     */
    List<NBTagRefer> findByTagId(Long tagId);

    /**
     * 根据tag参考类型和tagId查找对应的tag参照对象集合
     *
     * @param tagId
     * @param type
     * @return
     */
   List<NBTagRefer> findByTagIdAndType(Long tagId, String type);
}


