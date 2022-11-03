package org.boozsoft.repo.stock;

import org.boozsoft.domain.entity.stock.StockDefineHead;
import org.boozsoft.domain.entity.stock.StockDefineReceiptHead;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface StockDefineReceiptHeadRepository extends ReactiveCrudRepository<StockDefineReceiptHead, String> {
    Mono<Long> countByDefineName(String name);
    Mono<StockDefineHead> findByDefineName(String name);

    @Query("delete from stock_define_receipt_head where id=:id ")
    Mono<Void> deleteAllById(String id);

    @Query("update stock_define_receipt_head set flag=:flag where id in (:list) ")
    Mono<Void> eidtByFlag(String flag,List<String> list);

    @Query("select * from stock_define_receipt_head order by define_name")
    Flux<StockDefineReceiptHead> findAll();
}
