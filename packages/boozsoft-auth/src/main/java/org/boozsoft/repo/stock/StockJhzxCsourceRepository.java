package org.boozsoft.repo.stock;

import org.boozsoft.domain.entity.stock.StockJhzxCsource;
import org.boozsoft.domain.vo.stock.StockXySourceVo;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface StockJhzxCsourceRepository extends ReactiveCrudRepository<StockJhzxCsource, String> {

    Mono<StockJhzxCsource> findByIyearAndCcodeAndCcodeDate(String year,String code,String date);
    Mono<StockJhzxCsource> findByCcodeAndCcodeDate(String year,String code,String date);
    Mono<StockJhzxCsource> findByIyearAndCcodeAndXyBillStyle(String year,String code,String xyCode);
    Flux<StockJhzxCsource> findAllByBillStyleAndCcodeIn(String type,List<String> code);

    Flux<StockJhzxCsource> findAllByBillStyleAndIyear(String billStyle,String iyear);

    Mono<StockJhzxCsource> findByXyBillStyleAndXyccodeAndIyear(String xyStyle,String xyCode,String iyear);

    Flux<StockJhzxCsource> findByCcodeAndXyccode(String code,String xycode);

}



