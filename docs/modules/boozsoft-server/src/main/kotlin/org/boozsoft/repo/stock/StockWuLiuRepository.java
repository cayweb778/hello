package org.boozsoft.repo.stock;

import org.boozsoft.domain.entity.stock.StockWuliu;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface StockWuLiuRepository extends ReactiveCrudRepository<StockWuliu, String> {
    Flux<StockWuliu> findAllByIyearOrderByDdateDescCcodeDesc(String Iyaer);
    Mono<StockWuliu> findByCcode(String ccode);

    Flux<StockWuliu> findAllByDdateLikeOrderByBcheckAscDdateAscCcodeAsc(String s);
    Flux<StockWuliu> findAllByIyearAndCcodeLikeOrderByDdateAscCcodeAsc(String year,String s);
}



