package org.boozsoft.repo.stock;

import org.boozsoft.domain.entity.stock.StockJiesuan;
import org.boozsoft.domain.entity.stock.StockJiesuans;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface StockJiesuansRepository extends ReactiveCrudRepository<StockJiesuans, String> {
    Mono<Void> deleteByCcodeRuku(String ccodeRuku);
    Mono<Void> deleteByCcodeDaohuo(String ccodeDaohuo);
    Mono<Void> deleteByCcode(String ccode);
    Flux<StockJiesuans> findAllByCcode(String ccode);
    @Query("select sj.*\n" +
            "from stock_jiesuan sj\n" +
            "         left join stock_jiesuans sjs on sjs.ccode = sj.ccode\n" +
            "where sjs.ccode_daohuo =:ccode ")
    Flux<StockJiesuan> findAllByCcodeDaoHuo(String ccode);
}



