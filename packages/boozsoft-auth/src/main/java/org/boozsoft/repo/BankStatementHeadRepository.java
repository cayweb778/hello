package org.boozsoft.repo;

import org.boozsoft.domain.entity.account.BankStatementHeader;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface BankStatementHeadRepository extends ReactiveCrudRepository<BankStatementHeader, String> {

    Flux<BankStatementHeader> findByIyearAndCcodeOrderById(String iyear,String ccode);

}

