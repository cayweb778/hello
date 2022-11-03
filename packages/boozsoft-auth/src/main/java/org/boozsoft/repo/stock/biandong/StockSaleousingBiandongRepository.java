package org.boozsoft.repo.stock.biandong;

import org.boozsoft.domain.entity.stock.biandong.StockSaleousingBiandong;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface StockSaleousingBiandongRepository extends ReactiveCrudRepository<StockSaleousingBiandong, String> {

    Mono<StockSaleousingBiandong> findFirstByCcodeLikeOrderByBaindongDateDesc(String like);
    Flux<StockSaleousingBiandong> findAllByCcodeLikeOrderByBaindongDateDesc(String like);
    Flux<StockSaleousingBiandong> findAllByIyearAndCcodeLikeOrderByBaindongDateDescCcodeDesc(String year,String like);

}



