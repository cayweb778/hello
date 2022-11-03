package org.boozsoft.repo;

import org.boozsoft.domain.entity.AppGroupSysRole;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface SysRoleRepository extends ReactiveCrudRepository<AppGroupSysRole, String> {
        @Query("select case when max(role_num) !='' then max(role_num) else '0' end   from _app_group_sys_role")
        Mono<String> findByMaxRoleNum();
        @Query("select count(role_name) from _app_group_sys_role where role_name=:roleName")
        Mono<Long> findByRoleName(String roleName);
}
