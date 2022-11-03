package org.boozsoft.repo.group;

import org.boozsoft.domain.entity.group.GroupUser;
import org.boozsoft.domain.vo.GroupUserVo;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface GroupSysUserRepository extends ReactiveCrudRepository<GroupUser, String> {
    Mono<GroupUser> findByUsernameAndAndPassword(String username,String password);
    Flux<GroupUser> findByUsername(String username);
    Flux<GroupUser> findByPhone(String phone);
    Flux<GroupUser> findByEmail(String phone);
    Flux<GroupUser> findByRealName(String realName);
    Flux<GroupUser> findByUsernameIn(List<String> username);
    Flux<GroupUser> findByPhoneIn(List<String> phone);
    Flux<GroupUser> findAllByflag(String flag);

    @Query("select id as key, username as title from _app_group_sys_user where flag = '1'")
    Flux<GroupUserVo> findByflag();

    @Modifying
    @Query("insert into _app_group_sys_user_role(id,user_id,role_id) values (:id,:userId,:roleId)")
    Flux<Object[]> saveUserRole(String id ,String userId,String roleId);

    @Query("select role_id from _app_group_sys_user_role where 1=1 and user_id=:userId ")
    Flux<String> findRolesByUserId(String userId);

    @Modifying
    @Query("delete from _app_group_sys_user_role where 1=1 and user_id=:userId")
    Mono<Void> deleteUserRoleByUserId(String userId);
    Mono<Long> countAllBy();

}
