package org.boozsoft.repo;

import org.boozsoft.domain.entity.SysPeriod;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface SysPeriodRepository extends ReactiveCrudRepository<SysPeriod, String> {

    @Query("SELECT iyear,(select account_id from _app_group_sys_period where account_id=:accountId and enable_period='1' GROUP BY account_id ),(select enable_period from _app_group_sys_period where account_id=:accountId and enable_period='1' GROUP BY enable_period ) from _app_group_sys_period where account_id=:accountId group by iyear order by iyear desc")
    Flux<SysPeriod> findIyearAndEnablePeriod(String accountId);

    Flux<SysPeriod> findAllByAccountIdAndIyearAndBeiyong1NullOrderByIyearAscDateStartAsc(String acc,String year);

    @Query("SELECT * FROM _app_group_sys_period sp WHERE account_id= :acc AND (sp.beiyong1 IS NULL) ORDER BY iyear ASC, date_start ASC")
    Flux<SysPeriod> findAllByAccountIdAndBeiyong1NullOrderByIyearAscDateStartAsc(String acc);

    Flux<SysPeriod> findAllByAccountIdAndIyearAndBeiyong1OrderByIyearAscIperiodNumAsc(String acc,String year,String mark);

    Flux<SysPeriod> findAllByAccountIdAndIyearInOrderByIyearAscIperiodNumAsc(String acc, List<String> year);

    @Query("SELECT * FROM _app_group_sys_period where account_id = :accountId and beiyong1 is null ORDER BY iyear ASC,CAST(iperiod_num as INTEGER)  ASC")
    Flux<SysPeriod> findAllByAccountId(String accountId);

    @Query("SELECT * FROM _app_group_sys_period where account_id = :accountId and iyear=:year and beiyong1 is null ORDER BY iyear ASC,CAST(iperiod_num as INTEGER)  ASC")
    Flux<SysPeriod> findAllByAccountIdAndIyear(String accountId,String year);

    @Query("SELECT iyear FROM _app_group_sys_period where  account_id = :accountId group by iyear ORDER BY iyear")
    Flux<SysPeriod> findYearAccountId(String accountId);

    @Query("SELECT distinct iyear FROM _app_group_sys_period where  account_id = :accountId ORDER BY iyear")
    Flux<String> findAllYearAccountId(String accountId);

    //查询账套最小日期
    @Query("select iyear || '-' || date_start as date from _app_group_sys_period where account_id=:account_id and period is not null and period like :iyear ORDER BY period ASC LIMIT 1")
    Flux<String> findYearMinDate(@Param("account_id") String account_id, @Param("iyear") String iyear);

    //查询账套最大日期
    @Query("select iyear || '-' || date_end as date from _app_group_sys_period where account_id=:account_id and period is not null and period like :iyear ORDER BY period DESC LIMIT 1")
    Flux<String> findYearMaxDate(@Param("account_id") String account_id,@Param("iyear") String iyear);

    Mono<SysPeriod> findFirstByAccountIdAndIyearAndEnablePeriod(String accId,String year,String enable);

    Mono<SysPeriod> findFirstByAccountIdAndId(String accId,String ud);
    Mono<SysPeriod> findFirstByAccountIdAndIyearAndGlNotOrAccountIdAndIyearAndGlIsNullOrderByIperiodNumAsc(String accId,String year,String nVal,String accId2,String year2);
    Flux<SysPeriod> findAllByAccountIdAndIyearOrderByDateStartAsc(String accId,String year);
    Mono<Long> countAllByAccountIdAndIyear(String accId,String year);

    Mono<Void> deleteAllByAccountId(String accId);
    Mono<Void> deleteAllByAccountIdAndIyear(String accId,String year);

    @Query("select iyear from _app_group_sys_period where account_id=:accId group by iyear order by iyear")
    Flux<String> findByAccIdIyearGroupBy(String accId);
}
