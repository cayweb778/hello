package org.boozsoft.repo;

import org.boozsoft.domain.entity.SysAccCodeAuth;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface SysAccCodeAuthRepository extends ReactiveCrudRepository<SysAccCodeAuth, String> {

    Flux<SysAccCodeAuth>  findAllByUserNumAndAccIdAndIyearOrderByCodeNumAsc(String userId, String accId, String year);
}
