package org.boozsoft.repo.stock;

import org.boozsoft.domain.entity.stock.StockWuliuCsource;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface StockWuLiuCsourceRepository extends ReactiveCrudRepository<StockWuliuCsource, String> {

    @Query("delete from stock_wuliu_csource where ccode=:ccode ")
    Mono<Void> wuliuCsourceDel(String ccode);
}



