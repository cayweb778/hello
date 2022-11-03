package org.boozsoft.repo.stock;

import org.boozsoft.domain.entity.stock.StockAd;
import org.boozsoft.domain.entity.stock.StockWarehousing;
import org.boozsoft.domain.vo.stock.StockAdQTRKDAndQTCKDVo;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface StockAdRepository extends ReactiveCrudRepository<StockAd, String> {
    Flux<StockAd> findAllByIyearOrderByDdateAscCcodeAsc(String iyear);
    Mono<Void> deleteByCcode(String ccode);

    @Query("select distinct (select count(id)\n" +
            "                 from stock_xy_csource xy\n" +
            "                 where xy.ccode =:ccode  and xy.xy_bill_style = 'QTRKD') QTRKD,\n" +
            "                (select count(id)\n" +
            "                 from stock_xy_csource xy\n" +
            "                 where xy.ccode =:ccode and xy.xy_bill_style = 'QTCKD') QTCKD\n" +
            "from stock_xy_csource\n" +
            "where ccode =:ccode ")
    Mono<StockAdQTRKDAndQTCKDVo> getXyRkCk(String ccode);
}



