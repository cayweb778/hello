package org.boozsoft.repo;

import org.boozsoft.domain.entity.account.Contract;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ContractRepository extends ReactiveCrudRepository<Contract, String> {

    Flux<Contract> findAllByOrderById(Pageable pageable);
    Mono<Contract> findByConNumOrderById(String conNum);

}
