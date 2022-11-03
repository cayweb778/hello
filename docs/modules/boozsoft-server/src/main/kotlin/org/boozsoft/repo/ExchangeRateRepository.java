package org.boozsoft.repo;

import org.boozsoft.domain.entity.ExchangeRate;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ExchangeRateRepository extends ReactiveCrudRepository<ExchangeRate, Long> {
    Mono<ExchangeRate> findFirstByAccountIdAndIyear(String accId,String iyear);
    Flux<ExchangeRate> findAllByIyear(String iyear);

    Mono<ExchangeRate> findById(String id);
}
