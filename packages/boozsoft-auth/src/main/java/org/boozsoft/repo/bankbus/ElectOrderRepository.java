package org.boozsoft.repo.bankbus;

import org.boozsoft.domain.entity.account.bankbus.ElectOrder;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ElectOrderRepository extends ReactiveCrudRepository<ElectOrder, String> {

    Flux<ElectOrder> findAllByOrderById(Pageable pageable);
    Mono<ElectOrder> findBySerialNumberOrderById(String serialNumber);

}

