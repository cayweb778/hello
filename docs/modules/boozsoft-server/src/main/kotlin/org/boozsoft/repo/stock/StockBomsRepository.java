package org.boozsoft.repo.stock;

import org.boozsoft.domain.entity.stock.StockBoms;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface StockBomsRepository extends ReactiveCrudRepository<StockBoms, String> {
    Flux<StockBoms> findAllByBomUniqueIdOrderByGxIdAsc(String bomId);
}



