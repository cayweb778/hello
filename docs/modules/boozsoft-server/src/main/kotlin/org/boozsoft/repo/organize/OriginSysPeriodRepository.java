package org.boozsoft.repo.organize;


import org.boozsoft.domain.entity.organize.OrgUsedForeignCurrency;
import org.boozsoft.domain.entity.origin.OriginSysPeriod;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface OriginSysPeriodRepository extends ReactiveCrudRepository<OriginSysPeriod, String> {

    @Query("select iyear from _app_org_sys_period where org_id=:orgId group by iyear order by iyear")
    Flux<String> groupByOrgPeriodIyear(String orgId);

    Flux<OriginSysPeriod> findAllByOrgIdAndBeiyong1IsNullOrderByIyearAscPeriodAsc(String orgId);


    Flux<OriginSysPeriod> findAllByOrgIdAndIyearAndBeiyong1IsNullOrderByIyearAscPeriodAsc(String orgId,String iyear);

    Mono<Void> deleteAllByOrgIdAndIyear(String orgId, String iyear);
}

