package org.boozsoft.repo;

import org.boozsoft.domain.entity.account.SysPsnRollback;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface SysPsnRollbackRepository extends ReactiveCrudRepository<SysPsnRollback, String> {

    Flux<SysPsnRollback> findAllByOrderById(Pageable pageable);

}
