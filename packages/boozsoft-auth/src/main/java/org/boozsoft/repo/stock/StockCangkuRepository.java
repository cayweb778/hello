package org.boozsoft.repo.stock;

import org.boozsoft.domain.entity.stock.StockCangku;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface StockCangkuRepository extends ReactiveCrudRepository<StockCangku, String> {

    @Query("select * from stock_cangku order by ck_num asc ")
    Flux<StockCangku> findAllOrderByCreatTime();

    @Query("delete from stock_cangku where id in (:list) ")
    Mono<Void>delAllById(List<String> list);
    Mono<Long> countByCkName(String name);
    Mono<Long> countByCkNum(String num);
    Flux<StockCangku> findByCkIsDuli(String a);


}
