package org.boozsoft.repo;

import org.boozsoft.domain.entity.account.Reimbursement;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ReimbursementRepository extends ReactiveCrudRepository<Reimbursement, String> {

    Flux<Reimbursement> findAllByOrderById(Pageable pageable);
    Mono<Reimbursement> findByReimCodeOrderById(String reimCode);

}
