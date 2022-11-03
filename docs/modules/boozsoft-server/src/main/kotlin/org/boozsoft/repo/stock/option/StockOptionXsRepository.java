package org.boozsoft.repo.stock.option;

import org.boozsoft.domain.entity.stock.StockOptionXs;
import org.boozsoft.domain.entity.stock.StockWarehousing;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface StockOptionXsRepository extends ReactiveCrudRepository<StockOptionXs, String> {

}



