package org.boozsoft.repo.stock;

import org.boozsoft.domain.entity.stock.ArApExpenses;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ArApExpensesRepository extends ReactiveCrudRepository<ArApExpenses, String> {

    Flux<ArApExpenses> findAllByBillStyleAndCcodeOrderByDdateAscCcodeAscLineIdAsc(String billStyle, String ccode);
    @Query("delete from ar_ap_hzhcds where ccode=:ccode and bill_style=:billStyle ")
    Mono<ArApExpenses> deleteByCcodeAndBillType(String ccode,String billStyle);

}



