package org.boozsoft.repo;

import org.boozsoft.domain.entity.account.ContractBiaodi;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ContractBiaodiRepository extends ReactiveCrudRepository<ContractBiaodi, String> {

    Flux<ContractBiaodi> findByConIdOrderById(String conId);

    Mono<ContractBiaodi> deleteByConId(String conId);

}
