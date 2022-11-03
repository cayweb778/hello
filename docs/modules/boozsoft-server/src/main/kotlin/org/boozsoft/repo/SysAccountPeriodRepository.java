package org.boozsoft.repo;

import org.boozsoft.domain.entity.group.SysAccountPeriod;
import org.boozsoft.domain.vo.DataBaseVo;
import org.boozsoft.domain.vo.SysAccountPeriodVo;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface SysAccountPeriodRepository extends ReactiveCrudRepository<SysAccountPeriod, String> {

    Flux<SysAccountPeriod> findByAccountIdOrderByAccountYearDesc(String accountId);
    Mono<Long> countByAccountMode(String accountMode);
    Mono<Long> countByAccountIdAndAccountYear(String accountId,String accountYear);
    @Query("select acp.*,acc.acc_name,acc.id as database_id from _app_group_sys_account_period acp left join _app_group_sys_account acc on acc.acc_id=account_id where account_id=:accountId and account_year=:accountYear ")
    Mono<SysAccountPeriodVo> findByAccountIdAndAccountYear(String accountId, String accountYear);

    @Query("select acp.*,acc.acc_name,acc.id as database_id from _app_group_sys_account_period acp left join _app_group_sys_account acc on acc.co_code=acp.account_co_code where acp.account_co_code=:accountId and acp.account_year=:accountYear ")
    Mono<SysAccountPeriodVo> findByCoCodeAndAccountYear(String accountId, String accountYear);

    Flux<SysAccountPeriod> findAllByAccountCoCodeOrderByAccountYearDesc(String accountCode);
    Flux<SysAccountPeriod> findAllByAccountModeOrderByAccountYearDesc(String accountMode);

    Mono<Void> deleteAllByAccountId(String accountId);
    Mono<Void> deleteAllByAccountCoCode(String accountId);
    Mono<Void> deleteAllByAccountCoCodeAndAccountYear(String accountId,String year);

    @Query("select account_mode as acc_id,acc_name from _app_group_sys_account a left join _app_group_sys_account_period on a.acc_id=account_id where a.independent='0' and account_mode is not null")
    Flux<DataBaseVo> findByAllSysAccPeriodGroupDataBase();

    Flux<SysAccountPeriod> findAllByAccountIdLikeOrderByAccountIdDesc(String s);
}
