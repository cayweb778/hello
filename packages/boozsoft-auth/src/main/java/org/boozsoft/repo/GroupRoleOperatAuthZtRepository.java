package org.boozsoft.repo;

import org.boozsoft.domain.entity.group.GroupRoleOperatAuthZt;
import org.boozsoft.domain.entity.group.GroupRoleOperatAuthZt;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface GroupRoleOperatAuthZtRepository extends ReactiveCrudRepository<GroupRoleOperatAuthZt, String> {

    @Query("select distinct account_co_code as zt_unique_code,account_year as zt_year from _app_group_sys_account_period order by account_co_code asc ,account_year desc")
    Flux<GroupRoleOperatAuthZt> findAllByOrderByZtYearDesc();
    Flux<GroupRoleOperatAuthZt> findAllByRoleUniqueCodeOrderByZtYearDesc(String code);
    Mono<GroupRoleOperatAuthZt> findFirstByRoleUniqueCodeAndZtUniqueCodeAndZtYearOrderByZtYear(String userId,String coCode,String year);

    Flux<GroupRoleOperatAuthZt> findAllByRoleUniqueCodeAndDefaultLogin(String userId, String s);
    Flux<GroupRoleOperatAuthZt> findAllByRoleUniqueCodeAndZtUniqueCode(String userId, String s);

    Flux<GroupRoleOperatAuthZt> findAllByZtUniqueCodeOrderByZtYearDesc(String code);
}
