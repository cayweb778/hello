package org.boozsoft.repo.stock.biandong;

import org.boozsoft.domain.entity.stock.biandong.StockWarehousingsBiandong;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface StockWarehousingsBiandongRepository extends ReactiveCrudRepository<StockWarehousingsBiandong, String> {

    @Query("delete from stock_warehousings_biandong where ccode like :ccode ")
    Mono<Void> delStockJoinPojo(String ccode);
    Flux<StockWarehousingsBiandong> findAllByBillStyleAndCcodeOrderByDdateAscCcodeAscLineIdAsc(String type, String code);
}



