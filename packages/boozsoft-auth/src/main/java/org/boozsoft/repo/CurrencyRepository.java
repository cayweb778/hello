package org.boozsoft.repo;

import org.boozsoft.domain.entity.SysCurrency;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface CurrencyRepository extends ReactiveCrudRepository<SysCurrency, String> {
    Flux<SysCurrency> findAllByOrderByNumAsc();
}

