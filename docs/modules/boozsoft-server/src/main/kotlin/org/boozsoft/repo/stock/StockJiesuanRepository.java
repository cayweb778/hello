package org.boozsoft.repo.stock;

import org.boozsoft.domain.entity.stock.StockJiesuan;
import org.boozsoft.domain.entity.stock.StockWarehousing;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface StockJiesuanRepository extends ReactiveCrudRepository<StockJiesuan, String> {
    Mono<Void> deleteByCcodeRuku(String ccodeRuku);
    Mono<Void> deleteByCcode(String ccode);

    @Query("select * from stock_jiesuan where iyear=:iyear order by ddate asc")
    Flux<StockJiesuan> findAllByIyear(String iyear);

    Flux<StockJiesuan> findAllByIyearAndBdocumStyleAndCcodeLikeOrderByDdateAscCcodeAsc( String year, String style, String s);
}



