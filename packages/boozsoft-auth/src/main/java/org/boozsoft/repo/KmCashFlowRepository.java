package org.boozsoft.repo;


import org.boozsoft.domain.entity.KmCashFlow;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface KmCashFlowRepository extends ReactiveCrudRepository<KmCashFlow, String> {

    Flux<KmCashFlow> findAllByTenantId(String accontId);

    Mono<Void> deleteByTenantIdAndIyear(String accontId, String year);

    Mono<Long> countByIyearAndCcodeAndTenantId(String iyear, String ccode,String accontId);

    @Query("select ccode from km_cash_flow where iyear=:iyear ")
    Flux<String> findAllByCodeStr(String year);
}
