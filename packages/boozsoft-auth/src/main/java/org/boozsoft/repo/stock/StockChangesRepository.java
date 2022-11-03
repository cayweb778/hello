package org.boozsoft.repo.stock;

import cn.hutool.core.util.ArrayUtil;
import org.boozsoft.domain.entity.stock.StockChanges;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface StockChangesRepository extends ReactiveCrudRepository<StockChanges, String> {
    Flux<StockChanges> findAllByBillStyleAndCcodeOrderByDdateAscCcodeAscLineIdAsc(String type, String code);

    Flux<StockChanges> findAllByBillStyleAndCcodeAndFlgsOrderByDdateAscCcodeAscLineIdAsc(String type, String code, String flgs);
    Flux<StockChanges> findAllByBillStyleAndIyearOrderByDdateAscCcodeAscLineIdAsc(String type, String iyear);
    Flux<StockChanges> findAllByCcodeAndBillStyle(String ccode, String type);

    @Query("delete from stock_warehousings where sourcecode=:sourcecode and sourcetype=:billStyle ")
    Mono<Void> deleteBySourcecodeAndSourcetype(String sourcecode, String billStyle);

    Mono<StockChanges> findByLineCode(String linecode);

    Mono<Long> countAllByBillStyleAndCcode(String style, String code);

}



