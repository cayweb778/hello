package org.boozsoft.repo;

import org.boozsoft.domain.entity.group.Bank;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface BankRepository extends ReactiveCrudRepository<Bank, String> {

    Flux<Bank> findAllByOrderById(Pageable pageable);
    Flux<Bank> findByBankCodeOrderById(String bankCode);
    Flux<Bank> findBySuccessStateOrderById(String successState);

}
