package org.boozsoft.repo.stock;

import org.boozsoft.domain.entity.stock.ArApYsyfs;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ArApYsyfsRepository extends ReactiveCrudRepository<ArApYsyfs, String> {

    Flux<ArApYsyfs> findByBillStyleAndIyearOrderByCcode(String billStyle, String iyear);

    Flux<ArApYsyfs> findByCcodeOrderByLineId(String ccode);

    Mono<ArApYsyfs> deleteByCcode(String ccode);

}
