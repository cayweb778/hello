package org.boozsoft.repo.stock;

import org.boozsoft.domain.entity.stock.StockJhzx;
import org.boozsoft.domain.entity.stock.StockJhzxCust;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface StockJhzxCustRepository extends ReactiveCrudRepository<StockJhzxCust, String> {


    Flux<StockJhzxCust> findAllByCcode(String id);
}



