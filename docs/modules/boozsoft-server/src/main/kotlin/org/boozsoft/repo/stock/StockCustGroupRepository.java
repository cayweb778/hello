package org.boozsoft.repo.stock;

import org.boozsoft.domain.entity.stock.StockCustGroup;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface StockCustGroupRepository extends ReactiveCrudRepository<StockCustGroup, String> {

    @Query("select ccode from stock_cust_group")
    Flux<String> getStockCustGroupMaxCcode();
    Mono<Long> countByCustGroupName(String name);

    @Query("select * from stock_cust_group order by ccode")
    Flux<StockCustGroup> findAll1();

    Mono<Void> deleteByCcode(String ccode);

}



