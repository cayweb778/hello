package org.boozsoft.repo;

import org.boozsoft.domain.entity.BudgetSource;
import org.boozsoft.domain.entity.FaEconomyUse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FaEconomyUseRepository extends ReactiveCrudRepository<FaEconomyUse, String> {

    Flux<FaEconomyUse> findAllByOrderById(Pageable pageable);
    Mono<FaEconomyUse> findByBsCodeOrderById(String code);
    Mono<FaEconomyUse> findByBsNameOrderById(String bsName);
    Flux<FaEconomyUse> findByFlagOrderById(String flag);
}
