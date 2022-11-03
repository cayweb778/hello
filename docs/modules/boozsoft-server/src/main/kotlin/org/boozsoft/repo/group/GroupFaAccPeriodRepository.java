package org.boozsoft.repo.group;

import org.boozsoft.domain.entity.group.GroupFaAccPeriod;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.Set;

public interface GroupFaAccPeriodRepository extends ReactiveCrudRepository<GroupFaAccPeriod, String> {
    Mono<GroupFaAccPeriod> findFirstByUniqueCodeAndIyearAndImonth(String code,String year,String month);

    Mono<GroupFaAccPeriod> findFirstByClassUniqueCodeAndIyearAndIsSettleOrderByImonthAsc(String code,String year,String settle);
    Mono<GroupFaAccPeriod> findFirstByClassUniqueCodeAndIyearAndIsSettleOrderByImonthDesc(String code,String year,String settle);

    Flux<GroupFaAccPeriod> findByUniqueCodeAndIyearOrderByImonth(String uniqueCode,String year);

    Mono<GroupFaAccPeriod> findByUniqueCode(String uniqueCode);
    Flux<GroupFaAccPeriod> findAllByUniqueCodeOrderByIyearAscImonthAsc(String uniqueCode);

    Flux<GroupFaAccPeriod> findAllByUniqueCodeInOrderByIyearDescImonthAsc(Set<String> uniqueCode);
}
