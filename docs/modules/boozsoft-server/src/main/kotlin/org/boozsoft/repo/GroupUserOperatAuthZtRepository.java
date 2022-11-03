package org.boozsoft.repo;

import org.boozsoft.domain.entity.SysPeriod;
import org.boozsoft.domain.entity.group.GroupUserOperatAuthZt;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface GroupUserOperatAuthZtRepository extends ReactiveCrudRepository<GroupUserOperatAuthZt, String> {

    @Query("select distinct account_co_code as zt_unique_code,account_year as zt_year from _app_group_sys_account_period order by account_co_code asc ,account_year desc")
    Flux<GroupUserOperatAuthZt> findAllByOrderByZtYearDesc();
    Flux<GroupUserOperatAuthZt> findAllByUserUniqueCodeOrderByZtYearDesc(String code);
    Mono<GroupUserOperatAuthZt> findFirstByUserUniqueCodeAndZtUniqueCodeAndZtYearOrderByZtYear(String userId,String coCode,String year);

    Flux<GroupUserOperatAuthZt> findAllByUserUniqueCodeAndDefaultLogin(String userId, String s);
    Flux<GroupUserOperatAuthZt> findAllByUserUniqueCodeAndZtUniqueCode(String userId, String s);

    Flux<GroupUserOperatAuthZt> findAllByZtUniqueCodeOrderByZtYearDesc(String code);

    /***************************Miao******************************/
    @Query("select per.*\n" +
            "from _group_user_operat_auth_zt auth\n" +
            "left join _app_group_sys_account acc on acc.co_code=auth.zt_unique_code\n" +
            "left join _app_group_sys_period per on per.account_id=acc.acc_id\n" +
            "where auth.user_unique_code =:userUnique \n" +
            "  and auth.zt_unique_code =:ztCode \n" +
            "  and auth.zt_year=per.iyear and per.period is not null \n" +
            "order by per.iyear,per.iperiod_num")
    Flux<SysPeriod> findAllByAuthPeriod(String userUnique, String ztCode);
}
