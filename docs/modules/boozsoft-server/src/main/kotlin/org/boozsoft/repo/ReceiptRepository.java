package org.boozsoft.repo;

import org.boozsoft.domain.entity.account.Receipt;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ReceiptRepository extends ReactiveCrudRepository<Receipt,String> {

    Flux<Receipt> findByUniqueCodeOrderById(String uniqueCode);

    Mono<Receipt> deleteByUniqueCode(String uniqueCode);

}
