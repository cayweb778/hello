package org.boozsoft.repo.stock;

import org.boozsoft.domain.entity.stock.StockJhzxStock;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface StockJhzxStockRepository extends ReactiveCrudRepository<StockJhzxStock, String> {


    Flux<StockJhzxStock> findAllByCcode(String id);
}



