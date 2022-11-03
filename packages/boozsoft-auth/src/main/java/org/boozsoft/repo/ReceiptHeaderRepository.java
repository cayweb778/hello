package org.boozsoft.repo;

import org.boozsoft.domain.entity.account.ReceiptHeader;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ReceiptHeaderRepository extends ReactiveCrudRepository<ReceiptHeader,String> {

    Flux<ReceiptHeader> findAllByOrderById(Pageable pageable);

    Mono<ReceiptHeader> findByReceCodeOrderById(String receCode);

}
