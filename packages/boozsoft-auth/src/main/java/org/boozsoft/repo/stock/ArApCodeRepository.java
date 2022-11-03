package org.boozsoft.repo.stock;

import org.boozsoft.domain.entity.stock.ArApCode;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface ArApCodeRepository extends ReactiveCrudRepository<ArApCode, String> {

    Flux<ArApCode> findAllByBillStyleOrderById(String billStyle);

}
