package org.boozsoft.repo;

import org.boozsoft.domain.entity.SysCurrencyCorp;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;


public interface SysCurrencyCorpRepository extends ReactiveCrudRepository<SysCurrencyCorp, String> {

    Flux<SysCurrencyCorp> findAllByCurrFlag(String flag);

}

