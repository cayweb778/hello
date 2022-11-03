package org.boozsoft.repo.bankbus;

import org.boozsoft.domain.entity.account.bankbus.BankBill;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BankBillRepository extends ReactiveCrudRepository<BankBill, String> {

    Flux<BankBill> findAllByOrderById(Pageable pageable);
    Mono<BankBill> findByBillNumOrderById(String billNum);

}

