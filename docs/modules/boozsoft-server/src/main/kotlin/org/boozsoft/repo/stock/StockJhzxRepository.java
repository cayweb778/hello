package org.boozsoft.repo.stock;

import org.boozsoft.domain.entity.stock.StockJhzx;
import org.boozsoft.domain.entity.stock.StockSaleousing;
import org.boozsoft.domain.vo.stock.StockJhzxVo;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface StockJhzxRepository extends ReactiveCrudRepository<StockJhzx, String> {


    Mono<StockJhzx> findFirstByDdateLikeOrderByDdateDescCcodeDesc(String s);

    Flux<StockJhzx> findAllByDdateBetweenOrderByDdateAscCcodeAsc(String start, String end);


    @Query("select cust.out_ccode,cust.out_date,cust.cvencode,sa.* from stock_jhzx sa\n" +
            "    left join stock_jhzx_cust cust on cust.ccode=sa.ccode\n" +
            "where sa.ddate between :strDate and :endDate order  by sa.ddate asc,sa.ccode asc")
    Flux<StockJhzxVo> findAllByDdateBetweenOrderByDdateAscCcodeAscDetails(String start, String end);

    Mono<StockJhzx> findByCcode(String id);

    Flux<StockJhzx> findAllByCmakerDateLikeOrderByBcheckAscCmakerDateAscCcodeAsc(String s);
}



