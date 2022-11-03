package org.boozsoft.repo;

import org.boozsoft.domain.entity.ExchangeRateEntry;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ExchangeRateEntryRepository extends ReactiveCrudRepository<ExchangeRateEntry, Long> {
    Flux<ExchangeRateEntry> findAllByUniqueCodeAndCurrencyCodeAndIperiodLikeOrderByIperiodAsc(String code,String cuCode,String dateLike);
    Flux<ExchangeRateEntry> findAllByUniqueCodeAndCurrencyCodeOrderByIperiodAsc(String code,String cuCode);
    Mono<ExchangeRateEntry> findById(String id);
    Mono<ExchangeRateEntry> deleteById(String id);
}
