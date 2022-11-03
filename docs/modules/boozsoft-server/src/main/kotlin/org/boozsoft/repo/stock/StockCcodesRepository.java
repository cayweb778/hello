package org.boozsoft.repo.stock;

import org.boozsoft.domain.entity.stock.StockCcodes;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface StockCcodesRepository extends ReactiveCrudRepository<StockCcodes, String> {

    Flux<StockCcodes> findByUniqueIdOrderById(String uniqueId);
    Flux<StockCcodes> deleteAllByUniqueId(String uniqueId);

}
