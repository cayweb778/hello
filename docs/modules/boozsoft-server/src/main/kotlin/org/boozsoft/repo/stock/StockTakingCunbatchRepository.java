package org.boozsoft.repo.stock;

import org.boozsoft.domain.entity.stock.StockTakingClass;
import org.boozsoft.domain.entity.stock.StockTakingCunbatch;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface StockTakingCunbatchRepository extends ReactiveCrudRepository<StockTakingCunbatch, String> {

    @Query("delete from stock_taking_cunbatch where pid=:pid")
    Mono<Void> deleteByPid(String pid);

    @Query("select * from stock_taking_cunbatch where pid=:pid and stock_batch in (:classList)")
    Flux<StockTakingCunbatch> findAllByPidAndStockBatch(String pid ,  String cwhcode, String[] classList);
}



