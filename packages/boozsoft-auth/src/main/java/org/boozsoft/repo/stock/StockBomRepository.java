package org.boozsoft.repo.stock;

import org.boozsoft.domain.entity.stock.StockBom;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface StockBomRepository extends ReactiveCrudRepository<StockBom, String> {
    Flux<StockBom> findAllByBomSysIdOrderByCmakerTimeAsc(String sysId);
    Flux<StockBom> findAllByOrderByCmakerTimeAsc();

    Flux<StockBom> findByBcheck(String bcheck);
    Mono<StockBom> findByBomUniqueId(String bomUniqueId);
}



