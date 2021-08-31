package com.nonelonely.modules.system.repository;

import com.nonelonely.modules.system.domain.Dept;
import com.nonelonely.modules.system.domain.User;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @author nonelonely
 * @date 2018/8/14
 */
 public interface UserRepository extends BaseRepository<User, Long> {

    /**
     * 根据用户名查询用户数据
     * @param username 用户名
     * @return 用户数据
     */
     User findByUsername(String username);

    /**
     * 根据用户名查询用户数据,且排查指定ID的用户
     * @param username 用户名
     * @param id 排除的用户ID
     * @return 用户数据
     */
     User findByUsernameAndIdNot(String username, Long id);

    /**
     * 查找多个相应部门的用户列表
     * @param dept 部门对象
     * @return 用户列表
     */
     List<User> findByDept(Dept dept);

    /**
     * 删除多条数据
     * @param ids ID列表
     * @return 影响行数
     */
     Integer deleteByIdIn(List<Long> ids);


     User findByQqOpenId(String openId);

    @Query(nativeQuery = true, value = "SELECT COUNT(1)FROM sys_user WHERE TO_DAYS( NOW( ) ) = TO_DAYS(create_date )")
    int countUserNow();

    @Query(nativeQuery = true, value = "SELECT a.* FROM sys_user a , sys_user_role b WHERE a.id = b.user_id and b.role_id = ?1 ")
    List<User> getUserList(Long role_id);
}
