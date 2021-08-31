package com.nonelonely.modules.system.repository;

import com.nonelonely.modules.system.domain.Article;
import com.nonelonely.modules.system.domain.Comment;
import com.nonelonely.modules.system.repository.BaseRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @author nonelonely
 * @date 2020/01/02
 */
public interface CommentRepository extends BaseRepository<Comment, Long> {

    List<Comment> findAllByArticleAndStatus(Article article, Byte status);
    @Query(nativeQuery = true, value = "select count(1) from or_comment where status != 3")
    int countComment();


    @Query(nativeQuery = true, value = "SELECT COUNT(1)FROM or_comment WHERE TO_DAYS( NOW( ) ) = TO_DAYS(create_date )")
    int countCommentNow();
}
