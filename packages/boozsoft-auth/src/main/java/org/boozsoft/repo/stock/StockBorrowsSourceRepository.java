package org.boozsoft.repo.stock;

import org.boozsoft.domain.entity.stock.StockBorrowsSource;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface StockBorrowsSourceRepository extends ReactiveCrudRepository<StockBorrowsSource, String> {

    Mono<Void> deleteByCcodeAndXyccode(String ccode,String xyccode);
}
