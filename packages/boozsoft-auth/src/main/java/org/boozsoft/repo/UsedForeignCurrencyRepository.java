package org.boozsoft.repo;

import org.boozsoft.domain.entity.UsedForeignCurrency;
import org.boozsoft.repo.entity.Menu;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UsedForeignCurrencyRepository extends ReactiveCrudRepository<UsedForeignCurrency, Long> {

    Flux<UsedForeignCurrency> findAllByAccountIdOrderByForeignCodeAsc(String accId);
    Mono<UsedForeignCurrency> findFirstById(String accId);
    Mono<Void> deleteById(String id);

    Flux<UsedForeignCurrency> findAllByForeignCode(String value);

    Flux<UsedForeignCurrency> findAllByForeignName(String value);


    @Modifying
    @Query("delete from used_foreign_currency where tenant_id =:id ")
    Mono<Void> deleteAllByTenantId(String id);
}
