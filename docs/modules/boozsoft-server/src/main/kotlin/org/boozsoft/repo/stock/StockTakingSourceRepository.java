package org.boozsoft.repo.stock;

import org.boozsoft.domain.entity.stock.StockTaking;
import org.boozsoft.domain.entity.stock.StockTakingSource;
import org.boozsoft.domain.entity.stock.StockTransferSource;
import org.boozsoft.domain.vo.stock.StockTakingVo;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface StockTakingSourceRepository extends ReactiveCrudRepository<StockTakingSource, String> {

    @Query("delete from stock_taking_source where ccode=:ccode ")
    Mono<Object> deleteByCcode(String ccode);

    Flux<StockTakingSource> findALLByCcode(String ccode);

    Flux<StockTakingSource> findAllByCcodeAndIyear(String xyCode, String iyear);

}



