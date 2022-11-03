package org.boozsoft.repo.stock;

import org.boozsoft.domain.entity.stock.StockWulius;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

public interface StockWuLiusRepository extends ReactiveCrudRepository<StockWulius, String> {
    Flux<StockWulius> findAllByCcodeOrderByCcodeAsc(String code);

    @Query("update stock_saleousings set isum_wuliu=cast(isum_wuliu as float)+:wuliuNum where ccode =:ccode ")
    Mono<Void> editStockSaleousingsIsSumWuliu(BigDecimal wuliuNum, String ccode);
}



