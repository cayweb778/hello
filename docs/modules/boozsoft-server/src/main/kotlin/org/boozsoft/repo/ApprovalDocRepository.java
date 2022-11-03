package org.boozsoft.repo;

import org.boozsoft.domain.entity.account.ApprovalDoc;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ApprovalDocRepository extends ReactiveCrudRepository<ApprovalDoc, String> {

    Flux<ApprovalDoc> findAllByOrderById(Pageable pageable);
    Mono<ApprovalDoc> findByDocCodeOrderById(String docCode);

}
