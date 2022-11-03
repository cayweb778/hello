package org.boozsoft.repo.stock;

import org.boozsoft.domain.entity.stock.StockPrice;
import org.springbooz.core.tool.result.R;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

import java.util.List;

public interface StockPriceRepository extends ReactiveCrudRepository<StockPrice, String> {

    @Query("delete from stock_price where id in (:list)")
    Mono<Void> deleteByIds(List<String> list);

    Mono<StockPrice> findByStockNum(String stockNum);
    @Query("delete from stock_price where stock_num = :stockNum")
    Mono<Void> deleteByStockNum(String stockNum);

}



