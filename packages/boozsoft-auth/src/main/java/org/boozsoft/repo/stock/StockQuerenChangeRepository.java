package org.boozsoft.repo.stock;

import org.boozsoft.domain.entity.stock.StockQuerenChange;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface StockQuerenChangeRepository extends ReactiveCrudRepository<StockQuerenChange, String> {
    Flux<StockQuerenChange> findAllByBillStyleAndIyearAndQuerdId(String type,String iyear,String code);
}