package org.boozsoft.repo.stock;

import org.boozsoft.domain.entity.stock.StockChangesSource;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface StockChangesCsourceRepository extends ReactiveCrudRepository<StockChangesSource, String> {

    Flux<StockChangesSource> findAllByCcodeAndIyear(String xyCode,String iyear);

    @Query("delete from stock_changes_source where ccode = :ccode")
    Mono<Void> deleteByCcode(String ccode);


    @Query("select "+
            "  (case  " +
            "     when xy.xy_bill_style = 'QTRKD' then (select sw.bcheck from stock_warehousing sw where sw.ccode = xy.syccode) " +
            "     when xy.xy_bill_style = 'QTCKD' then (select sa.bcheck from stock_saleousing sa where sa.ccode = xy.syccode) " +
            "     else (xy.xy_bill_style) " +
            "   end) as flgs " +
            " from  stock_changes_source xy " +
            "where xy.ccode =:code ")
    Flux<String> findByXyOutDataSourrce(String code);


}



