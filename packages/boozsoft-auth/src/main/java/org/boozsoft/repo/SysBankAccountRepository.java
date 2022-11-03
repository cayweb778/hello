package org.boozsoft.repo;

import org.boozsoft.domain.entity.account.SysBankAccount;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface SysBankAccountRepository extends ReactiveCrudRepository<SysBankAccount, String> {

    Flux<SysBankAccount> findByCnameOrderById(String cname);
    Flux<SysBankAccount> findByStatusOrderById(String status);

}
