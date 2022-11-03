package org.boozsoft.repo.stock;

import org.boozsoft.domain.entity.stock.StockAd;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface StockAdRepository extends ReactiveCrudRepository<StockAd, String> {

}



