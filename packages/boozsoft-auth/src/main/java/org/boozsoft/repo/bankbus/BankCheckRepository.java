package org.boozsoft.repo.bankbus;

import org.boozsoft.domain.entity.account.bankbus.BankCheck;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BankCheckRepository extends ReactiveCrudRepository<BankCheck, String> {

    Flux<BankCheck> findAllByOrderById(Pageable pageable);
    Mono<BankCheck> findByCheckNumOrderById(String checkNum);

}
