package org.boozsoft.repo.stock;

import org.boozsoft.domain.entity.stock.ArXySource;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ArXySourceRepository extends ReactiveCrudRepository<ArXySource, String> {

    Mono<ArXySource> findByIyearAndCcodeAndCcodeDate(String year,String code,String date);
    Mono<ArXySource> findByCcodeAndCcodeDate(String year,String code,String date);
    Mono<ArXySource> findByIyearAndCcodeAndXyBillStyle(String year,String code,String xyCode);
    Flux<ArXySource> findAllByBillStyleAndCcodeIn(String type,List<String> code);

    Flux<ArXySource> findAllByBillStyleAndIyear(String billStyle,String iyear);

    Mono<ArXySource> findByXyBillStyleAndXyccodeAndIyear(String xyStyle,String xyCode,String iyear);

    Flux<ArXySource> findByCcodeAndXyccode(String code,String xycode);
    Flux<ArXySource> findByXyccodeAndXyBillStyle(String code,String xycode);
    Flux<ArXySource> findAllByCcode(String code);

}



